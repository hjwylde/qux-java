package com.hjwylde.qux.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.StmtNode;

import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.VertexTraversalEvent;

/**
 * An alias class for a {@link org.jgrapht.event.TraversalListener} with instantiated generics.
 * Provides an empty implementation of the required interface methods.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public abstract class AbstractControlFlowGraphListener implements ControlFlowGraphListener {

    protected final FunctionNode function;
    protected final ControlFlowGraph cfg;

    private boolean finished = false;

    public AbstractControlFlowGraphListener(FunctionNode function, ControlFlowGraph cfg) {
        this.function = checkNotNull(function, "function cannot be null");
        this.cfg = checkNotNull(cfg, "cfg cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void edgeTraversed(EdgeTraversalEvent<StmtNode, ControlFlowGraphEdge> e) {}

    public final boolean isFinished() {
        return finished;
    }

    public void notifyTraversalFinished() {}

    public void notifyTraversalStarted() {}

    public final void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vertexFinished(VertexTraversalEvent<StmtNode> e) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void vertexTraversed(VertexTraversalEvent<StmtNode> e) {}

    protected final ControlFlowGraph getControlFlowGraph() {
        return cfg;
    }

    protected final FunctionNode getFunction() {
        return function;
    }
}
