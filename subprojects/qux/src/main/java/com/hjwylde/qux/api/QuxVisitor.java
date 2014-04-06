package com.hjwylde.qux.api;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public interface QuxVisitor {

    QuxVisitor NULL_INSTANCE = new NullQuxVisitor();

    void visit(int version, String name);

    void visitEnd();

    FunctionVisitor visitFunction(int flags, String name, String desc);
}
