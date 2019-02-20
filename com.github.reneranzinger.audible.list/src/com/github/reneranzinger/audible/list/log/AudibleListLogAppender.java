package com.github.reneranzinger.audible.list.log;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.WriterAppender;
import org.eclipse.core.runtime.Platform;

import com.github.reneranzinger.audible.list.AudibleListConfig;

/**
 * Default log appender for the production system. Will log into a file in the
 * home directory. The log level is info for everything.
 *
 */
public class AudibleListLogAppender
{
    /**
     * Creates the appender for the log information. In this case logs in a file
     * in the home directory.
     *
     * @return appender for the log information
     */
    public static WriterAppender createAppender()
    {
        // find the folder for the log information
        String t_logFolderPath = getLogFolderPath();
        // check if a writable path is available
        if (t_logFolderPath != null)
        {
            // create a file appender
            FileAppender t_fileAppender = new FileAppender();
            t_fileAppender.setName("FileLogger");
            // create the file in the folder with the program name, version
            // number and current date/time
            SimpleDateFormat t_dateFormat = new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss",
                    Locale.ENGLISH);
            t_fileAppender.setFile(t_logFolderPath + File.separator + "Wishlist-v"
                    + AudibleListConfig.VERSION + "-" + t_dateFormat.format(new Date()) + ".log");
            t_fileAppender.setAppend(true);
            return t_fileAppender;
        }
        else
        {
            // no path available log to the console
            ConsoleAppender t_consoleAppender = new ConsoleAppender();
            t_consoleAppender.setName("ConsoleLogger");
            return t_consoleAppender;
        }
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
     * Log priority for application code: INFO
     *
     * @return
     */
    public static int getApplicationLoggingLevel()
    {
        return Level.INFO_INT;
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

    /**
     * Find the folder for the log files and if necessary create it.
     *
     * @return pathname of the the folder for the log files
     */
    private static String getLogFolderPath()
    {
        // try to create it inside the application folder
        String t_logFolderPath = Platform.getConfigurationLocation().getURL().getPath()
                + "com.github.reneranzinger.audible.wishlist" + File.separator + "log";
        // check if the folder exists and is writable
        if (isValidFolder(t_logFolderPath))
        {
            return t_logFolderPath;
        }
        // try another folder : "${user.home}/.audible-wishlist/log/"
        String t_homeDirectory = System.getProperty("user.home");
        if (t_homeDirectory != null && t_homeDirectory.trim().length() > 0)
        {
            t_logFolderPath = t_homeDirectory + File.separator + ".audible-wishlist"
                    + File.separator + "log";
            // check the folder
            if (isValidFolder(t_logFolderPath))
            {
                return t_logFolderPath;
            }
        }
        // none of the folders is available
        return null;
    }

    /**
     * Check if the log folder is valid and writable
     *
     * @param a_logFolderPath
     *            Logfolder path to be checked
     * @return true if the folder is valid, otherwise false
     */
    private static boolean isValidFolder(String a_logFolderPath)
    {
        try
        {
            File a_logFolder = new File(a_logFolderPath);
            if (a_logFolder.exists())
            {
                // if folder exists check write permissions
                if (Files.isExecutable(a_logFolder.toPath())
                        && Files.isWritable(a_logFolder.toPath()))
                {
                    return true;
                }
            }
            else if (a_logFolder.mkdirs())
            {
                // otherwise try creating it
                return true;
            }
        }
        catch (Exception e)
        {
            // this should not happen but just in case
            e.printStackTrace();
        }
        // log folder is not writable or could not be created
        return false;
    }
}
