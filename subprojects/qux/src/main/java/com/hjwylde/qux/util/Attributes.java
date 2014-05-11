package com.hjwylde.qux.util;

import com.hjwylde.qux.tree.Node;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

/**
 * Utility class for methods pertaining to an {@link com.hjwylde.qux.util.Attribute} and a {@link
 * com.hjwylde.qux.tree.Node}.
 *
 * @author Henry J. Wylde
 */
public final class Attributes {

    /**
     * This class cannot be instantiated.
     */
    private Attributes() {}

    /**
     * Attempts to get an attribute of the given class from the given node. If the attribute does
     * not exist, {@code Optional.absent()} is returned.
     *
     * @param node the node to search.
     * @param clazz the class of the attribute to get.
     * @param <T> the type of attribute.
     * @return the attribute if it was found, otherwise {@code Optional.absent()}.
     */
    public static <T extends Attribute> Optional<T> getAttribute(Node node, Class<T> clazz) {
        return getAttribute(node.getAttributes(), clazz);
    }

    /**
     * Attempts to get an attribute of the given class from the given attributes collection. If the
     * attribute does not exist, {@code Optional.absent()} is returned.
     *
     * @param attributes the attributes to search.
     * @param clazz the class of the attribute to get.
     * @param <T> the type of attribute.
     * @return the attribute if it was found, otherwise {@code Optional.absent()}.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Attribute> Optional<T> getAttribute(
            Collection<? extends Attribute> attributes, Class<T> clazz) {
        for (Attribute attribute : attributes) {
            if (clazz.isInstance(attribute)) {
                return Optional.of((T) attribute);
            }
        }

        return Optional.absent();
    }

    /**
     * Attempts to get an attribute of the given class from the given node. This method assumes the
     * attribute exists. If it doesn't, then an error is thrown.
     *
     * @param node the node to search.
     * @param clazz the class of the attribute to get.
     * @param <T> the type of attribute.
     * @return the attribute.
     */
    public static <T extends Attribute> T getAttributeUnchecked(Node node, Class<T> clazz) {
        return getAttributeUnchecked(node.getAttributes(), clazz);
    }

    /**
     * Attempts to get an attribute of the given class from the given attributes collection. This
     * method assumes the attribute exists. If it doesn't, then an error is thrown.
     *
     * @param attributes the attributes to search.
     * @param clazz the class of the attribute to get.
     * @param <T> the type of attribute.
     * @return the attribute.
     */
    public static <T extends Attribute> T getAttributeUnchecked(
            Collection<? extends Attribute> attributes, Class<T> clazz) {
        return getAttribute(attributes, clazz).get();
    }

    /**
     * Gets a list of all attributes of the given class from the given node.
     *
     * @param node the node to search.
     * @param clazz the class of the attribute to get.
     * @param <T> the type of attribute.
     * @return the attributes.
     */
    public static <T extends Attribute> ImmutableList<T> getAttributes(Node node, Class<T> clazz) {
        return getAttributes(node.getAttributes(), clazz);
    }

    /**
     * Gets a list of all attributes of the given class from the given attributes collection.
     *
     * @param attributes the attributes to search.
     * @param clazz the class of the attribute to get.
     * @param <T> the type of attribute.
     * @return the attributes.
     */
    @SuppressWarnings("unchecked")
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
