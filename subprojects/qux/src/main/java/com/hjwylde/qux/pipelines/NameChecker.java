package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.tree.ConstantNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.TypeNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Identifier;

import com.google.common.base.Optional;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO: Documentation.
 * <p/>
 * Responsible for checking the following: <ul> <li>No duplicate constants declared</li> <li>No
 * recursive constants declared</li><li>No duplicate functions declared</li> <li>No clashing
 * function parameters</li><li>No duplicate types declared</li><li>No recursive types
 * declared</li></ul>
 *
 * @author Henry J. Wylde
 * @since 0.2.3
 */
public final class NameChecker extends Pipeline {

    public NameChecker(QuxContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QuxNode apply(QuxNode node) {
        Set<Identifier> constants = new HashSet<>();
        // TODO: Check for recursive constants
        for (ConstantNode constant : node.getConstants()) {
            checkConstant(constant.getName(), constants);
        }

        Set<Identifier> functions = new HashSet<>();
        for (FunctionNode function : node.getFunctions()) {
            checkFunction(function.getName(), functions);

            Set<Identifier> parameters = new HashSet<>();
            for (Identifier parameter : function.getParameters()) {
                checkFunctionParameter(parameter, parameters, function);
            }
        }

        Set<Identifier> types = new HashSet<>();
        // TODO: Check for recursive types
        for (TypeNode type : node.getTypes()) {
            checkType(type.getName(), types);
        }

        return node;
    }

    private void checkConstant(Identifier constant, Set<Identifier> constants) {
        if (!constants.contains(constant)) {
            constants.add(constant);
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(constant, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.duplicateConstant(constant.getId(), source.getSource(),
                    source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.duplicateConstant(constant.getId());
        }
    }

    private void checkFunction(Identifier function, Set<Identifier> functions) {
        if (!functions.contains(function)) {
            functions.add(function);
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(function, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.duplicateFunction(function.getId(), source.getSource(),
                    source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.duplicateFunction(function.getId());
        }
    }

    private void checkFunctionParameter(Identifier parameter, Set<Identifier> parameters,
            FunctionNode function) {
        if (!parameters.contains(parameter)) {
            parameters.add(parameter);
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(function, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.duplicateFunctionParameter(parameter.getId(), source.getSource(),
                    source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.duplicateFunctionParameter(parameter.getId());
        }
    }

    private void checkType(Identifier type, Set<Identifier> types) {
        if (!types.contains(type)) {
            types.add(type);
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(type, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.duplicateType(type.getId(), source.getSource(), source.getLine(),
                    source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.duplicateType(type.getId());
        }
    }
}
