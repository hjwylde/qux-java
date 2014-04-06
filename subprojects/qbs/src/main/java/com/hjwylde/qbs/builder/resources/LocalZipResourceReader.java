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

    private static final LocalZipResourceReader INSTANCE = new LocalZipResourceReader();

    /**
     * This class is a singleton.
     */
    private LocalZipResourceReader() {}

    /**
     * Gets the singleton instance of a local zip resource reader.
     *
     * @return the instance.
     */
    public static LocalZipResourceReader getInstance() {
        return INSTANCE;
    }

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
