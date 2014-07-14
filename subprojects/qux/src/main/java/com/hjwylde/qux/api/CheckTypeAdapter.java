package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.util.Type;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.4
 */
public class CheckTypeAdapter extends TypeAdapter {

    private boolean visitedType = false;
    private boolean visitedEnd = false;

    public CheckTypeAdapter(TypeVisitor next) {
        super(next);
    }

    public final boolean hasVisitedEnd() {
        return visitedEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        checkState(visitedType, "must call visitType(Type) before visitEnd()");
        checkState(!visitedEnd, "may only call visitEnd() once");

        visitedEnd = true;

        super.visitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitType(Type type) {
        checkState(!visitedType, "may only call visitType(Type) once");
        checkState(!visitedEnd, "must call visitType(Type) before visitEnd()");
        checkNotNull(type, "type cannot be null");

        visitedType = true;

        super.visitType(type);
    }
}
