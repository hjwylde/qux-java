package qux.lang;

import static qux.lang.Meta.META_BOOL;

import qux.lang.op.And;
import qux.lang.op.Iff;
import qux.lang.op.Implies;
import qux.lang.op.Not;
import qux.lang.op.Or;
import qux.lang.op.Xor;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Bool extends Obj implements And, Or, Xor, Iff, Implies, Not {

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
    public Bool _and_(Bool t) {
        return (this == TRUE && t == TRUE) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(Obj obj) {
        if (!(obj instanceof Bool)) {
            return meta()._comp_(obj.meta());
        }

        Bool that = (Bool) obj;

        if (this == that) {
            return Int.ZERO;
        }

        return this == TRUE ? Int.M_ONE : Int.ONE;
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
    public Bool _eq_(Obj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        return this == obj ? TRUE : FALSE;
    }

    public Bool _gt_(Bool t) {
        return (this == TRUE && t == FALSE) ? TRUE : FALSE;
    }

    public Bool _gte_(Bool t) {
        return (this == TRUE || t == FALSE) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _iff_(Bool t) {
        return (this == t) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _implies_(Bool t) {
        return (this == FALSE || t == TRUE) ? TRUE : FALSE;
    }

    public Bool _lt_(Bool t) {
        return (this == FALSE && t == TRUE) ? TRUE : FALSE;
    }

    public Bool _lte_(Bool t) {
        return (this == FALSE || t == TRUE) ? TRUE : FALSE;
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
    public Bool _or_(Bool t) {
        return (this == TRUE || t == TRUE) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
