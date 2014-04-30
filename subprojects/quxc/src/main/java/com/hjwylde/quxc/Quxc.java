package com.hjwylde.quxc;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.common.error.CompilerError;
import com.hjwylde.common.util.ExitCode;
import com.hjwylde.common.util.LoggerUtils;
import com.hjwylde.qbs.compiler.Compiler;
import com.hjwylde.qbs.compiler.CompilerFactory;
import com.hjwylde.qux.util.QuxProperties;
import com.hjwylde.quxc.compiler.QuxCompileOptions;
import com.hjwylde.quxc.compiler.QuxCompileSpec;
import com.hjwylde.quxc.compiler.QuxCompilerFactory;
import com.hjwylde.quxc.util.QuxcProperties;

import com.google.common.base.Joiner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class for the command line call of "quxc". Used for compiling qux source files into quux binary
 * files.
 *
 * @author Henry J. Wylde
 */
public final class Quxc {

    private static final Logger logger = LoggerFactory.getLogger(Quxc.class);

    private static final String OPT_CHARSET = "charset";
    private static final String OPT_CLASSPATH = "classpath";
    private static final String OPT_HELP = "help";
    private static final String OPT_OUTDIR = "outdir";
    private static final String OPT_PROPERTIES = "properties";
    private static final String OPT_VERBOSE = "verbose";
    private static final String OPT_VERSION = "version";

    private static final String OPT_PROPERTIES_DEFAULT = "quxc.properties";

    private static final Options OPTIONS = generateCommandLineOptions();

    private final QuxCompileSpec spec;
    private CompilerFactory<QuxCompileSpec, QuxCompileOptions> factory = new QuxCompilerFactory();

    /**
     * Creates a new Qux unit with the given compile specification.
     *
     * @param spec the compile specification.
     */
    public Quxc(QuxCompileSpec spec) {
        this.spec = checkNotNull(spec, "spec cannot be null");
    }

    /**
     * Generates a Qux compile specification from the given command line object.
     *
     * @param cl the command line object.
     * @return the generated Qux compile specification.
     * @throws NoSuchFileException if a source file argument cannot be found.
     * @throws IOException if a properties file is specified and cannot be loaded.
     * @throws IllegalCharsetNameException if the character set name is illegal.
     * @throws UnsupportedCharsetException if the character set is not supported.
     */
    public static QuxCompileSpec generateCompileSpec(CommandLine cl)
            throws NoSuchFileException, IOException, IllegalCharsetNameException,
            UnsupportedCharsetException {
        return generateCompileSpec(generateProperties(cl), cl.getArgs());
    }

    /**
     * Generates a Qux compile specification from the given {@link com.hjwylde.quxc.util.QuxcProperties}
     * and source paths.
     *
     * @param properties the properties.
     * @param source the source files.
     * @return the generated Qux compile specification.
     * @throws NoSuchFileException if a source file argument cannot be found.
     * @throws IllegalCharsetNameException if the character set name is illegal.
     * @throws UnsupportedCharsetException if the character set is not supported.
     */
    public static QuxCompileSpec generateCompileSpec(QuxcProperties properties, String... source)
            throws NoSuchFileException, IllegalCharsetNameException, UnsupportedCharsetException {
        Path[] paths = new Path[source.length];
        for (int i = 0; i < source.length; i++) {
            paths[i] = Paths.get(source[i]).toAbsolutePath().normalize();
        }

        return generateCompileSpec(properties, paths);
    }

