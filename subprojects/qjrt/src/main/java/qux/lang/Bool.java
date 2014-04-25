package qux.lang;

import static qux.lang.Meta.META_BOOL;

import qux.lang.operators.Not;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Bool extends Obj implements Comparable<Bool>, Orderable<Bool>, Not {

    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(false);

    private final boolean value;

    private Bool(boolean value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return Str.valueOf(value ? "true" : "false");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(Bool t) {
        return (this == t) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _gt_(Bool t) {
        return (this == TRUE && t == FALSE) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _gte_(Bool t) {
        return (this == TRUE || t == FALSE) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lt_(Bool t) {
        return (this == FALSE && t == TRUE) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lte_(Bool t) {
        return (this == FALSE || t == TRUE) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _neq_(Bool t) {
        return this != t ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _not_() {
        return value ? FALSE : TRUE;
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
        return value ? 1 : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_BOOL;
    }

    public static Bool valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }
}
