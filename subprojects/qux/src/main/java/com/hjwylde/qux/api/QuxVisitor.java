package com.hjwylde.qux.api;

import javax.annotation.Nullable;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public abstract class QuxVisitor {

    public static final QuxVisitor NULL_INSTANCE = new NullQuxVisitor();

    private final QuxVisitor next;

    public QuxVisitor() {
        this(null);
    }

    public QuxVisitor(@Nullable QuxVisitor next) {
        this.next = next;
    }

    public void visit(int version, String name) {
        if (next != null) {
            next.visit(version, name);
        }
    }

    public void visitEnd() {
        if (next != null) {
            next.visitEnd();
        }
    }

    public FunctionVisitor visitFunction(int flags, String name, String desc) {
        if (next != null) {
            return next.visitFunction(flags, name, desc);
        }

        return FunctionVisitor.NULL_INSTANCE;
    }

    private static final class NullQuxVisitor extends QuxVisitor {

        @Override
        public FunctionVisitor visitFunction(int flags, String name, String desc) {
            return FunctionVisitor.NULL_INSTANCE;
        }
    }
}
