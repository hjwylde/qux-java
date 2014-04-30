package qux.lang;

import static qux.lang.Bool.TRUE;

import qux.lang.operators.Desc;
import qux.lang.operators.Eq;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class Obj implements Desc {

    /**
     * {@inheritDoc}
     */
    @Override
    public Str _desc_() {
        return Str.valueOf("_desc_() not implemented");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        if (this instanceof Eq<?> && obj instanceof Obj) {
            return ((Eq<Obj>) this)._eq_((Obj) obj) == TRUE ? true : false;
        }

        return false;
    }

    public abstract Meta meta();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _desc_().toString();
    }
}

