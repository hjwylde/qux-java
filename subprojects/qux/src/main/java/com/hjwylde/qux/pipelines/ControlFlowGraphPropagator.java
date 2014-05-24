package com.hjwylde.qux.pipelines;

import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.builder.ControlFlowGraph;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ControlFlowGraphPropagator extends Pipeline {

    public ControlFlowGraphPropagator(QuxContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuxNode apply(QuxNode node) {
        for (FunctionNode function : node.getFunctions()) {
            if (Attributes.getAttribute(function, Attribute.ControlFlowGraph.class).isPresent()) {
                continue;
            }

            function.addAttributes(new Attribute.ControlFlowGraph(ControlFlowGraph.of(function)));
        }

        return node;
    }
}
