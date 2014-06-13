package com.hjwylde.qux.pipelines;

import com.hjwylde.common.error.CompilerErrors;
import com.hjwylde.qbs.builder.QuxContext;
import com.hjwylde.qux.tree.ConstantNode;
import com.hjwylde.qux.tree.FunctionNode;
import com.hjwylde.qux.tree.QuxNode;
import com.hjwylde.qux.util.Attribute;
import com.hjwylde.qux.util.Attributes;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Documentation.
 * <p/>
 * Responsible for checking the following: <ul> <li>No duplicate imports declared</li> <li>No
 * clashing imports declared</li> <li>No duplicate constants declared</li> <li>No duplicate
 * functions declared</li> <li>No clashing function parameters</li> </ul>
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
        Map<String, String> namespace = new HashMap<>();
        for (String id : node.getImports()) {
            checkImport(id, namespace);
        }

        List<String> constants = new ArrayList<>();
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

    private void checkImport(String id, Map<String, String> namespace) {
        if (namespace.values().contains(id)) {
            throw CompilerErrors.duplicateImport(id);
        }

        if (id.contains("$")) {
            String key = id.substring(id.lastIndexOf("$"));

            if (namespace.containsKey(key)) {
                throw CompilerErrors.duplicateImport(id);
            }

            namespace.put(key, id);
        } else if (id.contains(".")) {
            String key = id.substring(id.lastIndexOf(".") + 1);

            if (namespace.containsKey(key)) {
                throw CompilerErrors.duplicateImport(id);
            }

            namespace.put(key, id);
        } else {
            throw new InternalError("cannot import file from root package");
        }
    }
}
