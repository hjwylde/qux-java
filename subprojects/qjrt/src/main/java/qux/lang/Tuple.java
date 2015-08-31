package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

import com.google.common.base.Joiner;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.4
 */
public final class Tuple extends AbstractObj {

    private final AbstractObj[] data;

    private Tuple(AbstractObj... data) {
        checkArgument(data.length > 1, "data must have at least 2 elements");

        this.data = data.clone();
    }

    public AbstractObj _get(Int index) {
        return get(index);
    }

    public Int _len() {
        return Int.valueOf(data.length);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        if (!(obj instanceof Tuple)) {
            return meta().compareTo(obj.meta());
        }

        Tuple that = (Tuple) obj;

        int comp = data.length - that.data.length;
        if (comp != 0) {
            return comp;
        }

        for (int i = 0; i < data.length; i++) {
            comp = data[i].compareTo(that.data[i]);
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
    public Tuple dup() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        Tuple that = (Tuple) obj;

        return Arrays.equals(data, that.data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "(" + Joiner.on(", ").join(data);
    }

    public static Tuple valueOf(AbstractObj... data) {
        return new Tuple(data);
    }

    AbstractObj get(Int index) {
        return get(index.value());
    }

    synchronized AbstractObj get(int index) {
        checkElementIndex(index, data.length);

        return data[index];
    }

    AbstractObj get(BigInteger index) {
        checkArgument(index.bitLength() < 32, "tuples of size larger than 32 bits is unsupported");

        return get(index.intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        List types = List.valueOf();

        for (AbstractObj datum : data) {
            types.add(datum.meta());
        }

        return Meta.forTuple(types);
    }
}
