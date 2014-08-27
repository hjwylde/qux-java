package com.hjwylde.qux.api;

import com.hjwylde.qux.util.Identifier;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public interface FunctionVisitor extends StmtVisitor {

    FunctionVisitor NULL_INSTANCE = new NullFunctionVisitor();

    void visitCode();

    void visitEnd();

    void visitParameter(Identifier var);
}
