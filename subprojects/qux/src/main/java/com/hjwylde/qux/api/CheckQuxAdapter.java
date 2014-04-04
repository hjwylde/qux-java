package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.hjwylde.qux.util.Constants.SUPPORTED_VERSIONS;

import javax.annotation.Nullable;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public class CheckQuxAdapter extends QuxVisitor {

    private boolean visitedStart = false;
    private boolean visitedEnd = false;

    public CheckQuxAdapter(@Nullable QuxVisitor next) {
        super(next);
    }

    @Override
    public void visit(int version, String name) {
        checkState(!visitedStart, "may only call visit(int, String) once");
        checkState(!visitedEnd, "must call visit(int, String) before visitEnd()");
        checkArgument(SUPPORTED_VERSIONS.contains(version), "version %d not supported", version);
        checkNotNull(name, "name cannot be null");

        visitedStart = true;

        super.visit(version, name);
    }

    @Override
    public void visitEnd() {
        checkState(visitedStart, "must call visit(int, String) before visitEnd()");
        checkState(!visitedEnd, "may only call visitEnd() once");

        visitedEnd = true;

        super.visitEnd();
    }

    @Override
    public FunctionVisitor visitFunction(int flags, String name, String desc) {
        checkState(visitedStart,
                "must call visit(int, String) before visitFunction(int, String, String)");
        checkState(!visitedEnd, "must call visitFunction(int, String, String) before visitEnd()");
        checkNotNull(name, "name cannot be null");
        checkNotNull(desc, "desc cannot be null");

        return new CheckFunctionAdapter(super.visitFunction(flags, name, desc));
    }
}
