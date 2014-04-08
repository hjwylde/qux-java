package com.hjwylde.quxc.compiler;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.compiler.AbstractCompileSpec;
import com.hjwylde.quxc.util.QuxcProperties;

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Compile specification for a Qux unit. This specification adds in the ability to set the {@link
 * com.hjwylde.quxc.compiler.QuxCompileOptions} and the classpath. The classpath is a set of paths
 * that are used to load resources from for use during compilation.
 * <p/>
 * The default contains an empty classpath and the default Qux compile options.
 *
 * @author Henry J. Wylde
 */
public class QuxCompileSpec extends AbstractCompileSpec {

    private static final long serialVersionUID = 1L;

    private final Set<Path> classpath = new HashSet<>();

    private QuxCompileOptions options = QuxCompileOptions.DEFAULT_OPTIONS;

    /**
     * Creates a new {@code QuxCompileSpec} and initialises the output directory and classpath to
     * the default as found in the {@link com.hjwylde.quxc.util.QuxcProperties}.
     */
    public QuxCompileSpec() {
        // Get the default properties
        QuxcProperties properties = QuxcProperties.loadDefaultProperties();

        setOutdir(Paths.get(properties.getOutdir()));

        for (String path : properties.getClasspath().split(File.pathSeparator)) {
            classpath(Paths.get(path));
        }
    }

    /**
     * Appends the given paths to the classpath.
     *
     * @param classpath the paths to append.
     */
    public final void classpath(Collection<Path> classpath) {
        for (Path path : classpath) {
            this.classpath.add(path.toAbsolutePath().normalize());
        }
    }

    /**
     * Appends the given paths to the classpath.
     *
     * @param classpath the paths to append.
     */
    public final void classpath(Path... classpath) {
        classpath(Arrays.asList(classpath));
    }

    /**
     * Gets the classpath.
     *
     * @return the classpath.
     */
    public final ImmutableSet<Path> getClasspath() {
        return ImmutableSet.copyOf(classpath);
    }

    /**
     * Sets the classpath to the given collection of paths.
     *
     * @param classpath the new classpath.
     */
    public final void setClasspath(Collection<Path> classpath) {
        this.classpath.clear();

        classpath(classpath);
    }

    /**
     * Gets the Qux compile options.
     *
     * @return the Qux compile options.
     */
    public final QuxCompileOptions getOptions() {
        return options;
    }

    /**
     * Sets the Qux compile options.
     *
     * @param options the new options.
     */
    public final void setOptions(QuxCompileOptions options) {
        this.options = checkNotNull(options, "options cannot be null");
    }
}
