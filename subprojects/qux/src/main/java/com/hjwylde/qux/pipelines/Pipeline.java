package com.hjwylde.qux.pipelines;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.tree.QuxNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public abstract class Pipeline {

    private final QuxNode node;

    public Pipeline(QuxNode node) {
        this.node = checkNotNull(node, "node cannot be null");
    }

    public final void apply() {
        if (this instanceof QuxVisitor) {
            node.accept((QuxVisitor) this);
        } else {
            apply(node);
        }
    }

    protected void apply(QuxNode node) {}

    protected final QuxNode getQuxNode() {
        return node;
    }
}
