package com.hjwylde.quxc;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.common.error.CompilerError;
import com.hjwylde.common.error.IllegalTimeUnitNameException;
import com.hjwylde.common.util.ExitCode;
import com.hjwylde.common.util.LoggerUtils;
import com.hjwylde.qbs.compiler.Compiler;
import com.hjwylde.qbs.compiler.CompilerFactory;
import com.hjwylde.qbs.util.QuxProperties;
import com.hjwylde.qux.util.Constants;
import com.hjwylde.quxc.compiler.QuxCompilerFactory;
import com.hjwylde.quxc.compiler.QuxcCompileOptions;
import com.hjwylde.quxc.compiler.QuxcCompileSpec;
import com.hjwylde.quxc.util.QuxcProperties;

import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Class for the command line call of {@code quxc}. Used for compiling qux source files into a
 * target file format.
 *
 * @author Henry J. Wylde
 */
public final class Quxc {

    public static final String OPT_CHARSET = QuxProperties.PROP_CHARSET;
    public static final String OPT_CLASSPATH = QuxProperties.PROP_CLASSPATH;
    public static final String OPT_HELP = "help";
    public static final String OPT_OUTDIR = QuxProperties.PROP_OUTDIR;
    public static final String OPT_PROPERTIES = "properties";
    public static final String OPT_TARGET = QuxcProperties.PROP_TARGET;
    public static final String OPT_TIMEOUT = QuxProperties.PROP_TIMEOUT;
    public static final String OPT_TIMEOUT_UNIT = QuxProperties.PROP_TIMEOUT_UNIT;
    public static final String OPT_VERBOSE = QuxProperties.PROP_VERBOSE;
    public static final String OPT_VERSION = "version";
    public static final String OPT_VERSION_CODE = "version-code";

    private static final Logger logger = LoggerFactory.getLogger(Quxc.class);

    private static final String OPT_PROPERTIES_DEFAULT = "quxc.properties";

    private static final Options OPTIONS = generateCommandLineOptions();

    private final QuxcCompileSpec spec;
    private CompilerFactory<? super QuxcCompileSpec, ? super QuxcCompileOptions> factory =
            new QuxCompilerFactory();

    /**
     * Creates a new Qux compilation unit with the given compile specification.
     *
     * @param spec the compile specification.
     */
    public Quxc(QuxcCompileSpec spec) {
        this.spec = checkNotNull(spec, "spec cannot be null");
    }

    /**
     * Generates a {@link com.hjwylde.quxc.compiler.QuxcCompileSpec} from the given command line
     * object.
     *
     * @param cl the command line object.
     * @return the generated compile specification.
     * @throws NoSuchFileException if a source file argument cannot be found.
     * @throws IOException if a properties file is specified and cannot be loaded.
     * @throws IllegalCharsetNameException if the character set name is illegal.
     * @throws UnsupportedCharsetException if the character set is not supported.
     */
    public static QuxcCompileSpec generateCompileSpec(CommandLine cl)
            throws NoSuchFileException, IOException, IllegalCharsetNameException,
            UnsupportedCharsetException {
        return generateCompileSpec(generateProperties(cl), cl.getArgs());
    }

    /**
     * Generates a {@link com.hjwylde.quxc.compiler.QuxcCompileSpec} from the given {@link
     * com.hjwylde.quxc.util.QuxcProperties} and source paths.
     *
     * @param properties the properties.
     * @param source the source files.
     * @return the generated compile specification.
     * @throws NoSuchFileException if a source file argument cannot be found.
     * @throws IllegalCharsetNameException if the character set name is illegal.
     * @throws UnsupportedCharsetException if the character set is not supported.
     */
    public static QuxcCompileSpec generateCompileSpec(QuxcProperties properties, String... source)
            throws NoSuchFileException, IllegalCharsetNameException, UnsupportedCharsetException {
        Path[] paths = new Path[source.length];
        for (int i = 0; i < source.length; i++) {
            paths[i] = Paths.get(source[i]).toAbsolutePath().normalize();
        }

        return generateCompileSpec(properties, paths);
    }

