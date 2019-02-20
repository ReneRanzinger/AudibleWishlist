package com.github.reneranzinger.audible.list.persist.om;


/**
 * Class that represents a category object.
 *
 * @author logan
 *
 */
public class Category
{
    private Integer m_id = null;
    private String m_name = null;

    public Category()
    {
        // empty constructor
    }

    public Category(Integer a_id, String a_name)
    {
        this.m_id = a_id;
        this.m_name = a_name;
    }

    public Integer getId()
    {
        return this.m_id;
    }

    public void setId(Integer a_id)
    {
        this.m_id = a_id;
    }

    public String getName()
    {
        return this.m_name;
    }

    public void setName(String a_name)
    {
        this.m_name = a_name;
    }

}
