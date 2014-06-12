package com.hjwylde.qux.pipelines;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.hjwylde.qux.util.Type.TYPE_BOOL;
import static com.hjwylde.qux.util.Type.TYPE_INT;
import static com.hjwylde.qux.util.Type.TYPE_ITERABLE;
import static com.hjwylde.qux.util.Type.TYPE_LIST_ANY;
import static com.hjwylde.qux.util.Type.TYPE_META;
import static com.hjwylde.qux.util.Type.TYPE_NULL;
import static com.hjwylde.qux.util.Type.TYPE_OBJ;
import static com.hjwylde.qux.util.Type.TYPE_REAL;
import static com.hjwylde.qux.util.Type.TYPE_SET_ANY;
import static com.hjwylde.qux.util.Type.TYPE_STR;
import static com.hjwylde.qux.util.Type.getInnerType;
import static com.hjwylde.qux.util.Types.isSubtype;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qbs.builder.Context;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qbs.builder.resources.Resource;
import com.hjwylde.qux.api.ConstantAdapter;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.StmtVisitor;
import com.hjwylde.qux.builder.AbstractControlFlowGraphListener;
import com.hjwylde.qux.builder.ControlFlowGraph;
import com.hjwylde.qux.builder.ControlFlowGraphEdge;
import com.hjwylde.qux.builder.ControlFlowGraphIterator;
import com.hjwylde.qux.builder.Environment;
import com.hjwylde.qux.tree.ConstantNode;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.Node;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;

