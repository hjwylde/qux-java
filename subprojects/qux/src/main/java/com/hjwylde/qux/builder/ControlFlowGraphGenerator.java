package com.hjwylde.qux.builder;

import static java.util.Arrays.asList;

import com.hjwylde.qux.api.FunctionAdapter;
import com.hjwylde.qux.tree.StmtNode;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
final class ControlFlowGraphGenerator extends FunctionAdapter {

    private final ControlFlowGraph graph = new ControlFlowGraph();

    private final List<StmtNode> previousStmts = new ArrayList<>();

    public ControlFlowGraph getGraph() {
        return graph;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtAssign(StmtNode.Assign stmt) {
        addStmt(stmt);
        setPreviousStmts(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtExpr(StmtNode.Expr stmt) {
        addStmt(stmt);
        setPreviousStmts(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtFor(StmtNode.For stmt) {
        addStmt(stmt);
        setPreviousStmts(stmt);

        for (StmtNode inner : stmt.getBody()) {
            inner.accept(this);
        }

        // Add the statement again to trigger the addition of the new edge
        addEdges(stmt);

        previousStmts.add(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtIf(StmtNode.If stmt) {
        // Add the if statement
        addStmt(stmt);
        setPreviousStmts(stmt);

        // Follow the true branch
        for (StmtNode inner : stmt.getTrueBlock()) {
            inner.accept(this);
        }

        // Save the current previous statements state, we will need this to concatenate at the end with the false previous statements state
        List<StmtNode> save = new ArrayList<>(previousStmts);

        // Set the previous statements to the if statement so the false branch has the proper initial parent
        setPreviousStmts(stmt);

        for (StmtNode inner : stmt.getFalseBlock()) {
            inner.accept(this);
        }

        previousStmts.addAll(save);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtPrint(StmtNode.Print stmt) {
        addStmt(stmt);
        setPreviousStmts(stmt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtReturn(StmtNode.Return stmt) {
        addStmt(stmt);
        setPreviousStmts();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitStmtWhile(StmtNode.While stmt) {
        addStmt(stmt);
        setPreviousStmts(stmt);

        for (StmtNode inner : stmt.getBody()) {
            inner.accept(this);
        }

        // Add the statement again to trigger the addition of the new edge
        addEdges(stmt);

        previousStmts.add(stmt);
    }

    private void addEdges(StmtNode to) {
        // Add an edge between all of the previous statements (the parents) and the destination
        for (StmtNode previousStmt : previousStmts) {
            graph.addEdge(previousStmt, to);
        }
    }

    private void addStmt(StmtNode stmt) {
        graph.addVertex(stmt);
        addEdges(stmt);
    }

    private void setPreviousStmts(StmtNode... stmts) {
        previousStmts.clear();
        previousStmts.addAll(asList(stmts));
    }
}
