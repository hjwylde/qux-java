package com.hjwylde.qux.pipelines;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.tree.QuxNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public abstract class Pipeline {

    protected final QuxNode node;

    public Pipeline(QuxNode node) {
        this.node = checkNotNull(node, "node cannot be null");
    }

    public abstract void apply();

    protected final QuxNode getNode() {
        return node;
    }
}
