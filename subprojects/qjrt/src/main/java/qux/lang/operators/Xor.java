package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * Xor operator. This operator is a binary operator for the 'xor' character sequence.
 *
 * @author Henry J. Wylde
 */
public interface Xor<T extends Obj> {

    public Bool _xor_(T t);
}
