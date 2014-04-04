package com.hjwylde.qbs.builder.resources;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A lazily initialised resource will attempt to provide as many default method implementations as
 * possible without having to read the delegate resource. The delegate resource will only be loaded
 * at the last minute needed and is guaranteed to be only loaded once.
 *
 * @author Henry J. Wylde
 */
public abstract class LazilyInitialisedResource extends AbstractResource {

    private final String id;
    private Resource.Single delegate;

    /**
     * Creates a new lazily initialised resource with the given identifier.
     *
     * @param id the identifier.
     */
    public LazilyInitialisedResource(String id) {
        this.id = checkNotNull(id, "id cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isClassVisible(String id) {
        if (delegate == null) {
            delegate = loadDelegate();
        }

        return delegate.isClassVisible(id);
    }

    /**
     * Loads the delegate resource. This method is only called when necessary to prevent unneeded
     * I/O operations.
     *
     * @return the resource delegate.
     */
    protected abstract Resource.Single loadDelegate();
}
