package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.api.ConstantAdapter;
import com.hjwylde.qux.api.ConstantVisitor;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.FunctionAdapter;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxAdapter;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.tree.ConstantNode;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Type;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.2.1
 */
public final class NamePropagator extends Pipeline {

    private final Map<String, String> namespace = new HashMap<>();

    private QuxNode node;

    public NamePropagator(QuxContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuxNode apply(QuxNode node) {
        this.node = node;

        namespace.clear();

        QuxNode resolved = new QuxNode();
        QuxVisitor qv = new QuxNamePropagator(resolved);

        node.accept(qv);

        return resolved;
    }

    private ExprNode propagate(ExprNode expr, List<String> namespace) {
        ExprNamePropagator enp = new ExprNamePropagator(namespace);

        expr.accept(enp);

        return enp.getResult();
    }

    private String resolveClass(String name) {
        if (!namespace.containsKey(name)) {
            if (name.contains(".")) {
                namespace.put(name, name);
            } else {
                throw CompilerErrors.noClassFound(name);
            }
        }

        return namespace.get(name);
    }

    private Optional<String> resolveConstant(String name) {
        String key = "$" + name;

        if (!namespace.containsKey(key)) {
            for (ConstantNode constant : node.getConstants()) {
                if (constant.getName().equals(name)) {
                    namespace.put(key, node.getId() + key);
                    break;
                }
            }
        }

        return Optional.fromNullable(namespace.get(key));
    }

    private String resolveFunction(String name) {
        String key = "$" + name;

        CHECK:
        if (!namespace.containsKey(key)) {
            for (FunctionNode function : node.getFunctions()) {
                if (function.getName().equals(name)) {
                    namespace.put(key, node.getId() + key);
                    break CHECK;
                }
            }

            throw CompilerErrors.noFunctionFound(node.getId(), name);
        }

        return namespace.get(key);
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.2
     */
    private final class ConstantNamePropagator extends ConstantAdapter {

        private ConstantNamePropagator(ConstantVisitor next) {
            super(next);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExpr(ExprNode expr) {
            super.visitExpr(propagate(expr, Collections.<String>emptyList()));
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.2
     */
    private final class ExprNamePropagator implements ExprVisitor {

        private final ImmutableList<String> namespace;

        private ExprNode result;

        public ExprNamePropagator(List<String> namespace) {
            this.namespace = ImmutableList.copyOf(namespace);
        }

        public ExprNode getResult() {
            return result;
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
            ExprNode target = result;
            visitExpr(expr.getIndex());
            ExprNode index = result;

            result = new ExprNode.Access(target, index, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprBinary(ExprNode.Binary expr) {
            visitExpr(expr.getLhs());
            ExprNode lhs = result;
            visitExpr(expr.getRhs());
            ExprNode rhs = result;

            result = new ExprNode.Binary(expr.getOp(), lhs, rhs, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprConstant(ExprNode.Constant expr) {
            result = expr;
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprExternal(ExprNode.External expr) {
            visitExpr(expr.getMeta());
            ExprNode.Meta meta = (ExprNode.Meta) result;

            switch (expr.getType()) {
                case CONSTANT:
                    result = expr.getExpr();
                    break;
                case FUNCTION:
                    ExprNode.Function function = (ExprNode.Function) expr.getExpr();

                    List<ExprNode> arguments = new ArrayList<>();
                    for (ExprNode argument : function.getArguments()) {
                        visitExpr(argument);
                        arguments.add(result);
                    }

                    result = new ExprNode.Function(function.getName(), arguments,
                            expr.getAttributes());
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getType().toString());
            }

            result = new ExprNode.External(expr.getType(), meta, result, Attributes.getAttributes(
                    expr, Attribute.Source.class));
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
                arguments.add(result);
            }

            ExprNode.Function function = new ExprNode.Function(expr.getName(), arguments,
                    expr.getAttributes());

            result = new ExprNode.External(ExprNode.External.Type.FUNCTION, meta, function,
                    Attributes.getAttributes(expr, Attribute.Source.class));
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprList(ExprNode.List expr) {
            List<ExprNode> values = new ArrayList<>();
            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
                values.add(result);
            }

            result = new ExprNode.List(values, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprMeta(ExprNode.Meta expr) {
            String id = resolveClass(expr.getId());

            result = new ExprNode.Meta(id, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprRecord(ExprNode.Record expr) {
            Map<String, ExprNode> fields = new HashMap<>();
            for (Map.Entry<String, ExprNode> field : expr.getFields().entrySet()) {
                visitExpr(field.getValue());
                fields.put(field.getKey(), result);
            }

            result = new ExprNode.Record(fields, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprRecordAccess(ExprNode.RecordAccess expr) {
            visitExpr(expr.getTarget());

            result = new ExprNode.RecordAccess(result, expr.getField(), expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprSet(ExprNode.Set expr) {
            List<ExprNode> values = new ArrayList<>();
            for (ExprNode value : expr.getValues()) {
                visitExpr(value);
                values.add(result);
            }

            result = new ExprNode.Set(values, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprSlice(ExprNode.Slice expr) {
            visitExpr(expr.getTarget());
            ExprNode target = result;

            ExprNode from = null;
            if (expr.getFrom().isPresent()) {
                visitExpr(expr.getFrom().get());
                from = result;
            }

            ExprNode to = null;
            if (expr.getTo().isPresent()) {
                visitExpr(expr.getTo().get());
                to = result;
            }

            result = new ExprNode.Slice(target, from, to, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprUnary(ExprNode.Unary expr) {
            visitExpr(expr.getTarget());
            ExprNode target = result;

            result = new ExprNode.Unary(expr.getOp(), target, expr.getAttributes());
        }

        /**
         * {@inheritDoc
         */
        @Override
        public void visitExprVariable(ExprNode.Variable expr) {
            Optional<String> id = resolveConstant(expr.getName());
            if (!id.isPresent() || namespace.contains(expr.getName())) {
                result = expr;
                return;
            }

            String owner = id.get().substring(0, id.get().lastIndexOf("$"));
            ExprNode.Meta meta = new ExprNode.Meta(owner, Attributes.getAttributes(expr,
                    Attribute.Source.class));

            result = new ExprNode.External(ExprNode.External.Type.CONSTANT, meta, expr,
                    Attributes.getAttributes(expr, Attribute.Source.class));
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.1
     */
    private final class FunctionNamePropagator extends FunctionAdapter {

        private final List<String> parameters = new ArrayList<>();

        private FunctionNamePropagator(FunctionVisitor next) {
            super(next);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitParameter(String var, Type type) {
            parameters.add(var);

            super.visitParameter(var, type);
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

        private ExprNode propagate(ExprNode expr) {
            return NamePropagator.this.propagate(expr, parameters);
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
            ExprNode.Access access = (ExprNode.Access) propagate(stmt.getAccess());
            ExprNode expr = propagate(stmt.getExpr());

            return new StmtNode.AccessAssign(access, expr, stmt.getAttributes());
        }

        private StmtNode.Assign resolveStmtAssign(StmtNode.Assign stmt) {
            ExprNode expr = propagate(stmt.getExpr());

            return new StmtNode.Assign(stmt.getVar(), expr, stmt.getAttributes());
        }

        private StmtNode.Expr resolveStmtExpr(StmtNode.Expr stmt) {
            ExprNode expr = propagate(stmt.getExpr());

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
            ExprNode expr = propagate(stmt.getExpr());

            List<StmtNode> body = new ArrayList<>();
            for (StmtNode inner : stmt.getBody()) {
                body.add(resolveStmt(inner));
            }

            return new StmtNode.For(stmt.getVar(), expr, body, stmt.getAttributes());
        }

        private StmtNode.If resolveStmtIf(StmtNode.If stmt) {
            ExprNode condition = propagate(stmt.getCondition());

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
            ExprNode expr = propagate(stmt.getExpr());

            return new StmtNode.Print(expr, stmt.getAttributes());
        }

        private StmtNode.Return resolveStmtReturn(StmtNode.Return stmt) {
            ExprNode expr = null;
            if (stmt.getExpr().isPresent()) {
                expr = propagate(stmt.getExpr().get());
            }

            return new StmtNode.Return(expr, stmt.getAttributes());
        }

        private StmtNode.While resolveStmtWhile(StmtNode.While stmt) {
            ExprNode condition = propagate(stmt.getCondition());

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
    private final class QuxNamePropagator extends QuxAdapter {

        private QuxNamePropagator(QuxVisitor next) {
            super(next);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ConstantVisitor visitConstant(int flags, String name, Type type) {
            return new ConstantNamePropagator(super.visitConstant(flags, name, type));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public FunctionVisitor visitFunction(int flags, String name, Type.Function type) {
            return new FunctionNamePropagator(super.visitFunction(flags, name, type));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitImport(String id) {
            if (id.contains("$")) {
                namespace.put(id.substring(id.lastIndexOf("$")), id);
            } else if (id.contains(".")) {
                namespace.put(id.substring(id.lastIndexOf(".") + 1), id);
            } else {
                throw new InternalError("cannot import file from root package");
            }

            super.visitImport(id);
        }
    }
}
