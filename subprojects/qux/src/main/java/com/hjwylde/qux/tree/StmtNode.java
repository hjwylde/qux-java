package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.api.FunctionVisitor;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class StmtNode {

    // TODO: Make these immutable

    /**
     * This class may only be sub-classed locally.
     */
    StmtNode() {}

    public abstract void accept(FunctionVisitor fv);

    public static final class Assign extends StmtNode {

        private String var;
        private ExprNode expr;

        public Assign(String var, ExprNode expr) {
            setVar(var);
            setExpr(expr);
        }

        @Override
        public void accept(FunctionVisitor fv) {
            fv.visitStmtAssign(var, expr);
        }

        public ExprNode getExpr() {
            return expr;
        }

        public void setExpr(ExprNode expr) {
            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        public String getVar() {
            return var;
        }

        public void setVar(String var) {
            this.var = checkNotNull(var, "var cannot be null");
        }
    }

    public static final class Function extends StmtNode {

        private String name;
        private ImmutableList<ExprNode> arguments;

        public Function(String name, List<ExprNode> arguments) {
            setName(name);
            setArguments(arguments);
        }

        @Override
        public void accept(FunctionVisitor fv) {
            fv.visitStmtFunction(name, arguments);
        }

        public ImmutableList<ExprNode> getArguments() {
            return arguments;
        }

        public void setArguments(List<ExprNode> arguments) {
            this.arguments = ImmutableList.copyOf(arguments);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = checkNotNull(name, "name cannot be null");
        }
    }

    public static final class If extends StmtNode {

        private ExprNode condition;
        private ImmutableList<StmtNode> trueBlock;
        private ImmutableList<StmtNode> falseBlock;

        public If(ExprNode condition, List<StmtNode> trueBlock, List<StmtNode> falseBlock) {
            setCondition(condition);
            setTrueBlock(trueBlock);
            setFalseBlock(falseBlock);
        }

        @Override
        public void accept(FunctionVisitor fv) {
            fv.visitStmtIf(condition, trueBlock, falseBlock);
        }

        public ExprNode getCondition() {
            return condition;
        }

        public void setCondition(ExprNode condition) {
            this.condition = checkNotNull(condition, "condition cannot be null");
        }

        public ImmutableList<StmtNode> getFalseBlock() {
            return falseBlock;
        }

        public void setFalseBlock(List<StmtNode> falseBlock) {
            this.falseBlock = ImmutableList.copyOf(falseBlock);
        }

        public ImmutableList<StmtNode> getTrueBlock() {
            return trueBlock;
        }

        public void setTrueBlock(List<StmtNode> trueBlock) {
            this.trueBlock = ImmutableList.copyOf(trueBlock);
        }
    }

    @Alpha
    public static final class Print extends StmtNode {

        private ExprNode expr;

        public Print(ExprNode expr) {
            setExpr(expr);
        }

        @Override
        public void accept(FunctionVisitor fv) {
            fv.visitStmtPrint(expr);
        }

        public ExprNode getExpr() {
            return expr;
        }

        public void setExpr(ExprNode expr) {
            this.expr = checkNotNull(expr, "expr cannot be null");
        }
    }

    public static final class Return extends StmtNode {

        private Optional<ExprNode> expr;

        public Return() {
            setExpr(Optional.<ExprNode>absent());
        }

        public Return(Optional<ExprNode> expr) {
            setExpr(expr);
        }

        public Return(ExprNode expr) {
            setExpr(expr);
        }

        @Override
        public void accept(FunctionVisitor fv) {
            fv.visitStmtReturn(expr);
        }

        public Optional<ExprNode> getExpr() {
            return expr;
        }

        public void setExpr(ExprNode expr) {
            setExpr(Optional.fromNullable(expr));
        }

        public void setExpr(Optional<ExprNode> expr) {
            this.expr = checkNotNull(expr, "expr cannot be null");
        }
    }
}
