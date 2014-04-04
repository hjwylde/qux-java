package com.hjwylde.qbs.builder.resources;

import java.util.zip.ZipFile;

/**
 * A jar resource is simply a {@link ZipResource} and may be used equivalently.
 *
 * @author Henry J. Wylde
 */
public class JarResource extends ZipResource {

    /**
     * The extension of a jar resource. Is the string {@code "jar"}.
     */
    public static final Resource.Extension EXTENSION = new Resource.Extension("jar");

    /**
     * Creates a new jar resource with the given zip file.
     *
     * @param file the zip file.
     */
    public JarResource(ZipFile file) {
        super(file);
    }
}
