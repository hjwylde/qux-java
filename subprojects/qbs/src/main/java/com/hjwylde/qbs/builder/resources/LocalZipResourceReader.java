package com.hjwylde.qbs.builder.resources;

import com.hjwylde.common.error.MethodNotImplementedError;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.zip.ZipFile;

/**
 * A reader for reading a local zip resource.
 *
 * @author Henry J. Wylde
 */
public final class LocalZipResourceReader implements Resource.Reader<ZipResource> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ZipResource read(Path path) throws IOException {
        try (ZipFile file = new ZipFile(path.toFile())) {
            return new ZipResource(file);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ZipResource read(InputStream in) {
        // TODO: Implement read(InputStream)
        throw new MethodNotImplementedError();
    }
}
