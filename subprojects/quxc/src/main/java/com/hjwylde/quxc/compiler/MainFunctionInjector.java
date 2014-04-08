package com.hjwylde.quxc.compiler;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hjwylde.quxc.compiler.Qux2ClassTranslater.getTypeFromQuxType;
import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ANEWARRAY;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;
import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.RETURN;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qux.lang.List;
import qux.lang.Obj;
import qux.lang.Str;

/**
 * A {@link org.objectweb.asm.ClassVisitor} that injects a main function when it detects the
 * required proxy method. If the visitor detects a method with the signature of {@code
 * main(Lqux/lang/List;)V}, then the standard Java main function will be injected with code to call
 * the proxy.
 *
 * @author Henry J. Wylde
 */
public class MainFunctionInjector extends ClassVisitor {

    private static final Logger logger = LoggerFactory.getLogger(MainFunctionInjector.class);

    private static final int FUNCTION_MAIN_FLAGS = ACC_PUBLIC | ACC_STATIC;
    private static final String FUNCTION_MAIN_NAME = "main";
    private static final com.hjwylde.qux.util.Type FUNCTION_MAIN_TYPE =
            com.hjwylde.qux.util.Type.forFunction(com.hjwylde.qux.util.Type.TYPE_VOID,
                    com.hjwylde.qux.util.Type.forList(com.hjwylde.qux.util.Type.TYPE_STR));

    private final String name;

    /**
     * Creates a new {@code MainFunctionInjector} with the given next {@link
     * org.objectweb.asm.ClassVisitor} and name of the source compile file.
     *
     * @param cv the next class visitor.
     * @param name the name of the Qux source file.
     */
    public MainFunctionInjector(ClassVisitor cv, String name) {
        super(ASM5, cv);

        this.name = checkNotNull(name, "name cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature,
            String[] exceptions) {
        // If there is a main function using the Qux types, then add an adapter function to call it
        if (access == FUNCTION_MAIN_FLAGS && name.equals(FUNCTION_MAIN_NAME) && desc.equals(
                getTypeFromQuxType(FUNCTION_MAIN_TYPE).getDescriptor())) {
            logger.debug("main function detected, injecting proxy function for {}.{}:{}", this.name,
                    name, desc);

            try {
                injectMainFunction();
            } catch (NoSuchMethodException e) {
                logger.error(e.getMessage(), e);

                throw new InternalError(e.getMessage());
            }
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    /**
     * Injects a Java main function in that will act as a proxy for the equivalent Qux main
     * function.
     *
     * @throws NoSuchMethodException should never occur.
     */
    private void injectMainFunction() throws NoSuchMethodException {
        MethodVisitor mv = super.visitMethod(FUNCTION_MAIN_FLAGS, FUNCTION_MAIN_NAME,
                Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(String[].class)), null,
                new String[0]);

        // If this visitor doesn't care, do nothing
        if (mv == null) {
            return;
        }

        // Local variable positions

        // String[] (method parameter)
        int args = 0;
        // Str[] (local variable)
        int strs = 1;
        // int (local variable)
        int i = 2;

        mv.visitParameter("args", args);
        mv.visitCode();

        // Create the array of Str
        mv.visitVarInsn(ALOAD, args);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitTypeInsn(ANEWARRAY, Type.getInternalName(Str.class));
        mv.visitVarInsn(ASTORE, strs);

        // Loop through the String args array
        Label start = new Label();
        Label end = new Label();

        // For loop initialisation
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, i);
        // For loop check
        mv.visitLabel(start);
        mv.visitVarInsn(ILOAD, i);
        mv.visitVarInsn(ALOAD, args);
        mv.visitInsn(ARRAYLENGTH);
        mv.visitJumpInsn(IF_ICMPGE, end);
        // For loop body
        mv.visitVarInsn(ALOAD, strs);
        mv.visitVarInsn(ALOAD, args);
        mv.visitVarInsn(ILOAD, i);
        mv.visitInsn(DUP_X1);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Str.class), "valueOf",
                Type.getMethodDescriptor(Str.class.getMethod("valueOf", String.class)), false);
        mv.visitInsn(AASTORE);
        // For loop increment
        mv.visitIincInsn(i, 1);
        // For loop end
        mv.visitJumpInsn(GOTO, start);
        mv.visitLabel(end);

        // Call the Qux main function
        mv.visitVarInsn(ALOAD, strs);
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(List.class), "valueOf",
                Type.getMethodDescriptor(List.class.getMethod("valueOf", Obj[].class)), false);
        mv.visitMethodInsn(INVOKESTATIC, this.name, FUNCTION_MAIN_NAME, getTypeFromQuxType(
                FUNCTION_MAIN_TYPE).getDescriptor(), false);
        mv.visitInsn(RETURN);

        // These values are ignored so long as ClassWriter.COMPUTE_MAXS is set
        mv.visitMaxs(0, 0);

        mv.visitEnd();
    }
}
