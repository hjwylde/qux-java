package com.hjwylde.qbs.compiler;

/**
 * Represents a factory that will create a compiler based on some {@link
 * com.hjwylde.qbs.compiler.CompileOptions}.
 *
 * @param <T> the type of {@link CompileSpec} the built compiler should use.
 * @param <E> the type of compile options to use to build the compiler.
 * @author Henry J. Wylde
 */
public abstract class CompilerFactory<T extends CompileSpec, E extends CompileOptions> {

    /**
     * Builds a compiler with the given options.
     *
     * @param options the options to use to build the compiler.
     * @return the compiler.
     */
    public abstract Compiler<T> buildCompiler(E options);
}

