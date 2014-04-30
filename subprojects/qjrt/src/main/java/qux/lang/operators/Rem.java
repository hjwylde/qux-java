package qux.lang.operators;

import qux.lang.Obj;

/**
 * Binary remainder operator, {@code %}.
 *
 * @author Henry J. Wylde
 */
public interface Rem<T extends Obj> {

    /**
     * Performs binary remainder, using {@code this % t} and returns the result.
     *
     * @param t the T to remainder.
     * @return the result.
     */
    public T _rem_(T t);
}
