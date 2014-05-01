package qux.lang;

import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_STR;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Objects;

import qux.lang.operators.Add;
import qux.lang.operators.Sub;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public final class Str extends Obj implements Comparable<Str>, Orderable<Str>, Add<Str>, Sub<Str> {

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _add_(Str str) {
        return valueOf(value.concat(str.value));
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
    public Bool _eq_(Str t) {
        return value.equals(t.value) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _gt_(Str t) {
        return value.compareTo(t.value) > 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _gte_(Str t) {
        return value.compareTo(t.value) >= 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lt_(Str t) {
        return value.compareTo(t.value) < 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _lte_(Str t) {
        return value.compareTo(t.value) <= 0 ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _neq_(Str t) {
        return value.equals(t.value) ? FALSE : TRUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _sub_(Str str) {
        if (value.endsWith(str.value)) {
            valueOf(value.substring(0, value.length()));
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return Objects.equals(value, ((Str) obj).value);
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

