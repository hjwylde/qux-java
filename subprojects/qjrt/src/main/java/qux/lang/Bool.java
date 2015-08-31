package qux.lang;

import static qux.lang.Meta.META_BOOL;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Bool extends AbstractObj {

    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(false);

    private final boolean value;

    private Bool(boolean value) {
        this.value = value;
    }

    public Bool _and(Bool t) {
        return value && t.value ? TRUE : FALSE;
    }

    public Bool _iff(Bool t) {
        return (this == t) ? TRUE : FALSE;
    }

    public Bool _imp(Bool t) {
        return (!value || t.value) ? TRUE : FALSE;
    }

    public Bool _not() {
        return value ? FALSE : TRUE;
    }

    public Bool _or(Bool t) {
        return (value || t.value) ? TRUE : FALSE;
    }

    public Bool _xor(Bool t) {
        return (value ^ t.value) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        if (!(obj instanceof Bool)) {
            return meta().compareTo(obj.meta());
        }

        Bool that = (Bool) obj;

        return this == that ? 0 : value ? 1 : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool dup() {
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
        return value ? 1 : -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value ? "true" : "false";
    }

    public static Bool valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        return META_BOOL;
    }
}
