package com.hjwylde.common.util;

import com.hjwylde.common.error.BuildError;
import com.hjwylde.common.error.CompilerError;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
     * Logs the given message at an assert level. An assert level is just an error with an extra
     * message to request the user contact the developer for assistance.
     *
     * @param msg the error message.
     */
    public static void logAssert(String msg) {
        logger.error(msg);
        logger.error("please contact the developer for assistance");
    }

    /**
     * Logs the given message at an assert level. An assert level is just an error with an extra
     * message to request the user contact the developer for assistance.
     *
     * @param msg the error message.
     * @param t the error cause.
     */
    public static void logAssert(String msg, Throwable t) {
        logger.error(msg, t);
        logger.error("please contact the developer for assistance");
    }

    /**
     * Logs the given message at an assert level. An assert level is just an error with an extra
     * message to request the user contact the developer for assistance.
     *
     * @param format the error message format.
     * @param args the error message arguments.
     */
    public static void logAssert(String format, Object... args) {
        logger.error(format, args);
        logger.error("please contact the developer for assistance");
    }

    /**
     * Logs the given throwable, attempting to make it as useful and clean for the user as possible.
     * The provided source file lines are used to create a more meaningful error message. The source
     * should be the source from the file that caused the error.
     *
     * @param t the throwable.
     * @param source the source file lines.
     */
    public static void logError(Throwable t, List<String> source) {
        logger.debug("error", t);

        if (t instanceof CompilerError || t instanceof BuildError) {
            for (String line : ErrorUtils.toString(t, source).split("\n")) {
                logger.error(line);
            }
        } else {
            logger.error("error", t);
        }
    }

    /**
     * Logs the given throwable, attempting to make it as useful and clean for the user as
     * possible.
     *
     * @param t the throwable.
     */
    public static void logError(Throwable t) {
        logger.debug("error", t);

        if (t instanceof CompilerError || t instanceof BuildError) {
            for (String line : ErrorUtils.toString(t).split("\n")) {
                logger.error(line);
            }
        } else {
            logger.error("error", t);
        }
    }
}
