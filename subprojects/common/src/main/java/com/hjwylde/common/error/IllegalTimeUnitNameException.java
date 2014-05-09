package com.hjwylde.common.error;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since 0.1.3
 */
public class IllegalTimeUnitNameException extends RuntimeException {

    public IllegalTimeUnitNameException() {}

    public IllegalTimeUnitNameException(String message) {
        super(message);
    }

    public IllegalTimeUnitNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalTimeUnitNameException(Throwable cause) {
        super(cause);
    }
}
