package com.hjwylde.qux.util;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Attributes {

    /**
     * This class cannot be instantiated.
     */
    private Attributes() {}

    public static <T extends Attribute> Optional<T> getAttribute(
            Collection<? extends Attribute> attributes, Class<T> clazz) {
        for (Attribute attribute : attributes) {
            if (clazz.isInstance(attribute)) {
                return Optional.of((T) attribute);
            }
        }

        return Optional.absent();
    }

    public static <T extends Attribute> T getAttributeUnchecked(
            Collection<? extends Attribute> attributes, Class<T> clazz) {
        return getAttribute(attributes, clazz).get();
    }

    public static <T extends Attribute> ImmutableList<T> getAttributes(
            Collection<? extends Attribute> attributes, Class<T> clazz) {
        ImmutableList.Builder<T> builder = ImmutableList.builder();
        for (Attribute attribute : attributes) {
            if (clazz.isInstance(attribute)) {
                builder.add((T) attribute);
            }
        }

        return builder.build();
    }
}
