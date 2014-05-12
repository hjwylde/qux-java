package com.hjwylde.qux.builder;

import com.hjwylde.qux.tree.StmtNode;

import org.jgrapht.graph.DefaultEdge;

/**
 * An edge that allows retrieval of its source and target vertices. These vertices are always
 * assumed to be an instance of a {@link com.hjwylde.qux.tree.StmtNode}.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class ControlFlowGraphEdge extends DefaultEdge {

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode getSource() {
        return (StmtNode) super.getSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode getTarget() {
        return (StmtNode) super.getTarget();
    }
}
