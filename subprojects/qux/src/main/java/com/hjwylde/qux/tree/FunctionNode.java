package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class FunctionNode extends Node implements FunctionVisitor {

    private final int flags;
    private final String name;
    private final Type.Function type;

    private Map<String, Type> parameters = new LinkedHashMap<>();
    private Type returnType;

    private List<StmtNode> stmts = new ArrayList<>();

    public FunctionNode(int flags, String name, Type.Function type, Attribute... attributes) {
        this(flags, name, type, Arrays.asList(attributes));
    }

    public FunctionNode(int flags, String name, Type.Function type,
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
        for (Map.Entry<String, Type> parameter : parameters.entrySet()) {
            fv.visitParameter(parameter.getKey(), parameter.getValue());
        }

        fv.visitReturnType(getReturnType());

        fv.visitCode();

        for (StmtNode stmt : stmts) {
            stmt.accept(fv);
        }
    }

    public int getFlags() {
        return flags;
    }

    public String getName() {
        return name;
    }

    public ImmutableMap<String, Type> getParameters() {
        return ImmutableMap.copyOf(parameters);
    }

    public Type getReturnType() {
        checkState(returnType != null, "returnType has not been set");

        return returnType;
    }

    public ImmutableList<StmtNode> getStmts() {
        return ImmutableList.copyOf(stmts);
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
    public void visitParameter(String var, Type type) {
        checkNotNull(var, "var cannot be null");
        checkNotNull(type, "type cannot be null");

        parameters.put(var, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitReturnType(Type type) {
        returnType = checkNotNull(type, "type cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtAccessAssign(StmtNode.AccessAssign stmt) {
        stmts.add(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtAssign(StmtNode.Assign stmt) {
        stmts.add(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtExpr(StmtNode.Expr stmt) {
        stmts.add(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtFor(StmtNode.For stmt) {
        stmts.add(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtIf(StmtNode.If stmt) {
        stmts.add(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtPrint(StmtNode.Print stmt) {
        stmts.add(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtReturn(StmtNode.Return stmt) {
        stmts.add(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtWhile(StmtNode.While stmt) {
        stmts.add(stmt);
    }
}
