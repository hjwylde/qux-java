package qux.lang;

import qux.lang.operators.Eq;
import qux.lang.operators.Neq;

/**
 * TODO: Documentation
 *
 * @author Henry J. Wylde
 */
public interface Comparable<T extends Obj> extends Eq<T>, Neq<T> {}
