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
    public ConstantVisitor visitConstant(int flags, String name, Type type) {
        return ConstantVisitor.NULL_INSTANCE;
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
    public FunctionVisitor visitFunction(int flags, String name, Type.Function type) {
        return FunctionVisitor.NULL_INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitPackage(@Nullable String pkg) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeVisitor visitType(int flags, String name) {
        return TypeVisitor.NULL_INSTANCE;
    }
}
