package com.hjwylde.qux.builder;

import com.hjwylde.qux.tree.StmtNode;

import org.jgrapht.Graph;
import org.jgrapht.traverse.DepthFirstIterator;

/**
 * An alias class for a {@link org.jgrapht.traverse.DepthFirstIterator} with instantiated generics.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public final class ControlFlowGraphIterator
        extends DepthFirstIterator<StmtNode, ControlFlowGraphEdge> {

    public ControlFlowGraphIterator(Graph<StmtNode, ControlFlowGraphEdge> g) {
        super(g);
    }

    public ControlFlowGraphIterator(Graph<StmtNode, ControlFlowGraphEdge> g, StmtNode startVertex) {
        super(g, startVertex);
    }
}
