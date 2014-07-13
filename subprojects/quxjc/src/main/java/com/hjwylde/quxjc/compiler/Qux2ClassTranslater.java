package com.hjwylde.quxjc.compiler;

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
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.LCONST_0;
import static org.objectweb.asm.Opcodes.LCONST_1;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SIPUSH;
import static org.objectweb.asm.Opcodes.SWAP;
import static org.objectweb.asm.Opcodes.T_BYTE;
import static org.objectweb.asm.Opcodes.V1_7;

import com.hjwylde.common.error.MethodNotImplementedError;
import com.hjwylde.qux.api.ConstantAdapter;
import com.hjwylde.qux.api.ConstantVisitor;
import com.hjwylde.qux.api.ExprVisitor;
import com.hjwylde.qux.api.FunctionAdapter;
import com.hjwylde.qux.api.FunctionVisitor;
import com.hjwylde.qux.api.QuxAdapter;
import com.hjwylde.qux.tree.ExprNode;
import com.hjwylde.qux.tree.Node;
import com.hjwylde.qux.tree.StmtNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Op;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.File;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import qux.lang.AbstractObj;
import qux.lang.Bool;
import qux.lang.Int;
import qux.lang.List;
import qux.lang.Null;
import qux.lang.Obj;
import qux.lang.Real;
import qux.lang.Record;
import qux.lang.Set;
import qux.lang.Str;
import qux.lang.op.Assign;
import qux.lang.op.Eq;
import qux.lang.op.Len;
import qux.lang.op.Neq;
import qux.lang.op.Slice;
import qux.util.Iterable;
import qux.util.Iterator;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Qux2ClassTranslater extends QuxAdapter {

    private final String source;

    private final ClassVisitor cv;

    private Identifier name;
    private ImmutableList<Identifier> pkg;

    /**
     * A method visitor for the static initialiser, "&lt;clinit&gt;". It is used when generating
     * constants to initialise their values to complex expressions.
     */
    private MethodVisitor simv;

    public Qux2ClassTranslater(String source, ClassVisitor cv) {
        this.source = checkNotNull(source, "source cannot be null");

        this.cv = checkNotNull(cv, "cv cannot be null");
    }

    public ImmutableList<Identifier> getId() {
        return ImmutableList.<Identifier>builder().addAll(pkg).add(name).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(int version, Identifier name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConstantVisitor visitConstant(int flags, Identifier name,
            com.hjwylde.qux.util.Type type) {
        FieldVisitor fv = cv.visitField(flags, name.getId(), getType(type).getDescriptor(), null,
                null);

        if (fv == null) {
            return ConstantVisitor.NULL_INSTANCE;
        }

        return new Constant2ClassTranslater(fv, flags, name, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitEnd() {
        simv.visitInsn(RETURN);
        simv.visitMaxs(0, 0);
        simv.visitEnd();

        cv.visitEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FunctionVisitor visitFunction(int flags, Identifier name,
            com.hjwylde.qux.util.Type.Function type) {
        MethodVisitor mv = cv.visitMethod(flags, name.getId(), getType(type).getDescriptor(), null,
                new String[0]);

        if (mv == null) {
            return FunctionVisitor.NULL_INSTANCE;
        }

        return new Function2ClassTranslater(mv, flags, name, type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitPackage(java.util.List<Identifier> pkg) {
        this.pkg = ImmutableList.copyOf(pkg);

        cv.visit(V1_7, ACC_PUBLIC | ACC_FINAL, Joiner.on('/').join(getId()), null,
                Type.getInternalName(AbstractObj.class), new String[0]);

        cv.visitSource(getFileName(), null);

        simv = cv.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        simv.visitCode();
    }

    static Class<?> getClass(Node node) {
        return getClass(getType(node));
    }

    static Class<?> getClass(Type type) {
        try {
            return Class.forName(type.getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static String getConstructorDescriptor(Class<?> clazz, Class<?>... parameterClasses) {
        try {
            return Type.getConstructorDescriptor(clazz.getConstructor(parameterClasses));
        } catch (NoSuchMethodException e) {
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
        return getType(getQuxType(node));
    }

    static Type getType(String desc) {
        return getType(com.hjwylde.qux.util.Type.forDescriptor(desc));
    }

    static Type getType(com.hjwylde.qux.util.Type type) {
        if (type instanceof com.hjwylde.qux.util.Type.Any) {
            return Type.getType(AbstractObj.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Bool) {
            return Type.getType(Bool.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Function) {
            com.hjwylde.qux.util.Type.Function function =
                    ((com.hjwylde.qux.util.Type.Function) type);

            Type[] parameterTypes = new Type[function.getParameterTypes().size()];
            for (int i = 0; i < parameterTypes.length; i++) {
                parameterTypes[i] = getType(function.getParameterTypes().get(i));
            }

            return Type.getMethodType(getType(function.getReturnType()), parameterTypes);
        } else if (type instanceof com.hjwylde.qux.util.Type.Int) {
            return Type.getType(Int.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.List) {
            return Type.getType(List.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Null) {
            return Type.getType(Null.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Obj) {
            return Type.getType(Obj.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Real) {
            return Type.getType(Real.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Record) {
            return Type.getType(Record.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Set) {
            return Type.getType(Set.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Str) {
            return Type.getType(Str.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Union) {
            return Type.getType(AbstractObj.class);
        } else if (type instanceof com.hjwylde.qux.util.Type.Void) {
            return Type.VOID_TYPE;
        }

        throw new MethodNotImplementedError(type.getClass().toString());
    }

    private String getFileName() {
        int index = source.lastIndexOf(File.separator);

        return index < 0 ? source : source.substring(index + 1);
    }

    private static int visitLineNumber(MethodVisitor mv, Node node, int line) {
        Optional<Attribute.Source> opt = Attributes.getAttribute(node, Attribute.Source.class);
        if (!opt.isPresent()) {
            return line;
        }

        Attribute.Source source = opt.get();

        if (line >= source.getLine()) {
            return line;
        }

        Label start = new Label();
        mv.visitLabel(start);
        mv.visitLineNumber(source.getLine(), start);

        return source.getLine();
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.2
     */
    private final class Constant2ClassTranslater extends ConstantAdapter {

        private final FieldVisitor fv;

        private final int flags;
        private final Identifier name;
        private final com.hjwylde.qux.util.Type type;

        public Constant2ClassTranslater(FieldVisitor fv, int flags, Identifier name,
                com.hjwylde.qux.util.Type type) {
            this.fv = checkNotNull(fv, "fv cannot be null");

            this.flags = flags;
            this.name = checkNotNull(name, "name cannot be null");
            this.type = checkNotNull(type, "type cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExpr(ExprNode expr) {
            new Expr2ClassTranslater(simv).visitExpr(expr);

            simv.visitFieldInsn(PUTSTATIC, Joiner.on('/').join(getId()), name.getId(), getType(type)
                    .getDescriptor());
        }
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     * @since 0.2.2
     */
    private final class Expr2ClassTranslater implements ExprVisitor {

        private final MethodVisitor mv;

        /**
         * Stores a list of variable names, each at their associated jvm local indices.
         * <p/>
         * TODO: Change this so that the number of local variables are minimized, i.e. can be used
         * more than once like for loop vars
         */
        private final java.util.List<Identifier> locals;

        private int line = Integer.MIN_VALUE;

        public Expr2ClassTranslater(MethodVisitor mv) {
            this(mv, new ArrayList<Identifier>());
        }

        public Expr2ClassTranslater(MethodVisitor mv, java.util.List<Identifier> locals) {
            this.mv = checkNotNull(mv, "mv cannot be null");

            this.locals = checkNotNull(locals, "locals cannot be null");
        }

        public void visitExpr(ExprNode expr) {
            visitLineNumber(expr);

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
                case ACC:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_access_",
                            getMethodDescriptor(lhsClass, "_access_", Int.class), false);
                    visitCheckcast(expr);
                    break;
                case ADD:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_add_",
                            getMethodDescriptor(lhsClass, "_add_", rhsClass), false);
                    break;
                case AND:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Bool.class), "_and_",
                            getMethodDescriptor(Bool.class, "_and_", Bool.class), false);
                    break;
                case DIV:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_div_",
                            getMethodDescriptor(lhsClass, "_div_", rhsClass), false);
                    break;
                case EQ:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Eq.class), "_eq_",
                            getMethodDescriptor(Eq.class, "_eq_", AbstractObj.class), true);
                    break;
                case EXP:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Int.class), "_exp_",
                            getMethodDescriptor(Int.class, "_exp_", Int.class), false);
                    break;
                case GT:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(AbstractObj.class),
                            "_gt_", getMethodDescriptor(AbstractObj.class, "_gt_",
                                    AbstractObj.class), false);
                    break;
                case GTE:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(AbstractObj.class),
                            "_gte_", getMethodDescriptor(AbstractObj.class, "_gte_",
                                    AbstractObj.class), false);
                    break;
                case IDIV:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Int.class), "_idiv_",
                            getMethodDescriptor(Int.class, "_idiv_", Int.class), false);
                    break;
                case IFF:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Bool.class), "_iff_",
                            getMethodDescriptor(Bool.class, "_iff_", Bool.class), false);
                    break;
                case IMP:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Bool.class), "_imp_",
                            getMethodDescriptor(Bool.class, "_imp_", Bool.class), false);
                    break;
                case IN:
                    mv.visitInsn(SWAP);
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(rhsClass), "_contains_",
                            getMethodDescriptor(rhsClass, "_contains_", AbstractObj.class), false);
                    break;
                case LT:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(AbstractObj.class),
                            "_lt_", getMethodDescriptor(AbstractObj.class, "_lt_",
                                    AbstractObj.class), false);
                    break;
                case LTE:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(AbstractObj.class),
                            "_lte_", getMethodDescriptor(AbstractObj.class, "_lte_",
                                    AbstractObj.class), false);
                    break;
                case MUL:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_mul_",
                            getMethodDescriptor(lhsClass, "_mul_", rhsClass), false);
                    break;
                case NEQ:
                    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Neq.class), "_neq_",
                            getMethodDescriptor(Neq.class, "_neq_", AbstractObj.class), true);
                    break;
                case OR:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Bool.class), "_or_",
                            getMethodDescriptor(Bool.class, "_or_", Bool.class), false);
                    break;
                case RNG:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Int.class), "_rng_",
                            getMethodDescriptor(Int.class, "_rng_", Int.class), false);
                    break;
                case REM:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_rem_",
                            getMethodDescriptor(lhsClass, "_rem_", rhsClass), false);
                    break;
                case SUB:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(lhsClass), "_sub_",
                            getMethodDescriptor(lhsClass, "_sub_", rhsClass), false);
                    break;
                case XOR:
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Bool.class), "_xor_",
                            getMethodDescriptor(Bool.class, "_xor_", Bool.class), false);
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
                case OBJ:
                    value = expr.getValue();

                    mv.visitLdcInsn(value);
                    mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Obj.class), "valueOf",
                            getMethodDescriptor(Obj.class, "valueOf", String.class), false);
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
        public void visitExprExternal(ExprNode.External expr) {
            switch (expr.getType()) {
                case CONSTANT:
                    visitExprVariable(expr.getMeta().getId(), (ExprNode.Variable) expr.getExpr());
                    break;
                case FUNCTION:
                    visitExprFunction(expr.getMeta().getId(), (ExprNode.Function) expr.getExpr());
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getType().toString());
            }
        }

        public void visitExprFunction(java.util.List<Identifier> owner, ExprNode.Function expr) {
            Type returnType = getType(expr);
            java.util.List<Type> argumentTypes = new ArrayList<>();

            for (int i = 0; i < expr.getArguments().size(); i++) {
                visitExpr(expr.getArguments().get(i));

                Type argumentType = getType(expr.getArguments().get(i));

                // Ensures pass-by-value semantics by cloning the values
                mv.visitMethodInsn(INVOKEVIRTUAL, argumentType.getInternalName(), "_dup_",
                        getMethodDescriptor(Qux2ClassTranslater.getClass(argumentType), "_dup_"),
                        false);

                argumentTypes.add(argumentType);
            }

            Type type = Type.getMethodType(returnType, argumentTypes.toArray(new Type[0]));

            mv.visitMethodInsn(INVOKESTATIC, Joiner.on('/').join(owner), expr.getName().getId(),
                    type.getDescriptor(), false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprFunction(ExprNode.Function expr) {
            visitExprFunction(getId(), expr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprList(ExprNode.List expr) {
            visitValue(expr.getValues().size());
            mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(AbstractObj.class));

            for (int i = 0; i < expr.getValues().size(); i++) {
                mv.visitInsn(DUP);
                visitValue(i);
                visitExpr(expr.getValues().get(i));
                mv.visitInsn(AASTORE);
            }

            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(List.class), "valueOf",
                    getMethodDescriptor(List.class, "valueOf", AbstractObj[].class), false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprMeta(ExprNode.Meta expr) {}

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprRecord(ExprNode.Record expr) {
            Map<Identifier, ExprNode> fields = expr.getFields();

            mv.visitTypeInsn(NEW, Type.getInternalName(HashMap.class));
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(HashMap.class), "<init>",
                    getConstructorDescriptor(HashMap.class), false);

            for (Map.Entry<Identifier, ExprNode> field : expr.getFields().entrySet()) {
                mv.visitInsn(DUP);

                mv.visitLdcInsn(field.getKey().getId());
                visitExpr(field.getValue());

                mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Map.class), "put",
                        getMethodDescriptor(Map.class, "put", Object.class, Object.class), true);
                mv.visitInsn(POP);
            }

            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Record.class), "valueOf",
                    getMethodDescriptor(Record.class, "valueOf", Map.class), false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprRecordAccess(ExprNode.RecordAccess expr) {
            visitExpr(expr.getTarget());
            mv.visitLdcInsn(expr.getField().getId());

            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Record.class), "get",
                    getMethodDescriptor(Record.class, "get", String.class), false);
            visitCheckcast(expr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprSet(ExprNode.Set expr) {
            visitValue(expr.getValues().size());
            mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(AbstractObj.class));

            for (int i = 0; i < expr.getValues().size(); i++) {
                mv.visitInsn(DUP);
                visitValue(i);
                visitExpr(expr.getValues().get(i));
                mv.visitInsn(AASTORE);
            }

            mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Set.class), "valueOf",
                    getMethodDescriptor(Set.class, "valueOf", AbstractObj[].class), false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprSlice(ExprNode.Slice expr) {
            visitExpr(expr.getTarget());
            if (expr.getFrom().isPresent()) {
                visitExpr(expr.getFrom().get());
            } else {
                visitExpr(new ExprNode.Constant(ExprNode.Constant.Type.INT, BigInteger.ZERO));
            }
            if (expr.getTo().isPresent()) {
                visitExpr(expr.getTo().get());
            } else {
                visitExpr(new ExprNode.Unary(Op.Unary.LEN, expr.getTarget()));
            }

            mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Slice.class), "_slice_",
                    getMethodDescriptor(Slice.class, "_slice_", Int.class, Int.class), true);
            visitCheckcast(expr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprUnary(ExprNode.Unary expr) {
            visitExpr(expr.getTarget());

            Class<?> clazz = Qux2ClassTranslater.getClass(expr.getTarget());

            switch (expr.getOp()) {
                case DEC:
                    int index = locals.indexOf(((ExprNode.Variable) expr.getTarget()).getName());

                    mv.visitInsn(DUP);
                    visitExpr(new ExprNode.Constant(ExprNode.Constant.Type.INT, BigInteger.ONE));
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Int.class), "_sub_",
                            getMethodDescriptor(Int.class, "_sub_", Int.class), false);
                    mv.visitVarInsn(ASTORE, index);
                    break;
                case INC:
                    index = locals.indexOf(((ExprNode.Variable) expr.getTarget()).getName());

                    mv.visitInsn(DUP);
                    visitExpr(new ExprNode.Constant(ExprNode.Constant.Type.INT, BigInteger.ONE));
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Int.class), "_add_",
                            getMethodDescriptor(Int.class, "_add_", Int.class), false);
                    mv.visitVarInsn(ASTORE, index);
                    break;
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
                    mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Bool.class), "_not_",
                            getMethodDescriptor(Bool.class, "_not_"), false);
                    break;
                default:
                    throw new MethodNotImplementedError(expr.getOp().toString());
            }
        }

        public void visitExprVariable(java.util.List<Identifier> owner, ExprNode.Variable expr) {
            mv.visitFieldInsn(GETSTATIC, Joiner.on('/').join(owner), expr.getName().getId(),
                    getType(expr).getDescriptor());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitExprVariable(ExprNode.Variable expr) {
            mv.visitVarInsn(ALOAD, locals.indexOf(expr.getName()));
        }

        private void visitCheckcast(Node node) {
            mv.visitTypeInsn(CHECKCAST, getType(node).getInternalName());
        }

        private void visitLineNumber(Node node) {
            line = Qux2ClassTranslater.visitLineNumber(mv, node, line);
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

            for (byte b : value) {
                // Duplicate the array reference
                mv.visitInsn(DUP);

                // Push the array index
                visitValue(1);
                // Push the byte value
                visitValue(b);
                // Store to the array reference
                mv.visitInsn(BASTORE);
            }
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
    }

    /**
     * TODO: Documentation.
     *
     * @author Henry J. Wylde
     */
    private final class Function2ClassTranslater extends FunctionAdapter {

        private final MethodVisitor mv;

        private final int flags;
        private final Identifier name;
        private final com.hjwylde.qux.util.Type.Function type;

        private Expr2ClassTranslater et;

        /**
         * Stores a map of parameter names to jvm types.
         */
        private Map<Identifier, String> parameters = new HashMap<>();
        /**
         * Stores the return type as a jvm type.
         */
        private String returnType;

        /**
         * Unique number for generating variable names.
         */
        private int gen;

        /**
         * Stores a list of variable names, each at their associated jvm local indices.
         * <p/>
         * TODO: Change this so that the number of local variables are minimized, i.e. can be used
         * more than once like for loop vars
         */
        private java.util.List<Identifier> locals = new ArrayList<>();

        /**
         * The current source line number, used for generating the line number table attribute.
         */
        private int line = Integer.MIN_VALUE;

        public Function2ClassTranslater(MethodVisitor mv, int flags, Identifier name,
                com.hjwylde.qux.util.Type.Function type) {
            this.mv = checkNotNull(mv, "mv cannot be null");

            this.flags = flags;
            this.name = checkNotNull(name, "name cannot be null");
            this.type = checkNotNull(type, "type cannot be null");
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

            et = new Expr2ClassTranslater(mv, locals);
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
            expr.accept(et);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitParameter(Identifier var, com.hjwylde.qux.util.Type type) {
            checkNotNull(var, "var cannot be null");
            checkNotNull(type, "type cannot be null");

            parameters.put(var, getType(type).getDescriptor());
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

            returnType = getType(type).getDescriptor();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAccessAssign(StmtNode.AccessAssign stmt) {
            visitLineNumber(stmt);

            visitExpr(stmt.getAccess().getLhs());
            visitExpr(stmt.getAccess().getRhs());
            visitExpr(stmt.getExpr());

            mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Assign.class), "_assign_",
                    getMethodDescriptor(Assign.class, "_assign_", Int.class, AbstractObj.class),
                    true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtAssign(StmtNode.Assign stmt) {
            visitLineNumber(stmt);

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
        public void visitStmtExpr(StmtNode.Expr stmt) {
            visitLineNumber(stmt);

            visitExpr(stmt.getExpr());

            if (getType(stmt.getExpr()) != Type.VOID_TYPE) {
                mv.visitInsn(POP);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtFor(StmtNode.For stmt) {
            visitLineNumber(stmt);

            visitExpr(stmt.getExpr());

            com.hjwylde.qux.util.Type innerType = com.hjwylde.qux.util.Type.getInnerType(getQuxType(
                    stmt.getExpr()));

            // The local variable holding each element of the iterable
            locals.add(stmt.getVar());
            int var = locals.indexOf(stmt.getVar());
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
            mv.visitTypeInsn(CHECKCAST, getType(innerType).getInternalName());
            mv.visitVarInsn(ASTORE, var);

            visitBlock(stmt.getBody());

            mv.visitJumpInsn(GOTO, startLabel);
            mv.visitLabel(endLabel);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtIf(StmtNode.If stmt) {
            visitLineNumber(stmt);

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
            visitLineNumber(stmt);

            mv.visitFieldInsn(GETSTATIC, Type.getInternalName(System.class), "out",
                    Type.getDescriptor(PrintStream.class));

            visitExpr(stmt.getExpr());

            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(Object.class), "toString",
                    getMethodDescriptor(Object.class, "toString"), false);
            mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(PrintStream.class), "println",
                    getMethodDescriptor(PrintStream.class, "println", String.class), false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtReturn(StmtNode.Return stmt) {
            visitLineNumber(stmt);

            if (stmt.getExpr().isPresent()) {
                visitExpr(stmt.getExpr().get());

                mv.visitInsn(ARETURN);
            } else {
                mv.visitInsn(RETURN);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visitStmtWhile(StmtNode.While stmt) {
            visitLineNumber(stmt);

            Label startLabel = new Label();
            Label endLabel = new Label();

            mv.visitLabel(startLabel);

            visitExpr(stmt.getCondition());

            mv.visitFieldInsn(GETSTATIC, Type.getInternalName(Bool.class), "TRUE",
                    Type.getDescriptor(Bool.class));
            mv.visitJumpInsn(IF_ACMPNE, endLabel);

            visitBlock(stmt.getBody());

            mv.visitJumpInsn(GOTO, startLabel);
            mv.visitLabel(endLabel);
        }

        private Identifier generateVariable() {
            Identifier name = new Identifier("$gen" + gen++);

            locals.add(name);

            return name;
        }

        private void visitLineNumber(Node node) {
            line = Qux2ClassTranslater.visitLineNumber(mv, node, line);
        }
    }
}
