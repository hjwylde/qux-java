package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class QuxNode extends QuxVisitor {

    private int version;
    private String name;

    private List<FunctionNode> functions = new ArrayList<>();

    public void accept(QuxVisitor qv) {
        qv.visit(version, name);

        for (FunctionNode function : functions) {
            function.accept(qv);
        }

        qv.visitEnd();
    }

    public ImmutableList<FunctionNode> getFunctions() {
        return ImmutableList.copyOf(functions);
    }

    public String getName() {
        checkState(name != null, "name has not been set");

        return name;
    }

    public int getVersion() {
        checkState(version != 0, "version has not been set");

        return version;
    }

    @Override
    public void visit(int version, String name) {
        this.version = version;
        this.name = checkNotNull(name, "name cannot be null");
    }

    @Override
    public FunctionVisitor visitFunction(int flags, String name, String desc) {
        FunctionNode fn = new FunctionNode(flags, name, desc);

        functions.add(fn);

        return fn;
    }
}

