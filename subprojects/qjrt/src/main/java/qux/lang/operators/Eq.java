package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Eq<T extends Obj> {

    public Bool _eq_(T t);
}
