package com.hjwylde.qux.pipelines;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hjwylde.qux.util.Type.TYPE_ANY;
import static com.hjwylde.qux.util.Type.TYPE_BOOL;
import static com.hjwylde.qux.util.Type.TYPE_INT;
import static com.hjwylde.qux.util.Type.TYPE_ITERABLE;
import static com.hjwylde.qux.util.Type.TYPE_META;
import static com.hjwylde.qux.util.Type.TYPE_REAL;
import static com.hjwylde.qux.util.Type.getInnerType;
import static com.hjwylde.qux.util.Types.isEquivalent;
import static com.hjwylde.qux.util.Types.isSubtype;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.api.ConstantAdapter;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.StmtVisitor;
import com.hjwylde.qux.builder.AbstractControlFlowGraphListener;
import com.hjwylde.qux.builder.ControlFlowGraph;
import com.hjwylde.qux.builder.ControlFlowGraphIterator;
import com.hjwylde.qux.tree.ConstantNode;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.Node;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import org.jgrapht.event.VertexTraversalEvent;

import java.util.Arrays;
import java.util.Map;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class TypeChecker extends Pipeline {

    public TypeChecker(QuxContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuxNode apply(QuxNode node) {
        for (ConstantNode constant : node.getConstants()) {
            apply(constant);
        }

        for (FunctionNode function : node.getFunctions()) {
            apply(function);
        }

        return node;
    }

    private static void apply(ConstantNode constant) {
        constant.accept(new ConstantTypeChecker(constant));
    }

    private static void apply(FunctionNode function) {
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

    private static void check(ExprNode expr) {
        expr.accept(new ExprTypeChecker());
    }

    private static void checkEquivalent(ExprNode expr, Type expected) {
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

    private static void checkSubtype(ExprNode expr, Type rhs) {
        Type type = getType(expr);

        if (isSubtype(type, rhs)) {
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(expr, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.invalidType(type.toString(), rhs.toString(), source.getSource(),
                    source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.invalidType(type.toString(), rhs.toString());
        }
    }

    private static Type getType(Node node) {
        return Attributes.getAttributeUnchecked(node, Attribute.Type.class).getType();
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.2
     */
    private static final class ConstantTypeChecker extends ConstantAdapter {

        private final ConstantNode constant;

        public ConstantTypeChecker(ConstantNode constant) {
            this.constant = checkNotNull(constant, "constant cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExpr(ExprNode expr) {
            check(expr);
            checkSubtype(expr, constant.getType());
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.2
     */
    private static final class ExprTypeChecker implements ExprVisitor {

        public void visitExpr(ExprNode expr) {
            expr.accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprBinary(ExprNode.Binary expr) {
            visitExpr(expr.getLhs());
            visitExpr(expr.getRhs());

            Type rhsType = getType(expr.getRhs());

            // TODO: Properly type check using references to methods that exist
            switch (expr.getOp()) {
                case ADD:
                case DIV:
                case MUL:
                case REM:
                case SUB:
                    checkEquivalent(expr.getLhs(), rhsType);
                    break;
                case EXP:
                case IDIV:
                case RNG:
                    checkEquivalent(expr.getLhs(), TYPE_INT);
                    checkEquivalent(expr.getRhs(), TYPE_INT);
                    break;
                case IN:
                    checkSubtype(expr.getRhs(), TYPE_ITERABLE);
                    checkSubtype(expr.getLhs(), getInnerType(rhsType));
                    break;
                case ACC:
                    checkSubtype(expr.getLhs(), TYPE_ITERABLE);
                    checkSubtype(expr.getRhs(), TYPE_INT);
                    break;
                case EQ:
                case GT:
                case GTE:
                case LT:
                case LTE:
                case NEQ:
                    break;
                case AND:
                case IFF:
                case IMP:
                case OR:
                case XOR:
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
        public void visitExprExternal(ExprNode.External expr) {
            visitExpr(expr.getMeta());
            visitExpr(expr.getExpr());
        }

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
        public void visitExprMeta(ExprNode.Meta expr) {
            checkEquivalent(expr, TYPE_META);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprRecord(ExprNode.Record expr) {
            for (Map.Entry<Identifier, ExprNode> field : expr.getFields().entrySet()) {
                visitExpr(field.getValue());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprRecordAccess(ExprNode.RecordAccess expr) {
            visitExpr(expr.getTarget());

            Type expected = Type.forRecord(ImmutableMap.<Identifier, Type>of(expr.getField(),
                    TYPE_ANY));
            checkSubtype(expr.getTarget(), expected);
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
                    checkSubtype(expr.getTarget(), Type.forUnion(Arrays.asList(TYPE_INT,
                            TYPE_REAL)));
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
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    private static final class FunctionTypeChecker extends AbstractControlFlowGraphListener
            implements StmtVisitor {

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

        /**
         * {@inheritDoc}
         */
        @Override
        public void vertexTraversed(VertexTraversalEvent<StmtNode> e) {
            e.getVertex().accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAccessAssign(StmtNode.AccessAssign stmt) {
            check(stmt.getAccess());
            check(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAssign(StmtNode.Assign stmt) {
            check(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtExpr(StmtNode.Expr stmt) {
            check(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFor(StmtNode.For stmt) {
            check(stmt.getExpr());
            checkSubtype(stmt.getExpr(), TYPE_ITERABLE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtIf(StmtNode.If stmt) {
            check(stmt.getCondition());
            checkSubtype(stmt.getCondition(), TYPE_BOOL);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtPrint(StmtNode.Print stmt) {
            check(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtReturn(StmtNode.Return stmt) {
            if (stmt.getExpr().isPresent()) {
                check(stmt.getExpr().get());
                Type returnType = function.getReturnType();
                checkSubtype(stmt.getExpr().get(), returnType);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtWhile(StmtNode.While stmt) {
            check(stmt.getCondition());
            checkSubtype(stmt.getCondition(), TYPE_BOOL);
        }
    }
}

