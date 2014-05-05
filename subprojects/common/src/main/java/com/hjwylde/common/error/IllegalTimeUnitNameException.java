package com.hjwylde.common.error;

/**
 * TODO: Documentation.
 *
 * @author Henry J. Wylde
 * @since TODO: SINCE
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
