package com.github.reneranzinger.audible.list.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Controller class that configures the log4j output and location.
 *
 * @author logan
 *
 */
public class AudibleListLogConfigurator
{
    /**
     * Configure the logging system (log4j)
     */
    public static void startLog()
    {
        // create an appender that knows where to store the logs (outsourced to
        // allow overwrite this part by developer fragment)
        WriterAppender t_logAppender = AudibleListLogAppender.createAppender();
        // specify the log format
        t_logAppender.setLayout(new PatternLayout("[%-5p] %d{ISO8601} [%C %M %L] - %m%n"));
        // Loglevel: debug<info<warn<error<fatal
        // set the default log level (definede in the outsourced classed to
        // allow different settings for developer and product)
        t_logAppender.setThreshold(AudibleListLogAppender.getDefaultThreshold());
        t_logAppender.activateOptions();
        // add a filter that allows to treat project code with different log
        // level
        t_logAppender.addFilter(new Filter()
        {
            @Override
            public int decide(LoggingEvent a_event)
            {
                try
                {
                    // use default level by default
                    int t_decision = NEUTRAL;
                    // get the name of the class that tries to log
                    String t_loggingClassName = a_event.getLocationInformation().getClassName();
                    // if the class is part of the project use different log
                    // level
                    if (t_loggingClassName.startsWith("com.github.reneranzinger.audible.wishlist"))
                    {
                        t_decision = a_event.getLevel().toInt() >= AudibleListLogAppender
                                .getApplicationLoggingLevel() ? ACCEPT : DENY;
                    }
                    else
                    {
                        t_decision = a_event.getLevel().toInt() >= AudibleListLogAppender
                                .getNonApplicationLoggingLevel() ? ACCEPT : DENY;
                    }
                    return t_decision;
                }
                catch (Exception ex)
                {
                    return ACCEPT;
                }
            }
        });
        // add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(t_logAppender);
    }
}
