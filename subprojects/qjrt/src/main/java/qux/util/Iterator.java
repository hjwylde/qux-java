package qux.util;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public interface Iterator {

    Bool hasNext();

    Obj next();
}
