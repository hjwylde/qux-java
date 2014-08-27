package qux.lang;

import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
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
    public Int _comp_(AbstractObj obj) {
        return meta()._comp_(obj.meta());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return Str.valueOf("null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Null _dup_() {
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
        return Int.M_ONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_NULL;
    }
}

