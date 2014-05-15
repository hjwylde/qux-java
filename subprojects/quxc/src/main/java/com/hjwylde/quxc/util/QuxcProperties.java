package com.hjwylde.quxc.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.util.QuxProperties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;
import java.util.Set;

/**
 * A properties helper to hold some common settings for a Qux compiler. The main utility of this
 * class is the ability to let the user specify compile properties via a standard properties file.
 * The properties may then be loaded in and used instantly. These properties extend a {@link
 * com.hjwylde.qbs.util.QuxProperties}.
 * <p/>
 * The default properties for this class are set as follows: <dl> <dt> target </dt> <dd> jvm
 * </dd></dl>
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public final class QuxcProperties extends QuxProperties {

    public static final String PROP_TARGET = "target";

    private static final ImmutableSet<String> PROP_KEYS = ImmutableSet.of(PROP_TARGET);

    private static final Logger logger = LoggerFactory.getLogger(QuxcProperties.class);

    /**
     * This class can only be instantiated locally.
     * <p/>
     * Creates a new {@code QuxcProperties} using the given properties to back it. If the given
     * properties does not contain the required keys then an error is thrown.
     *
     * @param properties the properties to use as the backing.
     */
    private QuxcProperties(Properties properties) {
        super(properties);
    }

    /**
     * Gets the target property.
     *
     * @return the target.
     */
    public final String getTarget() {
        return getProperty(PROP_TARGET);
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
     * Sets the target property. The value should be a {@link com.hjwylde.quxc.util.Target}.
     *
     * @param target the target.
     */
    public final void setTarget(String target) {
        setProperty(PROP_TARGET, checkNotNull(target, "target cannot be null"));
    }

    /**
     * Generates the default properties.
     *
     * @return the default properties.
     */
    protected static Properties generateDefaultProperties() {
        Properties properties = QuxProperties.generateDefaultProperties();

        properties.put(PROP_TARGET, "jvm");

        return new Properties(properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<String> getPropertyKeys() {
        return Sets.union(super.getPropertyKeys(), PROP_KEYS);
    }
}
