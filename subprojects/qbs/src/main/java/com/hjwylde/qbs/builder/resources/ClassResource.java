package com.hjwylde.qbs.builder.resources;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public final class ClassResource extends AbstractResource {

    private final Class<?> clazz;

    public ClassResource(Class<?> clazz) {
        this.clazz = checkNotNull(clazz, "clazz cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getConstantType(String name) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getFunctionType(String name) {
        Set<String> methods = new HashSet<>();
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(name)) {
                methods.add(getType(method));
            }
        }

        if (methods.isEmpty()) {
            return Optional.empty();
        }

        if (methods.size() == 1) {
            return Optional.of(methods.iterator().next());
        }

        // TODO: Throw a proper exception
        throw new InternalError();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return clazz.getName().replace('/', '.');
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getTypeType(String name) {
        return Optional.empty();
    }
}
