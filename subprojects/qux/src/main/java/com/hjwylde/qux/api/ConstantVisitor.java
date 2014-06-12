package com.hjwylde.qux.api;

import com.hjwylde.qux.tree.ExprNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.2
 */
public interface ConstantVisitor {

    ConstantVisitor NULL_INSTANCE = new NullConstantVisitor();

    void visitEnd();

    void visitExpr(ExprNode expr);
}
