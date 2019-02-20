package com.github.reneranzinger.audible.list.util.scrapper;

public class ScreenScapperExcpetion extends Exception
{

    public ScreenScapperExcpetion(String a_message)
    {
        super(a_message);
    }

    public ScreenScapperExcpetion(String a_message, Throwable a_exception)
    {
        super(a_message, a_exception);
    }

    private static final long serialVersionUID = 1L;

}
