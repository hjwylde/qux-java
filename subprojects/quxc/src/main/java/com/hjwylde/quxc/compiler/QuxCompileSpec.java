package com.hjwylde.quxc.compiler;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.compiler.AbstractCompileSpec;

import com.google.common.collect.ImmutableSet;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public class QuxCompileSpec extends AbstractCompileSpec {

    private static final long serialVersionUID = 1L;

    private QuxCompileOptions options;

    private final Set<Path> classpath = new HashSet<>();

    public final void classpath(Collection<Path> classpath) {
        for (Path path : classpath) {
            this.classpath.add(path.toAbsolutePath().normalize());
        }
    }

    public final void classpath(Path... classpath) {
        classpath(Arrays.asList(classpath));
    }

    public final ImmutableSet<Path> getClasspath() {
        return ImmutableSet.copyOf(classpath);
    }

    public final QuxCompileOptions getOptions() {
        return options;
    }

    public final void setClasspath(Collection<Path> classpath) {
        this.classpath.clear();

        classpath(classpath);
    }

    public final void setOptions(QuxCompileOptions options) {
        this.options = checkNotNull(options, "options cannot be null");
    }
}
