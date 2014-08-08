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
public final class List extends AbstractObj implements Assign, Iterable, Len, Slice {

    private AbstractObj[] data;
    private int count;

    private int refs;

    private List() {
        data = new AbstractObj[10];
        count = 0;
    }

    private List(List list) {
        data = list.data;
        count = list.count;

        // Lazily clone the data only when the first write is performed
        refs = 1;
        // Can't forget the fact that this list also references the prior list!
        list.refs++;
    }

    private List(AbstractObj[] data) {
        checkArgument(!asList(data).contains(null), "data cannot contain null");

        this.data = data.clone();
        count = data.length;

        if (count == 0) {
            this.data = new AbstractObj[10];
        }
    }

    public AbstractObj _access_(Int index) {
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
    public void _assign_(Int index, AbstractObj value) {
        set(index, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(AbstractObj obj) {
        if (!(obj instanceof List)) {
            return meta()._comp_(obj.meta());
        }

        List that = (List) obj;

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

    public Bool _contains_(AbstractObj obj) {
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

            if (it.hasNext() == TRUE) {
                sb.append(", ");
            }
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
    public Bool _eq_(AbstractObj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        List that = (List) obj;

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
        refs++;

        return new Iterator() {

            private AbstractObj[] data = List.this.data;
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

        if (types._len_().equals(Int.ONE)) {
            return Meta.forSet((Meta) types._iter_().next());
        }

        return Meta.forSet(Meta.forUnion(types));
    }

    public static List valueOf(AbstractObj... data) {
        return new List(data);
    }

    synchronized void add(AbstractObj obj) {
        checkRefs();

        ensureCapacity();

        data[count++] = checkNotNull(obj, "obj cannot be null");
    }

    AbstractObj get(Int index) {
        return get(index._value_());
    }

    synchronized AbstractObj get(int index) {
        checkElementIndex(index, count);

        return data[index];
    }

    AbstractObj get(BigInteger index) {
        checkArgument(index.bitLength() < 32, "lists of size larger than 32 bits is unsupported");

        return get(index.intValue());
    }

    int indexOf(AbstractObj obj) {
        int index = 0;
        for (Iterator it = _iter_(); it.hasNext() == TRUE; ) {
            if (it.next().equals(obj)) {
                return index;
            }

            index++;
        }

        return -index - 1;
    }

    Bool isEmpty() {
        return count == 0 ? TRUE : FALSE;
    }

    synchronized void remove(AbstractObj obj) {
        checkRefs();

        int index = indexOf(obj);

        if (index < 0) {
            return;
        }

        System.arraycopy(data, index + 1, data, index, count - (index + 1));

        count--;
    }

    void set(Int index, AbstractObj value) {
        set(index._value_(), value);
    }

    synchronized void set(int index, AbstractObj value) {
        checkElementIndex(index, count);

        checkRefs();

        data[index] = checkNotNull(value, "value cannot be null");
    }

    void set(BigInteger index, AbstractObj value) {
        checkArgument(index.bitLength() < 32, "lists of size larger than 32 bits is unsupported");

        set(index.intValue(), value);
    }

    List sublist(Int from, Int to) {
        return sublist(from._value_(), to._value_());
    }

    synchronized List sublist(int from, int to) {
        checkPositionIndex(from, count, "from");
        checkPositionIndex(to, count, "to");
        checkArgument(from <= to, "from must be less than or equal to to (from=%s, to=%s)", from,
                to);

        return valueOf(copyOfRange(data, from, to));
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
            data = copyOf(data, data.length * 2);
        }
    }
}
