package qux.lang;

import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_REAL;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.math.BigDecimal;
import java.math.BigInteger;

import qux.errors.InternalError;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Real extends Obj {

    private static final LoadingCache<BigDecimal, Real> cache =
            CacheBuilder.<BigInteger, Real>newBuilder().weakKeys().build(
                    new CacheLoader<BigDecimal, Real>() {
                        @Override
                        public Real load(BigDecimal key) throws Exception {
                            return new Real(key);
                        }
                    }
            );

    private final BigDecimal value;

    private Real(BigDecimal value) {
        this.value = checkNotNull(value, "value cannot be null");
    }

    public Real _add_(Real t) {
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

        return Int.valueOf(value.compareTo(((Real) obj).value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return Str.valueOf(value.toString());
    }

    public Real _div_(Real t) {
        if (t.value.equals(BigDecimal.ZERO)) {
            throw new InternalError("attempted division by zero");
        }

        return valueOf(value.divide(t.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Real _dup_() {
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

        return value.equals(((Real) obj).value) ? TRUE : FALSE;
    }

    public Bool _gt_(Real t) {
        return value.compareTo(t.value) > 0 ? TRUE : FALSE;
    }

    public Bool _gte_(Real t) {
        return value.compareTo(t.value) >= 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return Int.valueOf(value.hashCode());
    }

    public Bool _lt_(Real t) {
        return value.compareTo(t.value) < 0 ? TRUE : FALSE;
    }

    public Bool _lte_(Real t) {
        return value.compareTo(t.value) <= 0 ? TRUE : FALSE;
    }

    public Real _mul_(Real t) {
        return valueOf(value.multiply(t.value));
    }

    public Real _neg_() {
        return valueOf(value.negate());
    }

    public Real _sub_(Real t) {
        return valueOf(value.subtract(t.value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_REAL;
    }

    public static Real valueOf(String value) {
        return valueOf(new BigDecimal(value));
    }

    public static Real valueOf(BigDecimal value) {
        return cache.getUnchecked(value);
    }
}
