package qux.util;

import qux.lang.Obj;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 * @since 0.1.2
 */
public interface Iterable<T extends Obj> {

    Iterator<T> _iter_();
}

