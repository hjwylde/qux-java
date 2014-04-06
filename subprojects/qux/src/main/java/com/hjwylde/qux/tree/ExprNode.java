package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Op;

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collection;

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

    // TODO: Consider adding in an accept(ExprVisitor) method

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

        public Binary(Op.Binary op, ExprNode lhs, ExprNode rhs, Collection<Attribute> attributes) {
            super(attributes);

            this.op = checkNotNull(op, "op cannot be null");
            this.lhs = checkNotNull(lhs, "lhs cannot be null");
            this.rhs = checkNotNull(rhs, "rhs cannot be null");
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

        private final ExprNode.Constant.Type type;
        private final Object value;

        public Constant(Type type, Object value, Attribute... attributes) {
            this(type, value, Arrays.asList(attributes));
        }

        public Constant(Type type, Object value, Collection<Attribute> attribtues) {
            super(attribtues);

            checkArgument(value != null || type == Type.NULL,
                    "value cannot be null unless type is null");

            this.type = checkNotNull(type, "type cannot be null");
            this.value = value;
        }

        public ExprNode.Constant.Type getType() {
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
            BOOL, INT, NULL, REAL, STR;
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
                Collection<Attribute> attributes) {
            super(attributes);

            this.name = checkNotNull(name, "name cannot be null");
            this.arguments = ImmutableList.copyOf(arguments);
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

        public List(java.util.List<ExprNode> values, Collection<Attribute> attributes) {
            super(attributes);

            this.values = ImmutableList.copyOf(values);
        }

        public ImmutableList<ExprNode> getValues() {
            return values;
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

        public Unary(Op.Unary op, ExprNode target, Collection<Attribute> attributes) {
            super(attributes);

            this.op = checkNotNull(op, "op cannot be null");
            this.target = checkNotNull(target, "target cannot be null");
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

        public Variable(String name, Collection<Attribute> attributes) {
            super(attributes);

            this.name = checkNotNull(name, "name cannot be null");
        }

        public String getName() {
            return name;
        }
    }
}
