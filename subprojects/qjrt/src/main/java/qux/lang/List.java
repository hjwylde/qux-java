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

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class List extends AbstractObj implements Iterable<AbstractObj> {

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

    public List _add(List list) {
        List union = new List(this);

        union.ensureCapacity(list._len());

        list.forEach(union::add);

        return union;
    }

    public Bool _contains(AbstractObj obj) {
        return indexOf(obj) >= 0 ? TRUE : FALSE;
    }

    public AbstractObj _get(Int index) {
        return get(index);
    }

    public Int _len() {
        return Int.valueOf(count);
    }

    public void _set(Int index, AbstractObj value) {
        set(index, value);
    }

    public List _slice(Int from, Int to) {
        return sublist(from, to);
    }

    public List _sub(List list) {
        List difference = new List(this);

        list.forEach(difference::remove);

        return difference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        if (!(obj instanceof List)) {
            return meta().compareTo(obj.meta());
        }

        List that = (List) obj;

        int comp = count - that.count;
        if (comp != 0) {
            return comp;
        }

        Iterator<AbstractObj> it = that.iterator();

        for (AbstractObj datum : this) {
            comp = datum.compareTo(it.next());
            if (comp != 0) {
                return comp;
            }
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List dup() {
        return new List(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        List that = (List) obj;

        return Iterables.elementsEqual(this, that);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = 0;

        for (AbstractObj datum : this) {
            hash += datum.hashCode();
        }

        return hash;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Iterator<AbstractObj> iterator() {
        refs++;

        return new Iterator<AbstractObj>() {

            private AbstractObj[] data = List.this.data;
            private int count = List.this.count;
            private int index = 0;

            @Override
            public boolean hasNext() {
                if (index < count) {
                    return true;
                }

                // Check if the list is still the same, if it is we can decrement the refs count
                if (List.this.data == data) {
                    refs--;
                }

                return false;
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
    public String toString() {
        return "[" + Joiner.on(", ").join(this) + "]";
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
        return get(index.value());
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
        for (AbstractObj datum : this) {
            if (datum.equals(obj)) {
                return index;
            }

            index++;
        }

        return -index - 1;
    }

    Bool isEmpty() {
        return count == 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        Set types = Set.valueOf();

        forEach(e -> types.add(e.meta()));

        if (types.isEmpty() == TRUE) {
            return Meta.forSet(Meta.META_ANY);
        }

        if (types._len().equals(Int.ONE)) {
            return Meta.forSet((Meta) types.iterator().next());
        }

        return Meta.forSet(Meta.forUnion(types));
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
        set(index.value(), value);
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
        return sublist(from.value(), to.value());
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
        ensureCapacity(capacity.value());
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
