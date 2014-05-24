package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.util.Type;

import javax.annotation.Nullable;

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
    public void visit(int version, String name) {
        next.visit(version, name);
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
    public FunctionVisitor visitFunction(int flags, String name, Type.Function type) {
        return next.visitFunction(flags, name, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitImport(String id) {
        next.visitImport(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitPackage(@Nullable String pkg) {
        next.visitPackage(pkg);
    }
}
