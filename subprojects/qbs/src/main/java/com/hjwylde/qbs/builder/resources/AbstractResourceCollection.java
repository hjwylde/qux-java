package com.hjwylde.qbs.builder.resources;

import com.google.common.base.Optional;

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
    public Optional<Resource.Single> getById(String id) {
        for (Resource resource : this) {
            if (resource.containsId(id)) {
                return resource.getById(id);
            }
        }

        return Optional.absent();
    }
}
