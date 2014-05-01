package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * Iff operator. This operator is a binary operator for the 'iff' character sequence.
 *
 * @author Henry J. Wylde
 */
public interface Iff<T extends Obj> {

    public Bool _iff_(T t);
}
