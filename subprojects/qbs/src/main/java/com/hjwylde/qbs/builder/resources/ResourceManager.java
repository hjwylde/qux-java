package com.hjwylde.qbs.builder.resources;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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

    public static Resource getResource(String name, InputStream in) {
        return loadResource(name, in).get();
    }

    public static Resource.Single getResourceSingle(Path path) {
        return loadResourceSingle(path).get();
    }

    public static Resource.Single getResourceSingle(String name, InputStream in) {
        return loadResourceSingle(name, in).get();
    }

    public static Set<Resource.Extension> getSupportedExtensions() {
        return readers.keySet();
    }

    public static Optional<Resource> loadResource(Path path) {
        checkNotNull(path, "path cannot be null");

        String name = path.getFileName().toString();

        try {
            Resource.Extension extension = null;

            if (Files.isDirectory(path) || !name.contains(".")) {
                extension = DirectoryResource.EXTENSION;
            } else {
                extension = new Resource.Extension(com.google.common.io.Files.getFileExtension(
                        name));
            }

            return Optional.<Resource>of(getReader(extension).read(path));
        } catch (IOException e) {
            logger.warn("i/o exception when attempting to load resource: " + path, e);
            return Optional.absent();
        }
    }

    public static Optional<Resource> loadResource(String file, InputStream in) {
        checkNotNull(file, "file cannot be null");

        String fileExtension = com.google.common.io.Files.getFileExtension(file);

        try {
            Resource.Extension extension = null;

            if (fileExtension.isEmpty() || file.endsWith(File.separator)) {
                extension = DirectoryResource.EXTENSION;
            } else {
                extension = new Resource.Extension(fileExtension);
            }

            return Optional.<Resource>of(getReader(extension).read(in));
        } catch (IOException e) {
            logger.warn("i/o exception when attempting to load resource: " + file, e);
            return Optional.absent();
        }
    }

    public static Optional<Resource.Single> loadResourceSingle(Path path) {
        checkNotNull(path, "path cannot be null");

        String name = path.getFileName().toString();

        try {
            Resource.Extension extension = null;

            if (Files.isDirectory(path) || !name.contains(".")) {
                extension = DirectoryResource.EXTENSION;
            } else {
                extension = new Resource.Extension(com.google.common.io.Files.getFileExtension(
                        name));
            }

            Resource resource = getReader(extension).read(path);

            if (resource instanceof Resource.Single) {
                return Optional.of((Resource.Single) resource);
            }

            return Optional.absent();
        } catch (IOException e) {
            logger.warn("i/o exception when attempting to load resource: " + path, e);
            return Optional.absent();
        }
    }

    public static Optional<Resource.Single> loadResourceSingle(String file, InputStream in) {
        checkNotNull(file, "file cannot be null");

        String fileExtension = com.google.common.io.Files.getFileExtension(file);

        try {
            Resource.Extension extension = null;

            if (fileExtension.isEmpty() || file.endsWith(File.separator)) {
                extension = DirectoryResource.EXTENSION;
            } else {
                extension = new Resource.Extension(fileExtension);
            }

            Resource resource = getReader(extension).read(in);

            if (resource instanceof Resource.Single) {
                return Optional.of((Resource.Single) resource);
            }

            return Optional.absent();
        } catch (IOException e) {
            logger.warn("i/o exception when attempting to load resource: " + file, e);
            return Optional.absent();
        }
    }

    public static void register(Resource.Extension extension, Resource.Reader<?> reader) {
        readers.put(checkNotNull(extension, "extension cannot be null"), checkNotNull(reader,
                "reader cannot be null"));
    }

    private static Resource.Reader<?> getReader(Resource.Extension extension) {
        checkArgument(readers.containsKey(extension),
                "no resource reader mapped to extension: " + extension);

        return readers.get(extension);
    }
}
