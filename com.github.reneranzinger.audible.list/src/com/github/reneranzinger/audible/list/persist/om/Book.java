package com.github.reneranzinger.audible.list.persist.om;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Book
{
    private String m_title = null;
    private String m_productId = null;
    private String m_url = null;
    private Date m_releaseDate = null;
    private List<AudibleCategory> m_categories = new ArrayList<AudibleCategory>();
    private List<Person> m_author = new ArrayList<Person>();
    private List<Person> m_reader = new ArrayList<Person>();
    private Integer m_durationMin = null;
    private Score m_currentScore = null;
    private List<Score> m_scores = null;
    private String m_description = null;
    private String m_imageUrl = null;
    private Integer m_flag = null;
    private List<Serie> m_series = new ArrayList<Serie>();

    public String getTitle()
    {
        return this.m_title;
    }

    public void setTitle(String a_title)
    {
        this.m_title = a_title;
    }

    public String getUrl()
    {
        return this.m_url;
    }

    public void setUrl(String a_url)
    {
        this.m_url = a_url;
    }

    public Date getReleaseDate()
    {
        return this.m_releaseDate;
    }

    public void setReleaseDate(Date a_releaseDate)
    {
        this.m_releaseDate = a_releaseDate;
    }

    public List<AudibleCategory> getCategories()
    {
        return this.m_categories;
    }

    public void setCategories(List<AudibleCategory> a_categories)
    {
        this.m_categories = a_categories;
    }

    public List<Person> getAuthor()
    {
        return this.m_author;
    }

    public void setAuthor(List<Person> a_author)
    {
        this.m_author = a_author;
    }

    public List<Person> getReader()
    {
        return this.m_reader;
    }

    public void setReader(List<Person> a_reader)
    {
        this.m_reader = a_reader;
    }

    public Integer getDurationMin()
    {
        return this.m_durationMin;
    }

    public void setDurationMin(Integer a_durationMin)
    {
        this.m_durationMin = a_durationMin;
    }

    public Score getCurrentScore()
    {
        return this.m_currentScore;
    }

    public void setCurrentScore(Score a_currentScore)
    {
        this.m_currentScore = a_currentScore;
    }

    public List<Score> getScores()
    {
        return this.m_scores;
    }

    public void setScores(List<Score> a_scores)
    {
        this.m_scores = a_scores;
    }

    public String getDescription()
    {
        return this.m_description;
    }

    public String getImageUrl()
    {
        return this.m_imageUrl;
    }

    public void setDescription(String a_description)
    {
        this.m_description = a_description;
    }

    public void setImageUrl(String a_imageUrl)
    {
        this.m_imageUrl = a_imageUrl;
    }

    public String getProductId()
    {
        return m_productId;
    }

    public void setProductId(String a_productId)
    {
        this.m_productId = a_productId;
    }

    public Integer getFlag()
    {
        return m_flag;
    }

    public void setFlag(Integer a_flag)
    {
        this.m_flag = a_flag;
    }

    public List<Serie> getSeries()
    {
        return m_series;
    }

    public void setSeries(List<Serie> a_series)
    {
        this.m_series = a_series;
    }
}
