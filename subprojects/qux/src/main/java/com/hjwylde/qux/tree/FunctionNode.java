package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
    private final String desc;

    private Map<String, Type> parameters = new HashMap<>();
    private Type returnType;

    private List<StmtNode> stmts = new ArrayList<>();

    public FunctionNode(int flags, String name, String desc, Attribute... attributes) {
        this(flags, name, desc, Arrays.asList(attributes));
    }

    public FunctionNode(int flags, String name, String desc, Collection<Attribute> attributes) {
        super(attributes);

        this.flags = flags;
        this.name = checkNotNull(name, "name cannot be null");
        this.desc = checkNotNull(desc, "desc cannot be null");
    }

    public void accept(QuxVisitor qv) {
        FunctionVisitor fv = qv.visitFunction(flags, name, desc);

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

    @Override
    public void visitCode() {}

    @Override
    public void visitEnd() {}

    @Override
    public void visitParameter(String var, Type type) {
        checkNotNull(var, "var cannot be null");
        checkNotNull(type, "type cannot be null");

        parameters.put(var, type);
    }

    @Override
    public void visitReturnType(Type type) {
        returnType = checkNotNull(type, "type cannot be null");
    }

    @Override
    public void visitStmtAssign(String var, ExprNode expr) {
        stmts.add(new StmtNode.Assign(var, expr));
    }

    @Override
    public void visitStmtFunction(String name, ImmutableList<ExprNode> arguments) {
        stmts.add(new StmtNode.Function(name, arguments));
    }

    @Override
    public void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
            ImmutableList<StmtNode> falseBlock) {
        stmts.add(new StmtNode.If(condition, trueBlock, falseBlock));
    }

    @Override
    public void visitStmtPrint(ExprNode expr) {
        stmts.add(new StmtNode.Print(expr));
    }

    @Override
    public void visitStmtReturn(Optional<ExprNode> expr) {
        stmts.add(new StmtNode.Return(expr));
    }
}
