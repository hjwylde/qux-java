package qux.lang;

import qux.lang.operators.Add;
import qux.lang.operators.Div;
import qux.lang.operators.Mul;
import qux.lang.operators.Neg;
import qux.lang.operators.Sub;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Integral<T extends Obj> extends Add<T>, Div<T>, Mul<T>, Neg<T>, Sub<T> {}

