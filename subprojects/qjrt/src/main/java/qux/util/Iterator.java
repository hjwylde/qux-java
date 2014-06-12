package qux.util;

import qux.lang.AbstractObj;
import qux.lang.Bool;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public interface Iterator {

    Bool hasNext();

    AbstractObj next();
}
