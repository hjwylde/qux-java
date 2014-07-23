package com.hjwylde.qux.pipelines;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.hjwylde.qux.pipelines.TypeChecker.checkSubtype;
import static com.hjwylde.qux.util.Type.TYPE_ANY;
import static com.hjwylde.qux.util.Type.TYPE_BOOL;
import static com.hjwylde.qux.util.Type.TYPE_INT;
import static com.hjwylde.qux.util.Type.TYPE_ITERABLE;
import static com.hjwylde.qux.util.Type.TYPE_LIST_ANY;
import static com.hjwylde.qux.util.Type.TYPE_META;
import static com.hjwylde.qux.util.Type.TYPE_NULL;
import static com.hjwylde.qux.util.Type.TYPE_OBJ;
import static com.hjwylde.qux.util.Type.TYPE_RAT;
import static com.hjwylde.qux.util.Type.TYPE_SET_ANY;
import static com.hjwylde.qux.util.Type.TYPE_STR;
import static com.hjwylde.qux.util.Type.getFieldType;
import static com.hjwylde.qux.util.Type.getInnerType;
import static com.hjwylde.qux.util.Types.isSubtype;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qbs.builder.resources.Resource;
import com.hjwylde.qux.api.ConstantAdapter;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.StmtVisitor;
import com.hjwylde.qux.api.TypeAdapter;
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
import com.hjwylde.qux.tree.TypeNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import org.jgrapht.event.VertexTraversalEvent;

