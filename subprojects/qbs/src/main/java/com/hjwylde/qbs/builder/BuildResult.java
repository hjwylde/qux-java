package com.hjwylde.qbs.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Throwables;

import javax.annotation.Nullable;

/**
 * Represents a result of a build operation. A build operation may result in either a success, fail
 * or internal error.
 *
 * @author Henry J. Wylde
 */
public final class BuildResult {

    private final Code code;

    private final Throwable cause;

    /**
     * Creates a new <code>Result</code> with the given code and cause.
     *
     * @param code the code.
     * @param cause the cause.
     */
    private BuildResult(Code code, @Nullable Throwable cause) {
        this.code = checkNotNull(code, "code cannot be null");
        this.cause = cause;
    }

    /**
     * Gets the cause of this build result.
     *
     * @return the cause.
     */
    public @Nullable Throwable getCause() {
        return cause;
    }

    /**
     * Gets the code of this build result.
     *
     * @return the code.
     */
    public Code getCode() {
        return code;
    }

    /**
     * Gets the error message of this build result. This method is equivalent to
     * <code>getCause().getMessage()</code>.
     *
     * @return the error message.
     */
    public @Nullable String getMessage() {
        return code == Code.SUCCESS ? "" : cause.getMessage();
    }

    /**
     * Checks whether this build result is a failure. Equivalent to {@code getCode() ==
     * BuildResult.Code.FAIL}.
     *
     * @return true if this build result is a fail.
     */
    public boolean isFail() {
        return code == Code.FAIL;
    }

    /**
     * Checks whether this build result is an internal error. Equivalent to {@code getCode() ==
     * BuildResult.Code.INTERNAL_ERROR}.
     *
     * @return true if this build result is an internal error.
     */
    public boolean isInternalError() {
        return code == Code.INTERNAL_ERROR;
    }

    /**
     * Checks whether this build result is a success. Equivalent to {@code getCode() ==
     * BuildResult.Code.SUCCESS}.
     *
     * @return true if this build result is a success.
     */
    public boolean isSuccess() {
        return code == Code.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(code.toString());
        if (cause != null) {
            sb.append("\n").append(Throwables.getStackTraceAsString(cause));
        }

        return sb.toString();
    }

    /**
     * Creates a new fail build result with the given cause.
     *
     * @param cause the cause.
     * @return a new fail build result.
     */
    public static BuildResult fail(Throwable cause) {
        return new BuildResult(Code.FAIL, checkNotNull(cause, "cause cannot be null"));
    }

    /**
     * Creates a new internal error build result with the given cause.
     *
     * @param cause the cause.
     * @return a new internal error build result.
     */
    public static BuildResult internalError(Throwable cause) {
        return new BuildResult(Code.INTERNAL_ERROR, checkNotNull(cause, "cause cannot be null"));
    }

    /**
     * Creates a new success build result.
     *
     * @return a new success build result.
     */
    public static BuildResult success() {
        return new BuildResult(Code.SUCCESS, null);
    }

    /**
     * A build result code.
     *
     * @author Henry J. Wylde
     */
    public static enum Code {

        /**
         * Code representing a successful build.
         */
        SUCCESS,
        /**
         * Code representing a failed build.
         */
        FAIL,
        /**
         * Code representing an internal error occurred during the build.
         */
        INTERNAL_ERROR;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            switch (this) {
                case SUCCESS:
                    return "success";
                case FAIL:
                    return "fail";
                default:
                    // INTERNAL_ERROR
                    return "internal error";
            }
        }
    }
}
