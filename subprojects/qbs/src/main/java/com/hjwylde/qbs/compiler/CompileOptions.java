package com.hjwylde.qbs.compiler;

import java.io.Serializable;

/**
 * Represents a set of compile options that may be used to change how the compiler works. This is in
 * contrast to a {@link com.hjwylde.qbs.compiler.CompileSpec} which specifies resources for the
 * {@link com.hjwylde.qbs.compiler.Compiler}.
 *
 * @author Henry J. Wylde
 */
public abstract class CompileOptions implements Serializable {

    private static final long serialVersionUID = 1L;
}

