package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.qux.builder.AbstractControlFlowGraphListener;
import com.hjwylde.qux.builder.ControlFlowGraph;
import com.hjwylde.qux.builder.ControlFlowGraphIterator;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;

import com.google.common.base.Optional;

import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.VertexTraversalEvent;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class DeadCodeChecker extends Pipeline {

    public DeadCodeChecker(QuxNode node) {
        super(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply() {
        for (FunctionNode function : node.getFunctions()) {
            apply(function);
        }
    }

    private void apply(FunctionNode function) {
        ControlFlowGraph cfg = Attributes.getAttributeUnchecked(function,
                Attribute.ControlFlowGraph.class).getControlFlowGraph();

        FunctionDeadCodeChecker listener = new FunctionDeadCodeChecker(function, cfg);

        while (!listener.isFinished()) {
            ControlFlowGraphIterator it = new ControlFlowGraphIterator(cfg);
            it.addTraversalListener(listener);

            listener.notifyTraversalStarted();
            while (it.hasNext()) {
                it.next();
            }
            listener.notifyTraversalFinished();
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.0
     */
    private static final class FunctionDeadCodeChecker extends AbstractControlFlowGraphListener {

        private int component = 0;

        private boolean started = false;

        public FunctionDeadCodeChecker(FunctionNode function, ControlFlowGraph cfg) {
            super(function, cfg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {
            component++;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void notifyTraversalStarted() {
            // Dead code checking only needs one iteration
            setFinished(true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void vertexTraversed(VertexTraversalEvent<StmtNode> e) {
            if (e.getVertex() == function.getStmts().get(0)) {
                started = true;
            }

            checkReachable(e.getVertex());
        }

        private void checkReachable(StmtNode stmt) {
            if (component == 1 && started) {
                return;
            }

            Optional<Attribute.Source> opt = Attributes.getAttribute(stmt, Attribute.Source.class);

            if (opt.isPresent()) {
                Attribute.Source source = opt.get();

                throw CompilerErrors.unreachableStatement(stmt.toString(), source.getSource(),
                        source.getLine(), source.getCol(), source.getLength());
            } else {
                throw CompilerErrors.unreachableStatement(stmt.toString());
            }
        }
    }
}
