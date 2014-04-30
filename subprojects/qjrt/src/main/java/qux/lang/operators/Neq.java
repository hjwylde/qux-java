package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Neq<T extends Obj> {

    public Bool _neq_(T t);
}
