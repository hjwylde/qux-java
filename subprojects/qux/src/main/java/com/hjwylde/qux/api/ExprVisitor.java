package com.hjwylde.qux.api;

import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.tree.ExprNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
public interface ExprVisitor {

    @Alpha
    void visitExprBinary(ExprNode.Binary expr);

    @Alpha
    void visitExprConstant(ExprNode.Constant expr);

    @Alpha
    void visitExprFunction(ExprNode.Function expr);

    @Alpha
    void visitExprList(ExprNode.List expr);

    @Alpha
    void visitExprUnary(ExprNode.Unary expr);

    @Alpha
    void visitExprVariable(ExprNode.Variable expr);
}
