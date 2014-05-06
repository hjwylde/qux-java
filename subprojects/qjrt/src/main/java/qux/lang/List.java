package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import java.math.BigInteger;
import java.util.Arrays;

import qux.lang.op.Access;
import qux.lang.op.Assign;
import qux.lang.op.Len;
import qux.lang.op.Slice;
import qux.util.Iterable;
import qux.util.Iterator;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class List extends Obj implements Access, Assign, Iterable, Len, Slice {

    private Obj[] data;
    private int count;

    private int refs;

    private List() {
        this.data = new Obj[10];
        count = 0;
    }

    private List(List list) {
        this.data = list.data;
        this.count = list.count;

        // Lazily clone the data only when the first write is performed
        refs = 1;
        // Can't forget the fact that this list also references the prior list!
        list.refs++;
    }

    private List(Obj[] data) {
        checkArgument(!Arrays.asList(data).contains(null), "data cannot contain null");

        this.data = data.clone();
        this.count = data.length;

        if (count == 0) {
            this.data = new Obj[10];
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Obj _access_(Int index) {
        return get(index);
    }

    public List _add_(List list) {
        List union = new List(this);

        union.ensureCapacity(list._len_());

        for (Iterator it = list._iter_(); it.hasNext() == TRUE; ) {
            union.add(it.next());
        }

        return union;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void _assign_(Int index, Obj value) {
        set(index, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(Obj obj) {
        if (!(obj instanceof List)) {
            return meta()._comp_(obj.meta());
        }

        List that = (List) obj;

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

        sb.append("[");
        for (Iterator it = _iter_(); it.hasNext() == TRUE; ) {
            sb.append(it.next()._desc_());
            sb.append(", ");
        }
        if (sb.length() > 2) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");

        return Str.valueOf(sb.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List _dup_() {
        return new List(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(Obj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        List that = (List) obj;

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

            private Obj[] data = List.this.data;
            private int count = List.this.count;
            private int index = 0;

            @Override
            public Bool hasNext() {
                if (index < count) {
                    return TRUE;
                }

                // Check if the list is still the same, if it is we can decrement the refs count
                if (List.this.data == data) {
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

    public List _mul_(Int value) {
        checkArgument(value._gte_(Int.ZERO) == TRUE, "cannot multiply a list by negative value");

        List mul = new List();

        if (value._eq_(Int.ZERO) == TRUE) {
            return mul;
        }

        while (value._gt_(Int.ZERO) == TRUE) {
            mul = mul._add_(this);

            value = value._sub_(Int.ONE);
        }

        return mul;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List _slice_(Int from, Int to) {
        return sublist(from, to);
    }

    public List _sub_(List list) {
        List difference = new List(this);

        for (Iterator it = list._iter_(); it.hasNext() == TRUE; ) {
            difference.remove(it.next());
        }

        return difference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        Set types = Set.valueOf();

        for (Iterator it = _iter_(); it.hasNext() == TRUE; ) {
            types.add(it.next().meta());
        }

        if (types.isEmpty() == TRUE) {
            return Meta.forSet(Meta.META_ANY);
        }

        if (types._len_()._eq_(Int.ONE) == TRUE) {
            return Meta.forSet((Meta) types._iter_().next());
        }

        return Meta.forSet(Meta.forUnion(types));
    }

    public static List valueOf(Obj... data) {
        return new List(data);
    }

    synchronized void add(Obj obj) {
        checkRefs();

        ensureCapacity();

        data[count++] = checkNotNull(obj, "obj cannot be null");
    }

    Obj get(Int index) {
        return get(index._value_());
    }

    synchronized Obj get(int index) {
        checkElementIndex(index, count);

        return data[index];
    }

    Obj get(BigInteger index) {
        checkArgument(index.bitLength() < 32, "lists of size larger than 32 bits is unsupported");

        return get(index.intValue());
    }

    int indexOf(Obj obj) {
        int index = 0;
        for (Iterator it = _iter_(); it.hasNext() == TRUE; ) {
            if (it.next().equals(obj)) {
                return index;
            }

            index++;
        }

        return -index;
    }

    Bool isEmpty() {
        return count == 0 ? TRUE : FALSE;
    }

    synchronized void remove(Obj obj) {
        checkRefs();

        int index = indexOf(obj);

        if (index < 0) {
            return;
        }

        System.arraycopy(data, index + 1, data, index, count - (index + 1));

        count--;
    }

    void set(Int index, Obj value) {
        set(index._value_(), value);
    }

    synchronized void set(int index, Obj value) {
        checkElementIndex(index, count);

        checkRefs();

        data[index] = checkNotNull(value, "value cannot be null");
    }

    void set(BigInteger index, Obj value) {
        checkArgument(index.bitLength() < 32, "lists of size larger than 32 bits is unsupported");

        set(index.intValue(), value);
    }

    List sublist(Int from, Int to) {
        return sublist(from._value_(), to._value_());
    }

    synchronized List sublist(int from, int to) {
        checkElementIndex(from, count, "from index out of bounds");
        checkPositionIndex(to, count, "to index out of bounds");
        checkArgument(from <= to, "from must be less than or equal to to (from=%s, to=%s)", from,
                to);

        return valueOf(Arrays.copyOfRange(data, from, to));
    }

    List sublist(BigInteger from, BigInteger to) {
        checkArgument(from.bitLength() < 32, "lists of size larger than 32 bits is unsupported");
        checkArgument(to.bitLength() < 32, "lists of size larger than 32 bits is unsupported");

        return sublist(from.intValue(), to.intValue());
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
}
