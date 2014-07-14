package com.hjwylde.qux.api;

import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.tree.StmtNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public interface StmtVisitor {

    void visitStmtAssign(StmtNode.Assign stmt);

    void visitStmtExpr(StmtNode.Expr stmt);

    void visitStmtFor(StmtNode.For stmt);

    void visitStmtIf(StmtNode.If stmt);

    @Alpha
    void visitStmtPrint(StmtNode.Print stmt);

    void visitStmtReturn(StmtNode.Return stmt);

    void visitStmtWhile(StmtNode.While stmt);
}
