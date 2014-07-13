package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import java.math.BigInteger;

import qux.lang.op.Len;
import qux.util.Iterable;
import qux.util.Iterator;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public final class Tuple extends AbstractObj implements Iterable, Len {

    private final AbstractObj[] data;

    private Tuple(AbstractObj... data) {
        checkArgument(data.length > 1, "data must have at least 2 elements");

        this.data = data.clone();
    }

    public AbstractObj _access_(Int index) {
        return get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(AbstractObj obj) {
        if (!(obj instanceof Tuple)) {
            return meta()._comp_(obj.meta());
        }

        Tuple that = (Tuple) obj;

        Int comp = _len_()._comp_(that._len_());
        if (!comp.equals(Int.ZERO)) {
            return comp;
        }

        Iterator thisIt = _iter_();
        Iterator thatIt = that._iter_();

        while (thisIt.hasNext() == TRUE) {
            comp = thisIt.next()._comp_(thatIt.next());
            if (!comp.equals(Int.ZERO)) {
                return comp;
            }
        }

        return Int.ZERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        StringBuilder sb = new StringBuilder();

        sb.append("(");
        for (Iterator it = _iter_(); it.hasNext() == TRUE; ) {
            sb.append(it.next()._desc_());

            if (it.hasNext() == TRUE) {
                sb.append(", ");
            }
        }
        sb.append(")");

        return Str.valueOf(sb.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple _dup_() {
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

        Tuple that = (Tuple) obj;

        if (!_len_().equals(that._len_())) {
            return FALSE;
        }

        Iterator thisIt = _iter_();
        Iterator thatIt = that._iter_();

        while (thisIt.hasNext() == TRUE) {
            if (!thisIt.next().equals(thatIt.next())) {
                return FALSE;
            }
        }

        return TRUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        Int hash = Int.ZERO;

        for (Iterator it = _iter_(); it.hasNext() == TRUE; ) {
            hash = hash._add_(it.next()._hash_());
        }

        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Iterator _iter_() {
        return new Iterator() {

            private int index = 0;

            @Override
            public Bool hasNext() {
                return index < data.length ? TRUE : FALSE;
            }

            @Override
            public AbstractObj next() {
                return data[index++];
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _len_() {
        return Int.valueOf(data.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        List types = List.valueOf();

        for (Iterator it = _iter_(); it.hasNext() == TRUE; ) {
            types.add(it.next().meta());
        }

        return Meta.forTuple(types);
    }

    public static Tuple valueOf(AbstractObj... data) {
        return new Tuple(data);
    }

    AbstractObj get(Int index) {
        return get(index._value_());
    }

    synchronized AbstractObj get(int index) {
        checkElementIndex(index, data.length);

        return data[index];
    }

    AbstractObj get(BigInteger index) {
        checkArgument(index.bitLength() < 32, "tuples of size larger than 32 bits is unsupported");

        return get(index.intValue());
    }
}
