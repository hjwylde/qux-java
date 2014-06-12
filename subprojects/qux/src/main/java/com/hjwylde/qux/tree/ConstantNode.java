package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.api.ConstantVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Type;

import java.util.Arrays;
import java.util.Collection;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public final class ConstantNode extends Node implements ConstantVisitor {

    private final int flags;
    private final String name;
    private final Type type;

    private ExprNode expr;

    public ConstantNode(int flags, String name, Type type, Attribute... attributes) {
        this(flags, name, type, Arrays.asList(attributes));
    }

    public ConstantNode(int flags, String name, Type type,
            Collection<? extends Attribute> attributes) {
        super(attributes);

        this.flags = flags;
        this.name = checkNotNull(name, "name cannot be null");
        this.type = checkNotNull(type, "type cannot be null");
    }

    public void accept(QuxVisitor qv) {
        ConstantVisitor cv = qv.visitConstant(flags, name, type);

        accept(cv);

        cv.visitEnd();
    }

    public void accept(ConstantVisitor cv) {
        cv.visitExpr(expr);
    }

    public ExprNode getExpr() {
        checkState(expr != null, "expr has not been set");

        return expr;
    }

    public int getFlags() {
        return flags;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitExpr(ExprNode expr) {
        this.expr = checkNotNull(expr, "expr cannot be null");
    }
}
