package com.hjwylde.qbs.builder.resources;

import static java.util.Arrays.asList;

import com.google.common.collect.ImmutableSet;

import java.util.Iterator;

/**
 * A set of resources.
 *
 * @author Henry J. Wylde
 */
public class ResourceSet extends AbstractResourceCollection {

    private final ImmutableSet<Resource> resources;

    /**
     * Creates a new {@code ResourceSet} with the given resources.
     *
     * @param resources the resources.
     */
    public ResourceSet(java.util.Collection<? extends Resource> resources) {
        this.resources = ImmutableSet.copyOf(resources);
    }

    /**
     * Creates a new {@code ResourceSet} with the given resources.
     *
     * @param resources the resources.
     */
    public ResourceSet(Resource... resources) {
        this(asList(resources));
    }

    /**
     * Gets the resources in this set.
     *
     * @return the resources.
     */
    public final ImmutableSet<Resource> getResources() {
        return resources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<Resource> iterator() {
        return resources.iterator();
    }
}
