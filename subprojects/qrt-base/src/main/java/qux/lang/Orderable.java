package qux.lang;

import qux.lang.operators.Gt;
import qux.lang.operators.Gte;
import qux.lang.operators.Lt;
import qux.lang.operators.Lte;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Orderable<T extends Obj> extends Gt<T>, Gte<T>, Lt<T>, Lte<T> {}

