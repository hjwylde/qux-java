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
    private boolean visitedPackage = false;
    private boolean visitedEnd = false;

    private List<CheckConstantAdapter> ccas = new ArrayList<>();
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
        checkState(!visitedPackage, "must call visit(int, String) before visitPackage(String)");
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
    public ConstantVisitor visitConstant(int flags, String name, Type type) {
        checkState(visitedStart, "must call visit(int, String) before visitConstant(int, String)");
        checkState(visitedPackage,
                "must call visitPackage(String) before visitConstant(int, String)");
        checkState(!visitedEnd, "must call visitConstant(int, String) before visitEnd()");
        checkNotNull(name, "name cannot be null");
        checkNotNull(type, "type cannot be null");

        ccas.add(new CheckConstantAdapter(super.visitConstant(flags, name, type)));

        return ccas.get(ccas.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        checkState(visitedStart, "must call visit(int, String) before visitEnd()");
        checkState(visitedPackage, "must call visitPackage(String) before visitEnd()");
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
                "must call visit(int, String) before visitFunction(int, String, Type.Function)");
        checkState(visitedPackage,
                "must call visitPackage(String) before visitFunction(int, String, Type.Function)");
        checkState(!visitedEnd,
                "must call visitFunction(int, String, Type.Function) before visitEnd()");
        checkNotNull(name, "name cannot be null");
        checkNotNull(type, "type cannot be null");

        cfas.add(new CheckFunctionAdapter(super.visitFunction(flags, name, type)));

        return cfas.get(cfas.size() - 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitImport(String id) {
        checkState(visitedStart, "must call visit(int, String) before visitImport(String)");
        checkState(visitedPackage, "must call visitPackage(String) before visitImport(String)");
        checkState(!visitedEnd, "must call visitImport(String) before visitEnd()");
        checkNotNull(id, "id cannot be null");

        super.visitImport(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitPackage(String pkg) {
        checkState(visitedStart, "must call visit(int, String) before visitPackage(String)");
        checkState(!visitedPackage, "may only call visitPackage(String) once");
        checkState(!visitedEnd, "must call visitPackage(String) before visitEnd()");

        visitedPackage = true;

        super.visitPackage(pkg);
    }
}
