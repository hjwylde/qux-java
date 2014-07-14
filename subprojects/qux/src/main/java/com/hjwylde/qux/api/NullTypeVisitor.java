package com.hjwylde.qux.api;

import com.hjwylde.qux.util.Type;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.4
 */
final class NullTypeVisitor implements TypeVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitType(Type type) {}
}
