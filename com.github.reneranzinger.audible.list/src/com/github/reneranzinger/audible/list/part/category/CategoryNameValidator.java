package com.github.reneranzinger.audible.list.part.category;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;

import com.github.reneranzinger.audible.list.persist.om.Category;

/**
 * Validator class to check the correctness of a provided category name. It
 * mainly checks if the name already exists.
 *
 * @author logan
 *
 */
public class CategoryNameValidator implements IInputValidator
{
    private List<Category> m_categories = new ArrayList<>();
    private String m_allowedDublicate = null;

    /**
     * Constructor of the validator. Gets the list of existing categories.
     *
     * @param a_categories
     *            List of existing categories to check against.
     */
    public CategoryNameValidator(List<Category> a_categories)
    {
        this.m_categories = a_categories;
    }

    /**
     * Constructor of the validator. Gets the list of existing categories.
     *
     * @param a_categories
     *            List of existing categories to check against.
     * @param a_allowedDublicate
     *            A name that is in the categories list but can be chosen. For
     *            example if a category gets renamed its allowed to choose the
     *            same name.
     */
    public CategoryNameValidator(List<Category> a_categories, String a_allowedDublicate)
    {
        this.m_categories = a_categories;
        this.m_allowedDublicate = a_allowedDublicate;
    }

    @Override
    public String isValid(String a_newText)
    {
        // remove trailing whitespaces and line breaks
        String t_text = a_newText.trim();
        // is there an allowed dublicate?
        if (this.m_allowedDublicate != null)
        {
            // if it matches its valid
            if (this.m_allowedDublicate.equals(t_text))
            {
                return null;
            }
        }
        // check if that name already exists
        for (Category t_category : m_categories)
        {
            if (t_category.getName().equalsIgnoreCase(t_text))
            {
                return "This category already exists, please choose different name";
            }
        }
        // no error found
        return null;
    }

}
