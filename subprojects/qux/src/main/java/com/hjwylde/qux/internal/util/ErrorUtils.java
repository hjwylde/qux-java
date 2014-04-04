package com.hjwylde.qux.internal.util;

import com.hjwylde.common.error.CompilerError;
import com.hjwylde.common.error.CompilerErrorList;
import com.hjwylde.qux.internal.errors.SyntaxError;

import java.util.Iterator;

/**
 * Provides utility functions for dealing with errors.
 * <p/>
 * This class enhances the {@link com.hjwylde.common.util.ErrorUtils} by adding in a custom
 * implementation for a {@code SyntaxError}.
 *
 * @author Henry J. Wylde
 */
public final class ErrorUtils {

    /**
     * This class cannot be instantiated.
     */
    private ErrorUtils() {}

    /**
     * Converts the given throwable into a string message that is understandable by the user.
     *
     * @param t the throwable.
     * @return the string message.
     */
    public static String toString(Throwable t) {
        if (t instanceof CompilerError) {
            return toString((CompilerError) t);
        }

        // Delegate to the other error utility class
        return com.hjwylde.common.util.ErrorUtils.toString(t);
    }

    /**
     * Converts the given compiler error into a string message that is understandable by the user.
     *
     * @param e the compiler error.
     * @return the string message.
     */
    private static String toString(CompilerError e) {
        StringBuilder sb = new StringBuilder();

        if (e instanceof CompilerErrorList) {
            for (Iterator<CompilerError> it = ((CompilerErrorList) e).getErrors().iterator();
                    it.hasNext(); ) {
                sb.append(toString(it.next()));

                if (it.hasNext()) {
                    sb.append("\n");
                }
            }
        } else if (e instanceof SyntaxError) {
            return toString((SyntaxError) e);
        } else {
            // Delegate to the other error utility class
            return com.hjwylde.common.util.ErrorUtils.toString(e);
        }

        return sb.toString();
    }

    /**
     * Converts the given syntax error into a string message that is understandable by the user.
     *
     * @param e the syntax error.
     * @return the string message.
     */
    private static String toString(SyntaxError e) {
        // TODO: Implement this properly
        return e.getMessage();
    }
}
