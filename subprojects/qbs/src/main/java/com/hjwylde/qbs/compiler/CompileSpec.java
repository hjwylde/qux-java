package com.hjwylde.qbs.compiler;

import com.google.common.collect.ImmutableSet;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;

/**
 * Represents a compile specification. A compile specification lists some common properties of a
 * compiler that is required for the unit to operate. The basic compile specification must be able
 * to specify some source files and an output directory.
 *
 * @author Henry J. Wylde
 */
public interface CompileSpec extends Serializable {

    /**
     * Gets the compile options.
     *
     * @return the compile options.
     */
    CompileOptions getOptions();

    /**
     * Gets the output directory.
     *
     * @return the output directory.
     */
    Path getOutdir();

    /**
     * Gets the source paths.
     *
     * @return the source paths.
     */
    ImmutableSet<Path> getSource();

    /**
     * Sets the compile options.
     *
     * @param options the compile options.
     */
    void setOptions(CompileOptions options);

    /**
     * Sets the output directory.
     *
     * @param outdir the output directory.
     */
    void setOutdir(Path outdir);

    /**
     * Sets the source paths.
     *
     * @param source the source paths.
     */
    void setSource(Set<Path> source);

    /**
     * Appends the given source paths.
     *
     * @param source the source paths to append.
     */
    void source(Collection<Path> source);

    /**
     * Appends the given source paths.
     *
     * @param source the source paths to append.
     */
    void source(Path... source);
}

