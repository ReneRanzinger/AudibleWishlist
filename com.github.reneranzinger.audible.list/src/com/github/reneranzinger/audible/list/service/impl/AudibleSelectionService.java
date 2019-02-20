package com.github.reneranzinger.audible.list.service.impl;

import com.github.reneranzinger.audible.list.persist.om.Category;
import com.github.reneranzinger.audible.list.service.IAudibleSelectionService;

public class AudibleSelectionService implements IAudibleSelectionService
{
    private Category m_selectedCategory = null;

    /**
     * {@inheritDoc}
     */
    public Category getSelectedCategory()
    {
        return this.m_selectedCategory;
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectedCategory(Category a_selectedCategory)
    {
        this.m_selectedCategory = a_selectedCategory;
    }

}
