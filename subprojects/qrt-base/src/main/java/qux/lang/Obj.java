package qux.lang;

import qux.lang.operators.Desc;

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

    public abstract Meta meta();
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return _desc_().toString();
    }
}

