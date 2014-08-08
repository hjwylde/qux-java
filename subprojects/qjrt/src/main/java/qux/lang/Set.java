package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static java.util.Arrays.copyOfRange;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import java.math.BigInteger;

import qux.lang.op.Len;
import qux.lang.op.Slice;
import qux.util.Iterable;
import qux.util.Iterator;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.1.3
 */
public final class Set extends AbstractObj implements Iterable, Len, Slice {

    private AbstractObj[] data;
    private int count;

    private int refs;

    private Set() {
        data = new AbstractObj[10];
        count = 0;
    }

    private Set(Set set) {
        data = set.data;
        count = set.count;

        // Lazily clone the data only when the first write is performed
        refs = 1;
        // Can't forget the fact that this set also references the prior set!
        set.refs++;
    }

    private Set(AbstractObj[] data) {
        this();

        checkArgument(!asList(data).contains(null), "data cannot contain null");

        for (AbstractObj datum : data) {
            add(datum);
        }
    }

    public AbstractObj _access_(Int index) {
        return get(index);
    }

    public Set _add_(Set set) {
        Set union = new Set(this);

        union.ensureCapacity(set._len_());

        for (Iterator it = set._iter_(); it.hasNext() == TRUE; ) {
            union.add(it.next());
        }

        return union;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(AbstractObj obj) {
        if (!(obj instanceof Set)) {
            return meta()._comp_(obj.meta());
        }

        Set that = (Set) obj;

        Int comp = _len_()._comp_(that._len_());
        if (!comp.equals(Int.ZERO)) {
            return comp;
        }

        Iterator thisIt = _iter_();
        Iterator thatIt = that._iter_();

        while (thisIt.hasNext() == TRUE) {
            comp = thisIt.next()._comp_(thatIt.next());
            if (comp._eq_(Int.ZERO) == FALSE) {
                return comp;
            }
        }

        return Int.ZERO;
    }

    public Bool _contains_(AbstractObj obj) {
        return indexOf(obj) >= 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        StringBuilder sb = new StringBuilder();

        sb.append("{");
        for (Iterator it = _iter_(); it.hasNext() == TRUE; ) {
            sb.append(it.next()._desc_());

            if (it.hasNext() == TRUE) {
                sb.append(", ");
            }
        }
        sb.append("}");

        return Str.valueOf(sb.toString());
    }

    /**
     * {@inheritDocn}
     */
    @Override
    public Set _dup_() {
        return new Set(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(AbstractObj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        Set that = (Set) obj;

        if (!_len_().equals(that._len_())) {
            return FALSE;
        }

        Iterator thisIt = _iter_();
        Iterator thatIt = that._iter_();

        while (thisIt.hasNext() == TRUE) {
            if (thisIt.next()._eq_(thatIt.next()) == FALSE) {
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
        refs++;

        return new Iterator() {

            private AbstractObj[] data = Set.this.data;
            private int count = Set.this.count;
            private int index = 0;

            @Override
            public Bool hasNext() {
                if (index < count) {
                    return TRUE;
                }

                // Check if the set is still the same, if it is we can decrement the refs count
                if (Set.this.data == data) {
                    refs--;
                }

                return FALSE;
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
        return Int.valueOf(count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set _slice_(Int from, Int to) {
        return subset(from, to);
    }

    public Set _sub_(Set set) {
        Set difference = new Set(this);

        for (Iterator it = set._iter_(); it.hasNext() == TRUE; ) {
            difference.remove(it.next());
        }

        return difference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        Set types = new Set();

        for (Iterator it = _iter_(); it.hasNext() == TRUE; ) {
            types.add(it.next().meta());
        }

        // TODO: Normalise the set before the checks
        // The normalisation should happen in the Meta.forSet methods

        if (types.isEmpty() == TRUE) {
            return Meta.forSet(Meta.META_ANY);
        }

        if (types._len_().equals(Int.ONE)) {
            return Meta.forSet((Meta) types.data[0]);
        }

        return Meta.forSet(Meta.forUnion(types));
    }

    public static Set valueOf(AbstractObj... data) {
        return new Set(data);
    }

    synchronized void add(AbstractObj obj) {
        checkNotNull(obj, "obj cannot be null");

        checkRefs();

        ensureCapacity();

        int index = indexOf(obj);

        if (index >= 0) {
            return;
        }

        index = -index - 1;

        System.arraycopy(data, index, data, index + 1, count - index);

        data[index] = obj;
        count++;
    }

    AbstractObj get(Int index) {
        return get(index._value_());
    }

    synchronized AbstractObj get(int index) {
        checkElementIndex(index, count);

        return data[index];
    }

    AbstractObj get(BigInteger index) {
        checkArgument(index.bitLength() < 32, "sets of size larger than 32 bits is unsupported");

        return get(index.intValue());
    }

    Bool isEmpty() {
        return count == 0 ? TRUE : FALSE;
    }

    synchronized void remove(AbstractObj obj) {
        checkNotNull(obj, "obj cannot be null");

        checkRefs();

        int index = indexOf(obj);

        if (index < 0) {
            return;
        }

        System.arraycopy(data, index + 1, data, index, count - (index + 1));

        count--;
    }

    Set subset(Int from, Int to) {
        return subset(from._value_(), to._value_());
    }

    synchronized Set subset(int from, int to) {
        checkPositionIndex(from, count, "from");
        checkPositionIndex(to, count, "to");
        checkArgument(from <= to, "from must be less than or equal to to (from=%s, to=%s)", from,
                to);

        return valueOf(copyOfRange(data, from, to));
    }

    Set subset(BigInteger from, BigInteger to) {
        checkArgument(from.bitLength() < 32, "sets of size larger than 32 bits is unsupported");
        checkArgument(to.bitLength() < 32, "sets of size larger than 32 bits is unsupported");

        return subset(from.intValue(), to.intValue());
    }

    private synchronized void checkRefs() {
        if (refs > 0) {
            data = data.clone();
            refs = 0;
        }
    }

    private synchronized void ensureCapacity() {
        ensureCapacity(1);
    }

    private synchronized void ensureCapacity(Int capacity) {
        ensureCapacity(capacity._value_());
    }

    private synchronized void ensureCapacity(BigInteger capacity) {
        checkArgument(capacity.bitLength() < 32, "sets of size larger than 32 bits is unsupported");

        ensureCapacity(capacity.intValue());
    }

    private synchronized void ensureCapacity(int capacity) {
        checkArgument(capacity >= 0, "capacity must be non-negative");

        while (count + capacity > data.length) {
            data = copyOf(data, data.length * 2);
        }
    }

    private synchronized int indexOf(AbstractObj obj) {
        return indexOf(obj, 0, count);
    }

    private synchronized int indexOf(AbstractObj obj, int low, int high) {
        if (low == high) {
            return -low - 1;
        }

        // Javas integer division rounds up, so let's force the truncation of the division
        // (emulates floor)
        int mid = (int) ((double) (low + high) / 2);

        if (obj.equals(data[mid])) {
            return mid;
        } else if (obj._comp_(data[mid])._lt_(Int.ZERO) == TRUE) {
            high = mid;
        } else {
            low = mid + 1;
        }

        return indexOf(obj, low, high);
    }
}
