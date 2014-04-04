package qux.lang;

import qux.lang.operators.Desc;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public abstract class Obj implements Desc {

    @Override
    public Str _desc_() {
        return Str.valueOf("_desc_() not implemented");
    }

    @Override
    public String toString() {
        return _desc_().toString();
    }
}

