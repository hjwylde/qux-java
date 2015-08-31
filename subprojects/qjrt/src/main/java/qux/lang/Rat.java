package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_RAT;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Rat extends AbstractObj {

    private static final LoadingCache<Tuple, Rat> cache =
            CacheBuilder.<Tuple, Rat>newBuilder().weakKeys().build(new CacheLoader<Tuple, Rat>() {
                @Override
                public Rat load(Tuple key) throws Exception {
                    return new Rat((Int) key.get(0), (Int) key.get(1));
                }
            });

    private final Int num;
    private final Int den;

    private Rat(Int num, Int den) {
        if (num.equals(Int.ZERO)) {
            den = Int.ONE;
        }
        checkArgument(!den.equals(Int.ZERO), "den cannot be 0");

        // Normalise the values
        Int gcd = num.gcd(den);
        num = num._idiv(gcd);
        den = den._idiv(gcd);

        // Normalise the signs
        if (den._lt(Int.ZERO) == TRUE) {
            num = num._neg();
            den = den._neg();
        }

        this.num = num;
        this.den = den;
    }

    public Rat _add(Rat t) {
        Int a = num._mul(t.den)._add(den._mul(t.num));
        Int b = den._mul(t.den);

        return valueOf(a, b);
    }

    public Rat _div(Rat t) {
        if (t.den.equals(Int.ZERO)) {
            throw new InternalError("attempted division by zero");
        }

        Int a = num._mul(t.den);
        Int b = den._mul(t.num);

        return valueOf(a, b);
    }

    public Rat _mul(Rat t) {
        Int a = num._mul(t.num);
        Int b = den._mul(t.den);

        return valueOf(a, b);
    }

    public Rat _neg() {
        return valueOf(num._neg(), den);
    }

    public Rat _sub(Rat t) {
        Int a = num._mul(t.den)._sub(den._mul(t.num));
        Int b = den._mul(t.den);

        return valueOf(a, b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        if (!(obj instanceof Rat)) {
            return meta().compareTo(obj.meta());
        }

        Rat that = (Rat) obj;

        Int a = num._mul(that.den);
        Int b = den._mul(that.num);

        return a.compareTo(b);
    }

    public static Int den(Rat ths) {
        return ths.den;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rat dup() {
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

        Rat that = (Rat) obj;

        return num.equals(that.num) && den.equals(that.den);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return num.hashCode() * den.hashCode();
    }

    public static Int num(Rat ths) {
        return ths.num;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return num + "/" + den;
    }

    public static Rat valueOf(Int num, Int den) {
        return cache.getUnchecked(Tuple.valueOf(num, den));
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

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        return META_RAT;
    }
}
