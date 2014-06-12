package qux.lang;

import static com.google.common.base.Preconditions.checkNotNull;
import static qux.lang.Bool.FALSE;
import static qux.lang.Bool.TRUE;
import static qux.lang.Meta.META_OBJ;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.2.2
 */
public final class Obj extends AbstractObj {

    private String id;

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
    public Int _comp_(AbstractObj obj) {
        if (!(obj instanceof Obj)) {
            return meta()._comp_(obj.meta());
        }

        return Int.valueOf(id.compareTo(((Obj) obj).id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return Str.valueOf("obj");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Obj _dup_() {
        return valueOf(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bool _eq_(AbstractObj obj) {
        if (super._eq_(obj) == FALSE) {
            return FALSE;
        }

        return id.equals(((Obj) obj).id) ? TRUE : FALSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Int _hash_() {
        return Int.valueOf(id.hashCode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta meta() {
        return META_OBJ;
    }

    public static Obj valueOf(String id) {
        return new Obj(id);
    }
}

