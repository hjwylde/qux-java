package com.hjwylde.qbs.builder.resources;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * A reader for reading a local directory resource. This reader only supports reading from a {@link
 * java.nio.file.Path} and not an {@link java.io.InputStream}. This is due to the inability to
 * encode a directory as an input stream without the assistance of another program such as {@code
 * tar}.
 *
 * @author Henry J. Wylde
 */
public final class LocalDirectoryResourceReader implements Resource.Reader<DirectoryResource> {

    private static final LocalDirectoryResourceReader INSTANCE = new LocalDirectoryResourceReader();

    /**
     * This class is a singleton.
     */
    private LocalDirectoryResourceReader() {}

    /**
     * Gets the singleton instance of a local directory resource reader.
     *
     * @return the instance.
     */
    public static LocalDirectoryResourceReader getInstance() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectoryResource read(Path path) {
        return new DirectoryResource(path);
    }

    /**
     * Unsupported. Always throws an {@link java.lang.UnsupportedOperationException}.
     *
     * @param in the input stream.
     * @return n/a.
     * @throws UnsupportedOperationException always.
     */
    @Override
    public DirectoryResource read(InputStream in) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "unable to read directory resource from input stream");
    }
}
