package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Type;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class FunctionNode extends Node implements FunctionVisitor {

    public static final String RECEIVER_NAME = "this";

    private final int flags;
    private final Identifier name;
    private final Type.Function type;

    private List<Identifier> parameters = new ArrayList<>();

    private List<StmtNode> stmts = new ArrayList<>();

    public FunctionNode(int flags, Identifier name, Type.Function type, Attribute... attributes) {
        this(flags, name, type, Arrays.asList(attributes));
    }

    public FunctionNode(int flags, Identifier name, Type.Function type,
            Collection<? extends Attribute> attributes) {
        super(attributes);

        this.flags = flags;
        this.name = checkNotNull(name, "name cannot be null");
        this.type = checkNotNull(type, "type cannot be null");
    }

    public void accept(QuxVisitor qv) {
        FunctionVisitor fv = qv.visitFunction(flags, name, type);

        accept(fv);

        fv.visitEnd();
    }

    public void accept(FunctionVisitor fv) {
        for (Identifier parameter : parameters) {
            fv.visitParameter(parameter);
        }

        fv.visitCode();

        for (StmtNode stmt : stmts) {
            stmt.accept(fv);
        }
    }

    public int getFlags() {
        return flags;
    }

    public Identifier getName() {
        return name;
    }

    public ImmutableList<Identifier> getParameters() {
        return ImmutableList.copyOf(parameters);
    }

    public ImmutableList<StmtNode> getStmts() {
        return ImmutableList.copyOf(stmts);
    }

    public Type.Function getType() {
        return type;
    }

    public boolean isMethod() {
        return !parameters.isEmpty() && parameters.get(0).getId().equals(RECEIVER_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitCode() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitParameter(Identifier var) {
        checkNotNull(var, "var cannot be null");

        parameters.add(var);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtAssign(StmtNode.Assign stmt) {
        stmts.add(checkNotNull(stmt, "stmt cannot be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtExpr(StmtNode.Expr stmt) {
        stmts.add(checkNotNull(stmt, "stmt cannot be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtFor(StmtNode.For stmt) {
        stmts.add(checkNotNull(stmt, "stmt cannot be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtIf(StmtNode.If stmt) {
        stmts.add(checkNotNull(stmt, "stmt cannot be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtPrint(StmtNode.Print stmt) {
        stmts.add(checkNotNull(stmt, "stmt cannot be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtReturn(StmtNode.Return stmt) {
        stmts.add(checkNotNull(stmt, "stmt cannot be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtWhile(StmtNode.While stmt) {
        stmts.add(checkNotNull(stmt, "stmt cannot be null"));
    }
}
