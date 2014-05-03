package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_STR;

import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import qux.lang.op.Len;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Str extends Obj implements Len {

    private static final LoadingCache<String, Str> cache =
            CacheBuilder.<String, Str>newBuilder().weakKeys().build(new CacheLoader<String, Str>() {
                                                                        @Override
                                                                        public Str load(String key)
                                                                                throws Exception {
                                                                            return new Str(key);
                                                                        }
                                                                    }
            );

    private final String value;

    /**
     * Creates a new {@code Str} with the given value.
     *
     * @param value the string value.
     */
    private Str(String value) {
        this.value = checkNotNull(value, "value cannot be null");
    }

    public Str _add_(Str str) {
        return valueOf(value.concat(str.value));
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_STR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value;
    }

    public static Str valueOf(String value) {
        return cache.getUnchecked(value);
    }
}

