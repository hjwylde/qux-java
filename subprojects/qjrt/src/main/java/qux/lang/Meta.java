package qux.lang;

import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import qux.lang.operators.Eq;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public class Meta extends Obj implements Eq<Meta> {

    static final Meta META_BOOL = new Bool();
    static final Meta META_INT = new Int();
    static final Meta META_META = new Meta();
    static final Meta META_NULL = new Null();
    static final Meta META_REAL = new Real();
    static final Meta META_STR = new Str();

    private static final LoadingCache<Meta, List> listMetas =
            CacheBuilder.<Meta, Meta.List>newBuilder().weakKeys().build(
                    new CacheLoader<Meta, Meta.List>() {
                        @Override
                        public Meta.List load(Meta key) {
                            return new List(key);
                        }
                    }
            );

    /**
     * This class can only be instantiated locally.
     */
    Meta() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public qux.lang.Str _desc_() {
        return qux.lang.Str.valueOf("meta");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public qux.lang.Bool _eq_(Meta meta) {
        return this == meta ? TRUE : FALSE;
    }

    public static Meta forList(Meta inner) {
        return listMetas.getUnchecked(inner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_META;
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.0
     */
    private static final class Bool extends Meta {

        /**
         * {@inheritDoc}
         */
        @Override
        public qux.lang.Str _desc_() {
            return qux.lang.Str.valueOf("bool");
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.0
     */
    private static final class Int extends Meta {

        /**
         * {@inheritDoc}
         */
        @Override
        public qux.lang.Str _desc_() {
            return qux.lang.Str.valueOf("int");
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.0
     */
    private static final class List extends Meta {

        private final Meta inner;

        public List(Meta inner) {
            this.inner = checkNotNull(inner, "inner cannot be null");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public qux.lang.Str _desc_() {
            return qux.lang.Str.valueOf("[" + inner + "]");
        }

        public Meta getInner() {
            return inner;
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.0
     */
    private static final class Null extends Meta {

        /**
         * {@inheritDoc}
         */
        @Override
        public qux.lang.Str _desc_() {
            return qux.lang.Str.valueOf("null");
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.0
     */
    private static final class Real extends Meta {

        /**
         * {@inheritDoc}
         */
        @Override
        public qux.lang.Str _desc_() {
            return qux.lang.Str.valueOf("real");
        }
    }

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.0
     */
    private static final class Str extends Meta {

        /**
         * {@inheritDoc}
         */
        @Override
        public qux.lang.Str _desc_() {
            return qux.lang.Str.valueOf("str");
        }
    }
}

