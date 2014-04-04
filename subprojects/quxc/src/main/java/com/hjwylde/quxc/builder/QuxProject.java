package com.hjwylde.quxc.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.builder.Project;
import com.hjwylde.quxc.compiler.QuxCompileOptions;
import com.hjwylde.quxc.compiler.QuxCompileSpec;

import com.google.common.collect.ImmutableSet;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public final class QuxProject extends Project {

    private static final long serialVersionUID = 1L;

    private final QuxCompileOptions options;

    private final ImmutableSet<Path> classpath;

    QuxProject(Builder builder) {
        super(builder);

        this.options = checkNotNull(builder.options, "options cannot be null");

        ImmutableSet.Builder<Path> classpathBuilder = ImmutableSet.builder();
        for (Path path : builder.classpath) {
            classpathBuilder.add(path.toAbsolutePath().normalize());
        }
        this.classpath = classpathBuilder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(QuxCompileSpec spec) {
        return new Builder(spec);
    }

    public static Builder builder(QuxProject project) {
        return new Builder(project);
    }

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
    public ImmutableSet<Path> getClasspath() {
        return classpath;
    }

    public QuxCompileOptions getOptions() {
        return options;
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Builder extends Project.Builder {

        private QuxCompileOptions options = QuxCompileOptions.DEFAULT_OPTIONS;

        private Set<Path> classpath = new HashSet<>();

        Builder() {}

        Builder(Project project) {
            super(project);
        }

        Builder(QuxCompileSpec spec) {
            super(spec);

            this.options = spec.getOptions();

            this.classpath = new HashSet<>(spec.getClasspath());
        }

        Builder(QuxProject project) {
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
         * @return the builder for chaining.
         */
        public final Builder classpath(Path... classpath) {
            return classpath(Arrays.asList(classpath));
        }

        /**
         * Appends the given collection of paths to the classpath list in this builder. If the
         * classpath list is {@code null}, then the list is first initialised to an empty {@link
         * ArrayList}.
         *
         * @param classpath the classpath to append to the current classpath.
         * @return the builder for chaining.
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
         * @return the builder for chaining.
         */
        public final Builder setClasspath(Set<Path> classpath) {
            this.classpath = checkNotNull(classpath, "classpath cannot be null");
            return this;
        }

        public Builder setOptions(QuxCompileOptions options) {
            this.options = checkNotNull(options, "options cannot be null");
            return this;
        }
    }
}
