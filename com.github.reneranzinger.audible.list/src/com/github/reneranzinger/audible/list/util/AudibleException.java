package com.github.reneranzinger.audible.list.util;

public class AudibleException extends Exception
{
    private static final long serialVersionUID = 1L;

    public AudibleException(String a_message, Throwable a_exception)
    {
        super(a_message, a_exception);
    }

    public AudibleException(String a_message)
    {
        super(a_message);
    }

}
