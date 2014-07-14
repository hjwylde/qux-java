package com.hjwylde.qux.api;

import com.hjwylde.qux.util.Identifier;
import com.hjwylde.qux.util.Type;

import java.util.List;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public interface QuxVisitor {

    QuxVisitor NULL_INSTANCE = new NullQuxVisitor();

    void visit(Identifier name);

    ConstantVisitor visitConstant(int flags, Identifier name, Type type);

    void visitEnd();

    FunctionVisitor visitFunction(int flags, Identifier name, Type.Function type);

    void visitPackage(List<Identifier> pkg);

    TypeVisitor visitType(int flags, Identifier name);
}
