package com.hjwylde.quxc.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

/**
 * A properties helper to hold some common settings for a Qux compiler. The main utility of this
 * class is the ability to let the user specify compile properties via a standard properties file.
 * The properties may then be loaded in and used instantly.
 * <p/>
 * The default properties are set as follows: <dl> <dt> charset </dt> <dd> utf-8 </dd> <dt>
 * classpath </dt> <dd> <i>&lt;empty&gt;</i> </dd> <dt>outdir</dt> <dd>.</dd> <dt>verbose</dt>
 * <dd>false</dd> </dl>
 *
 * @author Henry J. Wylde
 */
public final class QuxcProperties {

    private static final Logger logger = LoggerFactory.getLogger(QuxcProperties.class);

    private static final String PROP_CHARSET = "charset";
    private static final String PROP_CLASSPATH = "classpath";
    private static final String PROP_OUTDIR = "outdir";
    private static final String PROP_VERBOSE = "verbose";

    private static final ImmutableList<String> PROP_KEYS = ImmutableList.of(PROP_CHARSET,
            PROP_CLASSPATH, PROP_OUTDIR, PROP_VERBOSE);

    private final Properties properties;

    /**
     * This class can only be instantiated locally.
     */
    private QuxcProperties(Properties properties) {
        this.properties = checkNotNull(properties, "properties cannot be null");

        // Check that all the properties are contained and non-null
        for (String property : PROP_KEYS) {
            checkArgument(properties.containsKey(property), "properties must contain '%s'",
                    property);
            checkNotNull(properties.getProperty(property),
                    "properties cannot contain null for key '%s'", property);
        }

        // Check for any extraneous properties and warn the user
        for (Object property : properties.keySet()) {
            if (!PROP_KEYS.contains(property)) {
                logger.warn("unrecognised property: {}", property);
            }
        }
    }

    /**
     * Gets the character set name property.
     *
     * @return the character set name.
     */
    public String getCharset() {
        return properties.getProperty(PROP_CHARSET);
    }

    /**
     * Sets the character set name property. The value should be a valid character set name.
     *
     * @param charset the character set.
     */
    public void setCharset(String charset) {
        properties.setProperty(PROP_CHARSET, checkNotNull(charset, "charset cannot be null"));
    }

    /**
     * Gets the classpath property.
     *
     * @return the classpath.
     */
    public String getClasspath() {
        return properties.getProperty(PROP_CLASSPATH);
    }

    /**
     * Sets the classpath property. The value should be a list of valid paths, separated by the
     * system path separator character.
     *
     * @param classpath the classpath.
     */
    public void setClasspath(String classpath) {
        properties.setProperty(PROP_CLASSPATH, checkNotNull(classpath, "classpath cannot be null"));
    }

    /**
     * Gets the output directory property.
     *
     * @return the output directory.
     */
    public String getOutdir() {
        return properties.getProperty(PROP_OUTDIR);
    }

    /**
     * Sets the output directory property. The value should be a valid single path.
     *
     * @param outdir the output directory.
     */
    public void setOutdir(String outdir) {
        properties.setProperty(PROP_OUTDIR, checkNotNull(outdir, "outdir cannot be null"));
    }

    /**
     * Gets the verbose property.
     *
     * @return the verbose property.
     */
    public String getVerbose() {
        return properties.getProperty(PROP_VERBOSE);
    }

    /**
     * Sets the verbose property. The value should a {@code boolean}.
     *
     * @param verbose the verbose property.
     */
    public void setVerbose(String verbose) {
        properties.setProperty(PROP_VERBOSE, checkNotNull(verbose, "verbose cannot be null"));
    }

    /**
     * Loads a new copy of the default properties.
     *
     * @return the default properties.
     */
    public static QuxcProperties loadDefaultProperties() {
        return new QuxcProperties(generateDefaultProperties());
    }

    /**
     * Loads the properties from the given path, backing it with the default properties as loaded
     * from {@link #loadDefaultProperties()}.
     *
     * @param path the path to load the properties from.
     * @return the properties, backed by the defaults.
     * @throws IOException if the file could not be read.
     */
    public static QuxcProperties loadProperties(Path path) throws IOException {
        Properties properties = new Properties(generateDefaultProperties());

        properties.load(Files.newInputStream(path, StandardOpenOption.READ));

        return new QuxcProperties(properties);
    }

    /**
     * Loads the properties from the given path, backing it with the default properties as loaded
     * from {@link #loadDefaultProperties()}.
     *
     * @param path the path to load the properties from.
     * @return the properties, backed by the defaults.
     * @throws IOException if the file could not be read.
     */
    public static QuxcProperties loadProperties(String path) throws IOException {
        return loadProperties(Paths.get(path));
    }

    /**
     * Generates the default properties.
     *
     * @return the default properties.
     */
    private static Properties generateDefaultProperties() {
        Properties properties = new Properties();

        properties.put(PROP_CLASSPATH, "");
        properties.put(PROP_CHARSET, StandardCharsets.UTF_8.name());
        properties.put(PROP_OUTDIR, ".");
        properties.put(PROP_VERBOSE, "false");

        return properties;
    }
}
