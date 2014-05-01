package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * Or operator. This operator is a binary operator for the 'or' character sequence.
 *
 * @author Henry J. Wylde
 */
public interface Or<T extends Obj> {

    public Bool _or_(T t);
}
