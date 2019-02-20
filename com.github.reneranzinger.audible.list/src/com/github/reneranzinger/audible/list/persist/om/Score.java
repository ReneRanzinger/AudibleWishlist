package com.github.reneranzinger.audible.list.persist.om;


import java.util.Date;

public class Score
{
    private Integer m_voters = null;
    private Double m_score = null;
    private Date m_date = null;

    public Integer getVoters()
    {
        return this.m_voters;
    }

    public void setVoters(Integer a_voters)
    {
        this.m_voters = a_voters;
    }

    public Double getScore()
    {
        return this.m_score;
    }

    public void setScore(Double a_score)
    {
        this.m_score = a_score;
    }

    public Date getDate()
    {
        return this.m_date;
    }

    public void setDate(Date a_date)
    {
        this.m_date = a_date;
    }
}
