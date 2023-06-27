package com.alttd.boosterapi.util;

import com.alttd.boosterapi.config.Config;

public class Logger {

    private final org.slf4j.Logger logger;

    static private final String RESET = "\u001B[0m";
    static private final String GREEN = "\u001B[32m";
    static private final String TEAL = "\u001B[36m";

    public Logger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    public void debug(String debug, String... variables) {
        if (!Config.LOGGING.PRINT_DEBUG)
            return;
        logger.info(TEAL + replace(debug, variables) + RESET);
    }

    public void info(String info, String... variables) {
        logger.info(GREEN + replace(info, variables) + RESET);
    }

    public void warning(String warning, String... variables) {
        if (!Config.LOGGING.PRINT_WARNINGS)
            return;
        logger.warn(replace(warning, variables));
    }

    public void severe(String severe, String... variables) {
        logger.error(replace(severe, variables));
    }

    private String replace(String text, String... variables) {
        for (String variable : variables) {
            text = text.replaceFirst("%", variable);
        }
        return text;
    }
}
