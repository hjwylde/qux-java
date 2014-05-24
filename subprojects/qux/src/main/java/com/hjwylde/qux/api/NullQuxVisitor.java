package com.hjwylde.qux.api;

import com.hjwylde.qux.util.Type;

import javax.annotation.Nullable;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
final class NullQuxVisitor implements QuxVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(int version, String name) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionVisitor visitFunction(int flags, String name, Type.Function type) {
        return FunctionVisitor.NULL_INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitImport(String id) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitPackage(@Nullable String pkg) {}
}
