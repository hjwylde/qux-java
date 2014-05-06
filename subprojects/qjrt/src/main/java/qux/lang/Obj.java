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
public abstract class Obj implements Desc, Dup, Eq, Neq, Hash, Comp {

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return Str.valueOf("_desc_() not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(Obj obj) {
        if (this == obj) {
            return TRUE;
        }

        return (obj != null && getClass() == obj.getClass()) ? TRUE : FALSE;
    }

    public Bool _gt_(Obj t) {
        return _comp_(t)._gt_(Int.ZERO);
    }

    public Bool _gte_(Obj t) {
        return _comp_(t)._gte_(Int.ZERO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return Int.valueOf(hashCode());
    }

    public Bool _lt_(Obj t) {
        return _comp_(t)._lt_(Int.ZERO);
    }

    public Bool _lte_(Obj t) {
        return _comp_(t)._lte_(Int.ZERO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _neq_(Obj obj) {
        return _eq_(obj)._not_();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return _eq_((Obj) obj) == TRUE ? true : false;
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

