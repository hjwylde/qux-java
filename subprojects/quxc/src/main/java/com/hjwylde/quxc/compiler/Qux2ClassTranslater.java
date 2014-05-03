package com.hjwylde.quxc.compiler;

import static com.google.common.base.Preconditions.checkNotNull;
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
import static org.objectweb.asm.Opcodes.CHECKCAST;
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
import static org.objectweb.asm.Opcodes.IF_ACMPNE;
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

import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.FunctionAdapter;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxAdapter;
import com.hjwylde.qux.api.QuxVisitor;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.Node;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.math.BigDecimal;
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
import qux.lang.op.And;
import qux.lang.op.Eq;
import qux.lang.op.Iff;
import qux.lang.op.Implies;
import qux.lang.op.Len;
import qux.lang.op.Neq;
import qux.lang.op.Not;
import qux.lang.op.Or;
import qux.lang.op.Xor;
import qux.util.Iterable;
import qux.util.Iterator;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Qux2ClassTranslater extends QuxAdapter {

    // TODO: Change from depending on qrt to using a ClassLoader and getting the runtime files that way
    // This may mean the user can specify the runtime library to compile against?
    // May need to do some checks about the version of the runtime library though

    private static final Logger logger = LoggerFactory.getLogger(QuxVisitor.class);

    private final String name;

    private final ClassVisitor cv;

    public Qux2ClassTranslater(String name, ClassVisitor cv) {
        this.name = checkNotNull(name, "fileName cannot be null");

        this.cv = checkNotNull(cv, "cv cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(int version, String name) {
        cv.visit(V1_7, ACC_PUBLIC | ACC_FINAL, name, null, Type.getInternalName(Obj.class),
                new String[0]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        cv.visitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionVisitor visitFunction(int flags, String name,
            com.hjwylde.qux.util.Type.Function type) {
        MethodVisitor mv = cv.visitMethod(flags, name, getTypeFromQuxType(type).getDescriptor(),
                null, new String[0]);

        if (mv == null) {
            return FunctionVisitor.NULL_INSTANCE;
        }

        return new Function2ClassTranslater(mv, flags, name, type);
    }

    static Class<?> getClass(Node node) {
        try {
            return Class.forName(getType(node).getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static String getMethodDescriptor(Class<?> clazz, String name, Class<?>... parameterClasses) {
        try {
            return Type.getMethodDescriptor(clazz.getMethod(name, parameterClasses));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static com.hjwylde.qux.util.Type getQuxType(Node node) {
        return Attributes.getAttributeUnchecked(node, Attribute.Type.class).getType();
    }

    static Type getType(Node node) {
        return getTypeFromQuxType(getQuxType(node));
    }

    static Type getTypeFromQuxType(String desc) {
        return getTypeFromQuxType(com.hjwylde.qux.util.Type.forDescriptor(desc));
    }

    static Type getTypeFromQuxType(com.hjwylde.qux.util.Type type) {
        if (type instanceof com.hjwylde.qux.util.Type.Any) {
            return Type.getType(Obj.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Bool) {
            return Type.getType(Bool.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Function) {
            com.hjwylde.qux.util.Type.Function function =
                    ((com.hjwylde.qux.util.Type.Function) type);

            Type[] parameterTypes = new Type[function.getParameterTypes().size()];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes[i] = getTypeFromQuxType(function.getParameterTypes().get(i));
            }

            return Type.getMethodType(getTypeFromQuxType(function.getReturnType()), parameterTypes);
        } else if (type instanceof com.hjwylde.qux.util.Type.Int) {
            return Type.getType(Int.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.List) {
            return Type.getType(List.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Null) {
            return Type.getType(Null.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Real) {
            return Type.getType(Real.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Str) {
            return Type.getType(Str.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Union) {
            return Type.getType(Obj.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Void) {
            return Type.VOID_TYPE;
        }

        throw new MethodNotImplementedError(type.getClass().toString());
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    private final class Function2ClassTranslater extends FunctionAdapter implements ExprVisitor {

        private static final String THIS = "this";

        private final MethodVisitor mv;

        private final int flags;
        private final String name;
        private final com.hjwylde.qux.util.Type.Function type;

        /**
         * Stores a map of parameter names to jvm types.
         */
        private Map<String, String> parameters = new HashMap<>();
        /**
         * Stores the return type as a jvm type.
         */
        private String returnType;

        /**
         * Unique number for generating variable names.
         */
        private int gen;

        /**
         * Stores a map of variable names to jvm local indices.
         * <p/>
         * TODO: Change this so that the number of local variables are minimized, i.e. can be used
         * more than once like for loop vars
         */
        private java.util.List<String> locals = new ArrayList<>();

        public Function2ClassTranslater(MethodVisitor mv, int flags, String name,
                com.hjwylde.qux.util.Type.Function type) {
            this.mv = checkNotNull(mv, "mv cannot be null");

            this.flags = flags;
            this.name = checkNotNull(name, "name cannot be null");
            this.type = checkNotNull(type, "type cannot be null");

            if ((flags & ACC_STATIC) == 0) {
                locals.add(THIS);
            }
        }

        public void visitBlock(java.util.List<StmtNode> stmts) {
            for (StmtNode stmt : stmts) {
                stmt.accept(this);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitCode() {
            mv.visitCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitEnd() {
            // If the type is Void, then add in a return instruction
            if (type.getReturnType() instanceof com.hjwylde.qux.util.Type.Void) {
                mv.visitInsn(RETURN);
            }

            // These values are ignored so long as ClassWriter.COMPUTE_MAXS is set
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }

        public void visitExpr(ExprNode expr) {
            expr.accept(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprBinary(ExprNode.Binary expr) {
            visitExpr(expr.getLhs());
            visitExpr(expr.getRhs());

            Class<?> lhsClass = Qux2ClassTranslater.getClass(expr.getLhs());
            Class<?> rhsClass = Qux2ClassTranslater.getClass(expr.getRhs());

            switch (expr.getOp()) {
                case ADD:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_add_",
                            getMethodDescriptor(lhsClass, "_add_", rhsClass), false);
                    visitCheckcast(expr);
                    break;
                case AND:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(And.class), "_and_",
                            getMethodDescriptor(And.class, "_and_", Bool.class), true);
                    visitCheckcast(expr);
                    break;
                case DIV:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_div_",
                            getMethodDescriptor(lhsClass, "_div_", rhsClass), false);
                    visitCheckcast(expr);
                    break;
                case EQ:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Eq.class), "_eq_",
                            getMethodDescriptor(Eq.class, "_eq_", Obj.class), true);
                    break;
                case GT:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_gt_",
                            getMethodDescriptor(lhsClass, "_gt_", rhsClass), false);
                    break;
                case GTE:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_gte_",
                            getMethodDescriptor(lhsClass, "_gte_", rhsClass), false);
                    break;
                case IFF:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Iff.class), "_iff_",
                            getMethodDescriptor(Iff.class, "_iff_", Bool.class), true);
                    break;
                case IMPLIES:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Implies.class),
                            "_implies_", getMethodDescriptor(Implies.class, "_implies_",
                                    Bool.class), true
                    );
                    break;
                case IN:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_contains_",
                            getMethodDescriptor(lhsClass, "_contains_", rhsClass), false);
                    break;
                case LT:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_lt_",
                            getMethodDescriptor(lhsClass, "_lt_", rhsClass), false);
                    break;
                case LTE:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_lte_",
                            getMethodDescriptor(lhsClass, "_lte_", rhsClass), false);
                    break;
                case MUL:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_mul_",
                            getMethodDescriptor(lhsClass, "_mul_", rhsClass), false);
                    visitCheckcast(expr);
                    break;
                case NEQ:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Neq.class), "_neq_",
                            getMethodDescriptor(Neq.class, "_neq_", Obj.class), true);
                    break;
                case OR:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Or.class), "_or_",
                            getMethodDescriptor(Or.class, "_or_", Bool.class), true);
                    break;
                case REM:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_rem_",
                            getMethodDescriptor(lhsClass, "_rem_", rhsClass), false);
                    visitCheckcast(expr);
                    break;
                case SUB:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_sub_",
                            getMethodDescriptor(lhsClass, "_sub_", rhsClass), false);
                    visitCheckcast(expr);
                    break;
                case XOR:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Xor.class), "_xor_",
                            getMethodDescriptor(Xor.class, "_xor_", Bool.class), true);
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
            Object value;

            switch (expr.getType()) {
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

                    mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Int.class), "valueOf",
                            getMethodDescriptor(Int.class, "valueOf", argumentClass), false);
                    break;
                case NULL:
                    mv.visitFieldInsn(GETSTATIC, Type.getInternalName(Null.class), "INSTANCE",
                            Type.getDescriptor(Null.class));
                    break;
                case REAL:
                    value = expr.getValue();

                    visitValue((BigDecimal) value);

                    mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Real.class), "valueOf",
                            getMethodDescriptor(Real.class, "valueOf", String.class), false);
                    break;
                case STR:
                    value = expr.getValue();

                    mv.visitLdcInsn(value);
                    mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Str.class), "valueOf",
                            getMethodDescriptor(Str.class, "valueOf", String.class), false);
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
            Type returnType = getType(expr);
            java.util.List<Type> argumentTypes = new ArrayList<>();

            for (int i = expr.getArguments().size() - 1; i >= 0; i--) {
                visitExpr(expr.getArguments().get(i));

                argumentTypes.add(0, getType(expr.getArguments().get(i)));
            }

            Type type = Type.getMethodType(returnType, argumentTypes.toArray(new Type[0]));

            mv.visitMethodInsn(INVOKESTATIC, Qux2ClassTranslater.this.name, expr.getName(),
                    type.getDescriptor(), false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprList(ExprNode.List expr) {
            visitValue(expr.getValues().size());
            mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(Obj.class));

            for (int i = 0; i < expr.getValues().size(); i++) {
                mv.visitInsn(DUP);
                visitValue(i);
                visitExpr(expr.getValues().get(i));
                mv.visitInsn(AASTORE);
            }

            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(List.class), "valueOf",
                    getMethodDescriptor(List.class, "valueOf", Obj[].class), false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprUnary(ExprNode.Unary expr) {
            visitExpr(expr.getTarget());

            Class<?> clazz = Qux2ClassTranslater.getClass(expr.getTarget());

            switch (expr.getOp()) {
                case LEN:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Len.class), "_len_",
                            getMethodDescriptor(Len.class, "_len_"), true);
                    break;
                case NEG:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(clazz), "_neg_",
                            getMethodDescriptor(clazz, "_neg_"), false);
                    visitCheckcast(expr.getTarget());
                    break;
                case NOT:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Not.class), "_not_",
                            getMethodDescriptor(Not.class, "_not_"), true);
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
            mv.visitVarInsn(ALOAD, locals.indexOf(expr.getName()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitParameter(String var, com.hjwylde.qux.util.Type type) {
            checkNotNull(var, "var cannot be null");
            checkNotNull(type, "type cannot be null");

            parameters.put(var, getTypeFromQuxType(type).getDescriptor());
            if (!locals.contains(var)) {
                locals.add(var);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitReturnType(com.hjwylde.qux.util.Type type) {
            checkNotNull(type, "type cannot be null");

            returnType = getTypeFromQuxType(type).getDescriptor();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAssign(StmtNode.Assign stmt) {
            visitExpr(stmt.getExpr());

            if (!locals.contains(stmt.getVar())) {
                locals.add(stmt.getVar());
            }

            mv.visitVarInsn(ASTORE, locals.indexOf(stmt.getVar()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFor(StmtNode.For stmt) {
            visitExpr(stmt.getExpr());

            // The local variable holding each element of the iterable
            locals.add(stmt.getVar());
            int var = locals.indexOf(stmt.getVar());
            Type varType = getTypeFromQuxType(((com.hjwylde.qux.util.Type.List) getQuxType(
                    stmt.getExpr())).getInnerType());
            // The local variable index holding the iterator object
            int iter = locals.indexOf(generateVariable());
            Label startLabel = new Label();
            Label endLabel = new Label();

            mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Iterable.class), "_iter_",
                    getMethodDescriptor(Iterable.class, "_iter_"), true);
            mv.visitVarInsn(ASTORE, iter);

            mv.visitLabel(startLabel);

            mv.visitVarInsn(ALOAD, iter);
            mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Iterator.class), "hasNext",
                    getMethodDescriptor(Iterator.class, "hasNext"), true);
            mv.visitFieldInsn(GETSTATIC, Type.getInternalName(Bool.class), "TRUE",
                    Type.getDescriptor(Bool.class));
            mv.visitJumpInsn(IF_ACMPNE, endLabel);
            mv.visitVarInsn(ALOAD, iter);
            mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Iterator.class), "next",
                    getMethodDescriptor(Iterator.class, "next"), true);
            mv.visitTypeInsn(CHECKCAST, varType.getInternalName());
            mv.visitVarInsn(ASTORE, var);

            visitBlock(stmt.getBody());

            mv.visitJumpInsn(GOTO, startLabel);
            mv.visitLabel(endLabel);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFunction(StmtNode.Function stmt) {
            Type returnType = Type.VOID_TYPE;
            java.util.List<Type> argumentTypes = new ArrayList<>();

            for (int i = stmt.getArguments().size() - 1; i >= 0; i--) {
                visitExpr(stmt.getArguments().get(i));

                argumentTypes.add(0, getType(stmt.getArguments().get(i)));
            }

            Type type = Type.getMethodType(returnType, argumentTypes.toArray(new Type[0]));

            mv.visitMethodInsn(INVOKESTATIC, Qux2ClassTranslater.this.name, stmt.getName(),
                    type.getDescriptor(), false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtIf(StmtNode.If stmt) {
            visitExpr(stmt.getCondition());

            mv.visitFieldInsn(GETSTATIC, Type.getInternalName(Bool.class), "FALSE",
                    Type.getDescriptor(Bool.class));

            Label falseLabel = new Label();
            Label endLabel = new Label();

            mv.visitJumpInsn(IF_ACMPEQ, falseLabel);

            visitBlock(stmt.getTrueBlock());

            // Only need to visit this GOTO if there is a false block
            if (!stmt.getFalseBlock().isEmpty()) {
                mv.visitJumpInsn(GOTO, endLabel);
            }

            mv.visitLabel(falseLabel);

            // Only need to visit this block and label if there is a false block
            if (!stmt.getFalseBlock().isEmpty()) {
                visitBlock(stmt.getFalseBlock());

                mv.visitLabel(endLabel);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtPrint(StmtNode.Print stmt) {
            mv.visitFieldInsn(GETSTATIC, Type.getInternalName(System.class), "out",
                    Type.getDescriptor(PrintStream.class));

            visitExpr(stmt.getExpr());

            Attribute.Type attribute = Attributes.getAttributeUnchecked(stmt.getExpr(),
                    Attribute.Type.class);

            mv.visitMethodInsn(INVOKEVIRTUAL, getTypeFromQuxType(attribute.getType())
                    .getInternalName(), "toString", Type.getMethodDescriptor(Type.getType(
                    String.class)), false);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(PrintStream.class), "println",
                    getMethodDescriptor(PrintStream.class, "println", String.class), false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtReturn(StmtNode.Return stmt) {
            if (stmt.getExpr().isPresent()) {
                visitExpr(stmt.getExpr().get());

                mv.visitInsn(ARETURN);
            } else {
                mv.visitInsn(RETURN);
            }
        }

        private String generateVariable() {
            String name = "$gen" + gen++;

            locals.add(name);

            return name;
        }

        private void visitCheckcast(Node node) {
            mv.visitTypeInsn(CHECKCAST, getType(node).getInternalName());
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

        private void visitValue(BigDecimal value) {
            mv.visitLdcInsn(value.toString());
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
