package com.hjwylde.qbs.builder.resources;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.filter;

import com.hjwylde.common.error.BuildError;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

/**
 * A management class for resources. This class handles the registration of resource readers to
 * extensions. It also provides utility methods for loading and getting a resource via certain input
 * methods. The utility methods hide away the code that discovers what reader is required to load
 * the resource for the given input method.
 *
 * @author Henry J. Wylde
 */
public final class ResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    private static final String PROP_PREFIX = "qux-resources/";

    private static final String PROP_EXTENSION_CLASS = "extension-class";
    private static final String PROP_READER_CLASS = "reader-class";

    private static final String EXTENSION_FIELD_NAME = "EXTENSION";
    private static final String READER_METHOD_NAME = "getInstance";

    /**
     * A mapping of extensions to registered readers.
     */
    private static final Map<Resource.Extension, Resource.Reader<?>> readers =
            new ConcurrentHashMap<>();

    static {
        // Registers extensions with their default reader
        // Searches the resources in the classpath for the appropriate properties that define
        // registrations
        try {
            for (Map.Entry<String, Properties> entry : getSpiProperties().entrySet()) {
                String name = entry.getKey();
                Properties properties = entry.getValue();

                // Grab the class names
                String extensionClass = properties.getProperty(PROP_EXTENSION_CLASS);
                String readerClass = properties.getProperty(PROP_READER_CLASS);

                // Check that they are not empty
                checkNotNull(extensionClass, "%s: property '%s' cannot be empty", name,
                        PROP_EXTENSION_CLASS);
                checkNotNull(readerClass, "%s: property '%s' cannot be empty", name,
                        PROP_READER_CLASS);

                // Grab the appropriate field and method from the classes
                Field extensionField = Class.forName(extensionClass).getField(EXTENSION_FIELD_NAME);
                Method readerMethod = Class.forName(readerClass).getMethod(READER_METHOD_NAME);

                // Get the extension and reader
                Resource.Extension extension = (Resource.Extension) extensionField.get(null);
                Resource.Reader<?> reader = (Resource.Reader<?>) readerMethod.invoke(null);

                // Register the extension with the reader
                register(extension, reader);
            }
        } catch (Exception e) {
            throw new BuildError(e);
        }
    }

    /**
     * This class cannot be instantiated.
     */
    private ResourceManager() {}

    /**
     * Loads the resource at the given path and returns it. This method assumes that the resource
     * exists and is able to be read, otherwise an exception will be thrown.
     *
     * @param path the path to load the resource from.
     * @return the resource.
     */
    public static Resource getResource(Path path) {
        return loadResource(path).get();
    }

    /**
     * Loads the resource from the given input stream and returns it. This method assumes that the
     * input stream resource matches the provided extension and is able to be read, otherwise an
     * exception will be thrown.
     *
     * @param in the input stream to read the resource from.
     * @param extension the extension (type) of the resource to read.
     * @return the resource.
     */
    public static Resource getResource(InputStream in, Resource.Extension extension) {
        return loadResource(in, extension).get();
    }

    /**
     * Loads the single resource at the given path and returns it. This method assumes that the
     * resource exists and is able to be read, otherwise an exception will be thrown.
     *
     * @param path the path to load the resource from.
     * @return the resource.
     */
    public static Resource.Single getResourceSingle(Path path) {
        return loadResourceSingle(path).get();
    }

    /**
     * Loads the single resource from the given input stream and returns it. This method assumes
     * that the input stream resource matches the provided extension and is able to be read,
     * otherwise an exception will be thrown.
     *
     * @param in the input stream to read the resource from.
     * @param extension the extension (type) of the resource to read.
     * @return the resource.
     */
    public static Resource.Single getResourceSingle(InputStream in, Resource.Extension extension) {
        return loadResourceSingle(in, extension).get();
    }

    /**
     * Gets a list of supported resource extensions.
     *
     * @return the supported resource extensions.
     */
    public static Set<Resource.Extension> getSupportedExtensions() {
        return readers.keySet();
    }

    /**
     * Loads the resource from the given path and returns it. If an exception occurs, {@link
     * com.google.common.base.Optional#absent()} is returned.
     *
     * @param path the path to load the resource from.
     * @return the resource or {@link com.google.common.base.Optional#absent()}.
     */
    public static Optional<Resource> loadResource(Path path) {
        String name = path.getFileName().toString();

        try {
            Resource.Extension extension = null;

            if (Files.isDirectory(path) || !name.contains(".")) {
                extension = DirectoryResource.EXTENSION;
            } else {
                extension = new Resource.Extension(com.google.common.io.Files.getFileExtension(
                        name));
            }

            return Optional.of(getReader(extension).read(path));
        } catch (IOException e) {
            logger.warn("i/o exception when attempting to load resource: " + path, e);
            return Optional.absent();
        }
    }

    /**
     * Loads the resource from the given input stream and returns it. If an exception occurs, {@link
     * com.google.common.base.Optional#absent()} is returned.
     *
     * @param in the input stream to read the resource from.
     * @param extension the extension (type) of the resource to read.
     * @return the resource or {@link com.google.common.base.Optional#absent()}.
     */
    public static Optional<Resource> loadResource(InputStream in, Resource.Extension extension) {
        try {
            return Optional.of(getReader(extension).read(in));
        } catch (IOException e) {
            logger.warn("i/o exception when attempting to load resource", e);
            return Optional.absent();
        }
    }

    /**
     * Loads the single resource from the given path and returns it. If an exception occurs, {@link
     * com.google.common.base.Optional#absent()} is returned.
     *
     * @param path the path to load the resource from.
     * @return the resource or {@link com.google.common.base.Optional#absent()}.
     */
    public static Optional<Resource.Single> loadResourceSingle(Path path) {
        Optional<Resource> opt = loadResource(path);

        if (!opt.isPresent() || !(opt.get() instanceof Resource.Single)) {
            return Optional.absent();
        }

        return Optional.of((Resource.Single) opt.get());
    }

    /**
     * Loads the single resource from the given input stream and returns it. If an exception occurs,
     * {@link com.google.common.base.Optional#absent()} is returned.
     *
     * @param in the input stream to read the resource from.
     * @param extension the extension (type) of the resource to read.
     * @return the resource or {@link com.google.common.base.Optional#absent()}.
     */
    public static Optional<Resource.Single> loadResourceSingle(InputStream in,
            Resource.Extension extension) {
        Optional<Resource> opt = loadResource(in, extension);

        if (!opt.isPresent() || !(opt.get() instanceof Resource.Single)) {
            return Optional.absent();
        }

        return Optional.of((Resource.Single) opt.get());
    }

    /**
     * Registers the given reader with the given extension. Any further attempts to load a resource
     * of that extension type will utilise the reader.
     *
     * @param extension the extension.
     * @param reader the reader to register.
     */
    public static void register(Resource.Extension extension, Resource.Reader<?> reader) {
        checkNotNull(extension, "extension cannot be null");
        checkNotNull(reader, "reader cannot be null");

        readers.put(extension, reader);
    }

    /**
     * Gets the reader for the given extension. If no reader is registered to the extension then an
     * exception is thrown. To determine if an extension is supported, use {@link
     * #getSupportedExtensions()}.
     *
     * @param extension the extension to get the reader for.
     * @return the reader.
     */
    private static Resource.Reader<?> getReader(Resource.Extension extension) {
        checkArgument(readers.containsKey(extension), "no resource reader mapped to extension '%s'",
                extension);

        return readers.get(extension);
    }

    private static ImmutableMap<String, Properties> getSpiProperties() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Set<ClassPath.ResourceInfo> resources = ClassPath.from(classLoader).getResources();

        resources = filter(resources, new Predicate<ClassPath.ResourceInfo>() {
            @Override
            public boolean apply(@Nullable ClassPath.ResourceInfo input) {
                return input.getResourceName().startsWith(PROP_PREFIX);
            }
        });

        ImmutableMap.Builder<String, Properties> builder = ImmutableMap.builder();
        for (ClassPath.ResourceInfo resource : resources) {
            String name = resource.getResourceName();
            Properties properties = new Properties();
            properties.load(classLoader.getResourceAsStream(name));

            builder.put(name, properties);
        }

        return builder.build();
    }
}
