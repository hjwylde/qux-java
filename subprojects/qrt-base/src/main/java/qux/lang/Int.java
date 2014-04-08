package qux.lang;

import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.math.BigInteger;
import java.util.Objects;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Int extends Obj implements Integral<Int>, Comparable<Int>, Orderable<Int> {

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

    private Int(BigInteger value) {
        this.value = checkNotNull(value, "value cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _add_(Int t) {
        return valueOf(value.add(t.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return Str.valueOf(value.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _div_(Int t) {
        if (t.value.equals(BigInteger.ZERO)) {
            throw new InternalError("attempted division by zero");
        }

        return valueOf(value.divide(t.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(Int t) {
        return value.equals(t.value) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _gt_(Int t) {
        return value.compareTo(t.value) > 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _gte_(Int t) {
        return value.compareTo(t.value) >= 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lt_(Int t) {
        return value.compareTo(t.value) < 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lte_(Int t) {
        return value.compareTo(t.value) <= 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _mul_(Int t) {
        return valueOf(value.multiply(t.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _neg_() {
        return valueOf(value.negate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _neq_(Int t) {
        return value.equals(t.value) ? FALSE : TRUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _sub_(Int t) {
        return valueOf(value.subtract(t.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return Objects.equals(value, ((Int) obj).value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return value.hashCode();
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
