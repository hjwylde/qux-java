package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.api.StmtVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Op;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    public static final class AccessAssign extends StmtNode {

        private final ExprNode.Access access;
        private final ExprNode expr;

        public AccessAssign(ExprNode.Access access, ExprNode expr, Attribute... attributes) {
            this(access, expr, Arrays.asList(attributes));
        }

        public AccessAssign(ExprNode.Access access, ExprNode expr,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.access = checkNotNull(access, "access cannot be null");
            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(StmtVisitor sv) {
            sv.visitStmtAccessAssign(this);
        }

        public ExprNode.Access getAccess() {
            return access;
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
    public static final class Assign extends StmtNode {

        private final String var;
        private final ExprNode expr;

        public Assign(String var, ExprNode expr, Attribute... attributes) {
            this(var, expr, Arrays.asList(attributes));
        }

        public Assign(String var, ExprNode expr, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.var = checkNotNull(var, "var cannot be null");
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

        public String getVar() {
            return var;
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.1.3
     */
    public static final class Expr extends StmtNode {

        private final StmtNode.Expr.Type type;
        private final ExprNode expr;

        public Expr(StmtNode.Expr.Type type, ExprNode expr, Attribute... attributes) {
            this(type, expr, Arrays.asList(attributes));
        }

        public Expr(StmtNode.Expr.Type type, ExprNode expr,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.type = checkNotNull(type, "type cannot be null");
            this.expr = checkNotNull(expr, "expr cannot be null");

            switch (type) {
                case FUNCTION:
                    checkArgument(expr instanceof ExprNode.Function);
                    break;
                case INCREMENT:
                    checkArgument(expr instanceof ExprNode.Unary);
                    checkArgument(((ExprNode.Unary) expr).getOp() == Op.Unary.INC);
                    break;
                default:
                    throw new MethodNotImplementedError(type.toString());
            }
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

        public Type getType() {
            return type;
        }

        /**
         * TODO: Documentation
         *
         * @author Henry J. Wylde
         * @since 0.1.3
         */
        public static enum Type {
            FUNCTION, INCREMENT;
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.1.2
     */
    public static final class For extends StmtNode {

        private final String var;
        private final ExprNode expr;
        private final ImmutableList<StmtNode> body;

        public For(String var, ExprNode expr, List<StmtNode> body, Attribute... attributes) {
            this(var, expr, body, Arrays.asList(attributes));
        }

        public For(String var, ExprNode expr, List<StmtNode> body,
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

        public String getVar() {
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
            this(Optional.<ExprNode>absent(), Arrays.asList(attributes));
        }

        public Return(Optional<ExprNode> expr, Attribute... attributes) {
            this(expr, Arrays.asList(attributes));
        }

        public Return(ExprNode expr, Attribute... attributes) {
            this(expr, Arrays.asList(attributes));
        }

        public Return(Collection<? extends Attribute> attributes) {
            this(Optional.<ExprNode>absent(), attributes);
        }

        public Return(Optional<ExprNode> expr, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        public Return(ExprNode expr, Collection<? extends Attribute> attributes) {
            this(Optional.fromNullable(expr), attributes);
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
