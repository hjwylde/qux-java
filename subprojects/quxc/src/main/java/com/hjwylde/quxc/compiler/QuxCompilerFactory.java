package com.hjwylde.quxc.compiler;

import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qbs.compiler.Compiler;
import com.hjwylde.qbs.compiler.CompilerFactory;
import com.hjwylde.quxjc.compiler.Qux2ClassCompilerFactory;

import java.util.Locale;

/**
 * A default compiler factory for creating a {@link com.hjwylde.qbs.compiler.Compiler} that takes a
 * {@link com.hjwylde.quxc.compiler.QuxcCompileSpec}.
 * <p/>
 * This factory will return a compiler based on the {@link com.hjwylde.quxc.util.Target} property of
 * the {@link com.hjwylde.quxc.compiler.QuxcCompileOptions}.
 *
 * @author Henry J. Wylde
 * @since 0.2.1
 */
public final class QuxCompilerFactory extends CompilerFactory<QuxcCompileSpec, QuxcCompileOptions> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Compiler<QuxcCompileSpec> buildCompiler(QuxcCompileOptions options) {
        switch (options.getTarget()) {
            case JVM:
                return new Qux2ClassCompilerFactory<QuxcCompileSpec, QuxcCompileOptions>()
                        .buildCompiler(options);
            default:
                throw new MethodNotImplementedError(options.getTarget().toString().toLowerCase(
                        Locale.ENGLISH));
        }
    }
}

