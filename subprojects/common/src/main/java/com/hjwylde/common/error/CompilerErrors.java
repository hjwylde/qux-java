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

    public static CompilerError invalidType(String received, String expected) {
        String message = String.format("invalid type, received '%s' but expected '%s'", received,
                expected);

        return new CompilerError(message);
    }

    public static CompilerError undeclaredVariableAccess(String var) {
        String message = String.format("access to undeclared variable '%s'", var);

        return new CompilerError(message);
    }

    public static CompilerError undeclaredVariableAssignment(String var) {
        String message = String.format("assignment to undeclared variable '%s'", var);

        return new CompilerError(message);
    }
}
