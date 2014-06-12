package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_REAL;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Real extends AbstractObj {

    // TODO: Add this back in once tuples are implemented
    /*private static final LoadingCache<Tuple, Real> cache =
            CacheBuilder.<Tuple, Real>newBuilder().weakKeys().build(
                    new CacheLoader<Tuple, Real>() {
                        @Override
                        public Real load(Tuple key) throws Exception {
                            return new Real(key);
                        }
                    }
            );*/

    private final Int num;
    private final Int den;

    private Real(Int num, Int den) {
        if (num.equals(Int.ZERO)) {
            den = Int.ONE;
        }
        checkArgument(!den.equals(Int.ZERO), "den cannot be 0");

        // Normalise the values
        Int gcd = num.gcd(den);
        num = num._idiv_(gcd);
        den = den._idiv_(gcd);

        // Normalise the signs
        if (den._lt_(Int.ZERO) == TRUE) {
            num = num._neg_();
            den = den._neg_();
        }

        this.num = num;
        this.den = den;
    }

    public Real _add_(Real t) {
        Int a = num._mul_(t.den)._add_(den._mul_(t.num));
        Int b = den._mul_(t.den);

        return valueOf(a, b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(AbstractObj obj) {
        if (!(obj instanceof Real)) {
            return meta()._comp_(obj.meta());
        }

        Real that = (Real) obj;

        Int a = num._mul_(that.den);
        Int b = den._mul_(that.num);

        return a._comp_(b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return Str.valueOf(num + "/" + den);
    }

    public Real _div_(Real t) {
        if (t.den.equals(Int.ZERO)) {
            throw new InternalError("attempted division by zero");
        }

        Int a = num._mul_(t.den);
        Int b = den._mul_(t.num);

        return valueOf(a, b);
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
    public Bool _eq_(AbstractObj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        Real that = (Real) obj;

        return num.equals(that.num) && den.equals(that.den) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return num._hash_()._mul_(den._hash_());
    }

    public Real _mul_(Real t) {
        Int a = num._mul_(t.num);
        Int b = den._mul_(t.den);

        return valueOf(a, b);
    }

    public Real _neg_() {
        return valueOf(num._neg_(), den);
    }

    public Real _sub_(Real t) {
        Int a = num._mul_(t.den)._sub_(den._mul_(t.num));
        Int b = den._mul_(t.den);

        return valueOf(a, b);
    }

    public Int den() {
        return den;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_REAL;
    }

    public Int num() {
        return num;
    }

    public static Real valueOf(Int num, Int den) {
        return new Real(num, den);
    }

    public static Real valueOf(BigInteger num, BigInteger den) {
        return valueOf(Int.valueOf(num), Int.valueOf(den));
    }

    public static Real valueOf(short value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Real valueOf(byte value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Real valueOf(int value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Real valueOf(long value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Real valueOf(byte[] bytes) {
        return valueOf(Int.valueOf(bytes), Int.ONE);
    }

    public static Real valueOf(BigInteger value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Real valueOf(String value) {
        return valueOf(new BigDecimal(value));
    }

    public static Real valueOf(BigDecimal value) {
        return valueOf(value.unscaledValue(), BigInteger.TEN.pow(value.scale()));
    }
}
