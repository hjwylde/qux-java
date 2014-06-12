package com.hjwylde.qux.api;

import com.hjwylde.qux.tree.ExprNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public interface ConstantVisitor {

    ConstantVisitor NULL_INSTANCE = new NullConstantVisitor();

    void visitEnd();

    void visitExpr(ExprNode expr);
}
