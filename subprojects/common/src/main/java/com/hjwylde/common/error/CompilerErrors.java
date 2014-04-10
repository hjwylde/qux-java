package com.hjwylde.common.error;

/**
 * Utility class for creating {@link com.hjwylde.common.error.CompilerError}s with consistent error
 * messages.
 *
 * @author Henry J. Wylde
 */
public final class CompilerErrors {

    /**
     * This class cannot be instantiated.
     */
    private CompilerErrors() {}

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an invalid type.
     *
     * @param received the received type.
     * @param expected the expected type.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError invalidType(String received, String expected) {
        String message = String.format("invalid type, received '%s' but expected '%s'", received,
                expected);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an invalid
     * type with the provided source information.
     *
     * @param received the received type.
     * @param expected the expected type.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError invalidType(String received, String expected, String source,
            int line, int col, int length) {
        String message = String.format("invalid type, received '%s' but expected '%s'", received,
                expected);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an access to an
     * undeclared variable.
     *
     * @param var the undeclared variable.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError undeclaredVariableAccess(String var) {
        String message = String.format("access to undeclared variable '%s'", var);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an access to
     * an undeclared variable with the provided source information.
     *
     * @param var the undeclared variable.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError undeclaredVariableAccess(String var, String source, int line,
            int col, int length) {
        String message = String.format("access to undeclared variable '%s'", var);

        return new SourceCompilerError(message, source, line, col, length);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.CompilerError} representing an unreachable
     * statement (dead code).
     *
     * @param statement the unreachable statement.
     * @return the created {@link com.hjwylde.common.error.CompilerError}.
     */
    public static CompilerError unreachableStatement(String statement) {
        String message = String.format("unreachable statement detected, '%s'", statement);

        return new CompilerError(message);
    }

    /**
     * Creates a new {@link com.hjwylde.common.error.SourceCompilerError} representing an
     * unreachable statement (dead code) with the provided source information.
     *
     * @param statement the unreachable statement.
     * @param source the source file name.
     * @param line the line number.
     * @param col the column number.
     * @param length the length.
     * @return the created {@link com.hjwylde.common.error.SourceCompilerError}.
     */
    public static SourceCompilerError unreachableStatement(String statement, String source,
            int line, int col, int length) {
        String message = String.format("unreachable statement detected, '%s'", statement);

        return new SourceCompilerError(message, source, line, col, length);
    }
}
