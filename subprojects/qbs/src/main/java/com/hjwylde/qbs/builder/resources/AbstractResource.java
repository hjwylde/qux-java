package com.hjwylde.qbs.builder.resources;

import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Represents an abstract implementation of a resource. This resource is a singular resource, so it
 * has an identification string.
 *
 * @author Henry J. Wylde
 */
public abstract class AbstractResource implements Resource.Single {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsId(String id) {
        return getId().equals(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        return getId().equals(((AbstractResource) obj).getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Single> getById(String id) {
        return Optional.ofNullable(containsId(id) ? this : null);
    }

    /**
     * Gets the identifier for this resource. The identifier is a '.' delimited identifier.
     *
     * @return the identifier.
     */
    @Override
    public abstract String getId();

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getId();
    }
}
