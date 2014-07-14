package com.hjwylde.qux.api;

import com.hjwylde.qux.util.Type;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.4
 */
public interface TypeVisitor {

    TypeVisitor NULL_INSTANCE = new NullTypeVisitor();

    void visitEnd();

    void visitType(Type type);
}
