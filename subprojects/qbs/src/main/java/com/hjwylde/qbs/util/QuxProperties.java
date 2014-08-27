package com.hjwylde.qbs.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.Set;

/**
 * A properties helper to hold some common settings for a Qux compiler. The main utility of this
 * class is the ability to let the user specify compiler properties via a standard properties file.
 * The properties may then be loaded in and used instantly.
 * <p/>
 * The default properties are set as follows: <dl> <dt> charset </dt> <dd> utf-8 </dd> <dt>
 * classpath </dt> <dd> <i>&lt;empty&gt;</i> </dd> <dt>outdir</dt> <dd>.</dd> <dt>verbose</dt>
 * <dd>false</dd> </dl>
 *
 * @author Henry J. Wylde
 */
public class QuxProperties extends AbstractProperties {

    public static final String PROP_CHARSET = "charset";
    public static final String PROP_CLASSPATH = "classpath";
    public static final String PROP_OUTDIR = "outdir";
    public static final String PROP_TIMEOUT = "timeout";
    public static final String PROP_TIMEOUT_UNIT = "timeout-unit";
    public static final String PROP_VERBOSE = "verbose";

    private static final ImmutableSet<String> PROP_KEYS = ImmutableSet.of(PROP_CHARSET,
            PROP_CLASSPATH, PROP_OUTDIR, PROP_TIMEOUT, PROP_TIMEOUT_UNIT, PROP_VERBOSE);

    private static final Logger logger = LoggerFactory.getLogger(QuxProperties.class);

    /**
     * Creates a new {@code QuxProperties} using the given properties to back it. If the given
     * properties does not contain the required keys then an error is thrown.
     *
     * @param properties the properties to use as the backing.
     */
    protected QuxProperties(Properties properties) {
        super(properties);
    }

    /**
     * Gets the character set name property.
     *
     * @return the character set name.
     */
    public final String getCharset() {
        return getProperty(PROP_CHARSET);
    }

    /**
     * Gets the classpath property.
     *
     * @return the classpath.
     */
    public final String getClasspath() {
        return getProperty(PROP_CLASSPATH);
    }

    /**
     * Gets the output directory property.
     *
     * @return the output directory.
     */
    public final String getOutdir() {
        return getProperty(PROP_OUTDIR);
    }

    /**
     * Gets the timeout property.
     *
     * @return the timeout.
     */
    public final String getTimeout() {
        return getProperty(PROP_TIMEOUT);
    }

    /**
     * Gets the timeout unit property.
     *
     * @return the timeout unit.
     */
    public final String getTimeoutUnit() {
        return getProperty(PROP_TIMEOUT_UNIT);
    }

    /**
     * Gets the verbose property.
     *
     * @return the verbose property.
     */
    public final String getVerbose() {
        return getProperty(PROP_VERBOSE);
    }

    /**
     * Loads a new copy of the default properties.
     *
     * @return the default properties.
     */
    public static QuxProperties loadDefaultProperties() {
        return new QuxProperties(generateDefaultProperties());
    }

    /**
     * Loads the properties from the given path, backing it with the default properties as loaded
     * from {@link #loadDefaultProperties()}.
     *
     * @param path the path to load the properties from.
     * @return the properties, backed by the defaults.
     * @throws IOException if the file could not be read.
     */
    public static QuxProperties loadProperties(Path path) throws IOException {
        Properties properties = new Properties(generateDefaultProperties());

        properties.load(Files.newInputStream(path, StandardOpenOption.READ));

        return new QuxProperties(properties);
    }

    /**
     * Loads the properties from the given path, backing it with the default properties as loaded
     * from {@link #loadDefaultProperties()}.
     *
     * @param path the path to load the properties from.
     * @return the properties, backed by the defaults.
     * @throws IOException if the file could not be read.
     */
    public static QuxProperties loadProperties(String path) throws IOException {
        return loadProperties(Paths.get(path));
    }

    /**
     * Sets the character set name property. The value should be a valid character set name.
     *
     * @param charset the character set.
     */
    public final void setCharset(String charset) {
        setProperty(PROP_CHARSET, checkNotNull(charset, "charset cannot be null"));
    }

    /**
     * Sets the classpath property. The value should be a list of valid paths, separated by the
     * system path separator character.
     *
     * @param classpath the classpath.
     */
    public final void setClasspath(String classpath) {
        setProperty(PROP_CLASSPATH, checkNotNull(classpath, "classpath cannot be null"));
    }

    /**
     * Sets the output directory property. The value should be a valid single path.
     *
     * @param outdir the output directory.
     */
    public final void setOutdir(String outdir) {
        setProperty(PROP_OUTDIR, checkNotNull(outdir, "outdir cannot be null"));
    }

    /**
     * Sets the timeout property. The value should be a valid long value.
     *
     * @param timeout the timeout.
     */
    public final void setTimeout(String timeout) {
        setProperty(PROP_TIMEOUT, checkNotNull(timeout, "timeout cannot be null"));
    }

    /**
     * Sets the timeout unit property. The value should be a valid {@link
     * java.util.concurrent.TimeUnit}.
     *
     * @param timeoutUnit the timeout unit.
     */
    public final void setTimeoutUnit(String timeoutUnit) {
        setProperty(PROP_TIMEOUT_UNIT, checkNotNull(timeoutUnit, "timeoutUnit cannot be null"));
    }

    /**
     * Sets the verbose property. The value should a {@code boolean}.
     *
     * @param verbose the verbose property.
     */
    public final void setVerbose(String verbose) {
        setProperty(PROP_VERBOSE, checkNotNull(verbose, "verbose cannot be null"));
    }

    /**
     * Generates the default properties.
     *
     * @return the default properties.
     */
    protected static Properties generateDefaultProperties() {
        Properties properties = AbstractProperties.generateDefaultProperties();

        properties.put(PROP_CLASSPATH, "");
        properties.put(PROP_CHARSET, StandardCharsets.UTF_8.name());
        properties.put(PROP_OUTDIR, ".");
        properties.put(PROP_TIMEOUT, "20");
        properties.put(PROP_TIMEOUT_UNIT, "seconds");
        properties.put(PROP_VERBOSE, "false");

        return new Properties(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<String> getPropertyKeys() {
        return PROP_KEYS;
    }
}
