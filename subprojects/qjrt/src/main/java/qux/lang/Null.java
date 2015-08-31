package qux.lang;

import static qux.lang.Meta.META_NULL;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Null extends AbstractObj {

    public static final Null INSTANCE = new Null();

    private Null() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        return meta().compareTo(obj.meta());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Null dup() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "null";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        return META_NULL;
    }
}

