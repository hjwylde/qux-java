package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import qux.lang.op.Len;
import qux.util.Iterable;
import qux.util.Iterator;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class List extends Obj implements Len, Iterable<Obj> {

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
    }

    private List(Obj[] data) {
        checkArgument(!Arrays.asList(data).contains(null), "data cannot contain null");

        this.data = data.clone();
        this.count = data.length;

        if (count == 0) {
            this.data = new Obj[10];
        }
    }

    public List _add_(List list) {
        List union = new List(this);

        union.ensureCapacity(list._len_());

        for (Iterator<Obj> it = list._iter_(); it.hasNext() == TRUE; ) {
            union.add(it.next());
        }

        return union;
    }

    public Bool _contains_(Obj obj) {
        for (Iterator<Obj> it = _iter_(); it.hasNext() == TRUE; ) {
            if (it.next().equals(obj)) {
                return TRUE;
            }
        }

        return FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        for (Iterator<Obj> it = _iter_(); it.hasNext() == TRUE; ) {
            sb.append(it.next()._desc_());
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");

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

        List that = (List) obj;

        if (_len_()._eq_(that._len_()) == FALSE) {
            return FALSE;
        }

        Iterator<Obj> thisIt = _iter_();
        Iterator<Obj> thatIt = that._iter_();

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
        Int hash = Int.valueOf(0);

        for (Iterator<Obj> it = _iter_(); it.hasNext() == TRUE; ) {
            hash = hash._add_(it.next()._hash_());
        }

        return hash;
    }

    public Obj _indexof_(Obj obj) {
        Int index = Int.valueOf(0);
        for (Iterator<Obj> it = _iter_(); it.hasNext() == TRUE; ) {
            if (it.next().equals(obj)) {
                return index;
            }

            index = index._add_(Int.valueOf(1));
        }

        return Null.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Iterator<Obj> _iter_() {
        refs++;

        return new Iterator<Obj>() {

            private Obj[] data = List.this.data;
            private int count = List.this.count;
            private int index = 0;

            @Override
            public Bool hasNext() {
                return index < count ? TRUE : FALSE;
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
        checkArgument(value._gte_(Int.valueOf(0)) == TRUE,
                "cannot multiply a list by negative value");

        List mul = new List();

        if (value._eq_(Int.valueOf(0)) == TRUE) {
            return mul;
        }

        while (value._gt_(Int.valueOf(0)) == TRUE) {
            mul = mul._add_(this);

            value = value._sub_(Int.valueOf(1));
        }

        return mul;
    }

    public List _sub_(List list) {
        List difference = new List(this);

        for (Iterator<Obj> it = list._iter_(); it.hasNext() == TRUE; ) {
            difference.remove(it.next());
        }

        return difference;
    }

    public synchronized void add(Obj obj) {
        checkRefs();

        ensureCapacity();

        data[count++] = checkNotNull(obj, "obj cannot be null");
    }

    public synchronized void clear() {
        checkRefs();

        ensureCapacity();

        count = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        Set<Meta> types = new HashSet<>();

        for (Iterator<Obj> it = _iter_(); it.hasNext() == TRUE; ) {
            types.add(it.next().meta());
        }

        // TODO: Normalise the list before the checks
        // The normalisation should happen in the Meta.forList methods

        if (types.isEmpty()) {
            return Meta.forList(Meta.META_ANY);
        }

        if (types.size() == 1) {
            return Meta.forList(types.iterator().next());
        }

        return Meta.forList(Meta.forUnion(types));
    }

    public synchronized void remove(Obj obj) {
        checkRefs();

        Obj tmp = _indexof_(obj);

        if (tmp instanceof Null) {
            return;
        }

        checkArgument(((Int) tmp)._value_().bitLength() < 32,
                "lists of size larger than 32 bits is unsupported");

        int index = ((Int) tmp)._value_().intValue();

        System.arraycopy(data, index + 1, data, index, count - (index + 1));

        count--;
    }

    public static List valueOf(Obj... data) {
        return new List(data);
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
