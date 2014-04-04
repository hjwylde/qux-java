package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.util.Op;
import com.hjwylde.qux.util.Type;

import com.google.common.collect.ImmutableList;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class ExprNode {

    // TODO: Make these immutable

    private Type type = null;

    /**
     * This class may only be sub-classed locally.
     */
    ExprNode() {}

    /**
     * Gets the type of this expression. The type is initially unresolved and needs to be set using
     * {@link #setType}.
     *
     * @return the type.
     * @throws java.lang.IllegalStateException if the type has not been resolved.
     */
    public final Type getType() {
        checkState(type != null, "type has not been resolved");

        return type;
    }

    /**
     * Sets the type of this expression. If this expressions type has already been set (resolved),
     * then this method will throw an error.
     *
     * @param type the type.
     * @throws java.lang.IllegalStateException if the type has already been resolved.
     */
    public final void setType(Type type) {
        checkState(this.type == null, "type has already been resolved");

        this.type = checkNotNull(type, "type cannot be null");
    }

    public final boolean isTypeResolved() {
        return type != null;
    }

    public static final class Binary extends ExprNode {

        private Op.Binary op;
        private ExprNode lhs, rhs;

        public Binary(Op.Binary op, ExprNode lhs, ExprNode rhs) {
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

    public static final class Constant extends ExprNode {

        private ExprNode.Constant.Type valueType;
        private Object value;

        public Constant(Type type, Object value) {
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

        public static enum Type {
            BOOL, INT, NULL, REAL, STR;
        }
    }

    public static final class Function extends ExprNode {

        private String name;
        private ImmutableList<ExprNode> arguments;

        public Function(String name, java.util.List<ExprNode> arguments) {
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

    public static final class List extends ExprNode {

        private ImmutableList<ExprNode> values;

        public List(java.util.List<ExprNode> values) {
            setValues(values);
        }

        public ImmutableList<ExprNode> getValues() {
            return values;
        }

        public void setValues(java.util.List<ExprNode> values) {
            this.values = ImmutableList.copyOf(values);
        }
    }

    public static final class Unary extends ExprNode {

        private Op.Unary op;
        private ExprNode target;

        public Unary(Op.Unary op, ExprNode target) {
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

    public static final class Variable extends ExprNode {

        private String name;

        public Variable(String name) {
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
