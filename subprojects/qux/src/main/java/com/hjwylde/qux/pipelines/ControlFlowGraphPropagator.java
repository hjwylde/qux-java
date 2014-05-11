package com.hjwylde.qux.pipelines;

import com.hjwylde.qux.builder.ControlFlowGraph;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.util.Attribute;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ControlFlowGraphPropagator extends Pipeline {

    public ControlFlowGraphPropagator(QuxNode node) {
        super(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        for (FunctionNode function : node.getFunctions()) {
            function.addAttributes(new Attribute.ControlFlowGraph(ControlFlowGraph.of(function)));
        }
    }
}
