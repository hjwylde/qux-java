package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.hjwylde.qux.util.Constants.SUPPORTED_VERSIONS;

import com.hjwylde.qux.util.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public class CheckQuxAdapter extends QuxAdapter {

    private boolean visitedStart = false;
    private boolean visitedEnd = false;

    private List<CheckFunctionAdapter> cfas = new ArrayList<>();

    public CheckQuxAdapter(QuxVisitor next) {
        super(next);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(int version, String name) {
        checkState(!visitedStart, "may only call visit(int, String) once");
        checkState(!visitedEnd, "must call visit(int, String) before visitEnd()");
        checkArgument(SUPPORTED_VERSIONS.contains(version), "version %s not supported", version);
        checkNotNull(name, "name cannot be null");

        visitedStart = true;

        super.visit(version, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        checkState(visitedStart, "must call visit(int, String) before visitEnd()");
        checkState(!visitedEnd, "may only call visitEnd() once");

        for (CheckFunctionAdapter cfa : cfas) {
            checkState(cfa.hasVisitedEnd(),
                    "visitEnd() must be called after all function visitors have called visitEnd()");
        }

        visitedEnd = true;

        super.visitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionVisitor visitFunction(int flags, String name, Type.Function type) {
        checkState(visitedStart,
                "must call visit(int, String) before visitFunction(int, String, String)");
        checkState(!visitedEnd, "must call visitFunction(int, String, String) before visitEnd()");
        checkNotNull(name, "name cannot be null");
        checkNotNull(type, "type cannot be null");

        cfas.add(new CheckFunctionAdapter(super.visitFunction(flags, name, type)));

        return cfas.get(cfas.size() - 1);
    }
}
