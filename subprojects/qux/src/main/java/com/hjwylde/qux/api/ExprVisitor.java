package com.hjwylde.qux.api;

import com.hjwylde.qux.tree.ExprNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
public interface ExprVisitor {

    void visitExprBinary(ExprNode.Binary expr);

    void visitExprConstant(ExprNode.Constant expr);

    void visitExprFunction(ExprNode.Function expr);

    void visitExprList(ExprNode.List expr);

    void visitExprSet(ExprNode.Set expr);

    void visitExprUnary(ExprNode.Unary expr);

    void visitExprVariable(ExprNode.Variable expr);
}
