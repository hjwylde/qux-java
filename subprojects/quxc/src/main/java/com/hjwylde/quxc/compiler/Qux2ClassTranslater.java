package com.hjwylde.quxc.compiler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hjwylde.qux.util.Type.ANY;
import static com.hjwylde.qux.util.Type.BOOL;
import static com.hjwylde.qux.util.Type.INT;
import static com.hjwylde.qux.util.Type.NULL;
import static com.hjwylde.qux.util.Type.REAL;
import static com.hjwylde.qux.util.Type.STR;
import static com.hjwylde.qux.util.Type.VOID;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.BASTORE;
import static org.objectweb.asm.Opcodes.BIPUSH;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.ICONST_2;
import static org.objectweb.asm.Opcodes.ICONST_3;
import static org.objectweb.asm.Opcodes.ICONST_4;
import static org.objectweb.asm.Opcodes.ICONST_5;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.IF_ACMPEQ;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LCONST_1;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SIPUSH;
import static org.objectweb.asm.Opcodes.T_BYTE;
import static org.objectweb.asm.Opcodes.V1_7;

import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Types;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import qux.lang.Bool;
import qux.lang.Int;
import qux.lang.List;
import qux.lang.Null;
import qux.lang.Obj;
import qux.lang.Real;
import qux.lang.Str;
import qux.lang.operators.Add;
import qux.lang.operators.Div;
import qux.lang.operators.Eq;
import qux.lang.operators.Gt;
import qux.lang.operators.Gte;
import qux.lang.operators.Lt;
import qux.lang.operators.Lte;
import qux.lang.operators.Mul;
import qux.lang.operators.Neg;
import qux.lang.operators.Neq;
import qux.lang.operators.Not;
import qux.lang.operators.Sub;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Qux2ClassTranslater extends QuxVisitor {

    // TODO: Change from depending on qrt to using a ClassLoader and getting the runtime files that way
    // This will mean the user can specify the runtime library to compile against
    // May need to do some checks about the version of the runtime library though

    private static final Logger logger = LoggerFactory.getLogger(QuxVisitor.class);

    private final String name;

    private final ClassVisitor cv;

    public Qux2ClassTranslater(String name, ClassVisitor cv) {
        this.name = checkNotNull(name, "fileName cannot be null");

        this.cv = checkNotNull(cv, "cv cannot be null");
    }

    @Override
    public void visit(int version, String name) {
        cv.visit(V1_7, ACC_PUBLIC | ACC_FINAL, name, null, Type.getInternalName(Obj.class),
                new String[0]);
    }

    @Override
    public void visitEnd() {
        cv.visitEnd();
    }

    @Override
    public FunctionVisitor visitFunction(int flags, String name, String desc) {
        MethodVisitor mv = cv.visitMethod(flags, name, getTypeFromQuxType(desc), null,
                new String[0]);

        if (mv == null) {
            return FunctionVisitor.NULL_INSTANCE;
        }

        return new Function2ClassTranslater(mv, flags, name, desc);
    }

    static Type getTypeFromQuxType(com.hjwylde.qux.util.Type type) {
        return Type.getMethodType(getTypeFromQuxType(type.getDescriptor()));
    }

    static String getTypeFromQuxType(String desc) {
        switch (desc) {
            case ANY:
                return Type.getDescriptor(Obj.class);
            case BOOL:
                return Type.getDescriptor(Bool.class);
            case INT:
                return Type.getDescriptor(Int.class);
            case NULL:
                return Type.getDescriptor(Null.class);
            case REAL:
                return Type.getDescriptor(Real.class);
            case STR:
                return Type.getDescriptor(Str.class);
            case VOID:
                return "V";
        }

        if (Types.isFunction(desc)) {
            StringBuilder sb = new StringBuilder("(");

            for (com.hjwylde.qux.util.Type parameter : Types.getFunctionParameterTypes(desc)) {
                sb.append(getTypeFromQuxType(parameter));
            }

            sb.append(")");
            sb.append(getTypeFromQuxType(Types.getFunctionReturnType(desc)));

            return sb.toString();
        }

        if (Types.isList(desc)) {
            return Type.getDescriptor(List.class);
        }

        throw new InternalError("getTypeFromQuxType(String) not fully implemented: " + desc);
    }

    private final class Function2ClassTranslater extends FunctionVisitor {

        private static final String THIS = "this";

        private final MethodVisitor mv;

        private final int flags;
        private final String name;
        private final String desc;

        /**
         * Stores a map of parameter names to jvm types.
         */
        private Map<String, String> parameters = new HashMap<>();
        /**
         * Stores the return type as a jvm type.
         */
        private String returnType;

        /**
         * Stores a map of variable names to jvm local indices.
         * <p/>
         * TODO: Change this so that the number of local variables are minimized, i.e. can be used
         * more than once like for loop vars
         */
        private java.util.List<String> locals = new ArrayList<>();

        public Function2ClassTranslater(MethodVisitor mv, int flags, String name, String desc) {
            this.mv = checkNotNull(mv, "mv cannot be null");

            this.flags = flags;
            this.name = checkNotNull(name, "name cannot be null");
            this.desc = checkNotNull(desc, "desc cannot be null");

            if ((flags & ACC_STATIC) == 0) {
                locals.add(THIS);
            }
        }

        @Override
        public void visitCode() {
            mv.visitCode();
        }

        @Override
        public void visitEnd() {
            // If the type is Void, then add in a return instruction
            if (Types.isVoid(Types.getFunctionReturnType(desc))) {
                mv.visitInsn(RETURN);
            }

            // These values are ignored so long as ClassWriter.COMPUTE_MAXS is set
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        @Override
        public void visitParameter(String var, com.hjwylde.qux.util.Type type) {
            checkNotNull(var, "var cannot be null");
            checkNotNull(type, "type cannot be null");

            parameters.put(var, getTypeFromQuxType(type).getDescriptor());
            if (!locals.contains(var)) {
                locals.add(var);
            }
        }

        @Override
        public void visitReturnType(com.hjwylde.qux.util.Type type) {
            checkNotNull(type, "type cannot be null");

            returnType = getTypeFromQuxType(type).getDescriptor();
        }

        @Override
        public void visitStmtAssign(String var, ExprNode expr) {
            visitExpr(expr);

            if (!locals.contains(var)) {
                locals.add(var);
            }

            mv.visitVarInsn(ASTORE, locals.indexOf(var));
        }

        @Override
        public void visitStmtFunction(String name, ImmutableList<ExprNode> arguments) {
            // TODO: Consider changing ExprNode.type to an attribute

            Type[] argumentTypes = new Type[arguments.size()];
            for (int i = 0; i < arguments.size(); i++) {
                visitExpr(arguments.get(i));

                argumentTypes[i] = getTypeFromQuxType(arguments.get(i).getType());
            }

            mv.visitMethodInsn(INVOKESTATIC, name, name, Type.getMethodDescriptor(Type.VOID_TYPE,
                    argumentTypes), false);
        }

        @Override
        public void visitStmtIf(ExprNode condition, ImmutableList<StmtNode> trueBlock,
                ImmutableList<StmtNode> falseBlock) {
            visitExpr(condition);

            mv.visitFieldInsn(GETSTATIC, Type.getInternalName(Bool.class), "FALSE",
                    Type.getDescriptor(Bool.class));

            Label falseLabel = new Label();
            Label endLabel = new Label();

            mv.visitJumpInsn(IF_ACMPEQ, falseLabel);

            visitBlock(trueBlock);

            // Only need to visit this GOTO if there is a false block
            if (!falseBlock.isEmpty()) {
                mv.visitJumpInsn(GOTO, endLabel);
            }

            mv.visitLabel(falseLabel);

            // Only need to visit this block and label if there is a false block
            if (!falseBlock.isEmpty()) {
                visitBlock(falseBlock);

                mv.visitLabel(endLabel);
            }
        }

        @Override
        public void visitStmtPrint(ExprNode expr) {
            mv.visitFieldInsn(GETSTATIC, Type.getInternalName(System.class), "out",
                    Type.getDescriptor(PrintStream.class));

            visitExpr(expr);

            mv.visitMethodInsn(INVOKEVIRTUAL, getTypeFromQuxType(expr.getType()).getInternalName(),
                    "toString", Type.getMethodDescriptor(Type.getType(String.class)), false);
            try {
                mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(PrintStream.class),
                        "println", Type.getMethodDescriptor(PrintStream.class.getMethod("println",
                                String.class)), false
                );
            } catch (NoSuchMethodException e) {
                logger.error(e.getMessage(), e);

                throw new InternalError(e.getMessage());
            }
        }

        @Override
        public void visitStmtReturn(Optional<ExprNode> expr) {
            if (expr.isPresent()) {
                visitExpr(expr.get());

                mv.visitInsn(ARETURN);
            } else {
                mv.visitInsn(RETURN);
            }
        }

        private void visitBlock(java.util.List<StmtNode> stmts) {
            for (StmtNode stmt : stmts) {
                stmt.accept(this);
            }
        }

        private void visitExpr(ExprNode expr) {
            try {
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
                }
            } catch (NoSuchMethodException e) {
                logger.error(e.getMessage(), e);

                throw new InternalError(e.getMessage());
            }
        }

        private void visitExprBinary(ExprNode.Binary expr) throws NoSuchMethodException {
            visitExpr(expr.getLhs());
            visitExpr(expr.getRhs());

            switch (expr.getOp()) {
                case ADD:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Add.class), "_add_",
                            Type.getMethodDescriptor(Add.class.getMethod("_add_", Obj.class)),
                            true);
                    break;
                case DIV:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Div.class), "_div_",
                            Type.getMethodDescriptor(Div.class.getMethod("_div_", Obj.class)),
                            true);
                    break;
                case EQ:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Eq.class), "_eq_",
                            Type.getMethodDescriptor(Eq.class.getMethod("_eq_", Obj.class)), true);
                    break;
                case GT:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Gt.class), "_gt_",
                            Type.getMethodDescriptor(Gt.class.getMethod("_gt_", Obj.class)), true);
                    break;
                case GTE:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Gte.class), "_gte_",
                            Type.getMethodDescriptor(Gte.class.getMethod("_gte_", Obj.class)),
                            true);
                    break;
                case LT:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Lt.class), "_lt_",
                            Type.getMethodDescriptor(Lt.class.getMethod("_lt_", Obj.class)), true);
                    break;
                case LTE:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Lte.class), "_lteadd_",
                            Type.getMethodDescriptor(Lte.class.getMethod("_lte_", Obj.class)),
                            true);
                    break;
                case MUL:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Add.class), "_mul_",
                            Type.getMethodDescriptor(Mul.class.getMethod("_mul_", Obj.class)),
                            true);
                    break;
                case NEQ:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Neq.class), "_neq_",
                            Type.getMethodDescriptor(Neq.class.getMethod("_neq_", Obj.class)),
                            true);
                    break;
                case SUB:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Sub.class), "_sub_",
                            Type.getMethodDescriptor(Sub.class.getMethod("_sub_", Obj.class)),
                            true);
                    break;
                default:
                    throw new InternalError(
                            "visitExprBinary(ExprNode.Binary) not fully implemented: " + expr
                                    .getOp()
                    );
            }
        }

        private void visitExprConstant(ExprNode.Constant expr) throws NoSuchMethodException {
            Object value;

            switch (expr.getValueType()) {
                case BOOL:
                    if ((boolean) expr.getValue()) {
                        mv.visitFieldInsn(GETSTATIC, Type.getInternalName(Bool.class), "TRUE",
                                Type.getDescriptor(Bool.class));
                    } else {
                        mv.visitFieldInsn(GETSTATIC, Type.getInternalName(Bool.class), "FALSE",
                                Type.getDescriptor(Bool.class));
                    }
                    break;
                case INT:
                    value = expr.getValue();

                    visitValue((BigInteger) value);

                    Class<?> argumentClass;

                    int bitLength = ((BigInteger) value).bitLength();
                    if (bitLength < 8) {
                        argumentClass = byte.class;
                    } else if (bitLength < 16) {
                        argumentClass = short.class;
                    } else if (bitLength < 32) {
                        argumentClass = int.class;
                    } else if (bitLength < 64) {
                        argumentClass = long.class;
                    } else {
                        argumentClass = byte[].class;
                    }

                    // TODO: FIXME: This doesn't work because we need to use the constructor if we have byte[] as the argument class
                    mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Int.class), "valueOf",
                            Type.getMethodDescriptor(Int.class.getMethod("valueOf", argumentClass)),
                            false);
                    break;
                case NULL:
                    mv.visitFieldInsn(GETSTATIC, Type.getInternalName(Null.class), "INSTANCE",
                            Type.getDescriptor(Null.class));
                    break;
                case REAL:
                    throw new InternalError(
                            "visitExprConstant(ExprNode.Constant) not fully implemented");
                case STR:
                    value = expr.getValue();

                    mv.visitLdcInsn((String) value);
                    mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Str.class), "valueOf",
                            Type.getMethodDescriptor(Str.class.getMethod("valueOf", String.class)),
                            false);
                    break;
                default:
                    throw new InternalError(
                            "visitExprConstant(ExprNode.Constant) not fully implemented: " + expr
                                    .getValueType()
                    );
            }
        }

        private void visitExprFunction(ExprNode.Function expr) {
            Type type = getTypeFromQuxType(expr.getType());

            for (int i = 0; i < expr.getArguments().size(); i++) {
                visitExpr(expr.getArguments().get(i));
            }

            mv.visitMethodInsn(INVOKESTATIC, name, name, type.getDescriptor(), false);
        }

        private void visitExprList(ExprNode.List expr) throws NoSuchMethodException {
            visitValue(expr.getValues().size());
            mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(Obj.class));

            for (int i = 0; i < expr.getValues().size(); i++) {
                mv.visitInsn(DUP);
                visitValue(i);
                visitExpr(expr.getValues().get(i));
                mv.visitInsn(AASTORE);
            }

            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(List.class), "valueOf",
                    Type.getMethodDescriptor(List.class.getMethod("valueOf", Obj[].class)), false);
        }

        private void visitExprUnary(ExprNode.Unary expr) throws NoSuchMethodException {
            visitExpr(expr.getTarget());

            switch (expr.getOp()) {
                case NEG:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Neg.class), "_neg_",
                            Type.getMethodDescriptor(Neg.class.getMethod("_neg_")), true);
                    break;
                case NOT:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Not.class), "_not_",
                            Type.getMethodDescriptor(Not.class.getMethod("_not_")), true);
                    break;
                default:
                    throw new InternalError(
                            "visitExprUnary(ExprNode.Unary) not fully implemented: " + expr.getOp()
                    );
            }
        }

        private void visitExprVariable(ExprNode.Variable expr) {
            mv.visitVarInsn(ALOAD, locals.indexOf(expr.getName()));
        }

        private void visitValue(BigInteger value) {
            if (value.bitLength() < 8) {
                visitValue(value.byteValue());
            } else if (value.bitLength() < 16) {
                visitValue(value.shortValue());
            } else if (value.bitLength() < 32) {
                visitValue(value.intValue());
            } else if (value.bitLength() < 64) {
                visitValue(value.longValue());
            } else {
                visitValue(value.toByteArray());
            }
        }

        private void visitValue(byte value) {
            mv.visitIntInsn(BIPUSH, value);
        }

        private void visitValue(short value) {
            mv.visitIntInsn(SIPUSH, value);
        }

        private void visitValue(int value) {
            switch (value) {
                case -1:
                    mv.visitInsn(ICONST_M1);
                    break;
                case 0:
                    mv.visitInsn(ICONST_0);
                    break;
                case 1:
                    mv.visitInsn(ICONST_1);
                    break;
                case 2:
                    mv.visitInsn(ICONST_2);
                    break;
                case 3:
                    mv.visitInsn(ICONST_3);
                    break;
                case 4:
                    mv.visitInsn(ICONST_4);
                    break;
                case 5:
                    mv.visitInsn(ICONST_5);
                    break;
                default:
                    mv.visitLdcInsn(value);
            }
        }

        private void visitValue(long value) {
            if (value == 0) {
                mv.visitInsn(LCONST_0);
            } else if (value == 1) {
                mv.visitInsn(LCONST_1);
            } else {
                mv.visitLdcInsn(value);
            }
        }

        private void visitValue(byte[] value) {
            // Push the array size
            visitValue(value.length);
            // Create the new byte array
            mv.visitIntInsn(NEWARRAY, T_BYTE);

            for (int i = 0; i < value.length; i++) {
                // Duplicate the array reference
                mv.visitInsn(DUP);

                // Push the array index
                visitValue(1);
                // Push the byte value
                visitValue(value[i]);
                // Store to the array reference
                mv.visitInsn(BASTORE);
            }
        }
    }
}
