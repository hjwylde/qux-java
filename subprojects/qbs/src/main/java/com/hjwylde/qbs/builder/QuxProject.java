package com.hjwylde.qbs.builder;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

import com.hjwylde.qbs.compiler.QuxCompileOptions;
import com.hjwylde.qbs.compiler.QuxCompileSpec;

import com.google.common.collect.ImmutableSet;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A Qux project builds on the configuration settings of a {@link com.hjwylde.qbs.builder.Project}
 * by adding in a {@link com.hjwylde.qbs.compiler.QuxCompileOptions} and a classpath.
 *
 * @author Henry J. Wylde
 */
public class QuxProject extends Project {

    private static final long serialVersionUID = 1L;

    private final QuxCompileOptions options;

    private final ImmutableSet<Path> classpath;

    /**
     * Creates a new project using the given builder. No elements inside the builder are allowed to
     * be null.
     *
     * @param builder the builder.
     */
    protected QuxProject(Builder builder) {
        super(builder);

        this.options = checkNotNull(builder.options, "options cannot be null");

        ImmutableSet.Builder<Path> classpathBuilder = ImmutableSet.builder();
        for (Path path : builder.classpath) {
            classpathBuilder.add(path.toAbsolutePath().normalize());
        }
        this.classpath = classpathBuilder.build();
    }

    /**
     * Gets a new builder for a project.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Gets a new builder for a project, initialising it with the given compile specification
     * settings.
     *
     * @param spec the specification to initialise from.
     * @return a new initialised builder.
     */
    public static Builder builder(QuxCompileSpec spec) {
        return new Builder(spec);
    }

    /**
     * Gets a new builder for a project, initialising it with the given projects settings.
     *
     * @param project the project to initialise from.
     * @return a new initialised builder.
     */
    public static Builder builder(QuxProject project) {
        return new Builder(project);
    }

    /**
     * Gets a new builder for a project, initialising it with the given projects settings.
     *
     * @param project the project to initialise from.
     * @return a new initialised builder.
     */
    public static Builder builder(Project project) {
        if (project instanceof QuxProject) {
            return new Builder((QuxProject) project);
        }

        return new Builder(project);
    }

    /**
     * Gets the classpath of this project.
     *
     * @return the classpath.
     */
    public final ImmutableSet<Path> getClasspath() {
        return classpath;
    }

    public final QuxCompileOptions getOptions() {
        return options;
    }

    /**
     * A builder for creating a {@link com.hjwylde.qbs.builder.QuxProject}.
     *
     * @author Henry J. Wylde
     */
    public static final class Builder extends Project.Builder {

        private QuxCompileOptions options = QuxCompileOptions.DEFAULT_OPTIONS;

        private Set<Path> classpath = new HashSet<>();

        /**
         * Creates a new {@code Builder}.
         */
        protected Builder() {}

        /**
         * Creates a new {@code Builder}, initialising the defaults with the values from the given
         * project.
         *
         * @param project the project to initialise the defaults from.
         */
        protected Builder(Project project) {
            super(project);
        }

        /**
         * Creates a new {@code Builder}, initialising the defaults with the values from the given
         * compile specification.
         *
         * @param spec the compile specification to initialise the defaults from.
         */
        protected Builder(QuxCompileSpec spec) {
            super(spec);

            this.options = spec.getOptions();

            this.classpath = new HashSet<>(spec.getClasspath());
        }

        /**
         * Creates a new {@code Builder}, initialising the defaults with the values from the given
         * project.
         *
         * @param project the project to initialise the defaults from.
         */
        protected Builder(QuxProject project) {
            super(project);

            this.options = project.options;

            this.classpath = new HashSet<>(project.classpath);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public QuxProject build() {
            return new QuxProject(this);
        }

        /**
         * Appends the given array of paths to the classpath list in this builder.
         *
         * @param classpath the classpath to append to the current classpath.
         * @return this builder for chaining.
         */
        public final Builder classpath(Path... classpath) {
            return classpath(asList(classpath));
        }

        /**
         * Appends the given collection of paths to the classpath list in this builder. If the
         * classpath list is {@code null}, then the list is first initialised to an empty {@link
         * ArrayList}.
         *
         * @param classpath the classpath to append to the current classpath.
         * @return this builder for chaining.
         */
        public final Builder classpath(Collection<Path> classpath) {
            for (Path path : classpath) {
                classpath.add(path);
            }

            return this;
        }

        /**
         * Sets the classpath list to the given classpath.
         *
         * @param classpath the classpath.
         * @return this builder for chaining.
         */
        public final Builder setClasspath(Set<Path> classpath) {
            this.classpath = checkNotNull(classpath, "classpath cannot be null");
            return this;
        }

        /**
         * Sets the qux compile options to the given options.
         *
         * @param options the options.
         * @return this builder for chaining.
         */
        public final Builder setOptions(QuxCompileOptions options) {
            this.options = checkNotNull(options, "options cannot be null");
            return this;
        }
    }
}
