package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class QuxNode extends Node implements QuxVisitor {

    private int version;
    private String name;

    private String pkg;

    private List<FunctionNode> functions = new ArrayList<>();

    public QuxNode(Attribute... attributes) {
        super(attributes);
    }

    public QuxNode(Collection<? extends Attribute> attributes) {
        super(attributes);
    }

    public void accept(QuxVisitor qv) {
        qv.visit(version, name);

        qv.visitPackage(pkg);

        for (FunctionNode function : functions) {
            function.accept(qv);
        }

        qv.visitEnd();
    }

    public ImmutableList<FunctionNode> getFunctions() {
        return ImmutableList.copyOf(functions);
    }

    public String getId() {
        return (pkg == null ? "" : pkg + ".") + name;
    }

    public Optional<String> getPackage() {
        return Optional.fromNullable(pkg);
    }

    public String getName() {
        checkState(name != null, "name has not been set");

        return name;
    }

    public int getVersion() {
        checkState(version != 0, "version has not been set");

        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(int version, String name) {
        checkArgument(version != 0, "version cannot be 0");

        this.version = version;
        this.name = checkNotNull(name, "name cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitPackage(String pkg) {
        this.pkg = pkg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionVisitor visitFunction(int flags, String name, Type.Function type) {
        FunctionNode fn = new FunctionNode(flags, name, type);

        functions.add(fn);

        return fn;
    }
}

