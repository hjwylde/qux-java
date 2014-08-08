package com.hjwylde.common.error;

/**
 * Exception representing an illegal {@link java.util.concurrent.TimeUnit} name.
 *
 * @author Henry J. Wylde
 * @since 0.1.3
 */
public class IllegalTimeUnitNameException extends RuntimeException {

    /**
     * Creates a new {@code IllegalTimeUnitNameException}.
     */
    public IllegalTimeUnitNameException() {}

    /**
     * Creates a new {@code IllegalTimeUnitNameException} with the given message.
     *
     * @param message the message.
     */
    public IllegalTimeUnitNameException(String message) {
        super(message);
    }

    /**
     * Creates a new {@code IllegalTimeUnitNameException} with the given message and cause.
     *
     * @param message the message.
     * @param cause the cause.
     */
    public IllegalTimeUnitNameException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@code IllegalTimeUnitNameException} with the given cause.
     *
     * @param cause the cause.
     */
    public IllegalTimeUnitNameException(Throwable cause) {
        super(cause);
    }
}
