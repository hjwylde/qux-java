package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_INT;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.math.BigInteger;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Int extends AbstractObj {

    public static final Int M_ONE;
    public static final Int ZERO;
    public static final Int ONE;
    public static final Int TWO;

    private static final LoadingCache<BigInteger, Int> cache =
            CacheBuilder.newBuilder().weakKeys().build(new CacheLoader<BigInteger, Int>() {
                @Override
                public Int load(BigInteger key) throws Exception {
                    return new Int(key);
                }
            });
    private final BigInteger value;

    static {
        M_ONE = valueOf(-1);
        ZERO = valueOf(0);
        ONE = valueOf(1);
        TWO = valueOf(2);
    }

    private Int(BigInteger value) {
        this.value = checkNotNull(value, "value cannot be null");
    }

    public Int _add(Int t) {
        return valueOf(value.add(t.value));
    }

    public Int _and(Int t) {
        return valueOf(value.and(t.value));
    }

    public Rat _div(Int t) {
        if (t.equals(Int.ZERO)) {
            throw new InternalError("attempted division by zero");
        }

        return Rat.valueOf(value)._div(Rat.valueOf(t.value));
    }

    public Int _exp(Int t) {
        checkArgument(t.value.bitLength() < 32,
                "exponents of size larger than 32 bits is unsupported");

        return valueOf(value.pow(t.value.intValue()));
    }

    public Int _idiv(Int t) {
        if (t.equals(Int.ZERO)) {
            throw new InternalError("attempted division by zero");
        }

        return valueOf(value.divide(t.value));
    }

    public Int _mul(Int t) {
        return valueOf(value.multiply(t.value));
    }

    public Int _neg() {
        return valueOf(value.negate());
    }

    public Int _or(Int t) {
        return valueOf(value.or(t.value));
    }

    public Int _rem(Int t) {
        return valueOf(value.remainder(t.value));
    }

    public List _rng(Int to) {
        checkArgument(_lte(to) == TRUE,
                "this must be less than or equal to high (this=%s, high=%s)", this, to);

        Int from = this;

        List range = List.valueOf();
        while (from._lt(to) == TRUE) {
            range.add(from);
            from = from._add(Int.ONE);
        }

        return range;
    }

    public Int _sub(Int t) {
        return valueOf(value.subtract(t.value));
    }

    public Int _xor(Int t) {
        return valueOf(value.xor(t.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        if (!(obj instanceof Int)) {
            return meta().compareTo(obj.meta());
        }

        return value.compareTo(((Int) obj).value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int dup() {
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

        return value.equals(((Int) obj).value);
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
        return value.toString();
    }

    public static Int valueOf(short value) {
        return valueOf(BigInteger.valueOf(value));
    }

    public static Int valueOf(byte value) {
        return valueOf(BigInteger.valueOf(value));
    }

    public static Int valueOf(int value) {
        return valueOf(BigInteger.valueOf(value));
    }

    public static Int valueOf(long value) {
        return valueOf(BigInteger.valueOf(value));
    }

    public static Int valueOf(byte[] bytes) {
        return valueOf(new BigInteger(bytes));
    }

    public static Int valueOf(BigInteger value) {
        return cache.getUnchecked(value);
    }

    Int gcd(Int t) {
        // TODO: Add in tests for this
        if (t.equals(ZERO)) {
            return this;
        }

        return t.gcd(this._rem(t));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        return META_INT;
    }

    BigInteger value() {
        return value;
    }
}
