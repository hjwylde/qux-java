package com.hjwylde.qux.internal.compiler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hjwylde.qux.util.Constants.QUX0_1_0;
import static com.hjwylde.qux.util.Op.ACC_PUBLIC;
import static com.hjwylde.qux.util.Op.ACC_STATIC;

import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.common.lang.annotation.Alpha;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.internal.antlr.QuxBaseVisitor;
import com.hjwylde.qux.internal.antlr.QuxParser;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Op;
import com.hjwylde.qux.util.Type;
import com.hjwylde.qux.util.Types;

import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Antlr2QuxTranslater extends QuxBaseVisitor<Object> {

    private final String name;

    private final QuxVisitor qv;
    private FunctionVisitor fv;

    public Antlr2QuxTranslater(String name, QuxVisitor qv) {
        this.name = checkNotNull(name, "name cannot be null");

        this.qv = checkNotNull(qv, "qv cannot be null");
    }

    @Override
    public List<StmtNode> visitBlock(@NotNull QuxParser.BlockContext ctx) {
        List<StmtNode> stmts = new ArrayList<>();
        for (QuxParser.StmtContext sctx : ctx.stmt()) {
            stmts.add(visitStmt(sctx));
        }

        return stmts;
    }

    @Override
    public Object visitDeclFunction(@NotNull QuxParser.DeclFunctionContext ctx) {
        String name = ctx.Identifier(0).getText();

        List<String> parameterNames = new ArrayList<>();
        for (int i = 1; i < ctx.Identifier().size(); i++) {
            parameterNames.add(ctx.Identifier(i).getText());
        }

        List<Type> parameterTypes = new ArrayList<>();
        for (QuxParser.TypeContext tctx : ctx.type()) {
            parameterTypes.add(visitType(tctx));
        }

        Type returnType = visitTypeReturn(ctx.typeReturn());

        Type functionType = Type.forFunction(returnType, parameterTypes.toArray(new Type[0]));

        fv = qv.visitFunction(ACC_PUBLIC | ACC_STATIC, name, functionType.getDescriptor());

        for (int i = 0; i < parameterNames.size(); i++) {
            fv.visitParameter(parameterNames.get(i), parameterTypes.get(i));
        }

        fv.visitReturnType(Types.getFunctionReturnType(functionType));

        for (StmtNode stmt : visitBlock(ctx.block())) {
            stmt.accept(fv);
        }

        fv.visitEnd();
        fv = null;

        return null;
    }

    @Override
    public ExprNode visitExpr(@NotNull QuxParser.ExprContext ctx) {
        return (ExprNode) super.visitExpr(ctx);
    }

    @Override
    public ExprNode visitExprBinary(@NotNull QuxParser.ExprBinaryContext ctx) {
        ExprNode expr = visitExprUnary(ctx.exprUnary());

        for (int i = 0; i < ctx.expr().size(); i++) {
            Op.Binary op;

            if (ctx.BOP_MUL(i) != null) {
                op = Op.Binary.MUL;
            } else if (ctx.BOP_DIV(i) != null) {
                op = Op.Binary.DIV;
            } else if (ctx.BOP_ADD(i) != null) {
                op = Op.Binary.ADD;
            } else if (ctx.BOP_SUB(i) != null) {
                op = Op.Binary.SUB;
            } else if (ctx.BOP_EQ(i) != null) {
                op = Op.Binary.EQ;
            } else if (ctx.BOP_NEQ(i) != null) {
                op = Op.Binary.NEQ;
            } else if (ctx.BOP_LT(i) != null) {
                op = Op.Binary.LT;
            } else if (ctx.BOP_LTE(i) != null) {
                op = Op.Binary.LTE;
            } else if (ctx.BOP_GT(i) != null) {
                op = Op.Binary.GT;
            } else if (ctx.BOP_GTE(i) != null) {
                op = Op.Binary.GTE;
            } else {
                throw new MethodNotImplementedError(ctx.getText());
            }

            expr = new ExprNode.Binary(op, expr, visitExpr(ctx.expr(i)));
        }

        return expr;
    }

    @Override
    public ExprNode visitExprBracket(@NotNull QuxParser.ExprBracketContext ctx) {
        List<ExprNode> values = new ArrayList<>();
        for (QuxParser.ExprContext ectx : ctx.expr()) {
            values.add(visitExpr(ectx));
        }

        return new ExprNode.List(values);
    }

    @Override
    public ExprNode.Function visitExprFunction(@NotNull QuxParser.ExprFunctionContext ctx) {
        String name = ctx.Identifier().getText();

        List<ExprNode> arguments = new ArrayList<>();
        for (QuxParser.ExprContext expr : ctx.expr()) {
            arguments.add(visitExpr(expr));
        }

        return new ExprNode.Function(name, arguments);
    }

    @Override
    public ExprNode visitExprParen(@NotNull QuxParser.ExprParenContext ctx) {
        return visitExpr(ctx.expr());
    }

    @Override
    public ExprNode visitExprTerm(@NotNull QuxParser.ExprTermContext ctx) {
        if (ctx.value() != null) {
            return visitValue(ctx.value());
        } else if (ctx.exprBracket() != null) {
            return visitExprBracket(ctx.exprBracket());
        } else if (ctx.exprFunction() != null) {
            return visitExprFunction(ctx.exprFunction());
        } else if (ctx.exprParen() != null) {
            return visitExprParen(ctx.exprParen());
        } else if (ctx.exprVariable() != null) {
            return visitExprVariable(ctx.exprVariable());
        } else {
            throw new MethodNotImplementedError(ctx.getText());
        }
    }

    @Override
    public ExprNode visitExprUnary(@NotNull QuxParser.ExprUnaryContext ctx) {
        Op.Unary op;

        if (ctx.UOP_NEGATE() != null) {
            op = Op.Unary.NEG;
        } else if (ctx.UOP_NOT() != null) {
            op = Op.Unary.NOT;
        } else {
            return visitExprTerm(ctx.exprTerm());
        }

        return new ExprNode.Unary(op, visitExprTerm(ctx.exprTerm()));
    }

    @Override
    public ExprNode.Variable visitExprVariable(@NotNull QuxParser.ExprVariableContext ctx) {
        return new ExprNode.Variable(ctx.Identifier().getText());
    }

    @Override
    public Object visitFile(@NotNull QuxParser.FileContext ctx) {
        qv.visit(QUX0_1_0, name);

        super.visitFile(ctx);

        qv.visitEnd();

        return null;
    }

    @Override
    public StmtNode visitStmt(@NotNull QuxParser.StmtContext ctx) {
        return (StmtNode) super.visitStmt(ctx);
    }

    @Override
    public StmtNode.Assign visitStmtAssign(@NotNull QuxParser.StmtAssignContext ctx) {
        String var = ctx.Identifier().getText();

        ExprNode expr = visitExpr(ctx.expr());

        return new StmtNode.Assign(var, expr);
    }

    @Override
    public StmtNode.If visitStmtIf(@NotNull QuxParser.StmtIfContext ctx) {
        ExprNode condition = visitExpr(ctx.expr());

        List<StmtNode> trueBlock = visitBlock(ctx.block(0));
        List<StmtNode> falseBlock = visitBlock(ctx.block(1));

        return new StmtNode.If(condition, trueBlock, falseBlock);
    }

    @Alpha
    @Override
    public StmtNode.Print visitStmtPrint(@NotNull QuxParser.StmtPrintContext ctx) {
        ExprNode expr = visitExpr(ctx.expr());

        return new StmtNode.Print(expr);
    }

    @Override
    public StmtNode.Return visitStmtReturn(@NotNull QuxParser.StmtReturnContext ctx) {
        ExprNode expr = null;
        if (ctx.expr() != null) {
            expr = visitExpr(ctx.expr());
        }

        return new StmtNode.Return(expr);
    }

    @Override
    public Type visitType(@NotNull QuxParser.TypeContext ctx) {
        return (Type) super.visitType(ctx);
    }

    @Override
    public Type visitTypeKeyword(@NotNull QuxParser.TypeKeywordContext ctx) {
        return getType(ctx.getText().toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Type visitTypeList(@NotNull QuxParser.TypeListContext ctx) {
        Type innerType = visitType(ctx.type());

        return Type.forList(innerType);
    }

    @Override
    public Type visitTypeReturn(@NotNull QuxParser.TypeReturnContext ctx) {
        if (ctx.VOID() != null) {
            return Type.TYPE_VOID;
        }

        return (Type) super.visitTypeReturn(ctx);
    }

    @Override
    public Type visitTypeTerm(@NotNull QuxParser.TypeTermContext ctx) {
        return (Type) super.visitTypeTerm(ctx);
    }

    @Override
    public ExprNode.Constant visitValue(@NotNull QuxParser.ValueContext ctx) {
        ExprNode.Constant.Type type = null;
        Object value = null;

        if (ctx.ValueInt() != null) {
            type = ExprNode.Constant.Type.INT;
            value = new BigInteger(ctx.ValueInt().getText());
        } else if (ctx.ValueReal() != null) {
            type = ExprNode.Constant.Type.REAL;
            value = new BigDecimal(ctx.ValueReal().getText());
        } else if (ctx.ValueString() != null) {
            type = ExprNode.Constant.Type.STR;
            String text = ctx.ValueString().getText();

            value = text.substring(1, text.length() - 1);
        } else if (ctx.ValueKeyword() != null) {
            switch (ctx.ValueKeyword().getText()) {
                case "false":
                    type = ExprNode.Constant.Type.BOOL;
                    value = false;
                    break;
                case "true":
                    type = ExprNode.Constant.Type.BOOL;
                    value = true;
                    break;
                case "null":
                    type = ExprNode.Constant.Type.NULL;
                    value = null;
            }
        }

        if (type != null) {
            return new ExprNode.Constant(type, value);
        }

        throw new MethodNotImplementedError(ctx.getText());
    }

    private static Type getType(String type) {
        return Type.of(getTypeDescriptor(type));
    }

    private static String getTypeDescriptor(String type) {
        switch (type) {
            case "bool":
                return Type.BOOL;
            case "int":
                return Type.INT;
            case "null":
                return Type.NULL;
            case "real":
                return Type.REAL;
            case "str":
                return Type.STR;
            case "void":
                return Type.VOID;
        }

        if (type.startsWith(Type.LIST_START)) {
            String innerType = type.substring(1, type.length() - 1);

            return Type.forList(getTypeDescriptor(innerType)).getDescriptor();
        }

        throw new MethodNotImplementedError(type);
    }
}
