package qux.lang;

import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_NULL;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Null extends Obj {

    public static final Null INSTANCE = new Null();

    private Null() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(Obj obj) {
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
    public Bool _eq_(Obj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        return this == obj ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_NULL;
    }
}

