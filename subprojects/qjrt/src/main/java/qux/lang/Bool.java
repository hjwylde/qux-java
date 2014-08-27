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

    public Bool _and_(Bool t) {
        return (this == TRUE && t == TRUE) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(AbstractObj obj) {
        if (!(obj instanceof Bool)) {
            return meta()._comp_(obj.meta());
        }

        Bool that = (Bool) obj;

        if (this == that) {
            return Int.ZERO;
        }

        return this == TRUE ? Int.ONE : Int.M_ONE;
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
    public Bool _dup_() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(AbstractObj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        return this == obj ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return value ? Int.ONE : Int.M_ONE;
    }

    public Bool _iff_(Bool t) {
        return (this == t) ? TRUE : FALSE;
    }

    public Bool _imp_(Bool t) {
        return (this == FALSE || t == TRUE) ? TRUE : FALSE;
    }

    public Bool _not_() {
        return value ? FALSE : TRUE;
    }

    public Bool _or_(Bool t) {
        return (this == TRUE || t == TRUE) ? TRUE : FALSE;
    }

    public Bool _xor_(Bool t) {
        return (this == TRUE ^ t == TRUE) ? TRUE : FALSE;
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
