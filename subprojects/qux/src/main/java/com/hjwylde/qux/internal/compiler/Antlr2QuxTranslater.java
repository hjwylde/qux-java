package com.hjwylde.qux.internal.compiler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Verify.verify;
import static com.hjwylde.qux.util.Constants.QUX0_2_0;
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

import com.google.common.io.Files;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

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

    private final String source;

    private final QuxVisitor qv;
    private FunctionVisitor fv;

    /**
     * Creates a new {@code Antlr2QuxTranslater} with the given source file name and visitor. The
     * source file name should include the extension (if applicable).
     *
     * @param source the source file name (inclusive of extension).
     * @param qv the qux visitor.
     */
    public Antlr2QuxTranslater(String source, QuxVisitor qv) {
        this.source = checkNotNull(source, "source cannot be null");

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
        ExprNode target = visitExprTerm(ctx.exprTerm());

        if (ctx.exprAccess_1().isEmpty()) {
            return target;
        }

        // Create a series of nested accesses
        for (QuxParser.ExprAccess_1Context ectx : ctx.exprAccess_1()) {
            if (ectx.exprAccess_1_1() != null) {
                // target[index]
                ExprNode index = visitExpr(ectx.exprAccess_1_1().expr());

                target = new ExprNode.Access(target, index, generateAttributeSource(ctx, ectx));
            } else if (ectx.exprAccess_1_2() != null) {
                // target[low:high]
                ExprNode low = visitExpr(ectx.exprAccess_1_2().expr(0));
                ExprNode high = visitExpr(ectx.exprAccess_1_2().expr(1));

                target = new ExprNode.Slice(target, low, high, generateAttributeSource(ctx, ectx));
            } else if (ectx.exprAccess_1_3() != null) {
                // target[low:]
                ExprNode low = visitExpr(ectx.exprAccess_1_3().expr());

                target = new ExprNode.Slice(target, low, null, generateAttributeSource(ctx, ectx));
            } else if (ectx.exprAccess_1_4() != null) {
                // target[:high]
                ExprNode high = visitExpr(ectx.exprAccess_1_4().expr());

                target = new ExprNode.Slice(target, null, high, generateAttributeSource(ctx, ectx));
            } else if (ectx.exprAccess_1_5() != null) {
                // target[:]
                target = new ExprNode.Slice(target, null, null, generateAttributeSource(ctx, ectx));
            }
        }

        return target;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary(@NotNull QuxParser.ExprBinaryContext ctx) {
        return visitExprBinary_1(ctx.exprBinary_1());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary_1(@NotNull QuxParser.ExprBinary_1Context ctx) {
        ParserRuleContext start = ctx.exprBinary_2(0);
        QuxParser.ExprBinary_2Context end;

        ExprNode expr = visitExprBinary_2(ctx.exprBinary_2(0));

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode node = (TerminalNode) ctx.getChild(i);

            Op.Binary op;
            switch (node.getSymbol().getType()) {
                case QuxParser.BOP_IMP:
                    op = Op.Binary.IMP;
                    break;
                default:
                    throw new MethodNotImplementedError(node.getText());
            }

            end = ctx.exprBinary_2((i + 1) / 2);

            expr = new ExprNode.Binary(op, expr, visitExprBinary_2(end), generateAttributeSource(
                    start, end));
        }

        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary_2(@NotNull QuxParser.ExprBinary_2Context ctx) {
        ParserRuleContext start = ctx.exprBinary_3(0);
        QuxParser.ExprBinary_3Context end;

        ExprNode expr = visitExprBinary_3(ctx.exprBinary_3(0));

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode node = (TerminalNode) ctx.getChild(i);

            Op.Binary op;
            switch (node.getSymbol().getType()) {
                case QuxParser.BOP_IFF:
                    op = Op.Binary.IFF;
                    break;
                case QuxParser.BOP_XOR:
                    op = Op.Binary.XOR;
                    break;
                default:
                    throw new MethodNotImplementedError(node.getText());
            }

            end = ctx.exprBinary_3((i + 1) / 2);

            expr = new ExprNode.Binary(op, expr, visitExprBinary_3(end), generateAttributeSource(
                    start, end));
        }

        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary_3(@NotNull QuxParser.ExprBinary_3Context ctx) {
        ParserRuleContext start = ctx.exprBinary_4(0);
        QuxParser.ExprBinary_4Context end;

        ExprNode expr = visitExprBinary_4(ctx.exprBinary_4(0));

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode node = (TerminalNode) ctx.getChild(i);

            Op.Binary op;
            switch (node.getSymbol().getType()) {
                case QuxParser.BOP_AND:
                    op = Op.Binary.AND;
                    break;
                case QuxParser.BOP_OR:
                    op = Op.Binary.OR;
                    break;
                default:
                    throw new MethodNotImplementedError(node.getText());
            }

            end = ctx.exprBinary_4((i + 1) / 2);

            expr = new ExprNode.Binary(op, expr, visitExprBinary_4(end), generateAttributeSource(
                    start, end));
        }

        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary_4(@NotNull QuxParser.ExprBinary_4Context ctx) {
        ParserRuleContext start = ctx.exprBinary_5(0);
        QuxParser.ExprBinary_5Context end;

        ExprNode expr = visitExprBinary_5(ctx.exprBinary_5(0));

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode node = (TerminalNode) ctx.getChild(i);

            Op.Binary op;
            switch (node.getSymbol().getType()) {
                case QuxParser.BOP_EQ:
                    op = Op.Binary.EQ;
                    break;
                case QuxParser.BOP_NEQ:
                    op = Op.Binary.NEQ;
                    break;
                default:
                    throw new MethodNotImplementedError(node.getText());
            }

            end = ctx.exprBinary_5((i + 1) / 2);

            expr = new ExprNode.Binary(op, expr, visitExprBinary_5(end), generateAttributeSource(
                    start, end));
        }

        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary_5(@NotNull QuxParser.ExprBinary_5Context ctx) {
        ParserRuleContext start = ctx.exprBinary_6(0);
        QuxParser.ExprBinary_6Context end;

        ExprNode expr = visitExprBinary_6(ctx.exprBinary_6(0));

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode node = (TerminalNode) ctx.getChild(i);

            Op.Binary op;
            switch (node.getSymbol().getType()) {
                case QuxParser.BOP_GT:
                    op = Op.Binary.GT;
                    break;
                case QuxParser.BOP_GTE:
                    op = Op.Binary.GTE;
                    break;
                case QuxParser.BOP_LT:
                    op = Op.Binary.LT;
                    break;
                case QuxParser.BOP_LTE:
                    op = Op.Binary.LTE;
                    break;
                default:
                    throw new MethodNotImplementedError(node.getText());
            }

            end = ctx.exprBinary_6((i + 1) / 2);

            expr = new ExprNode.Binary(op, expr, visitExprBinary_6(end), generateAttributeSource(
                    start, end));
        }

        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary_6(@NotNull QuxParser.ExprBinary_6Context ctx) {
        ParserRuleContext start = ctx.exprBinary_7(0);
        QuxParser.ExprBinary_7Context end;

        ExprNode expr = visitExprBinary_7(ctx.exprBinary_7(0));

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode node = (TerminalNode) ctx.getChild(i);

            Op.Binary op;
            switch (node.getSymbol().getType()) {
                case QuxParser.BOP_ADD:
                    op = Op.Binary.ADD;
                    break;
                case QuxParser.BOP_SUB:
                    op = Op.Binary.SUB;
                    break;
                default:
                    throw new MethodNotImplementedError(node.getText());
            }

            end = ctx.exprBinary_7((i + 1) / 2);

            expr = new ExprNode.Binary(op, expr, visitExprBinary_7(end), generateAttributeSource(
                    start, end));
        }

        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary_7(@NotNull QuxParser.ExprBinary_7Context ctx) {
        ParserRuleContext start = ctx.exprBinary_8(0);
        QuxParser.ExprBinary_8Context end;

        ExprNode expr = visitExprBinary_8(ctx.exprBinary_8(0));

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode node = (TerminalNode) ctx.getChild(i);

            Op.Binary op;
            switch (node.getSymbol().getType()) {
                case QuxParser.BOP_DIV:
                    op = Op.Binary.DIV;
                    break;
                case QuxParser.BOP_IDIV:
                    op = Op.Binary.IDIV;
                    break;
                case QuxParser.BOP_MUL:
                    op = Op.Binary.MUL;
                    break;
                case QuxParser.BOP_REM:
                    op = Op.Binary.REM;
                    break;
                default:
                    throw new MethodNotImplementedError(node.getText());
            }

            end = ctx.exprBinary_8((i + 1) / 2);

            expr = new ExprNode.Binary(op, expr, visitExprBinary_8(end), generateAttributeSource(
                    start, end));
        }

        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary_8(@NotNull QuxParser.ExprBinary_8Context ctx) {
        ParserRuleContext start = ctx.exprBinary_9(0);
        QuxParser.ExprBinary_9Context end;

        ExprNode expr = visitExprBinary_9(ctx.exprBinary_9(0));

        for (int i = 1; i < ctx.getChildCount(); i += 2) {
            TerminalNode node = (TerminalNode) ctx.getChild(i);

            Op.Binary op;
            switch (node.getSymbol().getType()) {
                case QuxParser.BOP_EXP:
                    op = Op.Binary.EXP;
                    break;
                default:
                    throw new MethodNotImplementedError(node.getText());
            }

            end = ctx.exprBinary_9((i + 1) / 2);

            expr = new ExprNode.Binary(op, expr, visitExprBinary_9(end), generateAttributeSource(
                    start, end));
        }

        return expr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprBinary_9(@NotNull QuxParser.ExprBinary_9Context ctx) {
        ExprNode lhs = visitExprUnary(ctx.exprUnary(0));

        if (ctx.exprUnary().size() == 1) {
            return lhs;
        }

        ExprNode rhs = visitExprUnary(ctx.exprUnary(1));

        return new ExprNode.Binary(Op.Binary.RNG, lhs, rhs, generateAttributeSource(ctx));
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
    public ExprNode visitExprDecrement(@NotNull QuxParser.ExprDecrementContext ctx) {
        ExprNode.Variable target = new ExprNode.Variable(ctx.Identifier().getText(),
                generateAttributeSource(ctx.Identifier()));

        return new ExprNode.Unary(Op.Unary.DEC, target, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitExprExternal(@NotNull QuxParser.ExprExternalContext ctx) {
        ExprNode.Meta meta = visitExprMeta(ctx.exprMeta());
        ExprNode.Function function = visitExprFunction(ctx.exprFunction());

        return new ExprNode.External(meta, function, generateAttributeSource(ctx));
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
    public ExprNode visitExprIncrement(@NotNull QuxParser.ExprIncrementContext ctx) {
        ExprNode.Variable target = new ExprNode.Variable(ctx.Identifier().getText(),
                generateAttributeSource(ctx.Identifier()));

        return new ExprNode.Unary(Op.Unary.INC, target, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode.Meta visitExprMeta(@NotNull QuxParser.ExprMetaContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(ctx.Identifier(0).getText());

        for (int i = 1; i < ctx.Identifier().size(); i++) {
            sb.append(".");
            sb.append(ctx.Identifier(i).getText());
        }

        return new ExprNode.Meta(sb.toString(), generateAttributeSource(ctx));
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
        } else if (ctx.exprDecrement() != null) {
            return visitExprDecrement(ctx.exprDecrement());
        } else if (ctx.exprExternal() != null) {
            return visitExprExternal(ctx.exprExternal());
        } else if (ctx.exprFunction() != null) {
            return visitExprFunction(ctx.exprFunction());
        } else if (ctx.exprIncrement() != null) {
            return visitExprIncrement(ctx.exprIncrement());
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
        } else if (!ctx.UOP_LEN().isEmpty()) {
            op = Op.Unary.LEN;
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
        qv.visit(QUX0_2_0, Files.getNameWithoutExtension(source));

        super.visitFile(ctx);

        qv.visitEnd();

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitImp(@NotNull QuxParser.ImpContext ctx) {
        Token start = ctx.Identifier(0).getSymbol();
        Token stop = ctx.Identifier(ctx.Identifier().size() - 1).getSymbol();

        String id = ctx.getText();
        id = id.substring(start.getCharPositionInLine() - 1,
                stop.getCharPositionInLine() + stop.getText().length() - 1);

        qv.visitImport(id);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object visitPkg(@NotNull QuxParser.PkgContext ctx) {
        StringBuilder sb = new StringBuilder();

        sb.append(ctx.Identifier(0).getText());

        for (int i = 1; i < ctx.Identifier().size(); i++) {
            sb.append(".");
            sb.append(ctx.Identifier(i));
        }

        qv.visitPackage(sb.toString());

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
        ExprNode access = new ExprNode.Variable(ctx.Identifier().getText(), generateAttributeSource(
                ctx.Identifier()));

        // Create a series of nested accesses
        for (int i = 0; i < ctx.expr().size() - 1; i++) {
            // TODO: VERIFY: The "ctx.expr(i).getStop()" may not include the suffix "]'
            access = new ExprNode.Access(access, visitExpr(ctx.expr(i)), generateAttributeSource(
                    ctx, ctx.expr(i)));
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

        TerminalNode node = null;
        Op.Binary op = null;
        if (ctx.AOP_ADD() != null) {
            node = ctx.AOP_ADD();
            op = Op.Binary.ADD;
        } else if (ctx.AOP_DIV() != null) {
            node = ctx.AOP_DIV();
            op = Op.Binary.DIV;
        } else if (ctx.AOP_MUL() != null) {
            node = ctx.AOP_MUL();
            op = Op.Binary.MUL;
        } else if (ctx.AOP_REM() != null) {
            node = ctx.AOP_REM();
            op = Op.Binary.REM;
        } else if (ctx.AOP_SUB() != null) {
            node = ctx.AOP_SUB();
            op = Op.Binary.SUB;
        } else if (ctx.AOP() == null) {
            throw new MethodNotImplementedError(ctx.getText());
        }

        if (op != null) {
            ExprNode varExpr = new ExprNode.Variable(var, generateAttributeSource(node));
            expr = new ExprNode.Binary(op, varExpr, expr, generateAttributeSource(node.getSymbol(),
                    ctx.getStop()));
        }

        return new StmtNode.Assign(var, expr, generateAttributeSource(ctx));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StmtNode.Expr visitStmtExpr(@NotNull QuxParser.StmtExprContext ctx) {
        if (ctx.exprDecrement() != null) {
            return new StmtNode.Expr(StmtNode.Expr.Type.DECREMENT, visitExprDecrement(
                    ctx.exprDecrement()), generateAttributeSource(ctx));
        } else if (ctx.exprExternal() != null) {
            return new StmtNode.Expr(StmtNode.Expr.Type.EXTERNAL, visitExprExternal(
                    ctx.exprExternal()), generateAttributeSource(ctx));
        } else if (ctx.exprFunction() != null) {
            return new StmtNode.Expr(StmtNode.Expr.Type.FUNCTION, visitExprFunction(
                    ctx.exprFunction()), generateAttributeSource(ctx));
        } else if (ctx.exprIncrement() != null) {
            return new StmtNode.Expr(StmtNode.Expr.Type.INCREMENT, visitExprIncrement(
                    ctx.exprIncrement()), generateAttributeSource(ctx));
        } else {
            throw new MethodNotImplementedError(ctx.getText());
        }
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
        StmtNode.If stmt = null;

        int index = ctx.block().size() - 1;

        List<StmtNode> falseBlock = new ArrayList<>();
        if (ctx.block().size() > ctx.expr().size()) {
            falseBlock = visitBlock(ctx.block(index--));
        }

        for (; index >= 0; index--) {
            QuxParser.ExprContext start = ctx.expr(index);

            ExprNode condition = visitExpr(ctx.expr(index));
            List<StmtNode> trueBlock = visitBlock(ctx.block(index));

            stmt = new StmtNode.If(condition, trueBlock, falseBlock, generateAttributeSource(start,
                    ctx));

            falseBlock = new ArrayList<>();
            falseBlock.add(stmt);
        }

        verify(stmt != null);

        return stmt;
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
    public StmtNode visitStmtWhile(@NotNull QuxParser.StmtWhileContext ctx) {
        ExprNode expr = visitExpr(ctx.expr());
        List<StmtNode> body = visitBlock(ctx.block());

        return new StmtNode.While(expr, body, generateAttributeSource(ctx));
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
    public ExprNode visitValue(@NotNull QuxParser.ValueContext ctx) {
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
        } else if (ctx.valueKeyword() != null) {
            return visitValueKeyword(ctx.valueKeyword());
        }

        if (type != null) {
            return new ExprNode.Constant(type, value, generateAttributeSource(ctx));
        }

        throw new MethodNotImplementedError(ctx.getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExprNode visitValueKeyword(@NotNull QuxParser.ValueKeywordContext ctx) {
        ExprNode.Constant.Type type = null;
        Object value = null;

        if (ctx.FALSE() != null) {
            type = ExprNode.Constant.Type.BOOL;
            value = false;
        } else if (ctx.NULL() != null) {
            type = ExprNode.Constant.Type.NULL;
            value = null;
        } else if (ctx.TRUE() != null) {
            type = ExprNode.Constant.Type.BOOL;
            value = true;
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

        return new Attribute.Source(source, line, col, length);
    }

    private Attribute.Source generateAttributeSource(TerminalNode node) {
        return generateAttributeSource(node.getSymbol());
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
