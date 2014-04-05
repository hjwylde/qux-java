package com.hjwylde.common.error;

/**
 * Error representing an unimplemented method.
 *
 * @author Henry J. Wylde
 */
public final class MethodNotImplementedError extends InternalError {

    /**
     * Creates a new {@code MethodNotImplementedError} with the given detail.
     *
     * @param detail the extra detail.
     */
    public MethodNotImplementedError(String detail) {
        super(getMethodSignature() + ": " + detail);
    }

    /**
     * Creates a new {@code MethodNotImplementedError}.
     */
    public MethodNotImplementedError() {
        super(getMethodSignature());
    }

    private static String getMethodSignature() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];

        return caller.getClassName() + ":" + caller.getMethodName();
    }
}
