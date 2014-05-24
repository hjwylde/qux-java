package com.hjwylde.qbs.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.compiler.CompileSpec;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A project is a static configuration to be used to assist in compilation. It has a root directory,
 * a binary output directory, and a classpath.
 *
 * @author Henry J. Wylde
 */
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Path root;

    private final Path outdir;

    /**
     * Creates a new project using the given builder. No elements inside the builder are allowed to
     * be null.
     *
     * @param builder the builder.
     */
    protected Project(Builder builder) {
        this.root = builder.root.toAbsolutePath().normalize();

        this.outdir = builder.outdir.toAbsolutePath().normalize();
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
    public static Builder builder(CompileSpec spec) {
        return new Builder(spec);
    }

    /**
     * Gets a new builder for a project, initialising it with the given projects settings.
     *
     * @param project the project to initialise from.
     * @return a new initialised builder.
     */
    public static Builder builder(Project project) {
        return new Builder(project);
    }

    /**
     * Gets the output directory of this project.
     *
     * @return the output directory.
     */
    public final Path getOutdir() {
        return outdir;
    }

    /**
     * Gets the root directory path of this project.
     *
     * @return the root directory path.
     */
    public final Path getRoot() {
        return root;
    }

    /**
     * A builder class for creating a project.
     *
     * @author Henry J. Wylde
     */
    public static class Builder {

        private Path root = Paths.get("./");

        private Path outdir = Paths.get("./");

        /**
         * Creates a new empty builder.
         */
        protected Builder() {}

        /**
         * Creates a new builder initialising it with the settings from the given compile
         * specification.
         *
         * @param spec the specification to initialise from.
         */
        protected Builder(CompileSpec spec) {
            this.outdir = spec.getOutdir();
        }

        /**
         * Creates a new builder initialising it with the settings from the given project.
         *
         * @param project the project to initialise from.
         */
        protected Builder(Project project) {
            this.root = project.root;

            this.outdir = project.outdir;
        }

        /**
         * Builds a new project with the settings from this builder.
         *
         * @return the new project.
         */
        public Project build() {
            return new Project(this);
        }

        /**
         * Sets the output directory.
         *
         * @param outdir the output directory.
         * @return this builder for chaining.
         */
        public final Builder setOutdir(Path outdir) {
            this.outdir = checkNotNull(outdir, "outdir cannot be null");
            return this;
        }

        /**
         * Sets the root directory.
         *
         * @param root the root directory.
         * @return this builder for chaining.
         */
        public final Builder setRoot(Path root) {
            this.root = checkNotNull(root, "root cannot be null");
            return this;
        }
    }
}
