package qux.lang;

import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Null extends Obj implements Comparable<Null>, Orderable<Null> {

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
    public Bool _gt_(Null t) {
        return FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _gte_(Null t) {
        return TRUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lt_(Null t) {
        return FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lte_(Null t) {
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
}

