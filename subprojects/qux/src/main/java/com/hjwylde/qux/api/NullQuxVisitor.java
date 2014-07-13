package com.hjwylde.qux.api;

import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Type;

import java.util.List;

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
    public void visit(int version, Identifier name) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstantVisitor visitConstant(int flags, Identifier name, Type type) {
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
    public FunctionVisitor visitFunction(int flags, Identifier name, Type.Function type) {
        return FunctionVisitor.NULL_INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitPackage(List<Identifier> pkg) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeVisitor visitType(int flags, Identifier name) {
        return TypeVisitor.NULL_INSTANCE;
    }
}
