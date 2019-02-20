package com.github.reneranzinger.audible.list.service;

import com.github.reneranzinger.audible.list.persist.om.Category;

public interface IAudibleSelectionService
{
    /**
     * Get the latest selected category.
     *
     * @return Category that was selected, if none is selected null
     */
    public Category getSelectedCategory();

    /**
     * Store latest selected category.
     *
     * @param a_selectedCategory
     *            selected category can be null
     */
    public void setSelectedCategory(Category a_selectedCategory);

}