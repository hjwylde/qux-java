package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.tree.ExprNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public abstract class ConstantAdapter implements ConstantVisitor {

    private final ConstantVisitor next;

    public ConstantAdapter() {
        this(ConstantVisitor.NULL_INSTANCE);
    }

    public ConstantAdapter(ConstantVisitor next) {
        this.next = checkNotNull(next, "next cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        next.visitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitExpr(ExprNode expr) {
        next.visitExpr(expr);
    }
}
