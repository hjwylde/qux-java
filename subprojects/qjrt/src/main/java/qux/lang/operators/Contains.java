package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * Contains operator. This operator is a binary operator for the 'in' character sequence.
 *
 * @author Henry J. Wylde
 */
public interface Contains<T extends Obj> {

    Bool _contains_(T t);
}
