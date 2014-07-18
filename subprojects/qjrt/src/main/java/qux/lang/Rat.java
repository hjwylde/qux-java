package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_RAT;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Rat extends AbstractObj {

    // TODO: Add this back in once tuples are implemented
    /*private static final LoadingCache<Tuple, Rat> cache =
            CacheBuilder.<Tuple, Rat>newBuilder().weakKeys().build(
                    new CacheLoader<Tuple, Rat>() {
                        @Override
                        public Rat load(Tuple key) throws Exception {
                            return new Rat(key);
                        }
                    }
            );*/

    private final Int num;
    private final Int den;

    private Rat(Int num, Int den) {
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

    public Rat _add_(Rat t) {
        Int a = num._mul_(t.den)._add_(den._mul_(t.num));
        Int b = den._mul_(t.den);

        return valueOf(a, b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _comp_(AbstractObj obj) {
        if (!(obj instanceof Rat)) {
            return meta()._comp_(obj.meta());
        }

        Rat that = (Rat) obj;

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

    public Rat _div_(Rat t) {
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
    public Rat _dup_() {
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

        Rat that = (Rat) obj;

        return num.equals(that.num) && den.equals(that.den) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return num._hash_()._mul_(den._hash_());
    }

    public Rat _mul_(Rat t) {
        Int a = num._mul_(t.num);
        Int b = den._mul_(t.den);

        return valueOf(a, b);
    }

    public Rat _neg_() {
        return valueOf(num._neg_(), den);
    }

    public Rat _sub_(Rat t) {
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
        return META_RAT;
    }

    public Int num() {
        return num;
    }

    public static Rat valueOf(Int num, Int den) {
        return new Rat(num, den);
    }

    public static Rat valueOf(BigInteger num, BigInteger den) {
        return valueOf(Int.valueOf(num), Int.valueOf(den));
    }

    public static Rat valueOf(short value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Rat valueOf(byte value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Rat valueOf(int value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Rat valueOf(long value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Rat valueOf(byte[] bytes) {
        return valueOf(Int.valueOf(bytes), Int.ONE);
    }

    public static Rat valueOf(BigInteger value) {
        return valueOf(Int.valueOf(value), Int.ONE);
    }

    public static Rat valueOf(String value) {
        return valueOf(new BigDecimal(value));
    }

    public static Rat valueOf(BigDecimal value) {
        return valueOf(value.unscaledValue(), BigInteger.TEN.pow(value.scale()));
    }
}
