package com.hjwylde.qux.api;

import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public abstract class FunctionVisitor {

    public static final FunctionVisitor NULL_INSTANCE = new NullFunctionVisitor();

    private final FunctionVisitor next;

    public FunctionVisitor() {
        this(null);
    }

    public FunctionVisitor(@Nullable FunctionVisitor next) {
        this.next = next;
    }

    public void visitCode() {
        if (next != null) {
            next.visitCode();
        }
    }

    public void visitEnd() {
        if (next != null) {
            next.visitEnd();
        }
    }

    public void visitParameter(String var, Type type) {
        if (next != null) {
            next.visitParameter(var, type);
        }
    }

    public void visitReturnType(Type type) {
        if (next != null) {
            next.visitReturnType(type);
        }
    }

    @Alpha
    public void visitStmtAssign(String var, ExprNode expr) {
        if (next != null) {
            next.visitStmtAssign(var, expr);
        }
    }

    @Alpha
    public void visitStmtFunction(String name, ImmutableList<ExprNode> arguments) {
        if (next != null) {
            next.visitStmtFunction(name, arguments);
        }
    }

    @Alpha
    public void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
            ImmutableList<StmtNode> falseBlock) {
        if (next != null) {
            next.visitStmtIf(condition, trueBlock, falseBlock);
        }
    }

    @Alpha
    public void visitStmtPrint(ExprNode expr) {
        if (next != null) {
            next.visitStmtPrint(expr);
        }
    }

    @Alpha
    public void visitStmtReturn(Optional<ExprNode> expr) {
        if (next != null) {
            next.visitStmtReturn(expr);
        }
    }

    private static final class NullFunctionVisitor extends FunctionVisitor {}
}
