package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qux.tree.QuxNode;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class DefiniteAssignmentChecker extends Pipeline {

    public DefiniteAssignmentChecker(QuxNode node) {
        super(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        throw new MethodNotImplementedError();
    }
}

