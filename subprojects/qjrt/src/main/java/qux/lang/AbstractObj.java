package qux.lang;

import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import qux.lang.op.Comp;
import qux.lang.op.Desc;
import qux.lang.op.Dup;
import qux.lang.op.Eq;
import qux.lang.op.Hash;
import qux.lang.op.Neq;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class AbstractObj implements Desc, Dup, Eq, Neq, Hash, Comp {

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(AbstractObj obj) {
        if (this == obj) {
            return TRUE;
        }

        return (obj != null && getClass() == obj.getClass()) ? TRUE : FALSE;
    }

    public Bool _gt_(AbstractObj t) {
        return _comp_(t)._gt_(Int.ZERO);
    }

    public Bool _gte_(AbstractObj t) {
        return _comp_(t)._gte_(Int.ZERO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return Int.valueOf(hashCode());
    }

    public Bool _lt_(AbstractObj t) {
        return _comp_(t)._lt_(Int.ZERO);
    }

    public Bool _lte_(AbstractObj t) {
        return _comp_(t)._lte_(Int.ZERO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _neq_(AbstractObj obj) {
        return _eq_(obj)._not_();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return _eq_((AbstractObj) obj) == TRUE ? true : false;
    }

    public abstract Meta meta();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _desc_().toString();
    }
}

