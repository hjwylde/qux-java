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

    @Override
    public void visitCode() {}

    @Override
    public void visitEnd() {}

    @Override
    public void visitParameter(String var, Type type) {}

    @Override
    public void visitReturnType(Type type) {}

    @Override
    public void visitStmtAssign(String var, ExprNode expr) {}

    @Override
    public void visitStmtFunction(String name, ImmutableList<ExprNode> arguments) {}

    @Override
    public void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
            ImmutableList<StmtNode> falseBlock) {}

    @Override
    public void visitStmtPrint(ExprNode expr) {}

    @Override
    public void visitStmtReturn(Optional<ExprNode> expr) {}
}