    /**
     * Generates a {@link com.hjwylde.quxc.compiler.QuxcCompileSpec} from the given {@link
     * com.hjwylde.quxc.util.QuxcProperties} and source paths.
     *
     * @param properties the properties.
     * @param source the source files.
     * @return the generated compile specification.
     * @throws NoSuchFileException if a source file argument cannot be found.
     * @throws IllegalCharsetNameException if the character set name is illegal.
     * @throws UnsupportedCharsetException if the character set is not supported.
     */
    public static QuxcCompileSpec generateCompileSpec(QuxcProperties properties, Path... source)
            throws NoSuchFileException, IllegalCharsetNameException, UnsupportedCharsetException {
        QuxcCompileSpec spec = new QuxcCompileSpec();

        spec.setOutdir(Paths.get(properties.getOutdir()));
        spec.setClasspath(new ArrayList<Path>());
        for (String path : properties.getClasspath().split(File.pathSeparator)) {
            spec.classpath(Paths.get(path));
        }

        QuxcCompileOptions.Builder optionsBuilder = QuxcCompileOptions.builder();
        optionsBuilder.setCharset(properties.getCharset());
        optionsBuilder.setTarget(properties.getTarget());
        optionsBuilder.setTimeout(properties.getTimeout());
        optionsBuilder.setTimeoutUnit(properties.getTimeoutUnit());
        optionsBuilder.setVerbose(properties.getVerbose());

        spec.setOptions(optionsBuilder.build());

        for (Path path : source) {
            if (!Files.exists(path) || Files.isDirectory(path)) {
                throw new NoSuchFileException(path.toString());
            }

            spec.source(path);
        }

        return spec;
    }

    /**
     * Generates a {@link com.hjwylde.quxc.compiler.QuxcCompileSpec} from the given command line
     * arguments.
     *
     * @param args the arguments.
     * @return the generated compile specification.
     * @throws ParseException if the arguments cannot be parsed.
     * @throws NoSuchFileException if a source file argument cannot be found.
     * @throws IOException if a properties file is specified and cannot be loaded.
     */
    public static QuxcCompileSpec generateCompileSpec(String[] args)
            throws ParseException, NoSuchFileException, IOException {
        return generateCompileSpec(new PosixParser().parse(OPTIONS, args));
    }

    /**
     * Generates a {@link com.hjwylde.quxc.util.QuxcProperties} from the given command line
     * arguments.
     *
     * @param args the arguments.
     * @return the generated properties.
     * @throws ParseException if the arguments cannot be parsed.
     * @throws IOException if a properties file is specified and cannot be loaded.
     */
    public static QuxcProperties generateProperties(String[] args)
            throws ParseException, IOException {
        return generateProperties(new PosixParser().parse(OPTIONS, args));
    }

