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
 * @since 0.1.3
 */
public final class Set extends AbstractObj implements Iterable<AbstractObj> {

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

    public Set _add(Set set) {
        Set union = new Set(this);

        union.ensureCapacity(set._len());

        set.forEach(union::add);

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

    public Set _slice(Int from, Int to) {
        return subset(from, to);
    }

    public Set _sub(Set set) {
        Set difference = new Set(this);

        set.forEach(difference::remove);

        return difference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        if (!(obj instanceof Set)) {
            return meta().compareTo(obj.meta());
        }

        Set that = (Set) obj;

        int comp = _len().compareTo(that._len());
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
     * {@inheritDocn}
     */
    @Override
    public Set dup() {
        return new Set(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        Set that = (Set) obj;

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

            private AbstractObj[] data = Set.this.data;
            private int count = Set.this.count;
            private int index = 0;

            @Override
            public boolean hasNext() {
                if (index < count) {
                    return true;
                }

                // Check if the set is still the same, if it is we can decrement the refs count
                if (Set.this.data == data) {
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
        return "{" + Joiner.on(", ").join(this) + "}";
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
        return get(index.value());
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

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        Set types = new Set();

        forEach(e -> types.add(e.meta()));

        // TODO: Normalise the set before the checks
        // The normalisation should happen in the Meta.forSet methods

        if (types.isEmpty() == TRUE) {
            return Meta.forSet(Meta.META_ANY);
        }

        if (types._len().equals(Int.ONE)) {
            return Meta.forSet((Meta) types.data[0]);
        }

        return Meta.forSet(Meta.forUnion(types));
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
        return subset(from.value(), to.value());
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
        ensureCapacity(capacity.value());
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
        } else if (obj.compareTo(data[mid]) < 0) {
            high = mid;
        } else {
            low = mid + 1;
        }

        return indexOf(obj, low, high);
    }
}
