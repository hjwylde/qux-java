package com.hjwylde.common.error;

/**
 * Represents a error that occurred during the compilation process. A compiler error is generally
 * due to bad user input or an invalid input file.
 *
 * @author Henry J. Wylde
 */
public class CompilerError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@code CompilerError} with no information.
     */
    public CompilerError() {}

    /**
     * Creates a new {@code CompilerError} with the given message.
     *
     * @param message the message.
     */
    public CompilerError(String message) {
        super(message);
    }

    /**
     * Creates a new {@code CompilerError} with the given message and cause.
     *
     * @param message the message.
     * @param cause the cause.
     */
    public CompilerError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@code CompilerError} with the given cause.
     *
     * @param cause the cause.
     */
    public CompilerError(Throwable cause) {
        super(cause);
    }
}

