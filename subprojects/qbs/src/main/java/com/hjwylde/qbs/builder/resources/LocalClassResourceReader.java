package com.hjwylde.qbs.builder.resources;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public final class LocalClassResourceReader implements Resource.Reader<ClassResource> {

    private static final ClassDefiner classDefiner = new ClassDefiner();

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassResource read(InputStream in) throws IOException {
        return classDefiner.defineClass(ByteStreams.toByteArray(in));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassResource read(Path path) throws IOException {
        return classDefiner.defineClass(Files.readAllBytes(path));
    }

    private static final class ClassDefiner extends ClassLoader {

        public Class<?> defineClass(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
