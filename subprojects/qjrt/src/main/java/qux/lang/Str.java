package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_STR;

import com.google.common.base.Strings;

import java.math.BigInteger;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Str extends AbstractObj {

    private String value;

    /**
     * Creates a new {@code Str} with the given value.
     *
     * @param value the string value.
     */
    private Str(String value) {
        this.value = checkNotNull(value, "value cannot be null");
    }

    public Str _add(Str str) {
        return valueOf(value.concat(str.value));
    }

    public Bool _contains(AbstractObj obj) {
        if (!(obj instanceof Str)) {
            return FALSE;
        }

        return value.contains(((Str) obj).value) ? TRUE : FALSE;
    }

    public Str _get(Int index) {
        return get(index);
    }

    public Int _len() {
        return Int.valueOf(value.length());
    }

    public synchronized Str _mul(Int value) {
        checkArgument(value._gte(Int.ZERO) == TRUE, "cannot multiply a str by negative value");

        if (value.equals(Int.ZERO)) {
            return valueOf("");
        }

        if (value.value().bitLength() <= 31) {
            return valueOf(Strings.repeat(this.value, value.value().intValue()));
        }

        Str ret = this;
        while (value._gt(Int.ONE) == TRUE) {
            ret = ret._add(this);

            value = value._sub(Int.ONE);
        }

        return ret;
    }

    public void _set(Int index, AbstractObj value) {
        set(index, value);
    }

    public Str _slice(Int from, Int to) {
        return substring(from, to);
    }

    public Str _sub(Str str) {
        if (value.endsWith(str.value)) {
            return valueOf(value.substring(0, value.length() - str.value.length()));
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        if (!(obj instanceof Str)) {
            return meta().compareTo(obj.meta());
        }

        return value.compareTo(((Str) obj).value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str dup() {
        return valueOf(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        return value.equals(((Str) obj).value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value;
    }

    public static Str valueOf(String value) {
        return new Str(value);
    }

    public static Str valueOf(char value) {
        return valueOf(String.valueOf(value));
    }

    Str get(Int index) {
        return get(index.value());
    }

    Str get(int index) {
        checkElementIndex(index, value.length());

        return valueOf(value.charAt(index));
    }

    Str get(BigInteger index) {
        checkArgument(index.bitLength() < 32, "strings of size larger than 32 bits is unsupported");

        return get(index.intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        return META_STR;
    }

    void set(Int index, AbstractObj value) {
        set(index.value(), value);
    }

    synchronized void set(int index, AbstractObj value) {
        checkElementIndex(index, this.value.length());

        this.value = this.value.substring(0, index) + value.toString() + this.value.substring(
                index + 1);
    }

    void set(BigInteger index, AbstractObj value) {
        checkArgument(index.bitLength() < 32, "lists of size larger than 32 bits is unsupported");

        set(index.intValue(), value);
    }

    Str substring(Int from, Int to) {
        return substring(from.value(), to.value());
    }

    Str substring(int from, int to) {
        checkPositionIndex(from, value.length(), "from");
        checkPositionIndex(to, value.length(), "to");
        checkArgument(from <= to, "from must be less than or equal to to (from=%s, to=%s)", from,
                to);

        return valueOf(value.substring(from, to));
    }

    Str substring(BigInteger from, BigInteger to) {
        checkArgument(from.bitLength() < 32, "strings of size larger than 32 bits is unsupported");
        checkArgument(to.bitLength() < 32, "strings of size larger than 32 bits is unsupported");

        return substring(from.intValue(), to.intValue());
    }
}

