package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.tree.ExprNode;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.2
 */
public class CheckConstantAdapter extends ConstantAdapter {

    private boolean visitedExpr;
    private boolean visitedEnd;

    public CheckConstantAdapter(ConstantVisitor next) {
        super(next);
    }

    public final boolean hasVisitedEnd() {
        return visitedEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        checkState(visitedExpr, "must call visitExpr(ExprNode) before visitEnd()");
        checkState(!visitedEnd, "may only call visitEnd() once");

        visitedEnd = true;

        super.visitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitExpr(ExprNode expr) {
        checkState(!visitedExpr, "may only call visitExpr(ExprNode) once");
        checkState(!visitedEnd, "must call visitExpr(ExprNode) before visitEnd()");
        checkNotNull(expr, "expr cannot be null");

        visitedExpr = true;

        super.visitExpr(expr);
    }
}
