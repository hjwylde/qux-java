package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Type;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public class CheckFunctionAdapter extends FunctionAdapter {

    private boolean visitedCode = false;
    private boolean visitedReturnType = false;
    private boolean visitedEnd = false;

    public CheckFunctionAdapter(FunctionVisitor next) {
        super(next);
    }

    public final boolean hasVisitedEnd() {
        return visitedEnd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitCode() {
        checkState(!visitedCode, "may only call visitCode() once");
        checkState(visitedReturnType, "must call visitReturnType(Type) before visitCode()");
        checkState(!visitedEnd, "must call visitCode() before visitEnd()");

        visitedCode = true;

        super.visitCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        checkState(visitedCode, "must call visitCode() before visitEnd()");
        checkState(!visitedEnd, "may only call visitEnd() once");

        visitedEnd = true;

        super.visitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitParameter(String var, Type type) {
        checkState(!visitedCode, "must call visitParameter(String, Type) before visitCode()");
        checkState(!visitedEnd, "must call visitParameter(String, Type) before visitEnd()");
        checkNotNull(var, "var cannot be null");
        checkNotNull(type, "type cannot be null");

        super.visitParameter(var, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitReturnType(Type type) {
        checkState(!visitedCode, "must call visitReturnType(Type) before visitCode()");
        checkState(!visitedReturnType, "may only call visitReturnType(Type) once");
        checkState(!visitedEnd, "must call visitReturnType(Type) before visitEnd()");
        checkNotNull(type, "type cannot be null");

        visitedReturnType = true;

        super.visitReturnType(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtAssign(StmtNode.Assign stmt) {
        checkState(visitedCode, "must call visitCode() before visitStmtAssign(StmtNode.Assign)");
        checkState(!visitedEnd, "must call visitStmtAssign(StmtNode.Assign) before visitEnd()");
        checkNotNull(stmt, "stmt cannot be null");

        super.visitStmtAssign(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtFor(StmtNode.For stmt) {
        checkState(visitedCode, "must call visitCode() before visitStmtFor(StmtNode.For)");
        checkState(!visitedEnd, "must call visitStmtFor(StmtNode.For) before visitEnd()");
        checkNotNull(stmt, "stmt cannot be null");

        super.visitStmtFor(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtFunction(StmtNode.Function stmt) {
        checkState(visitedCode,
                "must call visitCode() before visitStmtFunction(StmtNode.Function)");
        checkState(!visitedEnd, "must call visitStmtFunction(StmtNode.Function) before visitEnd()");
        checkNotNull(stmt, "stmt cannot be null");

        super.visitStmtFunction(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtIf(StmtNode.If stmt) {
        checkState(visitedCode, "must call visitCode() before visitStmtIf(StmtNode.If)");
        checkState(!visitedEnd, "must call visitStmtIf(StmtNode.If) before visitEnd()");
        checkNotNull(stmt, "stmt cannot be null");

        super.visitStmtIf(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtPrint(StmtNode.Print stmt) {
        checkState(visitedCode, "must call visitCode() before visitStmtPrint(StmtNode.Print)");
        checkState(!visitedEnd, "must call visitStmtPrint(StmtNode.Print) before visitEnd()");
        checkNotNull(stmt, "stmt cannot be null");

        super.visitStmtPrint(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtReturn(StmtNode.Return stmt) {
        checkState(visitedCode, "must call visitCode() before visitStmtReturn(StmtNode.Return)");
        checkState(!visitedEnd, "must call visitStmtReturn(StmtNode.Return) before visitEnd()");
        checkNotNull(stmt, "stmt cannot be null");

        super.visitStmtReturn(stmt);
    }
}
