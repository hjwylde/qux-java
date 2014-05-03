package qux.util;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public interface Iterator<T extends Obj> {

    Bool hasNext();

    T next();
}
