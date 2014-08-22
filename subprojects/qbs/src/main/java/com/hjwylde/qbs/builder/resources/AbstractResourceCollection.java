package com.hjwylde.qbs.builder.resources;

import com.google.common.collect.Iterables;

import java.util.Optional;

/**
 * Represents a collection of resources. A collection of resources can be iterated over in order to
 * try and locate a certain resource.
 *
 * @author Henry J. Wylde
 */
public abstract class AbstractResourceCollection implements Resource.Collection {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsId(String id) {
        for (Resource resource : this) {
            if (resource.containsId(id)) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        return Iterables.elementsEqual(this, (AbstractResourceCollection) obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Single> getById(String id) {
        for (Resource resource : this) {
            if (resource.containsId(id)) {
                return resource.getById(id);
            }
        }

        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (Resource resource : this) {
            hash += resource.hashCode();
        }

        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Iterables.toString(this);
    }
}
