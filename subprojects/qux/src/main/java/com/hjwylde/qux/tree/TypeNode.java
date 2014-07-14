package com.hjwylde.qux.tree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.api.TypeVisitor;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Type;

import java.util.Arrays;
import java.util.Collection;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.4
 */
public final class TypeNode extends Node implements TypeVisitor {

    private final int flags;
    private final Identifier name;

    private Type type;

    public TypeNode(int flags, Identifier name, Attribute... attributes) {
        this(flags, name, Arrays.asList(attributes));
    }

    public TypeNode(int flags, Identifier name, Collection<? extends Attribute> attributes) {
        super(attributes);

        this.flags = flags;
        this.name = checkNotNull(name, "name cannot be null");
    }

    public void accept(QuxVisitor qv) {
        TypeVisitor tv = qv.visitType(flags, name);

        accept(tv);

        tv.visitEnd();
    }

    public void accept(TypeVisitor tv) {
        tv.visitType(type);
    }

    public int getFlags() {
        return flags;
    }

    public Identifier getName() {
        return name;
    }

    public Type getType() {
        checkState(type != null, "type has not been set");

        return type;
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
    public void visitType(Type type) {
        this.type = checkNotNull(type, "type cannot be null");
    }
}
