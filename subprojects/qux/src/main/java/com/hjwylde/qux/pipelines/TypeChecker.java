package com.hjwylde.qux.pipelines;

import static com.hjwylde.qux.pipelines.TypePropagator.getType;
import static com.hjwylde.qux.pipelines.TypePropagator.initialiseFunctions;
import static com.hjwylde.qux.util.Type.TYPE_BOOL;
import static com.hjwylde.qux.util.Type.TYPE_INT;
import static com.hjwylde.qux.util.Type.TYPE_ITERABLE;
import static com.hjwylde.qux.util.Type.TYPE_REAL;
import static com.hjwylde.qux.util.Type.getInnerType;
import static com.hjwylde.qux.util.Types.isEquivalent;
import static com.hjwylde.qux.util.Types.isSubtype;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.StmtVisitor;
import com.hjwylde.qux.builder.AbstractControlFlowGraphListener;
import com.hjwylde.qux.builder.ControlFlowGraph;
import com.hjwylde.qux.builder.ControlFlowGraphIterator;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class TypeChecker extends Pipeline {

    private final ImmutableMap<String, Type> functions;

    public TypeChecker(QuxNode node) {
        super(node);

        functions = initialiseFunctions(node);
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

        FunctionTypeChecker listener = new FunctionTypeChecker(function, cfg);

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
     */
    private final class FunctionTypeChecker extends AbstractControlFlowGraphListener
            implements ExprVisitor, StmtVisitor {

        public FunctionTypeChecker(FunctionNode function, ControlFlowGraph cfg) {
            super(function, cfg);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void notifyTraversalStarted() {
            // Type checking only needs one iteration
            setFinished(true);
        }

        public void visitExpr(ExprNode expr) {
            expr.accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprAccess(ExprNode.Access expr) {
            visitExpr(expr.getTarget());
            checkSubtype(expr.getTarget(), TYPE_ITERABLE);

            visitExpr(expr.getIndex());
            checkSubtype(expr.getIndex(), TYPE_INT);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprBinary(ExprNode.Binary expr) {
            visitExpr(expr.getLhs());
            visitExpr(expr.getRhs());

            Type lhsType = getType(expr.getLhs());

            // TODO: Properly type check using references to methods that exist
            switch (expr.getOp()) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case REM:
                    checkEquivalent(expr.getLhs(), getType(expr.getRhs()));
                    break;
                case IN:
                    checkSubtype(expr.getRhs(), TYPE_ITERABLE);
                    checkSubtype(expr.getLhs(), getInnerType(getType(expr.getRhs())));
                case EQ:
                case NEQ:
                case GT:
                case GTE:
                case LT:
                case LTE:
                    break;
                case RNG:
                case EXP:
                    checkEquivalent(expr.getLhs(), TYPE_INT);
                    checkEquivalent(expr.getRhs(), TYPE_INT);
                    break;
                case AND:
                case OR:
                case XOR:
                case IFF:
                case IMP:
                    checkSubtype(expr.getLhs(), TYPE_BOOL);
                    checkSubtype(expr.getRhs(), TYPE_BOOL);
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getOp().toString());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprConstant(ExprNode.Constant expr) {}

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprFunction(ExprNode.Function expr) {
            // TODO: Type check the arguments
            for (ExprNode argument : expr.getArguments()) {
                visitExpr(argument);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprList(ExprNode.List expr) {
            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprSet(ExprNode.Set expr) {
            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprSlice(ExprNode.Slice expr) {
            visitExpr(expr.getTarget());
            checkSubtype(expr.getTarget(), TYPE_ITERABLE);

            if (expr.getFrom().isPresent()) {
                visitExpr(expr.getFrom().get());
                checkSubtype(expr.getFrom().get(), TYPE_INT);
            }

            if (expr.getTo().isPresent()) {
                visitExpr(expr.getTo().get());
                checkSubtype(expr.getTo().get(), TYPE_INT);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprUnary(ExprNode.Unary expr) {
            visitExpr(expr.getTarget());

            switch (expr.getOp()) {
                case DEC:
                case INC:
                    checkSubtype(expr.getTarget(), TYPE_INT);
                    break;
                case LEN:
                    checkSubtype(expr.getTarget(), TYPE_ITERABLE);
                    break;
                case NEG:
                    checkSubtype(expr.getTarget(), Type.forUnion(TYPE_INT, TYPE_REAL));
                    break;
                case NOT:
                    checkSubtype(expr.getTarget(), TYPE_BOOL);
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getOp().toString());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprVariable(ExprNode.Variable expr) {}

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAccessAssign(StmtNode.AccessAssign stmt) {
            visitExpr(stmt.getAccess());
            visitExpr(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAssign(StmtNode.Assign stmt) {
            visitExpr(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtExpr(StmtNode.Expr stmt) {
            visitExpr(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFor(StmtNode.For stmt) {
            visitExpr(stmt.getExpr());
            checkSubtype(stmt.getExpr(), TYPE_ITERABLE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtIf(StmtNode.If stmt) {
            visitExpr(stmt.getCondition());
            checkSubtype(stmt.getCondition(), TYPE_BOOL);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtPrint(StmtNode.Print stmt) {
            visitExpr(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtReturn(StmtNode.Return stmt) {
            if (stmt.getExpr().isPresent()) {
                Type returnType = functions.get(function.getName());
                checkSubtype(stmt.getExpr().get(), returnType);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtWhile(StmtNode.While stmt) {
            visitExpr(stmt.getCondition());
            checkSubtype(stmt.getCondition(), TYPE_BOOL);
        }

        private void checkEquivalent(ExprNode expr, Type expected) {
            visitExpr(expr);

            Type type = getType(expr);

            if (isEquivalent(type, expected)) {
                return;
            }

            Optional<Attribute.Source> opt = Attributes.getAttribute(expr, Attribute.Source.class);

            if (opt.isPresent()) {
                Attribute.Source source = opt.get();

                throw CompilerErrors.invalidType(type.toString(), expected.toString(),
                        source.getSource(), source.getLine(), source.getCol(), source.getLength());
            } else {
                throw CompilerErrors.invalidType(type.toString(), expected.toString());
            }
        }

        private void checkSubtype(ExprNode expr, Type rhs) {
            visitExpr(expr);

            Type type = getType(expr);

            if (isSubtype(type, rhs)) {
                return;
            }

            Optional<Attribute.Source> opt = Attributes.getAttribute(expr, Attribute.Source.class);

            if (opt.isPresent()) {
                Attribute.Source source = opt.get();

                throw CompilerErrors.invalidType(type.toString(), rhs.toString(),
                        source.getSource(), source.getLine(), source.getCol(), source.getLength());
            } else {
                throw CompilerErrors.invalidType(type.toString(), rhs.toString());
            }
        }
    }
}

