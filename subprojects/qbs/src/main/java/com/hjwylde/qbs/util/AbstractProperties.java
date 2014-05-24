package com.hjwylde.qbs.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Set;

/**
 * A properties helper to hold some common settings for a compiler. The main utility of this class
 * is the ability to let the user specify compiler properties via a standard properties file. The
 * properties may then be loaded in and used instantly.
 *
 * @author Henry J. Wylde
 * @since 0.2.1
 */
public abstract class AbstractProperties {

    private static final Logger logger = LoggerFactory.getLogger(AbstractProperties.class);

    private final Properties properties;

    /**
     * Creates a new {@code AbstractProperties} using the given properties to back it. If the given
     * properties does not contain the required keys then an error is thrown.
     *
     * @param properties the properties to use as the backing.
     */
    protected AbstractProperties(Properties properties) {
        this.properties = (Properties) properties.clone();

        // Check for any extraneous properties and warn the user
        Set<String> keys = getPropertyKeys();
        for (Object property : properties.keySet()) {
            if (!keys.contains(property)) {
                logger.warn("unrecognised property: {}", property);
            }
        }
    }

    /**
     * Generates the default properties.
     *
     * @return the default properties.
     */
    protected static Properties generateDefaultProperties() {
        return new Properties();
    }

    protected final String getProperty(String key) {
        checkArgument(getPropertyKeys().contains(key), "unrecognised property: {}", key);

        return properties.getProperty(key);
    }

    protected abstract Set<String> getPropertyKeys();

    protected final void setProperty(String key, String value) {
        checkArgument(getPropertyKeys().contains(key), "unrecognised property: {}", key);

        properties.setProperty(checkNotNull(key, "key cannot be null"), checkNotNull(value,
                "value cannot be null"));
    }
}
