package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Op;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

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
     *
     * @author Henry J. Wylde
     */
    public static final class Binary extends ExprNode {

        private final Op.Binary op;
        private final ExprNode lhs, rhs;

        public Binary(Op.Binary op, ExprNode lhs, ExprNode rhs, Attribute... attributes) {
            this(op, lhs, rhs, asList(attributes));
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
     * @since 0.2.6
     */
    public static final class Constant extends ExprNode {

        private final Meta owner;

        private final Identifier name;

        public Constant(Meta owner, Identifier name, Attribute... attributes) {
            this(owner, name, asList(attributes));
        }

        public Constant(Meta owner, Identifier name, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.owner = checkNotNull(owner, "owner cannot be null");

            this.name = checkNotNull(name, "name cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprConstant(this);
        }

        public Identifier getName() {
            return name;
        }

        public Meta getOwner() {
            return owner;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Function extends ExprNode {

        private final Meta owner;

        private final Identifier name;
        private final ImmutableList<ExprNode> arguments;

        private final boolean isMethodCall;

        public Function(Meta owner, Identifier name, java.util.List<ExprNode> arguments,
                boolean isMethodCall, Attribute... attributes) {
            this(owner, name, arguments, isMethodCall, asList(attributes));
        }

        public Function(Meta owner, Identifier name, java.util.List<ExprNode> arguments,
                boolean isMethodCall, Collection<? extends Attribute> attributes) {
            super(attributes);

            this.owner = checkNotNull(owner, "owner cannot be null");

            this.name = checkNotNull(name, "name cannot be null");
            this.arguments = ImmutableList.copyOf(arguments);

            this.isMethodCall = isMethodCall;
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

        public Identifier getName() {
            return name;
        }

        public Meta getOwner() {
            return owner;
        }

        public boolean isMethodCall() {
            return isMethodCall;
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
            this(values, asList(attributes));
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

        private final ImmutableList<Identifier> id;

        public Meta(java.util.List<Identifier> id, Attribute... attributes) {
            this(id, asList(attributes));
        }

        public Meta(java.util.List<Identifier> id, Collection<? extends Attribute> attributes) {
            super(attributes);

            checkArgument(id.size() > 1, "id must have 2 or more elements");

            this.id = ImmutableList.copyOf(id);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(ExprVisitor ev) {
            ev.visitExprMeta(this);
        }

        public ImmutableList<Identifier> getId() {
            return id;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.4
     */
    public static final class Record extends ExprNode {

        private final ImmutableMap<Identifier, ExprNode> fields;

        public Record(Map<Identifier, ExprNode> fields, Attribute... attributes) {
            this(fields, asList(attributes));
        }

        public Record(Map<Identifier, ExprNode> fields,
                Collection<? extends Attribute> attributes) {
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

        public ImmutableMap<Identifier, ExprNode> getFields() {
            return fields;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.4
     */
    public static final class RecordAccess extends ExprNode {

        private final ExprNode target;
        private final Identifier field;

        public RecordAccess(ExprNode target, Identifier field, Attribute... attributes) {
            this(target, field, asList(attributes));
        }

        public RecordAccess(ExprNode target, Identifier field,
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

        public Identifier getField() {
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
            this(values, asList(attributes));
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
            this(target, from, to, asList(attributes));
        }

        public Slice(ExprNode target, @Nullable ExprNode from, @Nullable ExprNode to,
                Collection<? extends Attribute> attributes) {
            super(attributes);

            this.target = checkNotNull(target, "target cannot be null");
            this.from = Optional.ofNullable(from);
            this.to = Optional.ofNullable(to);
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
            this(op, target, asList(attributes));
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
    public static final class Value extends ExprNode {

        private final Type type;

        private final Object value;

        public Value(Type type, Object value, Attribute... attributes) {
            this(type, value, asList(attributes));
        }

        public Value(Type type, Object value, Collection<? extends Attribute> attributes) {
            super(attributes);

            checkArgument(type != Type.BOOL || value instanceof Boolean,
                    "value must be of class Boolean for bool constant");
            checkArgument(type != Type.INT || value instanceof BigInteger,
                    "value must be of class BigInteger for int constant");
            checkArgument(type != Type.NULL || value == null,
                    "value must be null for null constant");
            checkArgument(type != Type.OBJ || value instanceof String,
                    "value must be of class String for obj constant");
            checkArgument(type != Type.RAT || value instanceof BigDecimal,
                    "value must be of class BigDecimal for rat constant");
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
            ev.visitExprValue(this);
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
            BOOL, INT, NULL, OBJ, RAT, STR
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Variable extends ExprNode {

        private final Identifier name;

        public Variable(Identifier name, Attribute... attributes) {
            this(name, asList(attributes));
        }

        public Variable(Identifier name, Collection<? extends Attribute> attributes) {
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

        public Identifier getName() {
            return name;
        }
    }
}
