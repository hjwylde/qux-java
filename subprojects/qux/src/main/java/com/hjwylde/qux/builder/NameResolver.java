package com.hjwylde.qux.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qbs.builder.QuxContext;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public final class NameResolver {

    // TODO: Use this class in name resolution and type resolution - basically anything that involves interacting with resources

    private final QuxContext context;

    public NameResolver(QuxContext context) {
        this.context = checkNotNull(context, "context cannot be null");
    }

    public QuxContext getContext() {
        return context;
    }
}
