package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.tree.ConstantNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.tree.TypeNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;

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
        List<String> constants = new ArrayList<>();
        // TODO: Check for recursive constants
        for (ConstantNode constant : node.getConstants()) {
            checkConstant(constant, constants);
        }

        List<String> functions = new ArrayList<>();
        for (FunctionNode function : node.getFunctions()) {
            checkFunction(function, functions);

            List<String> parameters = new ArrayList<>();
            for (String parameter : function.getParameters().keySet()) {
                checkFunctionParameter(parameter, parameters, function);
            }
        }

        List<String> types = new ArrayList<>();
        // TODO: Check for recursive types
        for (TypeNode type : node.getTypes()) {
            checkType(type, types);
        }

        return node;
    }

    private void checkConstant(ConstantNode constant, List<String> constants) {
        if (!constants.contains(constant.getName())) {
            constants.add(constant.getName());
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(constant, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.duplicateConstant(constant.getName(), source.getSource(),
                    source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.duplicateConstant(constant.getName());
        }
    }

    private void checkFunction(FunctionNode function, List<String> functions) {
        if (!functions.contains(function.getName())) {
            functions.add(function.getName());
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(function, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.duplicateFunction(function.getName(), source.getSource(),
                    source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.duplicateFunction(function.getName());
        }
    }

    private void checkFunctionParameter(String parameter, List<String> parameters,
            FunctionNode function) {
        if (!parameters.contains(parameter)) {
            parameters.add(parameter);
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(function, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.duplicateFunctionParameter(parameter, source.getSource(),
                    source.getLine(), source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.duplicateFunctionParameter(parameter);
        }
    }

    private void checkType(TypeNode type, List<String> types) {
        if (!types.contains(type.getName())) {
            types.add(type.getName());
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(type, Attribute.Source.class);

        if (opt.isPresent()) {
            Attribute.Source source = opt.get();

            throw CompilerErrors.duplicateType(type.getName(), source.getSource(), source.getLine(),
                    source.getCol(), source.getLength());
        } else {
            throw CompilerErrors.duplicateType(type.getName());
        }
    }
}
