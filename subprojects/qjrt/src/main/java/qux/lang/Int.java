package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_INT;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.math.BigInteger;

import qux.errors.InternalError;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Int extends Obj {

    public static final Int M_ONE;
    public static final Int ZERO;
    public static final Int ONE;
    public static final Int TWO;

    private static final LoadingCache<BigInteger, Int> cache =
            CacheBuilder.<BigInteger, Int>newBuilder().weakKeys().build(
                    new CacheLoader<BigInteger, Int>() {
                        @Override
                        public Int load(BigInteger key) throws Exception {
                            return new Int(key);
                        }
                    }
            );
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

    public Int _add_(Int t) {
        return valueOf(value.add(t.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(Obj obj) {
        if (!(obj instanceof Int)) {
            return meta()._comp_(obj.meta());
        }

        return valueOf(value.compareTo(((Int) obj).value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return Str.valueOf(value.toString());
    }

    public Int _div_(Int t) {
        if (t.equals(Int.ZERO)) {
            throw new InternalError("attempted division by zero");
        }

        return valueOf(value.divide(t.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _dup_() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(Obj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        return value.equals(((Int) obj).value) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _gt_(Obj t) {
        if (!(t instanceof Int)) {
            return super._gt_(t);
        }

        return value.compareTo(((Int) t).value) > 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _gte_(Obj t) {
        if (!(t instanceof Int)) {
            return super._gt_(t);
        }

        return value.compareTo(((Int) t).value) >= 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return valueOf(value.hashCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lt_(Obj t) {
        if (!(t instanceof Int)) {
            return super._gt_(t);
        }

        return value.compareTo(((Int) t).value) < 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lte_(Obj t) {
        if (!(t instanceof Int)) {
            return super._gt_(t);
        }

        return value.compareTo(((Int) t).value) <= 0 ? TRUE : FALSE;
    }

    public Int _mul_(Int t) {
        return valueOf(value.multiply(t.value));
    }

    public Int _neg_() {
        return valueOf(value.negate());
    }

    public List _range_(Int to) {
        checkArgument(_lte_(to) == TRUE,
                "this must be less than or equal to high (this=%s, high=%s)", this, to);

        Int from = this;

        List range = List.valueOf();
        while (from._lt_(to) == TRUE) {
            range.add(from);
            from = from._add_(Int.ONE);
        }

        return range;
    }

    public Int _rem_(Int t) {
        return valueOf(value.remainder(t.value));
    }

    public Int _sub_(Int t) {
        return valueOf(value.subtract(t.value));
    }

    public BigInteger _value_() {
        return value;
    }

    public Int gcd(Int t) {
        // TODO: Add in tests for this
        if (t.equals(ZERO)) {
            return this;
        }

        return t.gcd(this._rem_(t));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_INT;
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
}
