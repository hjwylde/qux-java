package com.hjwylde.qbs.compiler;

/**
 * A compiler will take a specification of the required type and compile it into some output, based
 * on the input specification.
 *
 * @param <T> the type of compile specification to compile for.
 * @author Henry J. Wylde
 */
public interface Compiler<T extends CompileSpec> {

    /**
     * Executes the compiler using the provided compile specification for all options and source
     * files.
     *
     * @param spec the compile specification.
     */
    void execute(T spec);
}

