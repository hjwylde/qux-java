package com.hjwylde.qux.api;

import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;

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
    void visitStmtAssign(StmtNode.Assign stmt);

    @Alpha
    void visitStmtFunction(StmtNode.Function stmt);

    @Alpha
    void visitStmtIf(StmtNode.If stmt);

    @Alpha
    void visitStmtPrint(StmtNode.Print stmt);

    @Alpha
    void visitStmtReturn(StmtNode.Return stmt);
}
