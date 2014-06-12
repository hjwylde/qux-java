package com.hjwylde.qbs.builder.resources;

import com.google.common.base.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Locale;

import javax.annotation.Nullable;

/**
 * A resource is used in relation to the context in the compilation process. It reflects different
 * resources that may be referenced in compilation.
 * <p/>
 * All identifiers are assumed to be fully qualified and in '.' delimited fashion.
 *
 * @author Henry J. Wylde
 */
public interface Resource {

    /**
     * Checks whether this resource contains a resource for the given identifier.
     *
     * @param id the identifier.
     * @return true if the resource contains a resource for the given identifier.
     */
    boolean containsId(String id);

    /**
     * Gets the resource with the given identifier. If the resource does not exist, {@code
     * Optional#absent()} is returned.
     *
     * @param id the identifier.
     * @return the resource or nothing.
     */
    Optional<Resource.Single> getById(String id);

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static interface Collection extends Resource, Iterable<Resource> {}

    /**
     * A resource reader provides methods for reading an {@link java.io.InputStream} or {@link
     * java.nio.file.Path} and translating it into a certain type of resource. Not all readers will
     * support both read methods and should be documented if they do not.
     *
     * @param <T> the type of resource to read.
     * @author Henry J. Wylde
     */
    public static interface Reader<T extends Resource> {

        /**
         * Reads the given input stream into a resource.
         *
         * @param in the input stream.
         * @return the resource.
         * @throws java.io.IOException if an i/o exception occurs during reading.
         * @throws java.lang.UnsupportedOperationException if this reader does not support this
         * method of reading.
         */
        T read(InputStream in) throws IOException;

        /**
         * Reads the given path into a resource.
         *
         * @param path the path.
         * @return the resource.
         * @throws java.io.IOException if an i/o exception occurs during reading.
         * @throws java.lang.UnsupportedOperationException if this reader does not support this
         * method of reading.
         */
        T read(Path path) throws IOException;
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static interface Single extends Resource {

        Optional<String> getConstantType(String name);

        Optional<String> getFunctionType(String name);

        String getId();
    }

    /**
     * A resource extension. The extension should not include the '.' prefix. An empty extension is
     * treated as a special extension and is assumed to be the {@code com.hjwylde.qbs.builder.resources.DirectoryResource#EXTENSION}.
     *
     * @author Henry J. Wylde
     */
    public static final class Extension {

        private final String extension;

        /**
         * Creates a new resource extension with the given string.
         *
         * @param extension the extension.
         */
        public Extension(String extension) {
            this.extension = extension.toLowerCase(Locale.ENGLISH);
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

            return extension.equals(((Resource.Extension) obj).extension);
        }

        /**
         * Gets the extension.
         *
         * @return the extension.
         */
        public String getExtension() {
            return extension;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return extension.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return extension;
        }
    }
}
