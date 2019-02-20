package com.github.reneranzinger.audible.list.log;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.WriterAppender;

/**
 * Developer log appender for the production system. Will log into the console
 *
 */
public class AudibleListLogAppender
{
    /**
     * Creates the appender for the log information. In this case logs to the
     * console
     *
     * @return appender for the log information
     */
    public static WriterAppender createAppender()
    {
        // log to the console
        ConsoleAppender t_consoleAppender = new ConsoleAppender();
        t_consoleAppender.setName("ConsoleLogger");
        return t_consoleAppender;
    }

    /**
     * Default log priority: TRACE
     *
     * @return
     */
    public static Priority getDefaultThreshold()
    {
        return Level.TRACE;
    }

    /**
     * Log priority for application code: DEBUG
     *
     * @return
     */
    public static int getApplicationLoggingLevel()
    {
        return Level.DEBUG_INT;
    }

    /**
     * Log level for non application code: INFO
     *
     * @return
     */
    public static int getNonApplicationLoggingLevel()
    {
        return Level.INFO_INT;
    }
}