    /**
     * Generates a {@link com.hjwylde.quxc.util.QuxcProperties} from the given command line object.
     *
     * @param cl the command line object.
     * @return the generated properties.
     * @throws IOException if a properties file is specified and cannot be loaded.
     */
    public static QuxcProperties generateProperties(CommandLine cl) throws IOException {
        QuxcProperties properties = QuxcProperties.loadDefaultProperties();
        if (cl.hasOption(OPT_PROPERTIES)) {
            Path path = Paths.get(cl.getOptionValue(OPT_PROPERTIES));

            logger.info("loading properties from {}", path);

            properties = QuxcProperties.loadProperties(path);
        } else {
            Path path = Paths.get(OPT_PROPERTIES_DEFAULT);

            if (Files.exists(path)) {
                logger.info("loading default properties from {}", path);

                properties = QuxcProperties.loadProperties(path);
            }
        }

        if (cl.hasOption(OPT_CHARSET)) {
            properties.setCharset(cl.getOptionValue(OPT_CHARSET));
        }
        if (cl.hasOption(OPT_CLASSPATH)) {
            properties.setClasspath(cl.getOptionValue(OPT_CLASSPATH));
        }
        if (cl.hasOption(OPT_OUTDIR)) {
            properties.setOutdir(cl.getOptionValue(OPT_OUTDIR));
        }
        if (cl.hasOption(OPT_TARGET)) {
            properties.setTarget(cl.getOptionValue(OPT_TARGET));
        }
        if (cl.hasOption(OPT_TIMEOUT)) {
            properties.setTimeout(cl.getOptionValue(OPT_TIMEOUT));
        }
        if (cl.hasOption(OPT_TIMEOUT_UNIT)) {
            properties.setTimeoutUnit(cl.getOptionValue(OPT_TIMEOUT_UNIT));
        }
        if (cl.hasOption(OPT_VERBOSE)) {
            properties.setVerbose("true");
        }

        return properties;
    }

    /**
     * Main.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        logger.debug("calling quxc with args: '{}'", Joiner.on(' ').join(args));

        try {
            CommandLine cl = new PosixParser().parse(OPTIONS, args);

            if (cl.hasOption(OPT_HELP)) {
                printHelp();
                System.exit(ExitCode.SUCCESS);
            } else if (cl.hasOption(OPT_VERSION)) {
                printVersion();
                System.exit(ExitCode.SUCCESS);
            } else if (cl.hasOption(OPT_VERSION_CODE)) {
                printVersionCode();
                System.exit(ExitCode.SUCCESS);
            } else if (cl.getArgs().length == 0) {
                throw new ParseException("no source files specified");
            }

            System.exit(new Quxc(generateCompileSpec(cl)).run());
        } catch (ParseException e) {
            String message = e.getMessage();
            if (message != null && !message.isEmpty()) {
                message = message.substring(0, 1).toLowerCase(Locale.ENGLISH) + message.substring(
                        1);
            } else {
                message = message == null ? null : message.toLowerCase(Locale.ENGLISH);
            }

            logger.error(message + "\n");
            printHelp();

            System.exit(ExitCode.FAIL);
        } catch (NoSuchFileException e) {
            logger.error("file not found: {}", e.getMessage());

            System.exit(ExitCode.FAIL);
        } catch (IllegalCharsetNameException e) {
            logger.error("illegal charset name: {}", e.getMessage());

            System.exit(ExitCode.FAIL);
        } catch (UnsupportedCharsetException e) {
            logger.error("unsupported charset: {}", e.getMessage());

            System.exit(ExitCode.FAIL);
        } catch (NumberFormatException e) {
            logger.error("illegal number: {}", e.getMessage());

            System.exit(ExitCode.FAIL);
        } catch (IllegalTimeUnitNameException e) {
            logger.error("illegal timeout unit name: {}", e.getMessage());

            System.exit(ExitCode.FAIL);
        } catch (IOException e) {
            // TODO: Verify what this outputs
            String thrown = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN,
                    e.getClass().getSimpleName());
            thrown = thrown.replace("-", " ");

            logger.error("{}: {}", thrown, e.getMessage());

            System.exit(ExitCode.FAIL);
        }
    }

    /**
     * Runs this Qux compilation unit. This command will create a {@link
     * com.hjwylde.qbs.compiler.Compiler} for a {@link com.hjwylde.quxc.compiler.QuxcCompileSpec}
     * using the factory. It will then attempt to execute the compiler using the {@link
     * com.hjwylde.quxc.compiler.QuxcCompileSpec} provided to this object.
     * <p/>
     * See {@link com.hjwylde.common.util.ExitCode} for return value details.
     *
     * @return the result of running this Qux compilation unit, {@code 0} indicates success.
     */
    public int run() {
        Compiler<? super QuxcCompileSpec> compiler = factory.buildCompiler(spec.getOptions());

        try {
            compiler.execute(spec);
        } catch (BuildError | CompilerError e) {
            LoggerUtils.logError(e);

            return ExitCode.FAIL;
        } catch (Throwable t) {
            LoggerUtils.logError(t);

            return ExitCode.INTERNAL_ERROR;
        }

        return ExitCode.SUCCESS;
    }

