package com.hjwylde.qux.tree;

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

        private Op.Binary op;
        private ExprNode lhs, rhs;

        public Binary(Op.Binary op, ExprNode lhs, ExprNode rhs, Attribute... attributes) {
            this(op, lhs, rhs, Arrays.asList(attributes));
        }

        public Binary(Op.Binary op, ExprNode lhs, ExprNode rhs, Collection<Attribute> attributes) {
            super(attributes);

            setOp(op);
            setLhs(lhs);
            setRhs(rhs);
        }

        public ExprNode getLhs() {
            return lhs;
        }

        public void setLhs(ExprNode lhs) {
            this.lhs = checkNotNull(lhs, "lhs cannot be null");
        }

        public Op.Binary getOp() {
            return op;
        }

        public void setOp(Op.Binary op) {
            this.op = checkNotNull(op, "op cannot be null");
        }

        public ExprNode getRhs() {
            return rhs;
        }

        public void setRhs(ExprNode rhs) {
            this.rhs = checkNotNull(rhs, "rhs cannot be null");
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Constant extends ExprNode {

        private ExprNode.Constant.Type valueType;
        private Object value;

        public Constant(Type type, Object value, Attribute... attributes) {
            this(type, value, Arrays.asList(attributes));
        }

        public Constant(Type type, Object value, Collection<Attribute> attribtues) {
            super(attribtues);

            setValueType(type);
            setValue(value);
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public ExprNode.Constant.Type getValueType() {
            return valueType;
        }

        public void setValueType(ExprNode.Constant.Type valueType) {
            this.valueType = checkNotNull(valueType, "valueType cannot be null");
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

        private String name;
        private ImmutableList<ExprNode> arguments;

        public Function(String name, java.util.List<ExprNode> arguments, Attribute... attributes) {
            this(name, arguments, Arrays.asList(attributes));
        }

        public Function(String name, java.util.List<ExprNode> arguments,
                Collection<Attribute> attributes) {
            super(attributes);

            setName(name);
            setArguments(arguments);
        }

        public ImmutableList<ExprNode> getArguments() {
            return arguments;
        }

        public void setArguments(java.util.List<ExprNode> arguments) {
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
    public static final class List extends ExprNode {

        private ImmutableList<ExprNode> values;

        public List(java.util.List<ExprNode> values, Attribute... attributes) {
            this(values, Arrays.asList(attributes));
        }

        public List(java.util.List<ExprNode> values, Collection<Attribute> attributes) {
            super(attributes);

            setValues(values);
        }

        public ImmutableList<ExprNode> getValues() {
            return values;
        }

        public void setValues(java.util.List<ExprNode> values) {
            this.values = ImmutableList.copyOf(values);
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Unary extends ExprNode {

        private Op.Unary op;
        private ExprNode target;

        public Unary(Op.Unary op, ExprNode target, Attribute... attributes) {
            this(op, target, Arrays.asList(attributes));
        }

        public Unary(Op.Unary op, ExprNode target, Collection<Attribute> attributes) {
            super(attributes);

            setOp(op);
            setTarget(target);
        }

        public Op.Unary getOp() {
            return op;
        }

        public void setOp(Op.Unary op) {
            this.op = checkNotNull(op, "op cannot be null");
        }

        public ExprNode getTarget() {
            return target;
        }

        public void setTarget(ExprNode target) {
            this.target = checkNotNull(target, "target cannot be null");
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     */
    public static final class Variable extends ExprNode {

        private String name;

        public Variable(String name, Attribute... attributes) {
            this(name, Arrays.asList(attributes));
        }

        public Variable(String name, Collection<Attribute> attributes) {
            super(attributes);

            setName(name);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = checkNotNull(name, "name cannot be null");
        }
    }
}
