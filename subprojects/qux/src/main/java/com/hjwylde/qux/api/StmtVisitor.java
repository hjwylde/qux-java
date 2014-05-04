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

    void visitStmtAccessAssign(StmtNode.AccessAssign stmt);

    void visitStmtAssign(StmtNode.Assign stmt);

    void visitStmtFor(StmtNode.For stmt);

    void visitStmtFunction(StmtNode.Function stmt);

    void visitStmtIf(StmtNode.If stmt);

    @Alpha
    void visitStmtPrint(StmtNode.Print stmt);

    void visitStmtReturn(StmtNode.Return stmt);
}
