package com.hjwylde.qbs.builder.resources;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A directory resource is an inclusion of all resources that may be located from a root directory.
 * The resources in the iterator of this directory are lazily initialised. That is, they will only
 * be read from the file system when needed.
 * <p/>
 * TODO: Consider adding in a FilteredDirectoryResource to simplify the process of includes /
 * excludes, especially with file types.
 *
 * @author Henry J. Wylde
 */
public class DirectoryResource extends AbstractResourceCollection {

    /**
     * The extension of a directory resource. Is the empty string {@code ""}.
     */
    public static final Resource.Extension EXTENSION = new Resource.Extension("");

    private static final Logger logger = LoggerFactory.getLogger(DirectoryResource.class);

    private final Path root;

    /**
     * Creates a new directory resource with the given root directory.
     *
     * @param root the root directory.
     */
    public DirectoryResource(Path root) {
        this.root = checkNotNull(root, "root cannot be null").toAbsolutePath().normalize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        return root.equals(((DirectoryResource) obj).root);
    }

    /**
     * Gets the root directory.
     *
     * @return the root directory.
     */
    public Path getRoot() {
        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return root.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Resource> iterator() {
        // TODO: Consider caching this list of resources
        final List<Resource> resources = new ArrayList<>();

        SimpleFileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(final Path file, BasicFileAttributes attrs) {
                // TODO: Verify what id returns here, is it just the name or the whole path without
                // the extension
                String id = Files.getNameWithoutExtension(root.relativize(file).toString());
                String extension = Files.getFileExtension(file.toString());

                if (ResourceManager.getSupportedExtensions().contains(new Resource.Extension(
                        extension))) {

                    resources.add(new LazilyInitialisedResource(id) {

                        @Override
                        protected Resource.Single loadDelegate() {
                            return ResourceManager.getResourceSingle(file);
                        }
                    });
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                logger.warn("i/o exception when trying to read file: " + file, exc);

                return FileVisitResult.CONTINUE;
            }
        };

        try {
            java.nio.file.Files.walkFileTree(root, visitor);
        } catch (IOException e) {
            logger.warn("i/o exception when trying to read directory: " + root, e);
        }

        return resources.iterator();
    }
}
