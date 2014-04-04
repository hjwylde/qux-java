package com.hjwylde.quxc.compiler;

import com.hjwylde.qbs.compiler.Compiler;
import com.hjwylde.qbs.compiler.CompilerFactory;

/**
 * TODO: Documentation.
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

