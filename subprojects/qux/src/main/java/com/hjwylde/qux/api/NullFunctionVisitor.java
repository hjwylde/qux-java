package com.hjwylde.qux.api;

import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Type;

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
    public void visitStmtAssign(StmtNode.Assign stmt) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtFor(StmtNode.For stmt) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtFunction(StmtNode.Function stmt) {}

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
}
