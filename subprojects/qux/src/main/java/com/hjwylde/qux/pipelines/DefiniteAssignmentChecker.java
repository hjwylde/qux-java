package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.tree.QuxNode;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class DefiniteAssignmentChecker extends Pipeline {

    public DefiniteAssignmentChecker(QuxContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuxNode apply(QuxNode node) {
        throw new MethodNotImplementedError();
    }
}

