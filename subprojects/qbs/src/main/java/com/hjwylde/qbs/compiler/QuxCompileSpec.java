package com.hjwylde.qbs.compiler;

import static com.google.common.base.Preconditions.checkArgument;

import com.hjwylde.qbs.util.QuxProperties;

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
 * com.hjwylde.qbs.compiler.QuxCompileOptions} and the classpath. The classpath is a set of paths
 * that are used to load resources from for use during compilation.
 * <p/>
 * The default contains the default Qux compile options and an empty classpath.
 *
 * @author Henry J. Wylde
 */
public class QuxCompileSpec extends AbstractCompileSpec {

    private static final long serialVersionUID = 1L;

    private final Set<Path> classpath = new HashSet<>();

    /**
     * Creates a new {@code QuxCompileSpec} and initialises the output directory and classpath to
     * the default as found in the {@link com.hjwylde.qbs.util.QuxProperties}.
     */
    public QuxCompileSpec() {
        this(QuxCompileOptions.DEFAULT_OPTIONS);
    }

    protected QuxCompileSpec(QuxCompileOptions options) {
        super(options);

        // Get the default properties
        QuxProperties properties = QuxProperties.loadDefaultProperties();

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
     * Gets the Qux compile options.
     *
     * @return the Qux compile options.
     */
    @Override
    public QuxCompileOptions getOptions() {
        return (QuxCompileOptions) super.getOptions();
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
     * Sets the Qux compile options. The options must be an instance of {@link
     * com.hjwylde.qbs.compiler.QuxCompileOptions}, otherwise an {@link
     * java.lang.IllegalArgumentException} is thrown.
     *
     * @param options the new options.
     * @throws java.lang.IllegalArgumentException if the options aren't an instance of {@link
     * com.hjwylde.qbs.compiler.QuxCompileOptions}.
     */
    @Override
    public void setOptions(CompileOptions options) {
        checkArgument(options instanceof QuxCompileOptions,
                "options must be an instanceof QuxCompileOptions");

        super.setOptions(options);
    }
}
