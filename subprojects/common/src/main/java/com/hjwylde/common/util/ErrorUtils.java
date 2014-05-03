package com.hjwylde.common.util;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.common.error.BuildErrorList;
import com.hjwylde.common.error.CompilerError;
import com.hjwylde.common.error.CompilerErrorList;
import com.hjwylde.common.error.SourceCompilerError;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

/**
 * A utility class for methods relating to errors. Some helpful methods provided include converting
 * an error into a descriptive string.
 *
 * @author Henry J. Wylde
 */
public final class ErrorUtils {

    private static final Logger logger = LoggerFactory.getLogger(ErrorUtils.class);

    /**
     * This class cannot be instantiated.
     */
    private ErrorUtils() {}

    /**
     * Converts the given throwable into a string. If the throwable is an instance of a {@link
     * com.hjwylde.common.error.CompilerError} or {@link com.hjwylde.common.error.BuildError}, then
     * it will create specialised, descriptive error message for it. The provided source file lines
     * are used to create a more meaningful error message.
     *
     * @param t the throwable.
     * @param source the source file lines.
     * @return the string representation.
     */
    public static String toString(Throwable t, List<String> source) {
        if (t instanceof CompilerError) {
            return toString((CompilerError) t, source);
        } else if (t instanceof BuildError) {
            return toString((BuildError) t, source);
        }

        return t.toString();
    }

    /**
     * Converts the given compiler error into a string. If the error is an instance of a {@link
     * com.hjwylde.common.error.CompilerErrorList}, then it will create a specialised, concatenated
     * error message for it. The provided source file lines are used to create a more meaningful
     * error message.
     *
     * @param e the compiler error.
     * @param source the source file lines.
     * @return the string representation.
     */
    public static String toString(CompilerError e, List<String> source) {
        StringBuilder sb = new StringBuilder();

        if (e instanceof CompilerErrorList) {
            for (Iterator<CompilerError> it = ((CompilerErrorList) e).getErrors().iterator();
                    it.hasNext(); ) {
                sb.append(toString(it.next(), source));

                if (it.hasNext()) {
                    sb.append("\n");
                }
            }
        } else if (e instanceof SourceCompilerError) {
            return toString((SourceCompilerError) e, source);
        } else {
            sb.append(e.getMessage());
        }

        return sb.toString();
    }

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
        } else if (e instanceof SourceCompilerError) {
            return toString((SourceCompilerError) e);
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

    /**
     * Converts the given source compiler error into a string message that is understandable by the
     * user. The source file lines are used to provide a more meaningful error message. It should be
     * the lines of the source file that caused the error.
     *
     * @param e the source compiler error.
     * @param source the source file lines.
     * @return the string message.
     */
    private static String toString(SourceCompilerError e, List<String> source) {
        if (e.getLine() < 1 || e.getLine() > source.size()) {
            logger.warn("line number '{}' is out of source bounds '{}'", e.getLine(),
                    source.size());

            return toString(e);
        }

        // The source line that caused the error
        String line = source.get(e.getLine() - 1);

        if (e.getCol() < 0 || e.getCol() > line.length()) {
            logger.warn("col number '{}' is out of line bounds '{}'", e.getCol(), line.length());

            return toString(e);
        }

        if (e.getCol() + e.getLength() > line.length() + 1) {
            logger.warn("erroneous token spans more than one line @{}:{}-{}", e.getLine(),
                    e.getCol(), e.getCol() + e.getLength());

            return toString(e);
        }

        // A space padded string of '^' characters to point out where in the line the error occurred
        String helper = Strings.repeat(" ", e.getCol()) + Strings.repeat("^", Math.max(1,
                e.getLength()));

        return toString(e) + "\n" + line + "\n" + helper + "\n";
    }

    /**
     * Converts the given source compiler error into a string message that is understandable by the
     * user.
     *
     * @param e the syntax error.
     * @return the string message.
     */
    private static String toString(SourceCompilerError e) {
        StringBuilder sb = new StringBuilder();

        sb.append(e.getSource());
        sb.append("@").append(e.getLine());
        sb.append(":").append(e.getCol());
        sb.append("-").append(e.getCol() + e.getLength());
        sb.append(": ").append(e.getMessage());

        return sb.toString();
    }
}
