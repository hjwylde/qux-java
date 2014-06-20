package com.hjwylde.qux.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.builder.resources.AbstractResource;
import com.hjwylde.qbs.builder.resources.Resource;
import com.hjwylde.qux.tree.ConstantNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.TypeNode;

import com.google.common.base.Optional;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.1
 */
public final class QuxResource extends AbstractResource {

    public static final Resource.Extension EXTENSION = new Resource.Extension("qux");

    private final QuxNode node;

    public QuxResource(QuxNode node) {
        this.node = checkNotNull(node, "node cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getConstantType(String name) {
        for (ConstantNode constant : node.getConstants()) {
            if (constant.getName().equals(name)) {
                return Optional.of(constant.getType().getDescriptor());
            }
        }

        return Optional.absent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getFunctionType(String name) {
        for (FunctionNode function : node.getFunctions()) {
            if (function.getName().equals(name)) {
                return Optional.of(function.getType().getDescriptor());
            }
        }

        return Optional.absent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return node.getId();
    }

    public QuxNode getQuxNode() {
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getTypeType(String name) {
        for (TypeNode type : node.getTypes()) {
            if (type.getName().equals(name)) {
                return Optional.of(type.getType().getDescriptor());
            }
        }

        return Optional.absent();
    }
}
