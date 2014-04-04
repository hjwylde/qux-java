package com.hjwylde.common.error;

/**
 * Represents an error that occurred during the build process. A build error is often the result of
 * bad input by the user.
 *
 * @author Henry J. Wylde
 */
public class BuildError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new {@code BuildError} with no information.
     */
    public BuildError() {}

    /**
     * Creates a new {@code BuildError} with the given message.
     *
     * @param message the message.
     */
    public BuildError(String message) {
        super(message);
    }

    /**
     * Creates a new {@code BuildError} with the given message and cause.
     *
     * @param message the message.
     * @param cause the cause.
     */
    public BuildError(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@code BuildError} with the given cause.
     *
     * @param cause the cause.
     */
    public BuildError(Throwable cause) {
        super(cause);
    }
}

