package com.hjwylde.qbs.builder;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * A <code>Builder</code> is responsible for converting a file of a specific type into another file
 * of another type. These types are dependent upon the builder itself.
 *
 * @author Henry J. Wylde
 */
public interface Builder {

    /**
     * Takes a set of input paths associated to build. Builds the input given from the input paths
     * and writes them to their corresponding output paths.
     *
     * This method will return the results of all of the build operations through a map linking each
     * path to the result.
     *
     * @param source a set of input paths to build.
     * @return the build results linked to each source path.
     */
    Map<Path, BuildResult> build(Set<Path> source);
}
