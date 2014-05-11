package com.hjwylde.qux.builder;

import com.hjwylde.qux.tree.StmtNode;

import org.jgrapht.event.TraversalListener;

/**
 * An alias class for a {@link org.jgrapht.event.TraversalListener} with instantiated generics.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public interface ControlFlowGraphListener
        extends TraversalListener<StmtNode, ControlFlowGraphEdge> {}
