package qux.util;

import qux.lang.Obj;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public interface Iterable<T extends Obj> {

    Iterator<T> _iter_();
}

