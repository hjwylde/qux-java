package com.hjwylde.qux.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to hold some constants / properties about the Qux language.
 *
 * @author Henry J. Wylde
 */
public final class QuxProperties {

    /**
     * The version code of the Qux language. This is the version name encoded into a 32 bit integer.
     * The {@code major} component takes up the most significant {@code 16} bits, while the {@code
     * minor} and {@code revision} take up 8 bits each.
     */
    public static final int VERSION_CODE;

    /**
     * The version name of the Qux language. It is represented in the format {@code
     * major.minor.revision}.
     */
    public static final String VERSION_NAME;

    private static final Logger logger = LoggerFactory.getLogger(QuxProperties.class);

    private static final String PROP_PATH =
            File.separator + "properties" + File.separator + "qux.properties";

    private static final String PROP_VERSION_CODE = "qux.versionCode";
    private static final String PROP_VERSION_NAME = "qux.versionName";

    static {
        // Initialise the properties
        Properties properties = loadProperties();

        VERSION_CODE = Integer.valueOf(properties.getProperty(PROP_VERSION_CODE));
        VERSION_NAME = properties.getProperty(PROP_VERSION_NAME);
    }

    /**
     * This class cannot be instantiated.
     */
    private QuxProperties() {}

    /**
     * Generates the default properties.
     *
     * @return the default properties.
     */
    private static Properties generateDefaultProperties() {
        Properties properties = new Properties();

        properties.put(PROP_VERSION_CODE, "0");
        properties.put(PROP_VERSION_NAME, "unknown");

        return properties;
    }

    /**
     * Attempts to load the properties file. If the properties cannot be read, then an error is
     * outputted and the default properties are returned.
     *
     * @return the properties.
     */
    private static Properties loadProperties() {
        Properties properties = new Properties(generateDefaultProperties());

        try {
            InputStream in = QuxProperties.class.getResourceAsStream(PROP_PATH);

            properties.load(in);
        } catch (IOException e) {
            logger.warn(
                    "unable to load properties from '" + PROP_PATH + "', using default properties",
                    e);
        }

        return properties;
    }
}
