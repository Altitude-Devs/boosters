package com.alttd.boosterapi.util;

import org.apache.commons.lang.exception.ExceptionUtils;
import java.util.logging.Logger;

public class ALogger {

    private static org.slf4j.Logger velocityLogger = null;
    private static Logger bukkitLogger = null;

    public static void init(org.slf4j.Logger log) {
        velocityLogger = log;
    }

    public static void init(Logger log) {
        bukkitLogger = log;
    }

    public static void warn(String message) {
        if (velocityLogger != null)
            velocityLogger.warn(message);
        if (bukkitLogger != null)
            bukkitLogger.warning(message);
    }

    public static void info(String message) {
        if (velocityLogger != null)
            velocityLogger.info(message);
        if (bukkitLogger != null)
            bukkitLogger.info(message);
    }

    public static void error(String message) {
        if (velocityLogger != null)
            velocityLogger.error(message);
        if (bukkitLogger != null)
            bukkitLogger.severe(message);
    }

    public static void fatal(String error, Exception exception) {
        error(error + "\n" +  ExceptionUtils.getStackTrace(exception));
    }
}
