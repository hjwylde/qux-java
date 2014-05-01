package qux.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;

import java.util.Set;

import qux.lang.operators.Eq;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.0
 */
public class Meta extends Obj implements Eq<Meta> {

    static final Meta META_ANY = new Any();
    static final Meta META_BOOL = new Bool();
    static final Meta META_INT = new Int();
    static final Meta META_META = new Meta();
    static final Meta META_NULL = new Null();
    static final Meta META_REAL = new Real();
    static final Meta META_STR = new Str();

    private static final LoadingCache<Meta, Meta> listMetas =
            CacheBuilder.<Meta, Meta>newBuilder().weakKeys().build(new CacheLoader<Meta, Meta>() {
                                                                       @Override
                                                                       public Meta load(Meta key) {
                                                                           return new List(key);
                                                                       }
                                                                   }
            );
    private static final LoadingCache<Set<Meta>, Meta> unionMetas =
            CacheBuilder.<Meta, Meta>newBuilder().weakKeys().build(
                    new CacheLoader<Set<Meta>, Meta>() {
                        @Override
                        public Meta load(Set<Meta> key) {
                            return new Union(key);
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

    public static Meta forUnion(Set<Meta> types) {
        return unionMetas.getUnchecked(types);
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
    private static final class Any extends Meta {

        /**
         * {@inheritDoc}
         */
        @Override
        public qux.lang.Str _desc_() {
            return qux.lang.Str.valueOf("any");
        }
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

    /**
     * TODO: Documentation
     *
     * @author Henry J. Wylde
     * @since 0.2.0
     */
    private static final class Union extends Meta {

        // TODO: Change this over to the Qux set type when it exists
        private final ImmutableSet<Meta> types;

        public Union(Set<Meta> types) {
            checkArgument(types.size() >= 2, "types must have at least 2 elements");

            this.types = ImmutableSet.copyOf(types);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public qux.lang.Str _desc_() {
            return qux.lang.Str.valueOf(Joiner.on("|").join(types));
        }

        public ImmutableSet<Meta> getTypes() {
            return types;
        }
    }
}

