package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import java.math.BigInteger;
import java.util.Arrays;

import qux.lang.op.Access;
import qux.lang.op.Len;
import qux.util.Iterable;
import qux.util.Iterator;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public final class Set extends Obj implements Len, Access, Iterable {

    private Obj[] data;
    private int count;

    private int refs;

    private Set() {
        this.data = new Obj[10];
        count = 0;
    }

    private Set(Set set) {
        this.data = set.data;
        this.count = set.count;
    }

    private Set(Obj[] data) {
        this();

        checkArgument(!Arrays.asList(data).contains(null), "data cannot contain null");

        for (Obj datum : data) {
            add(datum);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Obj _access_(Int index) {
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
    public Int _comp_(Obj obj) {
        if (!(obj instanceof List)) {
            return meta()._comp_(obj.meta());
        }

        Set that = (Set) obj;

        Int comp = _len_()._comp_(that._len_());
        if (comp._eq_(Int.ZERO) == FALSE) {
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

    public Bool _contains_(Obj obj) {
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
            sb.append(", ");
        }
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");

        return Str.valueOf(sb.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(Obj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        Set that = (Set) obj;

        if (_len_()._eq_(that._len_()) == FALSE) {
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

            private Obj[] data = Set.this.data;
            private int count = Set.this.count;
            private int index = 0;

            @Override
            public Bool hasNext() {
                if (index < count) {
                    return TRUE;
                }

                // Check if the list is still the same, if it is we can decrement the refs count
                if (Set.this.data == data) {
                    refs--;
                }

                return FALSE;
            }

            @Override
            public Obj next() {
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

    public Set _sub_(Set set) {
        Set difference = new Set(this);

        for (Iterator it = set._iter_(); it.hasNext() == TRUE; ) {
            difference.remove(it.next());
        }

        return difference;
    }

    public synchronized void add(Obj obj) {
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

    public synchronized void clear() {
        checkRefs();

        ensureCapacity();

        count = 0;
    }

    public Obj get(Int index) {
        return get(index._value_());
    }

    public Obj get(int index) {
        checkElementIndex(index, count,
                "index out of bounds (index='" + index + "', size='" + count + "')");

        return data[index];
    }

    public Obj get(BigInteger index) {
        checkArgument(index.bitLength() < 32, "sets of size larger than 32 bits is unsupported");

        return get(index.intValue());
    }

    public Bool isEmpty() {
        return count == 0 ? TRUE : FALSE;
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

        if (types._len_()._eq_(Int.ONE) == TRUE) {
            return Meta.forSet((Meta) types.data[0]);
        }

        return Meta.forSet(Meta.forUnion(types));
    }

    public synchronized void remove(Obj obj) {
        checkNotNull(obj, "obj cannot be null");

        checkRefs();

        int index = indexOf(obj);

        if (index < 0) {
            return;
        }

        System.arraycopy(data, index + 1, data, index, count - (index + 1));

        count--;
    }

    public static Set valueOf(Obj... data) {
        return new Set(data);
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
        checkArgument(capacity.bitLength() < 32,
                "lists of size larger than 32 bits is unsupported");

        ensureCapacity(capacity.intValue());
    }

    private synchronized void ensureCapacity(int capacity) {
        checkArgument(capacity >= 0, "capacity must be non-negative");

        while (count + capacity > data.length) {
            data = Arrays.copyOf(data, data.length * 2);
        }
    }

    private int indexOf(Obj obj) {
        return indexOf(obj, 0, count);
    }

    private int indexOf(Obj obj, int low, int high) {
        if (low == high) {
            return -low - 1;
        }

        int mid = low + high / 2;

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
