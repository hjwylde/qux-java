package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public class CheckFunctionAdapter extends FunctionVisitor {

    private boolean visitedCode = false;
    private boolean visitedEnd = false;

    public CheckFunctionAdapter(@Nullable FunctionVisitor next) {
        super(next);
    }

    @Override
    public void visitCode() {
        checkState(!visitedCode, "may only call visitCode() once");
        checkState(!visitedEnd, "must call visitCode() before visitEnd()");

        visitedCode = true;

        super.visitCode();
    }

    @Override
    public void visitEnd() {
        checkState(visitedCode, "must call visitCode() before visitEnd()");
        checkState(!visitedEnd, "may only call visitEnd() once");

        visitedEnd = true;

        super.visitEnd();
    }

    @Override
    public void visitParameter(String var, Type type) {
        checkState(!visitedCode, "must call visitParameter(String, Type) before visitCode()");
        checkState(!visitedEnd, "must call visitParameter(String, Type) before visitEnd()");
        checkNotNull(var, "var cannot be null");
        checkNotNull(type, "type cannot be null");

        super.visitParameter(var, type);
    }

    @Override
    public void visitReturnType(Type type) {
        checkState(!visitedCode, "must call visitReturnType(Type) before visitCode()");
        checkState(!visitedEnd, "must call visitReturnType(Type) before visitEnd()");
        checkNotNull(type, "type cannot be null");

        super.visitReturnType(type);
    }

    @Override
    public void visitStmtAssign(String var, ExprNode expr) {
        checkState(visitedCode, "must call visitCode() before visitStmtAssign(String, ExprNode)");
        checkState(!visitedEnd, "must call visitStmtAssign(String, ExprNode) before visitEnd()");
        checkNotNull(var, "var cannot be null");
        checkNotNull(expr, "expr cannot be null");

        super.visitStmtAssign(var, expr);
    }

    @Override
    public void visitStmtFunction(String name, ImmutableList<ExprNode> arguments) {
        checkState(visitedCode,
                "must call visitCode() before visitStmtFunction(String, ImmutablelistList<ExprNode>)");
        checkState(!visitedEnd,
                "must call visitStmtFunction(String, ImmutableList<ExprNode>) before visitEnd()");
        checkNotNull(name, "name cannot be null");
        checkNotNull(arguments, "arguments cannot be null");

        super.visitStmtFunction(name, arguments);
    }

    @Override
    public void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
            ImmutableList<StmtNode> falseBlock) {
        checkState(visitedCode,
                "must call visitCode() before visitStmtIf(ExprNode, ImmutableList<StmtNode>, ImmutableList<StmtNode>)");
        checkState(!visitedEnd,
                "must call visitStmtIf(ExprNode, ImmutableList<StmtNode>, ImmutableList<StmtNode>) before visitEnd()");
        checkNotNull(condition, "condition cannot be null");
        checkNotNull(trueBlock, "trueBlock cannot be null");
        checkNotNull(falseBlock, "falseBlock cannot be null");

        super.visitStmtIf(condition, trueBlock, falseBlock);
    }

    @Override
    public void visitStmtPrint(ExprNode expr) {
        checkState(visitedCode, "must call visitCode() before visitStmtAssign(String, ExprNode)");
        checkState(!visitedEnd, "must call visitStmtPrint(ExprNode) before visitEnd()");
        checkNotNull(expr, "expr cannot be null");

        super.visitStmtPrint(expr);
    }

    @Override
    public void visitStmtReturn(Optional<ExprNode> expr) {
        checkState(visitedCode, "must call visitCode() before visitStmtReturn(Optional<ExprNode>)");
        checkState(!visitedEnd, "must call visitStmtReturn(Optional<ExprNode>) before visitEnd()");
        checkNotNull(expr, "expr cannot be null");

        super.visitStmtReturn(expr);
    }
}
