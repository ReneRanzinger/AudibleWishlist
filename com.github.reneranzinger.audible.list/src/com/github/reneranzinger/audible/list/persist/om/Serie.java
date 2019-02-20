package com.github.reneranzinger.audible.list.persist.om;

public class Serie
{
    private String m_name = null;
    private String m_url = null;

    public String getName()
    {
        return this.m_name;
    }

    public String getUrl()
    {
        return this.m_url;
    }

    public void setName(String a_name)
    {
        this.m_name = a_name;
    }

    public void setUrl(String a_url)
    {
        this.m_url = a_url;
    }

}
