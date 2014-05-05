package com.hjwylde.common.error;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for creating {@link com.hjwylde.common.error.BuildError}s with consistent error
 * messages.
 *
 * @author Henry J. Wylde
 */
public final class BuildErrors {

    /**
     * This class cannot be instantiated.
     */
    private BuildErrors() {}

    /**
     * Creates a new {@link com.hjwylde.common.error.BuildError} representing a build timeout.
     *
     * @return the created {@link com.hjwylde.common.error.BuildError}.
     */
    public static BuildError buildTimeout(long timeout, TimeUnit unit) {
        String message = String.format("build timed out after %s %s", timeout,
                unit.toString().toLowerCase(Locale.ENGLISH));

        return new BuildError(message);
    }
}
