package com.hjwylde.qux.api;

import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Identifier;

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
    public void visitParameter(Identifier var) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtAssign(StmtNode.Assign stmt) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtExpr(StmtNode.Expr stmt) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtFor(StmtNode.For stmt) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtIf(StmtNode.If stmt) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtPrint(StmtNode.Print stmt) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtReturn(StmtNode.Return stmt) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtWhile(StmtNode.While stmt) {}
}
