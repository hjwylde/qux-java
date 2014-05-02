package qux.lang.operators;

import qux.lang.Bool;
import qux.lang.Obj;

/**
 * And operator. This operator is a binary operator for the 'and' character sequence.
 *
 * @author Henry J. Wylde
 */
public interface And<T extends Obj> {

    Bool _and_(T t);
}
