package com.hjwylde.qbs.compiler;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

import com.google.common.collect.ImmutableSet;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A basic implementation of a {@link com.hjwylde.qbs.compiler.CompileSpec}. Provides default
 * implementation of the methods and some fields to hold the properties. Note that this
 * implementation will automatically normalise any paths passed as arguments to methods.
 * <p/>
 * The default contains the default compile options, no source files and sets the output directory
 * to be the current working directory ({@code Paths.get(".")}).
 *
 * @author Henry J. Wylde
 */
public abstract class AbstractCompileSpec implements CompileSpec {

    private static final long serialVersionUID = 1L;
    private final Set<Path> source = new HashSet<>();
    private CompileOptions options;
    private Path outdir = Paths.get("./").toAbsolutePath().normalize();

    protected AbstractCompileSpec(CompileOptions options) {
        setOptions(options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompileOptions getOptions() {
        return options;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Path getOutdir() {
        return outdir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableSet<Path> getSource() {
        return ImmutableSet.copyOf(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOptions(CompileOptions options) {
        this.options = checkNotNull(options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setOutdir(Path outdir) {
        this.outdir = checkNotNull(outdir, "outdir cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setSource(Set<Path> source) {
        this.source.clear();

        source(source);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void source(Collection<Path> source) {
        for (Path path : source) {
            this.source.add(path.toAbsolutePath().normalize());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void source(Path... source) {
        source(asList(source));
    }
}
