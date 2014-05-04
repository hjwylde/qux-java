package com.hjwylde.qux.internal.compiler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hjwylde.qux.util.Constants.QUX0_1_1;
import static com.hjwylde.qux.util.Op.ACC_PUBLIC;
import static com.hjwylde.qux.util.Op.ACC_STATIC;

import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.internal.antlr.QuxBaseVisitor;
import com.hjwylde.qux.internal.antlr.QuxParser;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Op;
import com.hjwylde.qux.util.Type;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<StmtNode> visitBlock(@NotNull QuxParser.BlockContext ctx) {
        List<StmtNode> stmts = new ArrayList<>();
        for (QuxParser.StmtContext sctx : ctx.stmt()) {
            stmts.add(visitStmt(sctx));
        }

        return stmts;
    }

    /**
     * {@inheritDoc}
     */
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

        Type.Function functionType = Type.forFunction(returnType, parameterTypes.toArray(
                new Type[0]));

        fv = qv.visitFunction(ACC_PUBLIC | ACC_STATIC, name, functionType);

        for (int i = 0; i < parameterNames.size(); i++) {
            fv.visitParameter(parameterNames.get(i), parameterTypes.get(i));
        }

        fv.visitReturnType(functionType.getReturnType());

        for (StmtNode stmt : visitBlock(ctx.block())) {
            stmt.accept(fv);
        }

        fv.visitEnd();
        fv = null;

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExpr(@NotNull QuxParser.ExprContext ctx) {
        return (ExprNode) super.visitExpr(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprAccess(@NotNull QuxParser.ExprAccessContext ctx) {
        Token start = ctx.exprTerm().getStart();

        ExprNode target = visitExprTerm(ctx.exprTerm());

        if (ctx.expr().isEmpty()) {
            return target;
        }

        // Create a series of nested accesses
        for (QuxParser.ExprContext ectx : ctx.expr()) {
            // TODO: VERIFY: The "ectx.getStop()" may not include the suffix "]'
            target = new ExprNode.Access(target, visitExpr(ectx), generateAttributeSource(start,
                    ectx.getStop()));
        }

        return target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary(@NotNull QuxParser.ExprBinaryContext ctx) {
        ParserRuleContext start = ctx.exprUnary();
        QuxParser.ExprContext end;

        ExprNode expr = visitExprUnary(ctx.exprUnary());

        for (int i = 0; i < ctx.expr().size(); i++) {
            Op.Binary op;

            if (ctx.BOP_ADD(i) != null) {
                op = Op.Binary.ADD;
            } else if (ctx.BOP_AND(i) != null) {
                op = Op.Binary.AND;
            } else if (ctx.BOP_DIV(i) != null) {
                op = Op.Binary.DIV;
            } else if (ctx.BOP_EQ(i) != null) {
                op = Op.Binary.EQ;
            } else if (ctx.BOP_GT(i) != null) {
                op = Op.Binary.GT;
            } else if (ctx.BOP_GTE(i) != null) {
                op = Op.Binary.GTE;
            } else if (ctx.BOP_IFF(i) != null) {
                op = Op.Binary.IFF;
            } else if (ctx.BOP_IMPLIES(i) != null) {
                op = Op.Binary.IMPLIES;
            } else if (ctx.BOP_LT(i) != null) {
                op = Op.Binary.LT;
            } else if (ctx.BOP_LTE(i) != null) {
                op = Op.Binary.LTE;
            } else if (ctx.BOP_MUL(i) != null) {
                op = Op.Binary.MUL;
            } else if (ctx.BOP_NEQ(i) != null) {
                op = Op.Binary.NEQ;
            } else if (ctx.BOP_OR(i) != null) {
                op = Op.Binary.OR;
            } else if (ctx.BOP_REM(i) != null) {
                op = Op.Binary.REM;
            } else if (ctx.BOP_SUB(i) != null) {
                op = Op.Binary.SUB;
            } else if (ctx.BOP_XOR(i) != null) {
                op = Op.Binary.XOR;
            } else {
                throw new MethodNotImplementedError(ctx.getText());
            }

            end = ctx.expr(i);

            expr = new ExprNode.Binary(op, expr, visitExpr(end), generateAttributeSource(start,
                    end));
        }

        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBrace(@NotNull QuxParser.ExprBraceContext ctx) {
        List<ExprNode> values = new ArrayList<>();
        for (QuxParser.ExprContext ectx : ctx.expr()) {
            values.add(visitExpr(ectx));
        }

        return new ExprNode.Set(values, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBracket(@NotNull QuxParser.ExprBracketContext ctx) {
        List<ExprNode> values = new ArrayList<>();
        for (QuxParser.ExprContext ectx : ctx.expr()) {
            values.add(visitExpr(ectx));
        }

        return new ExprNode.List(values, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode.Function visitExprFunction(@NotNull QuxParser.ExprFunctionContext ctx) {
        String name = ctx.Identifier().getText();

        List<ExprNode> arguments = new ArrayList<>();
        for (QuxParser.ExprContext expr : ctx.expr()) {
            arguments.add(visitExpr(expr));
        }

        return new ExprNode.Function(name, arguments, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprLength(@NotNull QuxParser.ExprLengthContext ctx) {
        return new ExprNode.Unary(Op.Unary.LEN, visitExprAccess(ctx.exprAccess()),
                generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprParen(@NotNull QuxParser.ExprParenContext ctx) {
        return visitExpr(ctx.expr());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprTerm(@NotNull QuxParser.ExprTermContext ctx) {
        if (ctx.value() != null) {
            return visitValue(ctx.value());
        } else if (ctx.exprBrace() != null) {
            return visitExprBrace(ctx.exprBrace());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprUnary(@NotNull QuxParser.ExprUnaryContext ctx) {
        Op.Unary op;

        if (ctx.UOP_NEG() != null) {
            op = Op.Unary.NEG;
        } else if (ctx.UOP_NOT() != null) {
            op = Op.Unary.NOT;
        } else if (ctx.exprLength() != null) {
            return visitExprLength(ctx.exprLength());
        } else {
            return visitExprAccess(ctx.exprAccess());
        }

        return new ExprNode.Unary(op, visitExprAccess(ctx.exprAccess()), generateAttributeSource(
                ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode.Variable visitExprVariable(@NotNull QuxParser.ExprVariableContext ctx) {
        return new ExprNode.Variable(ctx.Identifier().getText(), generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitFile(@NotNull QuxParser.FileContext ctx) {
        qv.visit(QUX0_1_1, name);

        super.visitFile(ctx);

        qv.visitEnd();

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode visitStmt(@NotNull QuxParser.StmtContext ctx) {
        return (StmtNode) super.visitStmt(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode visitStmtAccessAssign(@NotNull QuxParser.StmtAccessAssignContext ctx) {
        Token start = ctx.getStart();

        ExprNode access = new ExprNode.Variable(ctx.Identifier().getText(), generateAttributeSource(
                start));

        // Create a series of nested accesses
        for (int i = 0; i < ctx.expr().size() - 1; i++) {
            // TODO: VERIFY: The "ctx.expr(i).getStop()" may not include the suffix "]'
            access = new ExprNode.Access(access, visitExpr(ctx.expr(i)), generateAttributeSource(
                    start, ctx.expr(i).getStop()));
        }

        ExprNode expr = visitExpr(ctx.expr(ctx.expr().size() - 1));

        return new StmtNode.AccessAssign((ExprNode.Access) access, expr, generateAttributeSource(
                ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode.Assign visitStmtAssign(@NotNull QuxParser.StmtAssignContext ctx) {
        String var = ctx.Identifier().getText();

        ExprNode expr = visitExpr(ctx.expr());

        return new StmtNode.Assign(var, expr, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode.For visitStmtFor(@NotNull QuxParser.StmtForContext ctx) {
        String var = ctx.Identifier().getText();

        ExprNode expr = visitExpr(ctx.expr());

        List<StmtNode> body = visitBlock(ctx.block());

        return new StmtNode.For(var, expr, body, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode.If visitStmtIf(@NotNull QuxParser.StmtIfContext ctx) {
        ExprNode condition = visitExpr(ctx.expr());

        List<StmtNode> trueBlock = visitBlock(ctx.block(0));
        List<StmtNode> falseBlock = new ArrayList<>();
        if (ctx.block(1) != null) {
            falseBlock.addAll(visitBlock(ctx.block(1)));
        }

        return new StmtNode.If(condition, trueBlock, falseBlock, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode.Print visitStmtPrint(@NotNull QuxParser.StmtPrintContext ctx) {
        ExprNode expr = visitExpr(ctx.expr());

        return new StmtNode.Print(expr, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode.Return visitStmtReturn(@NotNull QuxParser.StmtReturnContext ctx) {
        ExprNode expr = null;
        if (ctx.expr() != null) {
            expr = visitExpr(ctx.expr());
        }

        return new StmtNode.Return(expr, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visitType(@NotNull QuxParser.TypeContext ctx) {
        return (Type) super.visitType(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visitTypeKeyword(@NotNull QuxParser.TypeKeywordContext ctx) {
        return getType(ctx.getText().toLowerCase(Locale.ENGLISH));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visitTypeList(@NotNull QuxParser.TypeListContext ctx) {
        Type innerType = visitType(ctx.type());

        return Type.forList(innerType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visitTypeReturn(@NotNull QuxParser.TypeReturnContext ctx) {
        if (ctx.VOID() != null) {
            return Type.TYPE_VOID;
        }

        return (Type) super.visitTypeReturn(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visitTypeSet(@NotNull QuxParser.TypeSetContext ctx) {
        Type innerType = visitType(ctx.type());

        return Type.forSet(innerType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type visitTypeTerm(@NotNull QuxParser.TypeTermContext ctx) {
        return (Type) super.visitTypeTerm(ctx);
    }

    /**
     * {@inheritDoc}
     */
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
            return new ExprNode.Constant(type, value, generateAttributeSource(ctx));
        }

        throw new MethodNotImplementedError(ctx.getText());
    }

    private Attribute.Source generateAttributeSource(ParserRuleContext start,
            ParserRuleContext end) {
        return generateAttributeSource(start.getStart(), end.getStop());
    }

    private Attribute.Source generateAttributeSource(ParserRuleContext ctx) {
        return generateAttributeSource(ctx.getStart(), ctx.getStop());
    }

    private Attribute.Source generateAttributeSource(Token start, Token end) {
        int line = start.getLine();
        int col = start.getCharPositionInLine();
        int length = (end.getStopIndex() + 1) - start.getStartIndex();

        return new Attribute.Source(name, line, col, length);
    }

    private Attribute.Source generateAttributeSource(Token token) {
        return generateAttributeSource(token, token);
    }

    private static Type getType(String type) {
        switch (type) {
            case "any":
                return Type.TYPE_ANY;
            case "bool":
                return Type.TYPE_BOOL;
            case "int":
                return Type.TYPE_INT;
            case "null":
                return Type.TYPE_NULL;
            case "real":
                return Type.TYPE_REAL;
            case "str":
                return Type.TYPE_STR;
            case "void":
                return Type.TYPE_VOID;
        }

        if (type.startsWith("[") && type.endsWith("]")) {
            String innerType = type.substring(1, type.length() - 1);

            return Type.forList(getType(innerType));
        }

        throw new MethodNotImplementedError(type);
    }
}