import org.jgrapht.event.VertexTraversalEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public final class TypePropagator extends Pipeline {

    private QuxNode node;

    public TypePropagator(QuxContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuxNode apply(QuxNode node) {
        this.node = node;

        for (ConstantNode constant : node.getConstants()) {
            apply(constant);
        }

        for (FunctionNode function : node.getFunctions()) {
            apply(function);
        }

        return node;
    }

    private void apply(ConstantNode constant) {
        constant.accept(new ConstantTypePropagator());
    }

    private void apply(FunctionNode function) {
        ControlFlowGraph cfg = Attributes.getAttributeUnchecked(function,
                Attribute.ControlFlowGraph.class).getControlFlowGraph();

        FunctionTypePropagator listener = new FunctionTypePropagator(function, cfg);

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

    private static Type getConstantType(Context context, String owner, ExprNode.Variable constant,
            Node node) {
        Resource.Single resource = getResource(context, owner, node);

        Optional<String> type = resource.getConstantType(constant.getName());
        if (type.isPresent()) {
            return Type.forDescriptor(type.get());
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(node, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.noConstantFound(owner, constant.getName(), source.getSource(),
                    source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.noConstantFound(owner, constant.getName());
        }
    }

    private static Type.Function getFunctionType(Context context, String owner,
            ExprNode.Function function, Node node) {
        Resource.Single resource = getResource(context, owner, node);

        Optional<String> type = resource.getFunctionType(function.getName());
        if (type.isPresent()) {
            return (Type.Function) Type.forDescriptor(type.get());
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(node, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.noFunctionFound(owner, function.getName(), source.getSource(),
                    source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.noFunctionFound(owner, function.getName());
        }
    }

    private static Resource.Single getResource(Context context, String id, Node node) {
        Optional<Resource.Single> resource = context.getResourceById(id);
        if (resource.isPresent()) {
            return resource.get();
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(node, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.noClassFound(id, source.getSource(), source.getLine(),
                    source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.noClassFound(id);
        }
    }

    private static Type getType(Node node) {
        return Attributes.getAttributeUnchecked(node, Attribute.Type.class).getType();
    }

    private static Environment<String, Type> mergeEnvironments(
            List<Environment<String, Type>> envs) {
        checkArgument(!envs.isEmpty(), "envs cannot be empty");

        Environment<String, Type> merged = new Environment<>();

        Iterator<Environment<String, Type>> it = envs.iterator();
        merged.putAll(it.next().flatten());

        while (it.hasNext()) {
            Environment<String, Type> next = it.next().flatten();

            for (String key : merged.keySet()) {
                if (!next.contains(key)) {
                    merged.remove(key);
                } else {
                    merged.put(key, Type.forUnion(merged.getUnchecked(key), next.getUnchecked(
                            key)));
                }
            }
        }

        return merged;
    }

    private boolean propagate(ExprNode expr) {
        return propagate(expr, new Environment<String, Type>());
    }

    private boolean propagate(ExprNode expr, Environment<String, Type> env) {
        ExprTypePropagator etp = new ExprTypePropagator(env);

        expr.accept(etp);

        return etp.isModified();
    }

    private static boolean setType(Node node, Type type) {
        Optional<Attribute.Type> opt = Attributes.getAttribute(node, Attribute.Type.class);

        if (!opt.isPresent()) {
            node.addAttributes(new Attribute.Type(type));
            return true;
        }

        Attribute.Type attribute = opt.get();

        // Combine the two types and create a normalised union of them
        Type ntype = Type.forUnion(type, attribute.getType());
        attribute.setType(ntype);

        // Return true if the type was updated
        return !type.equals(ntype);
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since TODO: SINCE
     */
    private final class ConstantTypePropagator extends ConstantAdapter {

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExpr(ExprNode expr) {
            propagate(expr);
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since TODO: SINCE
     */
    private final class ExprTypePropagator implements ExprVisitor {

        private final Environment<String, Type> env;

        private boolean modified = false;

        public ExprTypePropagator(Environment<String, Type> env) {
            this.env = checkNotNull(env, "env cannot be null");
        }

        public boolean isModified() {
            return modified;
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
            visitExpr(expr.getIndex());

            // Sanity check, if it fails then we don't care - the type checker should pick it up
            if (isSubtype(getType(expr.getTarget()), TYPE_ITERABLE)) {
                setType(expr, getInnerType(getType(expr.getTarget())));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprBinary(ExprNode.Binary expr) {
            visitExpr(expr.getLhs());
            visitExpr(expr.getRhs());

            // TODO: Properly type check using references to methods that exist
            switch (expr.getOp()) {
                case ADD:
                case MUL:
                case REM:
                case SUB:
                    setType(expr, getType(expr.getLhs()));
                    break;
                case RNG:
                    setType(expr, Type.forList(TYPE_INT));
                    break;
                case DIV:
                    setType(expr, TYPE_REAL);
                    break;
                case IDIV:
                case EXP:
                    setType(expr, TYPE_INT);
                    break;
                case AND:
                case EQ:
                case IFF:
                case IMP:
                case IN:
                case GT:
                case GTE:
                case LT:
                case LTE:
                case NEQ:
                case OR:
                case XOR:
                    setType(expr, TYPE_BOOL);
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getOp().toString());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprConstant(ExprNode.Constant expr) {
            switch (expr.getType()) {
                case BOOL:
                    setType(expr, TYPE_BOOL);
                    break;
                case INT:
                    setType(expr, TYPE_INT);
                    break;
                case NULL:
                    setType(expr, TYPE_NULL);
                    break;
                case OBJ:
                    setType(expr, TYPE_OBJ);
                    break;
                case REAL:
                    setType(expr, TYPE_REAL);
                    break;
                case STR:
                    setType(expr, TYPE_STR);
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getType().toString());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprExternal(ExprNode.External expr) {
            visitExpr(expr.getMeta());
            if (expr.getExpr() instanceof ExprNode.Function) {
                visitExprFunction(expr.getMeta().getId(), (ExprNode.Function) expr.getExpr(), expr);
            } else if (expr.getExpr() instanceof ExprNode.Variable) {
                visitExprVariable(expr.getMeta().getId(), (ExprNode.Variable) expr.getExpr(), expr);
            } else {
                throw new MethodNotImplementedError(expr.getExpr().getClass().toString());
            }

            setType(expr, getType(expr.getExpr()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprFunction(ExprNode.Function expr) {
            visitExprFunction(node.getId(), expr, expr);
        }

        public void visitExprFunction(String owner, ExprNode.Function expr, Node node) {
            for (ExprNode argument : expr.getArguments()) {
                visitExpr(argument);
            }

            setType(expr, getFunctionType(getContext(), owner, expr, node).getReturnType());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprList(ExprNode.List expr) {
            Set<Type> types = new HashSet<>();

            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
                types.add(getType(value));
            }

            if (types.isEmpty()) {
                setType(expr, TYPE_LIST_ANY);
            } else {
                setType(expr, Type.forList(Type.forUnion(types)));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprMeta(ExprNode.Meta expr) {
            setType(expr, TYPE_META);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprSet(ExprNode.Set expr) {
            Set<Type> types = new HashSet<>();

            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
                types.add(getType(value));
            }

            if (types.isEmpty()) {
                setType(expr, TYPE_SET_ANY);
            } else {
                setType(expr, Type.forSet(Type.forUnion(types)));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprSlice(ExprNode.Slice expr) {
            visitExpr(expr.getTarget());

            if (expr.getFrom().isPresent()) {
                visitExpr(expr.getFrom().get());
            }

            if (expr.getTo().isPresent()) {
                visitExpr(expr.getTo().get());
            }

            setType(expr, getType(expr.getTarget()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprUnary(ExprNode.Unary expr) {
            visitExpr(expr.getTarget());

            Type targetType = getType(expr.getTarget());

            switch (expr.getOp()) {
                case DEC:
                case INC:
                case LEN:
                    setType(expr, TYPE_INT);
                    break;
                case NEG:
                    setType(expr, targetType);
                    break;
                case NOT:
                    setType(expr, TYPE_BOOL);
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getOp().toString());
            }
        }

        public void visitExprVariable(String owner, ExprNode.Variable expr, Node node) {
            setType(expr, getConstantType(getContext(), owner, expr, node));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprVariable(ExprNode.Variable expr) {
            setType(expr, env.getUnchecked(expr.getName()));
        }

        private void setType(Node node, Type type) {
            if (TypePropagator.setType(node, type)) {
                modified = true;
            }
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.0
     */
    private final class FunctionTypePropagator extends AbstractControlFlowGraphListener
            implements StmtVisitor {

        private static final String RETURN = "$";

        private final Environment<String, Type> baseEnv = new Environment<>();
        private final Map<StmtNode, Environment<String, Type>> envs = new HashMap<>();

        private Environment<String, Type> env;

        public FunctionTypePropagator(FunctionNode function, ControlFlowGraph cfg) {
            super(function, cfg);

            initialiseBaseEnvironment();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void notifyTraversalStarted() {
            setFinished(true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void vertexTraversed(VertexTraversalEvent<StmtNode> e) {
            StmtNode stmt = e.getVertex();

            env = getEnvironment(stmt);

            stmt.accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAccessAssign(StmtNode.AccessAssign stmt) {
            propagate(stmt.getAccess());
            propagate(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAssign(StmtNode.Assign stmt) {
            propagate(stmt.getExpr());

            env.put(stmt.getVar(), getType(stmt.getExpr()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtExpr(StmtNode.Expr stmt) {
            propagate(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFor(StmtNode.For stmt) {
            propagate(stmt.getExpr());

            // Sanity check, if it fails then we don't care - the type checker should pick it up
            if (isSubtype(getType(stmt.getExpr()), TYPE_ITERABLE)) {
                env.put(stmt.getVar(), getInnerType(getType(stmt.getExpr())));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtIf(StmtNode.If stmt) {
            propagate(stmt.getCondition());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtPrint(StmtNode.Print stmt) {
            propagate(stmt.getExpr());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtReturn(StmtNode.Return stmt) {
            if (stmt.getExpr().isPresent()) {
                propagate(stmt.getExpr().get());
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtWhile(StmtNode.While stmt) {
            propagate(stmt.getCondition());
        }

        private Environment<String, Type> getEnvironment(StmtNode stmt) {
            Environment<String, Type> env = baseEnv.push();

            List<Environment<String, Type>> incomingEnvs = new ArrayList<>();
            for (ControlFlowGraphEdge edge : cfg.incomingEdgesOf(stmt)) {
                StmtNode in = edge.getSource();

                if (envs.containsKey(in)) {
                    incomingEnvs.add(envs.get(in));
                } else {
                    setFinished(false);
                }
            }

            if (!incomingEnvs.isEmpty()) {
                env.putAll(mergeEnvironments(incomingEnvs));
            }

            env = env.push();

            if (envs.containsKey(stmt)) {
                env.putAll(envs.get(stmt));
            }

            envs.put(stmt, env);

            return env;
        }

        private void initialiseBaseEnvironment() {
            for (Map.Entry<String, Type> parameter : function.getParameters().entrySet()) {
                baseEnv.put(parameter.getKey(), parameter.getValue());
            }
            baseEnv.put(RETURN, function.getReturnType());
        }

        private void propagate(ExprNode expr) {
            boolean modified = TypePropagator.this.propagate(expr, env);

            setFinished(isFinished() && !modified);
        }
    }
}

