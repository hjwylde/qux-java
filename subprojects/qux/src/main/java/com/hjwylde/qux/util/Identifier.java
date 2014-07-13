package com.hjwylde.qux.util;

import static com.google.common.base.Preconditions.checkNotNull;

import com.hjwylde.qux.tree.Node;

import java.util.Arrays;
import java.util.Collection;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public final class Identifier extends Node implements Comparable<Identifier> {

    private final String id;

    public Identifier(String id, Attribute... attributes) {
        this(id, Arrays.asList(attributes));
    }

    public Identifier(String id, Collection<? extends Attribute> attributes) {
        super(attributes);

        this.id = checkNotNull(id, "id cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Identifier that) {
        return id.compareTo(that.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return id.equals(((Identifier) obj).id);
    }

    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return id;
    }
}
