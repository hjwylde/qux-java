package qux.lang.operators;

import qux.lang.Obj;

/**
 * Binary subtraction operator, {@code -}.
 *
 * @author Henry J. Wylde
 */
public interface Sub<T extends Obj> {

    /**
     * Performs binary subtraction, using {@code this - t} and returns the result.
     *
     * @param t the T to subtract.
     * @return the result.
     */
    T _sub_(T t);
}
