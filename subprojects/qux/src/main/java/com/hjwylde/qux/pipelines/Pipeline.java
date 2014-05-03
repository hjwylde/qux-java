package com.hjwylde.qux.pipelines;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.api.QuxAdapter;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.tree.QuxNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public abstract class Pipeline extends QuxAdapter {

    private final QuxNode node;

    public Pipeline(QuxNode node) {
        this.node = checkNotNull(node, "node cannot be null");
    }

    public Pipeline(QuxVisitor next, QuxNode node) {
        super(next);

        this.node = checkNotNull(node, "node cannot be null");
    }

    protected QuxNode getQuxNode() {
        return node;
    }
}
