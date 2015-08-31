package qux.lang;

import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Meta.META_OBJ;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.2
 */
public final class Obj extends AbstractObj {

    private final String id;

    /**
     * Creates a new {@code Obj} with the given id.
     *
     * @param id the id of this object.
     */
    private Obj(String id) {
        this.id = checkNotNull(id, "id cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(AbstractObj obj) {
        if (!(obj instanceof Obj)) {
            return meta().compareTo(obj.meta());
        }

        return id.compareTo(((Obj) obj).id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Obj dup() {
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

        return id.equals(((Obj) obj).id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "obj";
    }

    public static Obj valueOf(String id) {
        return new Obj(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Meta meta() {
        return META_OBJ;
    }
}

