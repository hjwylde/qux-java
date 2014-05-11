package com.hjwylde.qux.builder;

import com.hjwylde.qux.tree.StmtNode;

import org.jgrapht.graph.DefaultEdge;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
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