    /**
     * Sets the compiler factory for use when running this Qux compilation unit.
     *
     * @param factory the new compiler factory.
     */
    public void setCompilerFactory(
            CompilerFactory<? super QuxcCompileSpec, ? super QuxcCompileOptions> factory) {
        this.factory = checkNotNull(factory, "factory cannot be null");
    }

    /**
     * Gets the command line options for parsing the input.
     *
     * @return the command line options.
     */
    private static Options generateCommandLineOptions() {
        Options options = new Options();

        // Specification options
        Option outdir = OptionBuilder.withLongOpt(OPT_OUTDIR).hasArg().withArgName("dir")
                .withDescription(
                        "Specifies where to write the compiled source files to, defaults to '.'")
                .create("od");
        Option classpath = OptionBuilder.withLongOpt(OPT_CLASSPATH).hasArg().withArgName("file:...")
                .withDescription(
                        "Specifies the classpath for compilation as a ':' separated list of files, defaults to '.'")
                .create("cp");

        // Compile options
        Option target = OptionBuilder.withLongOpt(OPT_TARGET).hasArg().withArgName("name")
                .withDescription(
                        "Sets the target for the build process, may set to one of {'jvm'}, defaults to 'jvm'")
                .create("t");
        Option timeout = OptionBuilder.withLongOpt(OPT_TIMEOUT).hasArg().withArgName("long")
                .withDescription(
                        "Sets the timeout for the build process, may set to '0' to disable, defaults to '10'")
                .create("to");
        Option timeoutUnit = OptionBuilder.withLongOpt(OPT_TIMEOUT_UNIT).hasArg().withArgName(
                "name").withDescription("Sets the timeout unit, defaults to 'seconds'").create(
                "tu");
        Option verbose = new Option("v", OPT_VERBOSE, false, "Sets the compiler to be extra noisy");
        Option charset = OptionBuilder.withLongOpt(OPT_CHARSET).hasArg().withArgName("name")
                .withDescription(
                        "Sets the character set to use to read the source files, defaults to 'utf8'")
                .create("cs");

        // Properties
        Option properties = OptionBuilder.withLongOpt(OPT_PROPERTIES).hasArg().withArgName("file")
                .withDescription(
                        "Specifies where to laod the quxc properties file from, defaults to 'quxc.properties'")
                .create("qp");

        Option help = new Option("h", OPT_HELP, false, "Prints this message");
        Option version = new Option(null, OPT_VERSION, false,
                "Prints the version of this compiler");
        Option versionCode = new Option(null, OPT_VERSION_CODE, false,
                "Prints the version code of this compiler");

        options.addOption(outdir);
        options.addOption(classpath);

        options.addOption(target);
        options.addOption(timeout);
        options.addOption(timeoutUnit);
        options.addOption(verbose);
        options.addOption(charset);

        options.addOption(properties);

        options.addOption(help);
        options.addOption(version);
        options.addOption(versionCode);

        return options;
    }

    /**
     * Prints the help for how to call {@code quxc}.
     */
    private static void printHelp() {
        new HelpFormatter().printHelp(120, "quxc [options] file...", null, OPTIONS, null);
    }

    /**
     * Prints the version of the compiler.
     */
    private static void printVersion() {
        logger.warn(Constants.VERSION_NAME);
    }

    /**
     * Prints the version code of the compiler.
     */
    private static void printVersionCode() {
        logger.warn(String.valueOf(Constants.VERSION_CODE));
    }
}
