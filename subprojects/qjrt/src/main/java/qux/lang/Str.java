package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_STR;

import com.google.common.base.Strings;

import java.math.BigInteger;

import qux.lang.op.Access;
import qux.lang.op.Assign;
import qux.lang.op.Len;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Str extends Obj implements Len, Access, Assign {

    private String value;

    /**
     * Creates a new {@code Str} with the given value.
     *
     * @param value the string value.
     */
    private Str(String value) {
        this.value = checkNotNull(value, "value cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _access_(Int index) {
        return get(index);
    }

    public Str _add_(Str str) {
        return valueOf(value.concat(str.value));
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
        if (!(obj instanceof Str)) {
            return meta()._comp_(obj.meta());
        }

        return Int.valueOf(value.compareTo(((Str) obj).value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _dup_() {
        return valueOf(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(Obj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        return value.equals(((Str) obj).value) ? TRUE : FALSE;
    }

    public Bool _gt_(Str t) {
        return value.compareTo(t.value) > 0 ? TRUE : FALSE;
    }

    public Bool _gte_(Str t) {
        return value.compareTo(t.value) >= 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return Int.valueOf(value.hashCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _len_() {
        return Int.valueOf(value.length());
    }

    public Bool _lt_(Str t) {
        return value.compareTo(t.value) < 0 ? TRUE : FALSE;
    }

    public Bool _lte_(Str t) {
        return value.compareTo(t.value) <= 0 ? TRUE : FALSE;
    }

    public synchronized Str _mul_(Int value) {
        checkArgument(value._gte_(Int.ZERO) == TRUE, "cannot multiply a str by negative value");

        if (value._eq_(Int.ZERO) == TRUE) {
            return valueOf("");
        }

        if (value._value_().bitLength() <= 31) {
            return valueOf(Strings.repeat(this.value, value._value_().intValue()));
        }

        Str ret = this;
        while (value._gt_(Int.ONE) == TRUE) {
            ret = ret._add_(this);

            value = value._sub_(Int.ONE);
        }

        return ret;
    }

    public Str _sub_(Str str) {
        if (value.endsWith(str.value)) {
            return valueOf(value.substring(0, value.length() - str.value.length()));
        }

        return this;
    }

    public Str get(Int index) {
        return get(index._value_());
    }

    public Str get(int index) {
        checkElementIndex(index, value.length(),
                "index out of bounds (index='" + index + "', length='" + value.length() + "')");

        return valueOf(value.charAt(index));
    }

    public Str get(BigInteger index) {
        checkArgument(index.bitLength() < 32, "strings of size larger than 32 bits is unsupported");

        return get(index.intValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_STR;
    }

    public void set(Int index, Obj value) {
        set(index._value_(), value);
    }

    public synchronized void set(int index, Obj value) {
        checkElementIndex(index, this.value.length(),
                "index out of bounds (index='" + index + "', length='" + this.value.length() + "')"
        );

        this.value = this.value.substring(0, index) + value.toString() + this.value.substring(
                index + 1);
    }

    public void set(BigInteger index, Obj value) {
        checkArgument(index.bitLength() < 32, "lists of size larger than 32 bits is unsupported");

        set(index.intValue(), value);
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
}

