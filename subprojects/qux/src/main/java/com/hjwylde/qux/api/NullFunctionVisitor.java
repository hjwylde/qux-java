package com.hjwylde.qux.api;

import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
final class NullFunctionVisitor implements FunctionVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitCode() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitParameter(String var, Type type) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitReturnType(Type type) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtAssign(String var, ExprNode expr) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtFunction(String name, ImmutableList<ExprNode> arguments) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
            ImmutableList<StmtNode> falseBlock) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtPrint(ExprNode expr) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtReturn(Optional<ExprNode> expr) {}
}
