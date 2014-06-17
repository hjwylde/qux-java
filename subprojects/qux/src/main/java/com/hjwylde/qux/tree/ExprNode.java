package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Op;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class ExprNode extends Node {

    /**
     * This class may only be sub-classed locally.
     */
    ExprNode(Attribute... attributes) {
        super(attributes);
    }

    /**
     * This class may only be sub-classed locally.
     */
    ExprNode(Collection<? extends Attribute> attributes) {
        super(attributes);
    }

    public abstract void accept(ExprVisitor ev);

    /**
     * TODO: Documentation
     * <p/>
     * TODO: Consider turning this into a binary operation
     *
     * @author Henry J. Wylde
     * @since 0.1.3
     */
    public static final class Access extends ExprNode {

        private final ExprNode target;
        private final ExprNode index;

        public Access(ExprNode target, ExprNode index, Attribute... attributes) {
            this(target, index, Arrays.asList(attributes));
        }

        public Access(ExprNode target, ExprNode index, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.target = checkNotNull(target, "target cannot be null");
            this.index = checkNotNull(index, "index cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprAccess(this);
        }

        public ExprNode getIndex() {
            return index;
        }

        public ExprNode getTarget() {
            return target;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Binary extends ExprNode {

        private final Op.Binary op;
        private final ExprNode lhs, rhs;

        public Binary(Op.Binary op, ExprNode lhs, ExprNode rhs, Attribute... attributes) {
            this(op, lhs, rhs, Arrays.asList(attributes));
        }

        public Binary(Op.Binary op, ExprNode lhs, ExprNode rhs,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.op = checkNotNull(op, "op cannot be null");
            this.lhs = checkNotNull(lhs, "lhs cannot be null");
            this.rhs = checkNotNull(rhs, "rhs cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprBinary(this);
        }

        public ExprNode getLhs() {
            return lhs;
        }

        public Op.Binary getOp() {
            return op;
        }

        public ExprNode getRhs() {
            return rhs;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Constant extends ExprNode {

        private final Type type;

        private final Object value;

        public Constant(Type type, Object value, Attribute... attributes) {
            this(type, value, Arrays.asList(attributes));
        }

        public Constant(Type type, Object value, Collection<? extends Attribute> attributes) {
            super(attributes);

            checkArgument(type != Type.BOOL || value instanceof Boolean,
                    "value must be of class Boolean for bool constant");
            checkArgument(type != Type.INT || value instanceof BigInteger,
                    "value must be of class BigInteger for int constant");
            checkArgument(type != Type.NULL || value == null,
                    "value must be null for null constant");
            checkArgument(type != Type.OBJ || value instanceof String,
                    "value must be of class String for obj constant");
            checkArgument(type != Type.REAL || value instanceof BigDecimal,
                    "value must be of class BigDecimal for real constant");
            checkArgument(type != Type.STR || value instanceof String,
                    "value must be of class String for str constant");

            this.type = checkNotNull(type, "type cannot be null");

            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprConstant(this);
        }

        public Type getType() {
            return type;
        }

        public Object getValue() {
            return value;
        }

        /**
         * TODO: Documentation
         *
         * @author Henry J. Wylde
         */
        public static enum Type {
            BOOL, INT, NULL, OBJ, REAL, STR;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.1
     */
    public static final class External extends ExprNode {

        private final Type type;

        private final Meta meta;
        private final ExprNode expr;

        public External(Type type, Meta meta, ExprNode expr, Attribute... attributes) {
            this(type, meta, expr, Arrays.asList(attributes));
        }

        public External(Type type, Meta meta, ExprNode expr,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            checkArgument(expr instanceof Function || expr instanceof Variable,
                    "expr must be a function or variable");

            checkArgument(type != Type.CONSTANT || expr instanceof Variable,
                    "expr must be of class Variable for external constant");
            checkArgument(type != Type.FUNCTION || expr instanceof Function,
                    "expr must be of class Function for external function");

            this.type = checkNotNull(type, "type cannot be null");

            this.meta = checkNotNull(meta, "meta cannot be null");
            this.expr = checkNotNull(expr, "expr cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprExternal(this);
        }

        public ExprNode getExpr() {
            return expr;
        }

        public Meta getMeta() {
            return meta;
        }

        public Type getType() {
            return type;
        }

        /**
         * TODO: Documentation
         *
         * @author Henry J. Wylde
         * @since TODO: SINCE
         */
        public static enum Type {
            CONSTANT, FUNCTION;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Function extends ExprNode {

        private final String name;
        private final ImmutableList<ExprNode> arguments;

        public Function(String name, java.util.List<ExprNode> arguments, Attribute... attributes) {
            this(name, arguments, Arrays.asList(attributes));
        }

        public Function(String name, java.util.List<ExprNode> arguments,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.name = checkNotNull(name, "name cannot be null");
            this.arguments = ImmutableList.copyOf(arguments);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprFunction(this);
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
    public static final class List extends ExprNode {

        private final ImmutableList<ExprNode> values;

        public List(java.util.List<ExprNode> values, Attribute... attributes) {
            this(values, Arrays.asList(attributes));
        }

        public List(java.util.List<ExprNode> values, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.values = ImmutableList.copyOf(values);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprList(this);
        }

        public ImmutableList<ExprNode> getValues() {
            return values;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.1
     */
    public static final class Meta extends ExprNode {

        private final String id;

        public Meta(String id, Attribute... attributes) {
            this(id, Arrays.asList(attributes));
        }

        public Meta(String id, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.id = checkNotNull(id, "id cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprMeta(this);
        }

        public String getId() {
            return id;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since TODO: SINCE
     */
    public static final class Record extends ExprNode {

        private final ImmutableMap<String, ExprNode> fields;

        public Record(Map<String, ExprNode> fields, Attribute... attributes) {
            this(fields, Arrays.asList(attributes));
        }

        public Record(Map<String, ExprNode> fields, Collection<? extends Attribute> attributes) {
            super(attributes);

            checkArgument(!fields.isEmpty(), "fields must contain at least 1 element");

            this.fields = ImmutableMap.copyOf(fields);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprRecord(this);
        }

        public ImmutableMap<String, ExprNode> getFields() {
            return fields;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since TODO: SINCE
     */
    public static final class RecordAccess extends ExprNode {

        private final ExprNode target;
        private final String field;

        public RecordAccess(ExprNode target, String field, Attribute... attributes) {
            this(target, field, Arrays.asList(attributes));
        }

        public RecordAccess(ExprNode target, String field,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.target = checkNotNull(target, "target cannot be null");
            this.field = checkNotNull(field, "field cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprRecordAccess(this);
        }

        public String getField() {
            return field;
        }

        public ExprNode getTarget() {
            return target;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.1.3
     */
    public static final class Set extends ExprNode {

        private final ImmutableList<ExprNode> values;

        public Set(java.util.List<ExprNode> values, Attribute... attributes) {
            this(values, Arrays.asList(attributes));
        }

        public Set(java.util.List<ExprNode> values, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.values = ImmutableList.copyOf(values);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprSet(this);
        }

        public ImmutableList<ExprNode> getValues() {
            return values;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.1.3
     */
    public static final class Slice extends ExprNode {

        private final ExprNode target;
        private final Optional<ExprNode> from;
        private final Optional<ExprNode> to;

        public Slice(ExprNode target, @Nullable ExprNode from, @Nullable ExprNode to,
                Attribute... attributes) {
            this(target, from, to, Arrays.asList(attributes));
        }

        public Slice(ExprNode target, @Nullable ExprNode from, @Nullable ExprNode to,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.target = checkNotNull(target, "target cannot be null");
            this.from = Optional.fromNullable(from);
            this.to = Optional.fromNullable(to);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprSlice(this);
        }

        public Optional<ExprNode> getFrom() {
            return from;
        }

        public ExprNode getTarget() {
            return target;
        }

        public Optional<ExprNode> getTo() {
            return to;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Unary extends ExprNode {

        private final Op.Unary op;
        private final ExprNode target;

        public Unary(Op.Unary op, ExprNode target, Attribute... attributes) {
            this(op, target, Arrays.asList(attributes));
        }

        public Unary(Op.Unary op, ExprNode target, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.op = checkNotNull(op, "op cannot be null");
            this.target = checkNotNull(target, "target cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprUnary(this);
        }

        public Op.Unary getOp() {
            return op;
        }

        public ExprNode getTarget() {
            return target;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Variable extends ExprNode {

        private final String name;

        public Variable(String name, Attribute... attributes) {
            this(name, Arrays.asList(attributes));
        }

        public Variable(String name, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.name = checkNotNull(name, "name cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprVariable(this);
        }

        public String getName() {
            return name;
        }
    }
}