import java.util.ArrayList;
import java.util.Arrays;
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

        for (TypeNode type : node.getTypes()) {
            apply(type);
        }

        return node;
    }

    private void apply(TypeNode type) {
        type.accept(new TypeTypePropagator());
    }

    private void apply(ConstantNode constant) {
        constant.accept(new ConstantTypePropagator());
    }

    private void apply(FunctionNode function) {
        setType(function.getType(), propagate(function.getType()));

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

    private Type getConstantType(ExprNode.Constant constant) {
        List<Identifier> owner = constant.getOwner().getId();

        Resource.Single resource = getResource(owner, constant.getOwner());

        Optional<String> type = resource.getConstantType(constant.getName().getId());
        if (type.isPresent()) {
            return propagate(Type.forDescriptor(type.get()));
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(constant, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.noConstantFound(Joiner.on('.').join(owner),
                    constant.getName().getId(), source.getSource(), source.getLine(),
                    source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.noConstantFound(Joiner.on('.').join(owner),
                    constant.getName().getId());
        }
    }

    private Type.Function getFunctionType(ExprNode.Function function) {
        List<Identifier> owner = function.getOwner().getId();

        Resource.Single resource = getResource(owner, function.getOwner());

        Optional<String> type = resource.getFunctionType(function.getName().getId());
        if (type.isPresent()) {
            return (Type.Function) propagate(Type.forDescriptor(type.get()));
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(function, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.noFunctionFound(Joiner.on('.').join(owner),
                    function.getName().getId(), source.getSource(), source.getLine(),
                    source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.noFunctionFound(Joiner.on('.').join(owner),
                    function.getName().getId());
        }
    }

    private Resource.Single getResource(List<Identifier> id) {
        return getResource(Joiner.on('.').join(id));
    }

    private Resource.Single getResource(List<Identifier> id, Node node) {
        return getResource(Joiner.on('.').join(id), node);
    }

    private Resource.Single getResource(String id) {
        Optional<Resource.Single> resource = context.getResourceById(id);
        if (resource.isPresent()) {
            return resource.get();
        }

        throw CompilerErrors.noResourceFound(id);
    }

    private Resource.Single getResource(String id, Node node) {
        Optional<Resource.Single> resource = context.getResourceById(id);
        if (resource.isPresent()) {
            return resource.get();
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(node, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.noResourceFound(id, source.getSource(), source.getLine(),
                    source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.noResourceFound(id);
        }
    }

    private static Type getType(Node node) {
        return Attributes.getAttributeUnchecked(node, Attribute.Type.class).getType();
    }

    private Type getTypeType(Type.Named type) {
        List<Identifier> id = type.getId();
        List<Identifier> owner = id.subList(0, id.size() - 1);
        Identifier name = id.get(id.size() - 1);
        Resource.Single resource = getResource(Joiner.on('.').join(owner));

        Optional<String> desc = resource.getTypeType(name.getId());
        if (desc.isPresent()) {
            return propagate(Type.forDescriptor(desc.get()));
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(type, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.noTypeFound(Joiner.on('.').join(owner), name.getId(),
                    source.getSource(), source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.noTypeFound(Joiner.on('.').join(owner), name.getId());
        }
    }

    private static Environment<Identifier, Type> mergeEnvironments(
            List<Environment<Identifier, Type>> envs) {
        checkArgument(!envs.isEmpty(), "envs cannot be empty");

        Environment<Identifier, Type> merged = new Environment<>();

        Iterator<Environment<Identifier, Type>> it = envs.iterator();
        merged.putAll(it.next().flatten());

        while (it.hasNext()) {
            Environment<Identifier, Type> next = it.next().flatten();

            for (Identifier key : merged.keySet()) {
                if (!next.contains(key)) {
                    merged.remove(key);
                } else {
                    merged.put(key, Type.forUnion(Arrays.asList(merged.getUnchecked(key),
                            next.getUnchecked(key))));
                }
            }
        }

        return merged;
    }

    private Type propagate(Type type) {
        if (type instanceof Type.Function) {
            List<Type> parameters = new ArrayList<>();
            for (Type parameter : ((Type.Function) type).getParameterTypes()) {
                parameters.add(propagate(parameter));
            }

            Type returnType = propagate(((Type.Function) type).getReturnType());

            return Type.forFunction(returnType, parameters, type.getAttributes());
        } else if (type instanceof Type.List) {
            return Type.forList(propagate(((Type.List) type).getInnerType()), type.getAttributes());
        } else if (type instanceof Type.Set) {
            return Type.forSet(propagate(((Type.Set) type).getInnerType()), type.getAttributes());
        } else if (type instanceof Type.Union) {
            List<Type> types = new ArrayList<>();
            for (Type bound : ((Type.Union) type).getTypes()) {
                types.add(propagate(bound));
            }

            return Type.forUnion(types, type.getAttributes());
        } else if (type instanceof Type.Named) {
            return propagate(getTypeType((Type.Named) type));
        }

        return type;
    }

    private boolean propagate(ExprNode expr) {
        return propagate(expr, new Environment<Identifier, Type>());
    }

    private boolean propagate(ExprNode expr, Environment<Identifier, Type> env) {
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
        Type ntype = Type.forUnion(Arrays.asList(type, attribute.getType()));
        attribute.setType(ntype);

        // Return true if the type was updated
        return !type.equals(ntype);
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.2
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
     * @since 0.2.2
     */
    private final class ExprTypePropagator implements ExprVisitor {

        private final Environment<Identifier, Type> env;

        private boolean modified = false;

        public ExprTypePropagator(Environment<Identifier, Type> env) {
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
                case DIV:
                    setType(expr, TYPE_RAT);
                    break;
                case EXP:
                case IDIV:
                    setType(expr, TYPE_INT);
                    break;
                case RNG:
                    setType(expr, Type.forList(TYPE_INT));
                    break;
                case ACC:
                    checkSubtype(expr.getLhs(), TYPE_ITERABLE);

                    setType(expr, getInnerType(getType(expr.getLhs())));
                    break;
                case AND:
                case OR:
                case XOR:
                    Type lhsType = getType(expr.getLhs());

                    // Check if we are doing a bitwise or a boolean operation
                    if (isSubtype(lhsType, TYPE_INT)) {
                        setType(expr, TYPE_INT);
                        break;
                    }

                    // else lhsType <: TYPE_BOOL
                case EQ:
                case IFF:
                case IMP:
                case IN:
                case GT:
                case GTE:
                case LT:
                case LTE:
                case NEQ:
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
            visitExpr(expr.getOwner());

            setType(expr, getConstantType(expr));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprFunction(ExprNode.Function expr) {
            visitExpr(expr.getOwner());

            for (ExprNode argument : expr.getArguments()) {
                visitExpr(argument);
            }

            setType(expr, getFunctionType(expr).getReturnType());
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
        public void visitExprRecord(ExprNode.Record expr) {
            Map<Identifier, Type> fields = new HashMap<>();

            for (Map.Entry<Identifier, ExprNode> field : expr.getFields().entrySet()) {
                visitExpr(field.getValue());
                fields.put(field.getKey(), getType(field.getValue()));
            }

            setType(expr, Type.forRecord(fields));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprRecordAccess(ExprNode.RecordAccess expr) {
            visitExpr(expr.getTarget());
            checkSubtype(expr.getTarget(), Type.forRecord(ImmutableMap.<Identifier, Type>of(
                    expr.getField(), TYPE_ANY)));

            setType(expr, getFieldType(getType(expr.getTarget()), expr.getField()));
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

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprValue(ExprNode.Value expr) {
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
                case RAT:
                    setType(expr, TYPE_RAT);
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
        public void visitExprVariable(ExprNode.Variable expr) {
            setType(expr, propagate(getVariableType(expr)));
        }

        private Type getVariableType(ExprNode.Variable var) {
            if (env.contains(var.getName())) {
                return env.getUnchecked(var.getName());
            }

            Optional<Attribute.Source> opt = Attributes.getAttribute(var, Attribute.Source.class);

            if (opt.isPresent()) {
                Attribute.Source source = opt.get();

                throw CompilerErrors.unassignedVariableAccess(var.getName().getId(),
                        source.getSource(), source.getLine(), source.getCol(), source.getLength());
            } else {
                throw CompilerErrors.unassignedVariableAccess(var.getName().getId());
            }
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

        private final Identifier RETURN = new Identifier("$");

        private final Environment<Identifier, Type> baseEnv = new Environment<>();
        private final Map<StmtNode, Environment<Identifier, Type>> envs = new HashMap<>();

        private Environment<Identifier, Type> env;

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
        public void visitStmtAssign(StmtNode.Assign stmt) {
            propagate(stmt.getExpr());

            switch (stmt.getType()) {
                case ACCESS:
                    propagate(stmt.getLhs());

                    // After the access assignment, the type of the variable needs to be updated

                    ExprNode lhs = ((ExprNode.Binary) stmt.getLhs()).getLhs();
                    Type type = getType(lhs);

                    if (isSubtype(type, TYPE_STR)) {
                        // Don't need to do anything, a string assignment results in the same type
                    } else if (isSubtype(getType(stmt.getExpr()), getInnerType(type))) {
                        // TODO: Temporary solution to allow for some access assignments
                    } else {
                        //                        throw new MethodNotImplementedError(type.toString());
                    }

                    break;
                case RECORD_ACCESS:
                    propagate(stmt.getLhs());

                    // After the record access assignment, the type of the variable needs to be
                    // updated

                    // lhs = ((ExprNode.RecordAccess) stmt.getLhs()).getTarget();
                    // type = getType(lhs);

                    break;
                case VARIABLE:
                    env.put(((ExprNode.Variable) stmt.getLhs()).getName(), getType(stmt.getExpr()));
                    break;
                default:
                    throw new MethodNotImplementedError(stmt.getType().toString());
            }
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
            checkSubtype(stmt.getExpr(), TYPE_ITERABLE);

            env.put(stmt.getVar(), getInnerType(getType(stmt.getExpr())));
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

        private Environment<Identifier, Type> getEnvironment(StmtNode stmt) {
            Environment<Identifier, Type> env = baseEnv.push();

            List<Environment<Identifier, Type>> incomingEnvs = new ArrayList<>();
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
            Type.Function type = (Type.Function) getType(function.getType());

            for (int i = 0; i < function.getParameters().size(); i++) {
                baseEnv.put(function.getParameters().get(i), type.getParameterTypes().get(i));
            }

            baseEnv.put(RETURN, type.getReturnType());
        }

        private Type propagate(Type type) {
            return TypePropagator.this.propagate(type);
        }

        private void propagate(ExprNode expr) {
            boolean modified = TypePropagator.this.propagate(expr, env);

            setFinished(isFinished() && !modified);
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.4
     */
    private final class TypeTypePropagator extends TypeAdapter {

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitType(Type type) {
            propagate(type);
        }
    }
}

