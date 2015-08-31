package qux.lang;

import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class AbstractObj implements Comparable<AbstractObj> {

    /**
     * This class may only be extended locally.
     */
    AbstractObj() {}

    public final Bool _eq(AbstractObj obj) {
        return equals(obj) ? TRUE : FALSE;
    }

    public final Bool _gt(AbstractObj obj) {
        return compareTo(obj) > 0 ? TRUE : FALSE;
    }

    public final Bool _gte(AbstractObj obj) {
        return compareTo(obj) >= 0 ? TRUE : FALSE;
    }

    public final Bool _lt(AbstractObj obj) {
        return compareTo(obj) < 0 ? TRUE : FALSE;
    }

    public final Bool _lte(AbstractObj obj) {
        return compareTo(obj) <= 0 ? TRUE : FALSE;
    }

    public final Bool _neq(AbstractObj obj) {
        return _eq(obj)._not();
    }

    public static Str desc(AbstractObj obj) {
        return Str.valueOf(obj.toString());
    }

    public abstract AbstractObj dup();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj != null && getClass() == obj.getClass();
    }

    public static Int hash(AbstractObj obj) {
        return Int.valueOf(obj.hashCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int hashCode();

    public static Meta meta(AbstractObj obj) {
        return obj.meta();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

    abstract Meta meta();
}

