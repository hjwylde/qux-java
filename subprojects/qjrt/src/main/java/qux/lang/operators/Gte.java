package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Gte<T extends Obj> {

    public Bool _gte_(T t);
}
