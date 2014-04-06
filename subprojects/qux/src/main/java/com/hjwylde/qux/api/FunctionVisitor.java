package com.hjwylde.qux.api;

import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public interface FunctionVisitor {

    FunctionVisitor NULL_INSTANCE = new NullFunctionVisitor();

    void visitCode();

    void visitEnd();

    void visitParameter(String var, Type type);

    void visitReturnType(Type type);

    @Alpha
    void visitStmtAssign(String var, ExprNode expr);

    @Alpha
    void visitStmtFunction(String name, ImmutableList<ExprNode> arguments);

    @Alpha
    void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
            ImmutableList<StmtNode> falseBlock);

    @Alpha
    void visitStmtPrint(ExprNode expr);

    @Alpha
    void visitStmtReturn(Optional<ExprNode> expr);
}
