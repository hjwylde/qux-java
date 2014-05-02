package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Gt<T extends Obj> {

    Bool _gt_(T t);
}
