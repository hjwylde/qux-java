package qux.lang;

import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_NULL;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Null extends Obj implements Comparable<Null> {

    public static final Null INSTANCE = new Null();

    private Null() {}

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
    public Bool _eq_(Null t) {
        return TRUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _neq_(Null t) {
        return FALSE;
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
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_NULL;
    }
}

