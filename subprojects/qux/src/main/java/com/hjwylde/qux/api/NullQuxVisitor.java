package com.hjwylde.qux.api;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
final class NullQuxVisitor implements QuxVisitor {

    @Override
    public void visit(int version, String name) {}

    @Override
    public void visitEnd() {}

    @Override
    public FunctionVisitor visitFunction(int flags, String name, String desc) {
        return FunctionVisitor.NULL_INSTANCE;
    }
}
