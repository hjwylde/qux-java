package qux.lang.operators;

import qux.lang.Obj;

/**
 * Addition operator. This operator is a binary operator for the '+' character.
 *
 * @author Henry J. Wylde
 */
public interface Add<T extends Obj> {

    T _add_(T t);
}
