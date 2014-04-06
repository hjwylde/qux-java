package com.hjwylde.qux.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.hjwylde.qux.util.Type.TYPE_BOOL;
import static com.hjwylde.qux.util.Type.TYPE_INT;
import static com.hjwylde.qux.util.Type.TYPE_NULL;
import static com.hjwylde.qux.util.Type.TYPE_REAL;
import static com.hjwylde.qux.util.Type.TYPE_STR;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qux.internal.builder.Environment;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class TypeChecker extends QuxAdapter {

    public TypeChecker() {
        super();
    }

    public TypeChecker(QuxVisitor next) {
        super(next);
    }

    @Override
    public FunctionVisitor visitFunction(int flags, String name, String desc) {
        FunctionVisitor fv = super.visitFunction(flags, name, desc);

        FunctionTypeChecker fvc = new FunctionTypeChecker(fv);

        return fvc;
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

    @SafeVarargs
    private static Environment<String, Type> mergeEnvironments(Environment<String, Type>... envs) {
        return mergeEnvironments(Arrays.asList(envs));
    }

    private static final class FunctionTypeChecker extends FunctionAdapter {

        private static final String RETURN = "$";

        private Environment<String, Type> env = new Environment<>();

        public FunctionTypeChecker(FunctionVisitor next) {
            super(next);
        }

        @Override
        public void visitCode() {
            // Clone the environment to keep a clear separation of parameters and variables
            env = env.push();

            super.visitCode();
        }

        @Override
        public void visitParameter(String var, Type type) {
            env.put(var, type);

            super.visitParameter(var, type);
        }

        @Override
        public void visitReturnType(Type type) {
            env.put(RETURN, type);

            super.visitReturnType(type);
        }

        @Override
        public void visitStmtAssign(String var, ExprNode expr) {
            visitExpr(expr);

            Attribute.Type attribute = Attributes.getAttributeUnchecked(expr, Attribute.Type.class);
            env.put(var, attribute.getType());

            super.visitStmtAssign(var, expr);
        }

        @Override
        public void visitStmtFunction(String name, ImmutableList<ExprNode> arguments) {
            for (ExprNode argument : arguments) {
                visitExpr(argument);
            }

            super.visitStmtFunction(name, arguments);

            // TODO: Implement visitStmtFunction(String, ImmutableList<ExprNode>)
            throw new MethodNotImplementedError();
        }

        @Override
        public void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
                ImmutableList<StmtNode> falseBlock) {
            visitExpr(condition);

            // Create a new environment for the true branch
            env = env.push();

            visitBlock(trueBlock);

            Environment<String, Type> trueEnv = env.pop();

            // Create a new environment for the false branch
            env = env.push();

            visitBlock(falseBlock);

            Environment<String, Type> falseEnv = env.pop();

            // Merge the two environments
            env.putAll(mergeEnvironments(trueEnv, falseEnv));

            super.visitStmtIf(condition, trueBlock, falseBlock);
        }

        @Override
        public void visitStmtPrint(ExprNode expr) {
            visitExpr(expr);

            super.visitStmtPrint(expr);
        }

        @Override
        public void visitStmtReturn(Optional<ExprNode> expr) {
            if (expr.isPresent()) {
                checkEqual(expr.get(), env.getUnchecked(RETURN));
            }

            super.visitStmtReturn(expr);
        }

        private void checkEqual(ExprNode expr, Type expected) {
            visitExpr(expr);

            Attribute.Type attribute = Attributes.getAttributeUnchecked(expr, Attribute.Type.class);

            if (!attribute.getType().equals(expected)) {
                throw CompilerErrors.invalidType(attribute.getType().toString(),
                        expected.toString());
            }
        }

        private void visitBlock(List<StmtNode> stmts) {
            for (StmtNode stmt : stmts) {
                stmt.accept(this);
            }
        }

        private void visitExpr(ExprNode expr) {
            // If the type has already been resovled
            if (Attributes.getAttribute(expr, Attribute.Type.class).isPresent()) {
                return;
            }

            if (expr instanceof ExprNode.Binary) {
                visitExprBinary((ExprNode.Binary) expr);
            } else if (expr instanceof ExprNode.Constant) {
                visitExprConstant((ExprNode.Constant) expr);
            } else if (expr instanceof ExprNode.Function) {
                visitExprFunction((ExprNode.Function) expr);
            } else if (expr instanceof ExprNode.List) {
                visitExprList((ExprNode.List) expr);
            } else if (expr instanceof ExprNode.Unary) {
                visitExprUnary((ExprNode.Unary) expr);
            } else if (expr instanceof ExprNode.Variable) {
                visitExprVariable((ExprNode.Variable) expr);
            } else {
                throw new MethodNotImplementedError(expr.getClass().toString());
            }
        }

        private void visitExprBinary(ExprNode.Binary expr) {
            visitExpr(expr.getLhs());
            visitExpr(expr.getRhs());

            Attribute.Type lhsAttribute = Attributes.getAttributeUnchecked(expr.getLhs(),
                    Attribute.Type.class);
            Attribute.Type rhsAttribute = Attributes.getAttributeUnchecked(expr.getRhs(),
                    Attribute.Type.class);

            switch (expr.getOp()) {
                case EQ:
                case NEQ:
                    expr.addAttribute(lhsAttribute);
                    break;
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                    checkEqual(expr.getRhs(), lhsAttribute.getType());
                    expr.addAttribute(lhsAttribute);
                    break;
                case GT:
                case GTE:
                case LT:
                case LTE:
                    checkEqual(expr.getRhs(), lhsAttribute.getType());
                    expr.addAttributes(new Attribute.Type(TYPE_BOOL));
                default:
                    throw new MethodNotImplementedError(expr.getOp().toString());
            }
        }

        private void visitExprConstant(ExprNode.Constant expr) {
            switch (expr.getType()) {
                case BOOL:
                    expr.addAttributes(new Attribute.Type(TYPE_BOOL));
                    break;
                case INT:
                    expr.addAttributes(new Attribute.Type(TYPE_INT));
                    break;
                case NULL:
                    expr.addAttributes(new Attribute.Type(TYPE_NULL));
                    break;
                case REAL:
                    expr.addAttributes(new Attribute.Type(TYPE_REAL));
                    break;
                case STR:
                    expr.addAttributes(new Attribute.Type(TYPE_STR));
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getType().toString());
            }
        }

        private void visitExprFunction(ExprNode.Function expr) {
            for (ExprNode argument : expr.getArguments()) {
                visitExpr(argument);
            }

            // TODO: Implement visitExprFunction(ExprNode.Function)
            throw new MethodNotImplementedError();
        }

        private void visitExprList(ExprNode.List expr) {
            // TODO: Implement visitExprList(ExprNode.List)
            throw new MethodNotImplementedError();
        }

        private void visitExprUnary(ExprNode.Unary expr) {
            visitExpr(expr.getTarget());

            Attribute.Type targetAttribute = Attributes.getAttributeUnchecked(expr.getTarget(),
                    Attribute.Type.class);

            switch (expr.getOp()) {
                case NEG:
                    expr.addAttribute(targetAttribute);
                    break;
                case NOT:
                    checkEqual(expr.getTarget(), TYPE_BOOL);
                    expr.addAttributes(new Attribute.Type(TYPE_BOOL));
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getOp().toString());
            }
        }

        private void visitExprVariable(ExprNode.Variable expr) {
            expr.addAttributes(new Attribute.Type(env.getUnchecked(expr.getName())));
        }
    }
}

