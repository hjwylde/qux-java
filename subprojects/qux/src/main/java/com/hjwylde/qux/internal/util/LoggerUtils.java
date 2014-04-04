package com.hjwylde.qux.internal.util;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.common.error.CompilerError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides utility functions for use with a logger.
 *
 * @author Henry J. Wylde
 */
public final class LoggerUtils {

    private static final Logger logger = LoggerFactory.getLogger(LoggerUtils.class);

    /**
     * This class cannot be instantiated.
     */
    private LoggerUtils() {}

    /**
     * Logs the given throwable, attempting to make it as useful and clean for the user as
     * possible.
     *
     * @param t the throwable.
     */
    public static void logError(Throwable t) {
        logger.debug(t.toString(), t);

        if (t instanceof CompilerError || t instanceof BuildError) {
            for (String line : ErrorUtils.toString(t).split("\n")) {
                logger.error(line);
            }
        } else {
            logger.error(t.toString(), t);
        }
    }
}
