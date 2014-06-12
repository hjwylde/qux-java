package com.hjwylde.qux.api;

import com.hjwylde.qux.tree.ExprNode;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.2
 */
final class NullConstantVisitor implements ConstantVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitExpr(ExprNode expr) {}
}
