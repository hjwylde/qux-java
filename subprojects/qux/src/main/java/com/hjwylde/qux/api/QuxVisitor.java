package com.hjwylde.qux.api;

import com.hjwylde.qux.util.Type;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public interface QuxVisitor {

    QuxVisitor NULL_INSTANCE = new NullQuxVisitor();

    void visit(int version, String name);

    void visitEnd();

    FunctionVisitor visitFunction(int flags, String name, Type.Function type);
}
