package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
public abstract class FunctionAdapter implements FunctionVisitor {

    private final FunctionVisitor next;

    public FunctionAdapter() {
        this(FunctionVisitor.NULL_INSTANCE);
    }

    public FunctionAdapter(FunctionVisitor next) {
        this.next = checkNotNull(next, "next cannot be null");
    }

    @Override
    public void visitCode() {
        next.visitCode();
    }

    @Override
    public void visitEnd() {
        next.visitEnd();
    }

    @Override
    public void visitParameter(String var, Type type) {
        next.visitParameter(var, type);
    }

    @Override
    public void visitReturnType(Type type) {
        next.visitReturnType(type);
    }

    @Override
    public void visitStmtAssign(String var, ExprNode expr) {
        next.visitStmtAssign(var, expr);
    }

    @Override
    public void visitStmtFunction(String name, ImmutableList<ExprNode> arguments) {
        next.visitStmtFunction(name, arguments);
    }

    @Override
    public void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
            ImmutableList<StmtNode> falseBlock) {
        next.visitStmtIf(condition, trueBlock, falseBlock);
    }

    @Override
    public void visitStmtPrint(ExprNode expr) {
        next.visitStmtPrint(expr);
    }

    @Override
    public void visitStmtReturn(Optional<ExprNode> expr) {
        next.visitStmtReturn(expr);
    }
}
