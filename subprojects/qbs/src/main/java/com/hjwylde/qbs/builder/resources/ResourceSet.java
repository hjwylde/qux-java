package com.hjwylde.qbs.builder.resources;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Iterator;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 */
public class ResourceSet extends AbstractResourceCollection {

    private final ImmutableSet<Resource> resources;

    public ResourceSet(java.util.Collection<? extends Resource> resources) {
        this.resources = ImmutableSet.copyOf(resources);
    }

    public ResourceSet(Resource... resources) {
        this(Arrays.asList(resources));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        return resources.equals(((ResourceSet) obj).resources);
    }

    public final ImmutableSet<Resource> getResources() {
        return resources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return resources.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Iterator<Resource> iterator() {
        return resources.iterator();
    }
}
