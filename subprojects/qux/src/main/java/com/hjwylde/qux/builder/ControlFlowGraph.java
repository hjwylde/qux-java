package com.hjwylde.qux.builder;

import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.StmtNode;

import org.jgrapht.graph.DirectedMultigraph;

/**
 * An alias class for a {@link org.jgrapht.graph.DirectedMultigraph} with instantiated generics.
 * Also provides a method to generate a control flow graph from a function node.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ControlFlowGraph extends DirectedMultigraph<StmtNode, ControlFlowGraphEdge> {

    ControlFlowGraph() {
        super(ControlFlowGraphEdge.class);
    }

    public static ControlFlowGraph of(FunctionNode function) {
        ControlFlowGraphGenerator generator = new ControlFlowGraphGenerator();
        function.accept(generator);

        return generator.getGraph();
    }
}
