package com.hjwylde.qbs.builder.resources;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class ResourceManager {

    private static final Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    private static final Map<Resource.Extension, Resource.Reader<?>> readers =
            new ConcurrentHashMap<>();

    static {
        register(DirectoryResource.EXTENSION, LocalDirectoryResourceReader.getInstance());
        register(ZipResource.EXTENSION, LocalZipResourceReader.getInstance());
        register(JarResource.EXTENSION, LocalZipResourceReader.getInstance());
    }

    /**
     * This class cannot be instantiated.
     */
    private ResourceManager() {}

    public static Resource getResource(Path path) {
        return loadResource(path).get();
    }

    public static Resource getResource(InputStream in, Resource.Extension extension) {
        return loadResource(in, extension).get();
    }

    public static Resource.Single getResourceSingle(Path path) {
        return loadResourceSingle(path).get();
    }

    public static Resource.Single getResourceSingle(InputStream in, Resource.Extension extension) {
        return loadResourceSingle(in, extension).get();
    }

    public static Set<Resource.Extension> getSupportedExtensions() {
        return readers.keySet();
    }

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

    public static Optional<Resource> loadResource(InputStream in, Resource.Extension extension) {
        try {
            return Optional.of(getReader(extension).read(in));
        } catch (IOException e) {
            logger.warn("i/o exception when attempting to load resource", e);
            return Optional.absent();
        }
    }

    public static Optional<Resource.Single> loadResourceSingle(Path path) {
        Optional<Resource> opt = loadResource(path);

        if (!opt.isPresent() || !(opt.get() instanceof Resource.Single)) {
            return Optional.absent();
        }

        return Optional.of((Resource.Single) opt.get());
    }

    public static Optional<Resource.Single> loadResourceSingle(InputStream in,
            Resource.Extension extension) {
        Optional<Resource> opt = loadResource(in, extension);

        if (!opt.isPresent() || !(opt.get() instanceof Resource.Single)) {
            return Optional.absent();
        }

        return Optional.of((Resource.Single) opt.get());
    }

    public static void register(Resource.Extension extension, Resource.Reader<?> reader) {
        checkNotNull(extension, "extension cannot be null");
        checkNotNull(reader, "reader cannot be null");

        readers.put(extension, reader);
    }

    private static Resource.Reader<?> getReader(Resource.Extension extension) {
        checkArgument(readers.containsKey(extension), "no resource reader mapped to extension '%s'",
                extension);

        return readers.get(extension);
    }
}