    /**
     * Generates a Qux compile specification from the given {@link com.hjwylde.quxc.util.QuxcProperties}
     * and source paths.
     *
     * @param properties the properties.
     * @param source the source files.
     * @return the generated Qux compile specification.
     * @throws NoSuchFileException if a source file argument cannot be found.
     * @throws IllegalCharsetNameException if the character set name is illegal.
     * @throws UnsupportedCharsetException if the character set is not supported.
     */
    public static QuxCompileSpec generateCompileSpec(QuxcProperties properties, Path... source)
            throws NoSuchFileException, IllegalCharsetNameException, UnsupportedCharsetException {
        QuxCompileSpec spec = new QuxCompileSpec();

        spec.setOutdir(Paths.get(properties.getOutdir()));
        for (String path : properties.getClasspath().split(File.pathSeparator)) {
            spec.classpath(Paths.get(path));
        }

        QuxCompileOptions.Builder optionsBuilder = QuxCompileOptions.builder();
        optionsBuilder.setVerbose(Boolean.valueOf(properties.getVerbose()));
        optionsBuilder.setCharset(Charset.forName(properties.getCharset()));

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
     * Generates a Qux compile specification from the given command line arguments.
     *
     * @param args the arguments.
     * @return the generated Qux compile specification.
     * @throws ParseException if the arguments cannot be parsed.
     * @throws NoSuchFileException if a source file argument cannot be found.
     * @throws IOException if a properties file is specified and cannot be loaded.
     */
    public static QuxCompileSpec generateCompileSpec(String[] args)
            throws ParseException, NoSuchFileException, IOException {
        return generateCompileSpec(new PosixParser().parse(OPTIONS, args));
    }

    /**
     * Generates a Qux properties from the given command line arguments.
     *
     * @param args the arguments.
     * @return the generated Qux properties.
     * @throws ParseException if the arguments cannot be parsed.
     * @throws IOException if a properties file is specified and cannot be loaded.
     */
    public static QuxcProperties generateProperties(String[] args)
            throws ParseException, IOException {
        return generateProperties(new PosixParser().parse(OPTIONS, args));
    }

    /**
     * Generates a Qux properties from the given command line object.
     *
     * @param cl the command line object.
     * @return the generated Qux properties.
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
            } else if (cl.getArgs().length == 0) {
                throw new ParseException("no source files specified");
            }

            System.exit(new Quxc(generateCompileSpec(cl)).run());
        } catch (ParseException e) {
            logger.error(e.getMessage() + "\n");
            printHelp();

            System.exit(ExitCode.FAIL);
        } catch (NoSuchFileException e) {
            logger.error("file not found: " + e.getMessage());

            System.exit(ExitCode.FAIL);
        } catch (IllegalCharsetNameException e) {
            logger.error("illegal charset name: " + e.getMessage());

            System.exit(ExitCode.FAIL);
        } catch (UnsupportedCharsetException e) {
            logger.error("unsupported charset: " + e.getMessage());

            System.exit(ExitCode.FAIL);
        } catch (IOException e) {
            logger.error("{}: {}", e.getClass(), e.getMessage());

            System.exit(ExitCode.FAIL);
        }
    }

    /**
     * Runs this Qux unit. This command will create a {@code Compiler} for a {@code QuxCompileSpec}
     * using the factory. It will then attempt to execute the compiler using the Qux compile
     * specification provided to this object.
     * <p/>
     * See {@link com.hjwylde.common.util.ExitCode} for return value details.
     *
     * @return the result of running this Qux unit, {@code 0} indicates success.
     */
    public int run() {
        Compiler<QuxCompileSpec> compiler = factory.buildCompiler(spec.getOptions());

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
     * Sets the compiler factory for use when running this Qux unit.
     *
     * @param factory the new compiler factory.
     */
    public void setCompilerFactory(CompilerFactory<QuxCompileSpec, QuxCompileOptions> factory) {
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
        Option outdir = new Option("od", OPT_OUTDIR, true,
                "Specifies where to write the compiled source files to, defaults to '.'");
        Option classpath = new Option("cp", OPT_CLASSPATH, true,
                "Specifies the classpath for compilation; this is a path separator separated list");

        // Compile options
        Option verbose = new Option("v", OPT_VERBOSE, false, "Sets the compiler to be extra noisy");
        Option charset = new Option("cs", OPT_CHARSET, true,
                "Sets the character set to use to read the source files, defaults to 'utf8'");

        // Properties
        Option properties = new Option("qp", OPT_PROPERTIES, true,
                "Specifies where to load the quxc properties file from, defaults to 'quxc.properties'");

        Option help = new Option("h", OPT_HELP, false, "Prints this message");
        Option version = new Option("V", OPT_VERSION, false, "Prints the version of this compiler");

        options.addOption(outdir);
        options.addOption(classpath);

        options.addOption(verbose);
        options.addOption(charset);

        options.addOption(properties);

        options.addOption(help);
        options.addOption(version);

        return options;
    }

    /**
     * Prints the help for how to call <code>Quxc</code>.
     */
    private static void printHelp() {
        new HelpFormatter().printHelp("quxc [options] <source>", OPTIONS);
    }

    /**
     * Prints the version of the compiler language and intermediate language.
     */
    private static void printVersion() {
        logger.info(QuxProperties.VERSION_NAME);
    }
}
