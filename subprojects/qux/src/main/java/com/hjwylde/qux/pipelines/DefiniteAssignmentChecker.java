package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.FunctionAdapter;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxAdapter;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.builder.Environment;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;

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
    public FunctionVisitor visitFunction(int flags, String name, Type.Function type) {
        // TODO: Check if a function is declared twice
        FunctionVisitor fv = super.visitFunction(flags, name, type);

        FunctionDefiniteAssignmentChecker fvc = new FunctionDefiniteAssignmentChecker(fv);

        return fvc;
    }

    private static final class FunctionDefiniteAssignmentChecker extends FunctionAdapter
            implements ExprVisitor {

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

        public void visitExpr(ExprNode expr) {
            expr.accept(this);
        }

        @Override
        public void visitExprBinary(ExprNode.Binary expr) {
            visitExpr(expr.getLhs());
            visitExpr(expr.getRhs());
        }

        @Override
        public void visitExprConstant(ExprNode.Constant expr) {
            // Do nothing
        }

        @Override
        public void visitExprFunction(ExprNode.Function expr) {
            for (ExprNode argument : expr.getArguments()) {
                visitExpr(argument);
            }
        }

        @Override
        public void visitExprList(ExprNode.List expr) {
            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
            }
        }

        @Override
        public void visitExprUnary(ExprNode.Unary expr) {
            visitExpr(expr.getTarget());
        }

        @Override
        public void visitExprVariable(ExprNode.Variable expr) {
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

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitParameter(String var, Type type) {
            // TODO: Check if a parameter is declared twice

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
        public void visitStmtAssign(StmtNode.Assign stmt) {
            visitExpr(stmt.getExpr());

            env.put(stmt.getVar(), true);

            super.visitStmtAssign(stmt);
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
        public void visitStmtIf(StmtNode.If stmt) {
            visitExpr(stmt.getCondition());

            super.visitStmtIf(stmt);

            // TODO: Implement visitStmtIf(ExprNode, ImmutableList<StmtNode>, ImmutableList<StmtNode>)
            throw new MethodNotImplementedError();
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
                visitExpr(stmt.getExpr().get());
            }

            super.visitStmtReturn(stmt);
        }
    }
}

