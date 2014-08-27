package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;
import com.hjwylde.qux.util.Identifier;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * TODO: Documentation.
 * <p>
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
        node.getConstants().forEach(c -> checkConstant(c.getName(), constants));

        Set<Identifier> functions = new HashSet<>();
        for (FunctionNode function : node.getFunctions()) {
            checkFunction(function.getName(), functions);

            Set<Identifier> parameters = new HashSet<>();
            function.getParameters().forEach(p -> checkFunctionParameter(p, parameters, function));
        }

        Set<Identifier> types = new HashSet<>();
        // TODO: Check for recursive types
        node.getTypes().forEach(t -> checkType(t.getName(), types));

        return node;
    }

    private void checkConstant(Identifier constant, Set<Identifier> constants) {
        if (!constants.contains(constant)) {
            constants.add(constant);
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(constant, Attribute.Source.class);
        Attribute.Source source = opt.orElseThrow(() -> CompilerErrors.duplicateConstant(
                constant.getId()));

        throw CompilerErrors.duplicateConstant(constant.getId(), source.getSource(),
                source.getLine(), source.getCol(), source.getLength());
    }

    private void checkFunction(Identifier function, Set<Identifier> functions) {
        if (!functions.contains(function)) {
            functions.add(function);
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(function, Attribute.Source.class);
        Attribute.Source source = opt.orElseThrow(() -> CompilerErrors.duplicateFunction(
                function.getId()));

        throw CompilerErrors.duplicateFunction(function.getId(), source.getSource(),
                source.getLine(), source.getCol(), source.getLength());
    }

    private void checkFunctionParameter(Identifier parameter, Set<Identifier> parameters,
            FunctionNode function) {
        if (!parameters.contains(parameter)) {
            parameters.add(parameter);
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(function, Attribute.Source.class);
        Attribute.Source source = opt.orElseThrow(() -> CompilerErrors.duplicateFunctionParameter(
                parameter.getId()));

        throw CompilerErrors.duplicateFunctionParameter(parameter.getId(), source.getSource(),
                source.getLine(), source.getCol(), source.getLength());
    }

    private void checkType(Identifier type, Set<Identifier> types) {
        if (!types.contains(type)) {
            types.add(type);
            return;
        }

        Optional<Attribute.Source> opt = Attributes.getAttribute(type, Attribute.Source.class);
        Attribute.Source source = opt.orElseThrow(() -> CompilerErrors.duplicateType(type.getId()));

        throw CompilerErrors.duplicateType(type.getId(), source.getSource(), source.getLine(),
                source.getCol(), source.getLength());
    }
}
