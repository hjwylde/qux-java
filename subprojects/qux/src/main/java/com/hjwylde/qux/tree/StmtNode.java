package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.api.StmtVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Op;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

/**
 * TODO: Documentation.
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
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Assign extends StmtNode {

        private final Type type;

        private final ExprNode lhs;
        private final ExprNode expr;

        public Assign(Type type, ExprNode lhs, ExprNode expr, Attribute... attributes) {
            this(type, lhs, expr, Arrays.asList(attributes));
        }

        public Assign(Type type, ExprNode lhs, ExprNode expr,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            checkArgument(type != Type.ACCESS || lhs instanceof ExprNode.Binary,
                    "lhs must be of class ExprNode.Binary for access assignment");
            checkArgument(type != Type.ACCESS || ((ExprNode.Binary) lhs).getOp() == Op.Binary.ACC,
                    "lhs must be of class ExprNode.Binary with ACC operator for access assignment");
            checkArgument(type != Type.RECORD_ACCESS || lhs instanceof ExprNode.RecordAccess,
                    "lhs must be of class ExprNode.Binary for access assignment");
            checkArgument(type != Type.VARIABLE || lhs instanceof ExprNode.Variable,
                    "lhs must be of class ExprNode.Variable for variable assignment");

            this.type = checkNotNull(type, "type cannot be null");

            this.lhs = checkNotNull(lhs, "lhs cannot be null");
            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtAssign(this);
        }

        public ExprNode getExpr() {
            return expr;
        }

        public ExprNode getLhs() {
            return lhs;
        }

        public Type getType() {
            return type;
        }

        /**
         * TODO: Documentation
         *
         * @author Henry J. Wylde
         * @since 0.2.4
         */
        public static enum Type {
            ACCESS, RECORD_ACCESS, VARIABLE;
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.1.3
     */
    public static final class Expr extends StmtNode {

        private final ExprNode expr;

        public Expr(ExprNode expr, Attribute... attributes) {
            this(expr, Arrays.asList(attributes));
        }

        public Expr(ExprNode expr, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtExpr(this);
        }

        public ExprNode getExpr() {
            return expr;
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.1.2
     */
    public static final class For extends StmtNode {

        private final Identifier var;
        private final ExprNode expr;
        private final ImmutableList<StmtNode> body;

        public For(Identifier var, ExprNode expr, List<StmtNode> body, Attribute... attributes) {
            this(var, expr, body, Arrays.asList(attributes));
        }

        public For(Identifier var, ExprNode expr, List<StmtNode> body,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.var = checkNotNull(var, "var cannot be null");
            this.expr = checkNotNull(expr, "expr cannot be null");
            this.body = ImmutableList.copyOf(body);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtFor(this);
        }

        public ImmutableList<StmtNode> getBody() {
            return body;
        }

        public ExprNode getExpr() {
            return expr;
        }

        public Identifier getVar() {
            return var;
        }
    }

    /**
     * TODO: Documentation.
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
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.condition = checkNotNull(condition, "condition cannot be null");
            this.trueBlock = ImmutableList.copyOf(trueBlock);
            this.falseBlock = ImmutableList.copyOf(falseBlock);
        }

        /**
         * {@inheritDoc}
         */
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
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    @Alpha
    public static final class Print extends StmtNode {

        private final ExprNode expr;

        public Print(ExprNode expr, Attribute... attributes) {
            this(expr, Arrays.asList(attributes));
        }

        public Print(ExprNode expr, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtPrint(this);
        }

        public ExprNode getExpr() {
            return expr;
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    public static final class Return extends StmtNode {

        private final Optional<ExprNode> expr;

        public Return(Attribute... attributes) {
            this(null, Arrays.asList(attributes));
        }

        public Return(@Nullable ExprNode expr, Attribute... attributes) {
            this(expr, Arrays.asList(attributes));
        }

        public Return(Collection<? extends Attribute> attributes) {
            this(null, attributes);
        }

        public Return(@Nullable ExprNode expr, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.expr = Optional.fromNullable(expr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtReturn(this);
        }

        public Optional<ExprNode> getExpr() {
            return expr;
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.1.3
     */
    public static final class While extends StmtNode {

        private final ExprNode condition;
        private final ImmutableList<StmtNode> body;

        public While(ExprNode condition, List<StmtNode> body, Attribute... attributes) {
            this(condition, body, Arrays.asList(attributes));
        }

        public While(ExprNode condition, List<StmtNode> body,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.condition = checkNotNull(condition, "condition cannot be null");
            this.body = ImmutableList.copyOf(body);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtWhile(this);
        }

        public ImmutableList<StmtNode> getBody() {
            return body;
        }

        public ExprNode getCondition() {
            return condition;
        }
    }
}
