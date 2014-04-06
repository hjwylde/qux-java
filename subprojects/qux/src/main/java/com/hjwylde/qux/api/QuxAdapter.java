package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.1
 */
public abstract class QuxAdapter implements QuxVisitor {

    private final QuxVisitor next;

    public QuxAdapter() {
        this(QuxVisitor.NULL_INSTANCE);
    }

    public QuxAdapter(QuxVisitor next) {
        this.next = checkNotNull(next, "next cannot be null");
    }

    @Override
    public void visit(int version, String name) {
        next.visit(version, name);
    }

    @Override
    public void visitEnd() {
        next.visitEnd();
    }

    @Override
    public FunctionVisitor visitFunction(int flags, String name, String desc) {
        return next.visitFunction(flags, name, desc);
    }
}
