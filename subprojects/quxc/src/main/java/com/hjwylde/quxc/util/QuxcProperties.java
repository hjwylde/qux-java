package com.hjwylde.quxc.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class QuxcProperties {

    private static final Logger logger = LoggerFactory.getLogger(QuxcProperties.class);

    private static final String PROP_CHARSET = "charset";
    private static final String PROP_CLASSPATH = "classpath";
    private static final String PROP_OUTDIR = "outdir";
    private static final String PROP_VERBOSE = "verbose";

    private final Properties properties;

    /**
     * This class can only be instantiated locally.
     */
    private QuxcProperties(Properties properties) {
        this.properties = checkNotNull(properties, "properties cannot be null");

        for (String property : new String[] {PROP_CLASSPATH, PROP_CHARSET, PROP_OUTDIR,
                PROP_VERBOSE}) {
            checkArgument(properties.containsKey(property), "properties must contain '%s'",
                    property);
            checkNotNull(properties.getProperty(property),
                    "properties cannot contain null for key '%s'", property);
        }
    }

    public String getCharset() {
        return properties.getProperty(PROP_CHARSET);
    }

    public void setCharset(String charset) {
        properties.setProperty(PROP_CHARSET, checkNotNull(charset, "charset cannot be null"));
    }

    public String getClasspath() {
        return properties.getProperty(PROP_CLASSPATH);
    }

    public void setClasspath(String classpath) {
        properties.setProperty(PROP_CLASSPATH, checkNotNull(classpath, "classpath cannot be null"));
    }

    public String getOutdir() {
        return properties.getProperty(PROP_OUTDIR);
    }

    public void setOutdir(String outdir) {
        properties.setProperty(PROP_OUTDIR, checkNotNull(outdir, "outdir cannot be null"));
    }

    public String getVerbose() {
        return properties.getProperty(PROP_VERBOSE);
    }

    public void setVerbose(String verbose) {
        properties.setProperty(PROP_VERBOSE, checkNotNull(verbose, "verbose cannot be null"));
    }

    public static QuxcProperties loadDefaultProperties() {
        return new QuxcProperties(generateDefaultProperties());
    }

    public static QuxcProperties loadProperties(Path path) throws IOException {
        Properties properties = new Properties(generateDefaultProperties());

        properties.load(Files.newInputStream(path, StandardOpenOption.READ));

        return new QuxcProperties(properties);
    }

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
