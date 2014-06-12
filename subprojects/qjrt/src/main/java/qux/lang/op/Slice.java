package qux.lang.op;

import qux.lang.AbstractObj;
import qux.lang.Int;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.3
 */
public interface Slice {

    AbstractObj _slice_(Int from, Int to);
}
