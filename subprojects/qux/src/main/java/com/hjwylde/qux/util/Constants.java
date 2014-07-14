package com.hjwylde.qux.util;

import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for holding constants.
 *
 * @author Henry J. Wylde
 */
public final class Constants {

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

    public static final int QUX0_1_0 = 0 << 16 | 1 << 8 | 0;
    public static final int QUX0_1_1 = 0 << 16 | 1 << 8 | 1;
    public static final int QUX0_1_2 = 0 << 16 | 1 << 8 | 2;
    public static final int QUX0_1_3 = 0 << 16 | 1 << 8 | 3;
    public static final int QUX0_2_0 = 0 << 16 | 2 << 8 | 0;
    public static final int QUX0_2_1 = 0 << 16 | 2 << 8 | 1;
    public static final int QUX0_2_2 = 0 << 16 | 2 << 8 | 2;
    public static final int QUX0_2_3 = 0 << 16 | 2 << 8 | 3;
    public static final int QUX0_2_4 = 0 << 16 | 2 << 8 | 4;

    /**
     * A list of supported Qux versions for this API.
     */
    public static final ImmutableList<Integer> SUPPORTED_VERSIONS = ImmutableList.of(QUX0_2_4);

    private static final Logger logger = LoggerFactory.getLogger(Constants.class);

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
    private Constants() {}

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
            InputStream in = Constants.class.getResourceAsStream(PROP_PATH);

            properties.load(in);
        } catch (IOException e) {
            logger.warn(
                    "unable to load properties from '" + PROP_PATH + "', using default properties",
                    e);
        }

        return properties;
    }
}
