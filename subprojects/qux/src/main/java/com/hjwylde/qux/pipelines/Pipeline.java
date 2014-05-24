package com.hjwylde.qux.pipelines;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.tree.QuxNode;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public abstract class Pipeline {

    protected final QuxContext context;

    public Pipeline(QuxContext context) {
        this.context = checkNotNull(context, "context cannot be null");
    }

    public abstract QuxNode apply(QuxNode node);

    protected final QuxContext getContext() {
        return context;
    }
}
