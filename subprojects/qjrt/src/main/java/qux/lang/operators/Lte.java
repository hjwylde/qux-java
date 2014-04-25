package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Lte<T extends Obj> {

    public Bool _lte_(T t);
}
