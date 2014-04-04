package com.hjwylde.qbs.builder.resources;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public class ZipResource extends AbstractResourceCollection {

    public static final Resource.Extension EXTENSION = new Resource.Extension("zip");

    private static final Logger logger = LoggerFactory.getLogger(ZipResource.class);

    private final ZipFile file;

    public ZipResource(ZipFile file) {
        this.file = checkNotNull(file, "file cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        return file.equals(((ZipResource) obj).file);
    }

    public ZipFile getFile() {
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return file.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Resource> iterator() {
        // TODO: Consider caching this list of resources
        final List<Resource> resources = new ArrayList<>();

        Enumeration<? extends ZipEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();

            // TODO: Verify what id returns here, is it just the name or the whole path without the
            // extension
            final String id = Files.getNameWithoutExtension(entry.getName());
            String extension = Files.getFileExtension(entry.getName());
            if (!ResourceManager.getSupportedExtensions().contains(extension)) {
                continue;
            }

            resources.add(new LazilyInitialisedResource(id) {

                @Override
                protected Resource.Single loadDelegate() {
                    try {
                        return ResourceManager.getResourceSingle(Files.getNameWithoutExtension(
                                entry.getName()), file.getInputStream(entry));
                    } catch (IOException e) {
                        logger.error("i/o exception on loading resource: " + id, e);
                        throw new InternalError(e.getMessage());
                    }
                }
            });
        }

        return resources.iterator();
    }
}
