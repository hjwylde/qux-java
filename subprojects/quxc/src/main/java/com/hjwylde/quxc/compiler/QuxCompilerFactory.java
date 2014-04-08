package com.hjwylde.quxc.compiler;

import com.hjwylde.qbs.compiler.Compiler;
import com.hjwylde.qbs.compiler.CompilerFactory;

/**
 * A default compiler factory for creating a {@link com.hjwylde.qbs.compiler.Compiler} that takes a
 * {@link com.hjwylde.quxc.compiler.QuxCompileSpec}.
 * <p/>
 * This factory simply returns a new {@link com.hjwylde.quxc.compiler.QuxCompiler}.
 *
 * @author Henry J. Wylde
 */
public final class QuxCompilerFactory extends CompilerFactory<QuxCompileSpec, QuxCompileOptions> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Compiler<QuxCompileSpec> buildCompiler(QuxCompileOptions options) {
        return new QuxCompiler();
    }
}

