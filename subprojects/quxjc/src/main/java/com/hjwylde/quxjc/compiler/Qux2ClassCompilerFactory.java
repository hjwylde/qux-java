package com.hjwylde.quxjc.compiler;

import com.hjwylde.qbs.compiler.Compiler;
import com.hjwylde.qbs.compiler.CompilerFactory;
import com.hjwylde.qbs.compiler.QuxCompileOptions;
import com.hjwylde.qbs.compiler.QuxCompileSpec;

/**
 * A default compiler factory for creating a {@link com.hjwylde.qbs.compiler.Compiler} that takes a
 * {@link com.hjwylde.qbs.compiler.QuxCompileSpec}.
 * <p/>
 * This factory simply returns a new {@link com.hjwylde.quxjc.compiler.Qux2ClassCompiler}.
 *
 * @author Henry J. Wylde
 */
public final class Qux2ClassCompilerFactory<T extends QuxCompileSpec, E extends QuxCompileOptions>
        extends CompilerFactory<T, E> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Compiler<T> buildCompiler(E options) {
        return new Qux2ClassCompiler<T>();
    }
}

