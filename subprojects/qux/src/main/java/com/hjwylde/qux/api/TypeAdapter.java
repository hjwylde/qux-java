package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.util.Type;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public abstract class TypeAdapter implements TypeVisitor {

    private final TypeVisitor next;

    public TypeAdapter() {
        this(TypeVisitor.NULL_INSTANCE);
    }

    public TypeAdapter(TypeVisitor next) {
        this.next = checkNotNull(next, "next cannot be null");
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
    public void visitType(Type type) {
        next.visitType(type);
    }
}
