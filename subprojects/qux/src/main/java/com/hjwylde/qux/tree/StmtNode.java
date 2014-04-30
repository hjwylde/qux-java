package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.common.lang.annotation.Beta;
import com.hjwylde.qux.api.StmtVisitor;
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

    public abstract void accept(StmtVisitor sv);

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    @Beta
    public static final class Assign extends StmtNode {

        private final String var;
        private final ExprNode expr;

        public Assign(String var, ExprNode expr, Attribute... attributes) {
            this(var, expr, Arrays.asList(attributes));
        }

        public Assign(String var, ExprNode expr, Collection<Attribute> attributes) {
            super(attributes);

            this.var = checkNotNull(var, "var cannot be null");
            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtAssign(this);
        }

        public ExprNode getExpr() {
            return expr;
        }

        public String getVar() {
            return var;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Function extends StmtNode {

        private final String name;
        private final ImmutableList<ExprNode> arguments;

        public Function(String name, List<ExprNode> arguments, Attribute... attributes) {
            this(name, arguments, Arrays.asList(attributes));
        }

        public Function(String name, List<ExprNode> arguments, Collection<Attribute> attributes) {
            super(attributes);

            this.name = checkNotNull(name, "name cannot be null");
            this.arguments = ImmutableList.copyOf(arguments);
        }

        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtFunction(this);
        }

        public ImmutableList<ExprNode> getArguments() {
            return arguments;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class If extends StmtNode {

        private final ExprNode condition;
        private final ImmutableList<StmtNode> trueBlock;
        private final ImmutableList<StmtNode> falseBlock;

        public If(ExprNode condition, List<StmtNode> trueBlock, List<StmtNode> falseBlock,
                Attribute... attributes) {
            this(condition, trueBlock, falseBlock, Arrays.asList(attributes));
        }

        public If(ExprNode condition, List<StmtNode> trueBlock, List<StmtNode> falseBlock,
                Collection<Attribute> attributes) {
            super(attributes);

            this.condition = checkNotNull(condition, "condition cannot be null");
            this.trueBlock = ImmutableList.copyOf(trueBlock);
            this.falseBlock = ImmutableList.copyOf(falseBlock);
        }

        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtIf(this);
        }

        public ExprNode getCondition() {
            return condition;
        }

        public ImmutableList<StmtNode> getFalseBlock() {
            return falseBlock;
        }

        public ImmutableList<StmtNode> getTrueBlock() {
            return trueBlock;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    @Alpha
    public static final class Print extends StmtNode {

        private final ExprNode expr;

        public Print(ExprNode expr, Attribute... attributes) {
            this(expr, Arrays.asList(attributes));
        }

        public Print(ExprNode expr, Collection<Attribute> attributes) {
            super(attributes);

            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtPrint(this);
        }

        public ExprNode getExpr() {
            return expr;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Return extends StmtNode {

        private final Optional<ExprNode> expr;

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

            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        public Return(ExprNode expr, Collection<Attribute> attributes) {
            this(Optional.fromNullable(expr), attributes);
        }

        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtReturn(this);
        }

        public Optional<ExprNode> getExpr() {
            return expr;
        }
    }
}
