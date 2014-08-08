package com.hjwylde.qbs.builder.resources;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Nullable;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public class ZipResource extends AbstractResourceCollection {

    public static final Resource.Extension EXTENSION = new Resource.Extension("zip");

    private static final Logger logger = LoggerFactory.getLogger(ZipResource.class);

    private final ZipFile file;

    private List<Resource> resources;

    public ZipResource(ZipFile file) {
        this.file = checkNotNull(file, "file cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
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
    public synchronized Iterator<Resource> iterator() {
        if (resources == null) {
            loadResources();
        }

        return resources.iterator();
    }

    private synchronized void loadResources() {
        ImmutableList.Builder<Resource> builder = ImmutableList.builder();

        Enumeration<? extends ZipEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            final ZipEntry entry = entries.nextElement();

            // TODO: Verify what id returns here, is it just the name or the whole path without the
            // extension
            final String id = Files.getNameWithoutExtension(entry.getName()).replace("/", ".");
            final String extension = Files.getFileExtension(entry.getName());
            if (!ResourceManager.getSupportedExtensions().contains(extension)) {
                continue;
            }

            builder.add(new LazilyInitialisedResource(id) {

                @Override
                protected Resource.Single loadDelegate() {
                    try {
                        return ResourceManager.getResourceSingle(file.getInputStream(entry),
                                new Resource.Extension(extension));
                    } catch (IOException e) {
                        logger.error("i/o exception on loading resource: " + id, e);
                        throw new InternalError(e.getMessage());
                    }
                }
            });
        }

        resources = builder.build();
    }
}
