package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Type;

import java.util.List;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
public abstract class QuxAdapter implements QuxVisitor {

    private final QuxVisitor next;

    public QuxAdapter() {
        this(QuxVisitor.NULL_INSTANCE);
    }

    public QuxAdapter(QuxVisitor next) {
        this.next = checkNotNull(next, "next cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(int version, Identifier name) {
        next.visit(version, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstantVisitor visitConstant(int flags, Identifier name, Type type) {
        return next.visitConstant(flags, name, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        next.visitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionVisitor visitFunction(int flags, Identifier name, Type.Function type) {
        return next.visitFunction(flags, name, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitPackage(List<Identifier> pkg) {
        next.visitPackage(pkg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeVisitor visitType(int flags, Identifier name) {
        return next.visitType(flags, name);
    }
}
