package com.hjwylde.qux.pipelines;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hjwylde.qux.util.Type.TYPE_BOOL;
import static com.hjwylde.qux.util.Type.TYPE_INT;
import static com.hjwylde.qux.util.Type.TYPE_LIST_ANY;
import static com.hjwylde.qux.util.Type.TYPE_NULL;
import static com.hjwylde.qux.util.Type.TYPE_REAL;
import static com.hjwylde.qux.util.Type.TYPE_SET_ANY;
import static com.hjwylde.qux.util.Type.TYPE_STR;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.FunctionAdapter;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.builder.Environment;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.Node;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Type;
import com.hjwylde.qux.util.Types;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    public TypeChecker(QuxVisitor next, QuxNode node) {
        super(next, node);

        functions = initialiseFunctions(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionVisitor visitFunction(int flags, String name, Type.Function type) {
        FunctionVisitor fv = super.visitFunction(flags, name, type);

        FunctionTypeChecker fvc = new FunctionTypeChecker(fv);

        return fvc;
    }

    private static Type getType(Node node) {
        return Attributes.getAttributeUnchecked(node, Attribute.Type.class).getType();
    }

    private static ImmutableMap<String, Type> initialiseFunctions(QuxNode node) {
        ImmutableMap.Builder<String, Type> builder = ImmutableMap.builder();

        for (FunctionNode function : node.getFunctions()) {
            builder.put(function.getName(), function.getReturnType());
        }

        return builder.build();
    }

    @SafeVarargs
    private static Environment<String, Type> mergeEnvironments(Environment<String, Type>... envs) {
        return mergeEnvironments(Arrays.asList(envs));
    }

    private static Environment<String, Type> mergeEnvironments(
            List<Environment<String, Type>> envs) {
        checkArgument(!envs.isEmpty(), "envs cannot be empty");

        Environment<String, Type> merged = new Environment<>();

        Iterator<Environment<String, Type>> it = envs.iterator();
        merged.putAll(it.next());

        while (it.hasNext()) {
            for (Map.Entry<String, Type> entry : it.next().entries()) {
                String key = entry.getKey();

                if (!merged.contains(key)) {
                    merged.remove(key);
                } else if (!merged.getUnchecked(key).equals(entry.getValue())) {
                    // Two environments have different types here, let's create a union of them

                    merged.put(key, Type.forUnion(merged.getUnchecked(key), entry.getValue()));
                }
                // else they are the same type, leave it in the merged environment
            }
        }

        return merged;
    }

    private static void setType(Node node, Type type) {
        Optional<Attribute.Type> opt = Attributes.getAttribute(node, Attribute.Type.class);

        if (!opt.isPresent()) {
            node.addAttributes(new Attribute.Type(type));

            opt = Attributes.getAttribute(node, Attribute.Type.class);
        }

        Attribute.Type attribute = opt.get();

        // Combine the two types and create a normalised union of them
        attribute.setType(Type.forUnion(type, attribute.getType()));
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    private final class FunctionTypeChecker extends FunctionAdapter implements ExprVisitor {

        private static final String RETURN = "$";

        private Environment<String, Type> env = new Environment<>();

        public FunctionTypeChecker(FunctionVisitor next) {
            super(next);
        }

        public void visitBlock(List<StmtNode> stmts) {
            for (StmtNode stmt : stmts) {
                stmt.accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitCode() {
            // Clone the environment to keep a clear separation of parameters and variables
            env = env.push();

            super.visitCode();
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
            checkSubtype(expr.getTarget(), Type.forUnion(TYPE_LIST_ANY, TYPE_SET_ANY, TYPE_STR));

            visitExpr(expr.getIndex());
            checkSubtype(expr.getIndex(), TYPE_INT);

            // TODO: Not quite right, when we introduce union types this is going to break
            Type targetType = getType(expr.getTarget());
            if (targetType instanceof Type.List) {
                setType(expr, ((Type.List) targetType).getInnerType());
            } else if (targetType instanceof Type.Set) {
                setType(expr, ((Type.Set) targetType).getInnerType());
            } else if (targetType instanceof Type.Str) {
                setType(expr, TYPE_STR);
            } else {
                throw new MethodNotImplementedError(targetType.toString());
            }
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
                    setType(expr, lhsType);
                    break;
                case IN:
                case EQ:
                case NEQ:
                    setType(expr, TYPE_BOOL);
                    break;
                case RANGE:
                    checkEquivalent(expr.getLhs(), TYPE_INT);
                    checkEquivalent(expr.getRhs(), TYPE_INT);
                    setType(expr, Type.forList(TYPE_INT));
                    break;
                case GT:
                case GTE:
                case LT:
                case LTE:
                    // TODO: This feels wrong, what is the lhs and rhs are equivalent unions? Surely we can't do a binary operation then!
                    checkEquivalent(expr.getRhs(), lhsType);
                    setType(expr, TYPE_BOOL);
                    break;
                case AND:
                case OR:
                case XOR:
                case IFF:
                case IMPLIES:
                    checkSubtype(expr.getLhs(), TYPE_BOOL);
                    checkSubtype(expr.getRhs(), TYPE_BOOL);
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
        public void visitExprFunction(ExprNode.Function expr) {
            for (ExprNode argument : expr.getArguments()) {
                visitExpr(argument);
            }

            setType(expr, functions.get(expr.getName()));
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
            checkSubtype(expr.getTarget(), Type.forUnion(TYPE_LIST_ANY, TYPE_SET_ANY, TYPE_STR));

            if (expr.getFrom().isPresent()) {
                visitExpr(expr.getFrom().get());
                checkSubtype(expr.getFrom().get(), TYPE_INT);
            }

            if (expr.getTo().isPresent()) {
                visitExpr(expr.getTo().get());
                checkSubtype(expr.getTo().get(), TYPE_INT);
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
                case LEN:
                    checkSubtype(expr.getTarget(), Type.forUnion(TYPE_LIST_ANY, TYPE_SET_ANY,
                            TYPE_STR));

                    // TODO: Not quite right, when we introduce union types this is going to break
                    if (targetType instanceof Type.List) {
                        setType(expr, ((Type.List) targetType).getInnerType());
                    } else if (targetType instanceof Type.Set) {
                        setType(expr, ((Type.Set) targetType).getInnerType());
                    } else if (targetType instanceof Type.Str) {
                        setType(expr, TYPE_STR);
                    } else {
                        throw new MethodNotImplementedError(targetType.toString());
                    }

                    break;
                case NEG:
                    setType(expr, targetType);
                    break;
                case NOT:
                    checkSubtype(expr.getTarget(), TYPE_BOOL);
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
        public void visitExprVariable(ExprNode.Variable expr) {
            setType(expr, env.getUnchecked(expr.getName()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitParameter(String var, Type type) {
            env.put(var, type);

            super.visitParameter(var, type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitReturnType(Type type) {
            env.put(RETURN, type);

            super.visitReturnType(type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAccessAssign(StmtNode.AccessAssign stmt) {
            visitExpr(stmt.getAccess());
            visitExpr(stmt.getExpr());

            super.visitStmtAccessAssign(stmt);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAssign(StmtNode.Assign stmt) {
            visitExpr(stmt.getExpr());

            env.put(stmt.getVar(), getType(stmt.getExpr()));

            super.visitStmtAssign(stmt);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFor(StmtNode.For stmt) {
            visitExpr(stmt.getExpr());
            checkSubtype(stmt.getExpr(), Type.forUnion(TYPE_LIST_ANY, TYPE_SET_ANY));

            Type type = getType(stmt.getExpr());

            // TODO: Make this generic, like with an iterable interface
            Type inner;
            if (type instanceof Type.List) {
                inner = ((Type.List) type).getInnerType();
            } else if (type instanceof Type.Set) {
                inner = ((Type.Set) type).getInnerType();
            } else {
                throw new MethodNotImplementedError(type.toString());
            }

            env = env.push();
            env.put(stmt.getVar(), inner);

            visitBlock(stmt.getBody());

            env = env.pop();

            super.visitStmtFor(stmt);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFunction(StmtNode.Function stmt) {
            for (ExprNode argument : stmt.getArguments()) {
                visitExpr(argument);
            }

            super.visitStmtFunction(stmt);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFunctionCall(StmtNode.FunctionCall stmt) {
            visitExpr(stmt.getCall());

            super.visitStmtFunctionCall(stmt);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtIf(StmtNode.If stmt) {
            visitExpr(stmt.getCondition());

            // Create a new environment for the true branch
            env = env.push();

            visitBlock(stmt.getTrueBlock());

            Environment<String, Type> trueEnv = env.pop();

            // Create a new environment for the false branch
            env = env.push();

            visitBlock(stmt.getFalseBlock());

            Environment<String, Type> falseEnv = env.pop();

            // Merge the two environments
            env.putAll(mergeEnvironments(trueEnv, falseEnv));

            super.visitStmtIf(stmt);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtPrint(StmtNode.Print stmt) {
            visitExpr(stmt.getExpr());

            super.visitStmtPrint(stmt);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtReturn(StmtNode.Return stmt) {
            if (stmt.getExpr().isPresent()) {
                checkSubtype(stmt.getExpr().get(), env.getUnchecked(RETURN));
            }

            super.visitStmtReturn(stmt);
        }

        private void checkEquivalent(ExprNode expr, Type expected) {
            visitExpr(expr);

            Type type = getType(expr);

            if (!Types.isEquivalent(type, expected)) {
                Optional<Attribute.Source> opt = Attributes.getAttribute(expr,
                        Attribute.Source.class);

                if (opt.isPresent()) {
                    Attribute.Source source = opt.get();

                    throw CompilerErrors.invalidType(type.toString(), expected.toString(),
                            source.getSource(), source.getLine(), source.getCol(),
                            source.getLength());
                } else {
                    throw CompilerErrors.invalidType(type.toString(), expected.toString());
                }
            }
        }

        private void checkSubtype(ExprNode expr, Type rhs) {
            visitExpr(expr);

            Type type = getType(expr);

            if (!Types.isSubtype(type, rhs)) {
                Optional<Attribute.Source> opt = Attributes.getAttribute(expr,
                        Attribute.Source.class);

                if (opt.isPresent()) {
                    Attribute.Source source = opt.get();

                    throw CompilerErrors.invalidType(type.toString(), rhs.toString(),
                            source.getSource(), source.getLine(), source.getCol(),
                            source.getLength());
                } else {
                    throw CompilerErrors.invalidType(type.toString(), rhs.toString());
                }
            }
        }
    }
}

