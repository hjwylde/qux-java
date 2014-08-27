package qux.lang;

import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import java.math.BigInteger;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class AbstractObj {

    /**
     * This class may only be extended locally.
     */
    AbstractObj() {}

    public abstract Int _comp_(AbstractObj obj);

    public abstract Str _desc_();

    public abstract AbstractObj _dup_();

    public Bool _eq_(AbstractObj obj) {
        if (this == obj) {
            return TRUE;
        }

        return (obj != null && getClass() == obj.getClass()) ? TRUE : FALSE;
    }

    public Bool _gt_(AbstractObj obj) {
        return _comp_(obj)._gt_(Int.ZERO);
    }

    public Bool _gte_(AbstractObj obj) {
        return _comp_(obj)._gte_(Int.ZERO);
    }

    public abstract Int _hash_();

    public Bool _lt_(AbstractObj obj) {
        return _comp_(obj)._lt_(Int.ZERO);
    }

    public Bool _lte_(AbstractObj obj) {
        return _comp_(obj)._lte_(Int.ZERO);
    }

    public Bool _neq_(AbstractObj obj) {
        return _eq_(obj)._not_();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return _eq_((AbstractObj) obj) == TRUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return _hash_()._value_().mod(BigInteger.valueOf(Integer.MAX_VALUE)).intValue();
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

