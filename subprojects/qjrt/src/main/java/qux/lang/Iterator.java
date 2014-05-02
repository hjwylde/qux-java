package qux.lang;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
 */
public interface Iterator<T extends Obj> {

    Bool hasNext();

    T next();
}
