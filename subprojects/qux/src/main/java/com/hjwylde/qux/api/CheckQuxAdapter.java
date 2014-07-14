package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Type;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public class CheckQuxAdapter extends QuxAdapter {

    private boolean visitedStart;
    private boolean visitedPackage;
    private boolean visitedEnd;

    private List<CheckConstantAdapter> ccas = new ArrayList<>();
    private List<CheckFunctionAdapter> cfas = new ArrayList<>();
    private List<CheckTypeAdapter> ctas = new ArrayList<>();

    public CheckQuxAdapter(QuxVisitor next) {
        super(next);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Identifier name) {
        checkState(!visitedStart, "may only call visit(int, String) once");
        checkState(!visitedPackage, "must call visit(int, String) before visitPackage(String)");
        checkState(!visitedEnd, "must call visit(int, String) before visitEnd()");
        checkNotNull(name, "name cannot be null");

        visitedStart = true;

        super.visit(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstantVisitor visitConstant(int flags, Identifier name, Type type) {
        checkState(visitedStart,
                "must call visit(int, String) before visitConstant(int, String, Type)");
        checkState(visitedPackage,
                "must call visitPackage(String) before visitConstant(int, String, Type)");
        checkState(!visitedEnd, "must call visitConstant(int, String, Type) before visitEnd()");
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

        for (CheckConstantAdapter cca : ccas) {
            checkState(cca.hasVisitedEnd(),
                    "visitEnd() must be called after all constant visitors have called visitEnd()");
        }
        for (CheckFunctionAdapter cfa : cfas) {
            checkState(cfa.hasVisitedEnd(),
                    "visitEnd() must be called after all function visitors have called visitEnd()");
        }
        for (CheckTypeAdapter cta : ctas) {
            checkState(cta.hasVisitedEnd(),
                    "visitEnd() must be called after all type visitors have called visitEnd()");
        }

        visitedEnd = true;

        super.visitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionVisitor visitFunction(int flags, Identifier name, Type.Function type) {
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
    public void visitPackage(List<Identifier> pkg) {
        checkState(visitedStart, "must call visit(int, String) before visitPackage(String)");
        checkState(!visitedPackage, "may only call visitPackage(String) once");
        checkState(!visitedEnd, "must call visitPackage(String) before visitEnd()");
        checkNotNull(pkg, "pkg cannot be null");
        checkArgument(!pkg.contains(null), "pkg cannot contain null");

        visitedPackage = true;

        super.visitPackage(pkg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeVisitor visitType(int flags, Identifier name) {
        checkState(visitedStart, "must call visit(int, String) before visitType(int, String)");
        checkState(visitedPackage, "must call visitPackage(String) before visitType(int, String)");
        checkState(!visitedEnd, "must call visitType(int, String) before visitEnd()");
        checkNotNull(name, "name cannot be null");

        ctas.add(new CheckTypeAdapter(super.visitType(flags, name)));

        return ctas.get(ctas.size() - 1);
    }
}
