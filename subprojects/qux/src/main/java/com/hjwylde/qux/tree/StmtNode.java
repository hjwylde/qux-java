package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.util.Attribute;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class StmtNode extends Node {

    /**
     * This class may only be sub-classed locally.
     */
    StmtNode(Attribute... attributes) {
        super(attributes);
    }

    /**
     * This class may only be sub-classed locally.
     */
    StmtNode(Collection<? extends Attribute> attributes) {
        super(attributes);
    }

    public abstract void accept(FunctionVisitor fv);

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Assign extends StmtNode {

        private String var;
        private ExprNode expr;

        public Assign(String var, ExprNode expr, Attribute... attributes) {
            this(var, expr, Arrays.asList(attributes));
        }

        public Assign(String var, ExprNode expr, Collection<Attribute> attributes) {
            super(attributes);

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

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Function extends StmtNode {

        private String name;
        private ImmutableList<ExprNode> arguments;

        public Function(String name, List<ExprNode> arguments, Attribute... attributes) {
            this(name, arguments, Arrays.asList(attributes));
        }

        public Function(String name, List<ExprNode> arguments, Collection<Attribute> attributes) {
            super(attributes);

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

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class If extends StmtNode {

        private ExprNode condition;
        private ImmutableList<StmtNode> trueBlock;
        private ImmutableList<StmtNode> falseBlock;

        public If(ExprNode condition, List<StmtNode> trueBlock, List<StmtNode> falseBlock,
                Attribute... attributes) {
            this(condition, trueBlock, falseBlock, Arrays.asList(attributes));
        }

        public If(ExprNode condition, List<StmtNode> trueBlock, List<StmtNode> falseBlock,
                Collection<Attribute> attributes) {
            super(attributes);

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

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    @Alpha
    public static final class Print extends StmtNode {

        private ExprNode expr;

        public Print(ExprNode expr, Attribute... attributes) {
            this(expr, Arrays.asList(attributes));
        }

        public Print(ExprNode expr, Collection<Attribute> attributes) {
            super(attributes);

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

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Return extends StmtNode {

        private Optional<ExprNode> expr;

        public Return(Attribute... attributes) {
            this(Optional.<ExprNode>absent(), Arrays.asList(attributes));
        }

        public Return(Optional<ExprNode> expr, Attribute... attributes) {
            this(expr, Arrays.asList(attributes));
        }

        public Return(ExprNode expr, Attribute... attributes) {
            this(expr, Arrays.asList(attributes));
        }

        public Return(Collection<Attribute> attributes) {
            this(Optional.<ExprNode>absent(), attributes);
        }

        public Return(Optional<ExprNode> expr, Collection<Attribute> attributes) {
            super(attributes);

            setExpr(expr);
        }

        public Return(ExprNode expr, Collection<Attribute> attributes) {
            this(Optional.fromNullable(expr), attributes);
        }

        @Override
        public void accept(FunctionVisitor fv) {
            fv.visitStmtReturn(expr);
        }

        public Optional<ExprNode> getExpr() {
            return expr;
        }

        public void setExpr(Optional<ExprNode> expr) {
            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        public void setExpr(ExprNode expr) {
            setExpr(Optional.fromNullable(expr));
        }
    }
}
