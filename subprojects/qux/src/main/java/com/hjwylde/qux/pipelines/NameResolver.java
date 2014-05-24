package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.FunctionAdapter;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxAdapter;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.Node;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.1
 */
public final class NameResolver extends Pipeline {

    private QuxNode node;

    public NameResolver(QuxContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuxNode apply(QuxNode node) {
        this.node = node;

        QuxNode resolved = new QuxNode();
        QuxVisitor qv = new QuxNameResolver(resolved);

        node.accept(qv);

        return resolved;
    }

    static void checkInstance(ExprNode expr, Class<? extends Node> clazz) {
        if (clazz.isInstance(expr)) {
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(expr, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.invalidInstance(expr.getClass().getSimpleName(),
                    clazz.getSimpleName(), source.getSource(), source.getLine(), source.getCol(),
                    source.getLength());
        } else {
            throw CompilerErrors.invalidInstance(expr.getClass().getSimpleName(),
                    clazz.getSimpleName());
        }
    }

    private String resolveClass(String name) {
        if (name.contains(".")) {
            return name;
        }

        for (String id : node.getImports()) {
            if (id.endsWith("." + name)) {
                return id;
            }
        }

        throw CompilerErrors.noClassFound(name);
    }

    private String resolveFunction(String name) {
        for (FunctionNode function : node.getFunctions()) {
            if (name.equals(function.getName())) {
                return node.getId() + "$" + name;
            }
        }

        for (String id : node.getImports()) {
            if (id.endsWith("$" + name)) {
                return id;
            }
        }

        throw CompilerErrors.noFunctionFound(node.getId(), name);
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.1
     */
    private final class FunctionNameResolver extends FunctionAdapter implements ExprVisitor {

        private final Map<String, String> namespace = new HashMap<>();

        private ExprNode current;

        private FunctionNameResolver(FunctionVisitor next) {
            super(next);
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitCode() {
            namespace.clear();
        }

        public void visitExpr(ExprNode expr) {
            expr.accept(this);
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprAccess(ExprNode.Access expr) {
            visitExpr(expr.getTarget());
            ExprNode target = current;
            visitExpr(expr.getIndex());
            ExprNode index = current;

            current = new ExprNode.Access(target, index, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprBinary(ExprNode.Binary expr) {
            visitExpr(expr.getLhs());
            ExprNode lhs = current;
            visitExpr(expr.getRhs());
            ExprNode rhs = current;

            current = new ExprNode.Binary(expr.getOp(), lhs, rhs, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprConstant(ExprNode.Constant expr) {
            current = expr;
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprExternal(ExprNode.External expr) {
            visitExpr(expr.getMeta());
            ExprNode.Meta meta = (ExprNode.Meta) current;

            List<ExprNode> arguments = new ArrayList<>();
            for (ExprNode argument : expr.getFunction().getArguments()) {
                visitExpr(argument);
                arguments.add(current);
            }

            ExprNode.Function function = new ExprNode.Function(expr.getFunction().getName(),
                    arguments, expr.getAttributes());

            current = new ExprNode.External(meta, function, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprFunction(ExprNode.Function expr) {
            String id = resolveFunction(expr.getName());
            String owner = id.substring(0, id.lastIndexOf("$"));
            ExprNode.Meta meta = new ExprNode.Meta(owner, Attributes.getAttributes(expr,
                    Attribute.Source.class));

            List<ExprNode> arguments = new ArrayList<>();
            for (ExprNode argument : expr.getArguments()) {
                visitExpr(argument);
                arguments.add(current);
            }

            ExprNode.Function function = new ExprNode.Function(expr.getName(), arguments,
                    expr.getAttributes());

            current = new ExprNode.External(meta, function, Attributes.getAttributes(expr,
                    Attribute.Source.class));
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprList(ExprNode.List expr) {
            List<ExprNode> values = new ArrayList<>();
            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
                values.add(current);
            }

            current = new ExprNode.List(values, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprMeta(ExprNode.Meta expr) {
            String id = resolveClass(expr.getId());

            current = new ExprNode.Meta(id, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprSet(ExprNode.Set expr) {
            List<ExprNode> values = new ArrayList<>();
            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
                values.add(current);
            }

            current = new ExprNode.Set(values, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprSlice(ExprNode.Slice expr) {
            visitExpr(expr.getTarget());
            ExprNode target = current;

            ExprNode from = null;
            if (expr.getFrom().isPresent()) {
                visitExpr(expr.getFrom().get());
                from = current;
            }

            ExprNode to = null;
            if (expr.getTo().isPresent()) {
                visitExpr(expr.getTo().get());
                to = current;
            }

            current = new ExprNode.Slice(target, from, to, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprUnary(ExprNode.Unary expr) {
            visitExpr(expr.getTarget());
            ExprNode target = current;

            current = new ExprNode.Unary(expr.getOp(), target, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprVariable(ExprNode.Variable expr) {
            current = expr;
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitStmtAccessAssign(StmtNode.AccessAssign stmt) {
            super.visitStmtAccessAssign(resolveStmtAccessAssign(stmt));
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitStmtAssign(StmtNode.Assign stmt) {
            super.visitStmtAssign(resolveStmtAssign(stmt));
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitStmtExpr(StmtNode.Expr stmt) {
            super.visitStmtExpr(resolveStmtExpr(stmt));
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitStmtFor(StmtNode.For stmt) {
            super.visitStmtFor(resolveStmtFor(stmt));
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitStmtIf(StmtNode.If stmt) {
            super.visitStmtIf(resolveStmtIf(stmt));
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitStmtPrint(StmtNode.Print stmt) {
            super.visitStmtPrint(resolveStmtPrint(stmt));
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitStmtReturn(StmtNode.Return stmt) {
            super.visitStmtReturn(resolveStmtReturn(stmt));
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitStmtWhile(StmtNode.While stmt) {
            super.visitStmtWhile(resolveStmtWhile(stmt));
        }

        private String resolveClass(String id) {
            if (!namespace.containsKey(id)) {
                namespace.put(id, NameResolver.this.resolveClass(id));
            }

            return namespace.get(id);
        }

        private String resolveFunction(String name) {
            String id = "$" + name;

            if (!namespace.containsKey(id)) {
                namespace.put(id, NameResolver.this.resolveFunction(name));
            }

            return namespace.get(id);
        }

        private StmtNode resolveStmt(StmtNode stmt) {
            if (stmt instanceof StmtNode.AccessAssign) {
                return resolveStmtAccessAssign((StmtNode.AccessAssign) stmt);
            } else if (stmt instanceof StmtNode.Assign) {
                return resolveStmtAssign((StmtNode.Assign) stmt);
            } else if (stmt instanceof StmtNode.Expr) {
                return resolveStmtExpr((StmtNode.Expr) stmt);
            } else if (stmt instanceof StmtNode.For) {
                return resolveStmtFor((StmtNode.For) stmt);
            } else if (stmt instanceof StmtNode.If) {
                return resolveStmtIf((StmtNode.If) stmt);
            } else if (stmt instanceof StmtNode.Print) {
                return resolveStmtPrint((StmtNode.Print) stmt);
            } else if (stmt instanceof StmtNode.Return) {
                return resolveStmtReturn((StmtNode.Return) stmt);
            } else if (stmt instanceof StmtNode.While) {
                return resolveStmtWhile((StmtNode.While) stmt);
            }

            throw new MethodNotImplementedError(stmt.getClass().toString());
        }

        private StmtNode.AccessAssign resolveStmtAccessAssign(StmtNode.AccessAssign stmt) {
            visitExpr(stmt.getAccess());
            ExprNode.Access access = (ExprNode.Access) current;
            visitExpr(stmt.getExpr());
            ExprNode expr = current;

            return new StmtNode.AccessAssign(access, expr, stmt.getAttributes());
        }

        private StmtNode.Assign resolveStmtAssign(StmtNode.Assign stmt) {
            visitExpr(stmt.getExpr());
            ExprNode expr = current;

            return new StmtNode.Assign(stmt.getVar(), expr, stmt.getAttributes());
        }

        private StmtNode.Expr resolveStmtExpr(StmtNode.Expr stmt) {
            visitExpr(stmt.getExpr());
            ExprNode expr = current;

            StmtNode.Expr.Type type = null;
            if (expr instanceof ExprNode.External) {
                type = StmtNode.Expr.Type.EXTERNAL;
            } else if (expr instanceof ExprNode.Function) {
                type = StmtNode.Expr.Type.FUNCTION;
            } else if (expr instanceof ExprNode.Unary) {
                switch (((ExprNode.Unary) expr).getOp()) {
                    case DEC:
                        type = StmtNode.Expr.Type.DECREMENT;
                        break;
                    case INC:
                        type = StmtNode.Expr.Type.INCREMENT;
                }
            }

            if (type == null) {
                throw new MethodNotImplementedError(expr.getClass().toString());
            }

            return new StmtNode.Expr(type, expr, stmt.getAttributes());
        }

        private StmtNode.For resolveStmtFor(StmtNode.For stmt) {
            visitExpr(stmt.getExpr());
            ExprNode expr = current;

            List<StmtNode> body = new ArrayList<>();
            for (StmtNode inner : stmt.getBody()) {
                body.add(resolveStmt(inner));
            }

            return new StmtNode.For(stmt.getVar(), expr, body, stmt.getAttributes());
        }

        private StmtNode.If resolveStmtIf(StmtNode.If stmt) {
            visitExpr(stmt.getCondition());
            ExprNode condition = current;

            List<StmtNode> trueBlock = new ArrayList<>();
            for (StmtNode inner : stmt.getTrueBlock()) {
                trueBlock.add(resolveStmt(inner));
            }

            List<StmtNode> falseBlock = new ArrayList<>();
            for (StmtNode inner : stmt.getFalseBlock()) {
                falseBlock.add(resolveStmt(inner));
            }

            return new StmtNode.If(condition, trueBlock, falseBlock, stmt.getAttributes());
        }

        private StmtNode.Print resolveStmtPrint(StmtNode.Print stmt) {
            visitExpr(stmt.getExpr());
            ExprNode expr = current;

            return new StmtNode.Print(expr, stmt.getAttributes());
        }

        private StmtNode.Return resolveStmtReturn(StmtNode.Return stmt) {
            ExprNode expr = null;
            if (stmt.getExpr().isPresent()) {
                visitExpr(stmt.getExpr().get());
                expr = current;
            }

            return new StmtNode.Return(expr, stmt.getAttributes());
        }

        private StmtNode.While resolveStmtWhile(StmtNode.While stmt) {
            visitExpr(stmt.getCondition());
            ExprNode condition = current;

            List<StmtNode> body = new ArrayList<>();
            for (StmtNode inner : stmt.getBody()) {
                body.add(resolveStmt(inner));
            }

            return new StmtNode.While(condition, body, stmt.getAttributes());
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.1
     */
    private final class QuxNameResolver extends QuxAdapter {

        private QuxNameResolver(QuxVisitor next) {
            super(next);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FunctionVisitor visitFunction(int flags, String name, Type.Function type) {
            return new FunctionNameResolver(super.visitFunction(flags, name, type));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitImport(String id) {
            super.visitImport(id);
        }
    }
}
