package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * Implies operator. This operator is a binary operator for the 'implies' character sequence.
 *
 * @author Henry J. Wylde
 */
public interface Implies<T extends Obj> {

    public Bool _implies_(T t);
}
