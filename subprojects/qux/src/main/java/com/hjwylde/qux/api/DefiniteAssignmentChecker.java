package com.hjwylde.qux.api;

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

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class DefiniteAssignmentChecker extends QuxAdapter {

    public DefiniteAssignmentChecker() {
        super();
    }

    public DefiniteAssignmentChecker(QuxVisitor next) {
        super(next);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionVisitor visitFunction(int flags, String name, String desc) {
        FunctionVisitor fv = super.visitFunction(flags, name, desc);

        FunctionDefiniteAssignmentChecker fvc = new FunctionDefiniteAssignmentChecker(fv);

        return fvc;
    }

    private static final class FunctionDefiniteAssignmentChecker extends FunctionAdapter {

        private static final String RETURN = "$";

        private Environment<String, Boolean> env = new Environment<>();

        public FunctionDefiniteAssignmentChecker(FunctionVisitor next) {
            super(next);
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

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitParameter(String var, Type type) {
            env.put(var, true);

            super.visitParameter(var, type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitReturnType(Type type) {
            env.put(RETURN, true);

            super.visitReturnType(type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAssign(String var, ExprNode expr) {
            visitExpr(expr);

            env.put(var, true);

            super.visitStmtAssign(var, expr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFunction(String name, ImmutableList<ExprNode> arguments) {
            for (ExprNode argument : arguments) {
                visitExpr(argument);
            }

            super.visitStmtFunction(name, arguments);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
                ImmutableList<StmtNode> falseBlock) {
            visitExpr(condition);

            super.visitStmtIf(condition, trueBlock, falseBlock);

            // TODO: Implement visitStmtIf(ExprNode, ImmutableList<StmtNode>, ImmutableList<StmtNode>)
            throw new MethodNotImplementedError();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtPrint(ExprNode expr) {
            visitExpr(expr);

            super.visitStmtPrint(expr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtReturn(Optional<ExprNode> expr) {
            if (expr.isPresent()) {
                visitExpr(expr.get());
            }

            super.visitStmtReturn(expr);
        }

        private void visitExpr(ExprNode expr) {
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
        }

        private void visitExprConstant(ExprNode.Constant expr) {
            // Do nothing
        }

        private void visitExprFunction(ExprNode.Function expr) {
            for (ExprNode argument : expr.getArguments()) {
                visitExpr(argument);
            }
        }

        private void visitExprList(ExprNode.List expr) {
            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
            }
        }

        private void visitExprUnary(ExprNode.Unary expr) {
            visitExpr(expr.getTarget());
        }

        private void visitExprVariable(ExprNode.Variable expr) {
            // Check to see if the variable exists
            if (!env.contains(expr.getName())) {
                Optional<Attribute.Source> opt = Attributes.getAttribute(expr,
                        Attribute.Source.class);

                if (opt.isPresent()) {
                    Attribute.Source source = opt.get();

                    throw CompilerErrors.undeclaredVariableAccess(expr.getName(),
                            source.getSource(), source.getLine(), source.getCol(),
                            source.getLength());
                } else {
                    throw CompilerErrors.undeclaredVariableAccess(expr.getName());
                }
            }
        }
    }
}

