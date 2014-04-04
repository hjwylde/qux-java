package com.hjwylde.common.util;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.common.error.BuildErrorList;
import com.hjwylde.common.error.CompilerError;
import com.hjwylde.common.error.CompilerErrorList;

import java.util.Iterator;

/**
 * A utility class for methods relating to errors. Some helpful methods provided include converting
 * an error into a descriptive string.
 *
 * @author Henry J. Wylde
 */
public final class ErrorUtils {

    /**
     * This class cannot be instantiated.
     */
    private ErrorUtils() {}

    /**
     * Converts the given throwable into a string. If the throwable is an instance of a {@link
     * com.hjwylde.common.error.CompilerError} or {@link com.hjwylde.common.error.BuildError}, then
     * it will create specialised, descriptive error message for it.
     *
     * @param t the throwable.
     * @return the string representation.
     */
    public static String toString(Throwable t) {
        if (t instanceof CompilerError) {
            return toString((CompilerError) t);
        } else if (t instanceof BuildError) {
            return toString(((BuildError) t));
        }

        return t.toString();
    }

    /**
     * Converts the given compiler error into a string. If the error is an instance of a {@link
     * com.hjwylde.common.error.CompilerErrorList}, then it will create a specialised, concatenated
     * error message for it.
     *
     * @param e the compiler error.
     * @return the string representation.
     */
    public static String toString(CompilerError e) {
        StringBuilder sb = new StringBuilder();

        if (e instanceof CompilerErrorList) {
            for (Iterator<CompilerError> it = ((CompilerErrorList) e).getErrors().iterator();
                    it.hasNext(); ) {
                sb.append(toString(it.next()));

                if (it.hasNext()) {
                    sb.append("\n");
                }
            }
        } else {
            sb.append(e.getMessage());
        }

        return sb.toString();
    }

    /**
     * Converts the given build error into a string. If the error is an instance of a {@link
     * com.hjwylde.common.error.BuildErrorList}, then it will create a specialised, concatenated
     * error message for it.
     *
     * @param e the build error.
     * @return the string representation.
     */
    public static String toString(BuildError e) {
        StringBuilder sb = new StringBuilder();

        if (e instanceof BuildErrorList) {
            for (Iterator<BuildError> it = ((BuildErrorList) e).getErrors().iterator();
                    it.hasNext(); ) {
                sb.append(toString(it.next()));

                if (it.hasNext()) {
                    sb.append("\n");
                }
            }
        } else {
            sb.append(e.getMessage());
        }

        return sb.toString();
    }
}
