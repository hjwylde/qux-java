package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.api.ConstantVisitor;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.api.TypeVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Type;

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
    private Identifier name;

    private ImmutableList<Identifier> pkg;

    private List<ConstantNode> constants = new ArrayList<>();
    private List<FunctionNode> functions = new ArrayList<>();
    private List<TypeNode> types = new ArrayList<>();

    public QuxNode(Attribute... attributes) {
        super(attributes);
    }

    public QuxNode(Collection<? extends Attribute> attributes) {
        super(attributes);
    }

    public void accept(QuxVisitor qv) {
        qv.visit(version, name);

        qv.visitPackage(pkg);

        for (ConstantNode constant : constants) {
            constant.accept(qv);
        }

        for (FunctionNode function : functions) {
            function.accept(qv);
        }

        for (TypeNode type : types) {
            type.accept(qv);
        }

        qv.visitEnd();
    }

    public ImmutableList<ConstantNode> getConstants() {
        return ImmutableList.copyOf(constants);
    }

    public ImmutableList<FunctionNode> getFunctions() {
        return ImmutableList.copyOf(functions);
    }

    public List<Identifier> getId() {
        return ImmutableList.<Identifier>builder().addAll(getPackage()).add(getName()).build();
    }

    public Identifier getName() {
        checkState(name != null, "name has not been set");

        return name;
    }

    public List<Identifier> getPackage() {
        checkState(pkg != null, "pkg has not been set");

        return pkg;
    }

    public ImmutableList<TypeNode> getTypes() {
        return ImmutableList.copyOf(types);
    }

    public int getVersion() {
        checkState(version != 0, "version has not been set");

        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(int version, Identifier name) {
        checkArgument(version != 0, "version cannot be 0");

        this.version = version;
        this.name = checkNotNull(name, "name cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstantVisitor visitConstant(int flags, Identifier name, Type type) {
        ConstantNode cn = new ConstantNode(flags, name, type);

        constants.add(cn);

        return cn;
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
    public FunctionVisitor visitFunction(int flags, Identifier name, Type.Function type) {
        FunctionNode fn = new FunctionNode(flags, name, type);

        functions.add(fn);

        return fn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitPackage(List<Identifier> pkg) {
        checkArgument(!pkg.isEmpty(), "pkg cannot be empty");

        this.pkg = ImmutableList.copyOf(pkg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TypeVisitor visitType(int flags, Identifier name) {
        TypeNode tn = new TypeNode(flags, name);

        types.add(tn);

        return tn;
    }
}

