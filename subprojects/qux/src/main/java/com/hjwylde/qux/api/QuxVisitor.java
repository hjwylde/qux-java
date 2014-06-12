package com.hjwylde.qux.api;

import com.hjwylde.qux.util.Type;

import javax.annotation.Nullable;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public interface QuxVisitor {

    QuxVisitor NULL_INSTANCE = new NullQuxVisitor();

    void visit(int version, String name);

    ConstantVisitor visitConstant(int flags, String name, Type type);

    void visitEnd();

    FunctionVisitor visitFunction(int flags, String name, Type.Function type);

    void visitImport(String id);

    void visitPackage(@Nullable String pkg);
}
