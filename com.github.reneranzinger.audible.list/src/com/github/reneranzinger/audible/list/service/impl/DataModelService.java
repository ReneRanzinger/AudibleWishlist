package com.github.reneranzinger.audible.list.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.reneranzinger.audible.list.persist.DBInterface;
import com.github.reneranzinger.audible.list.persist.om.Category;
import com.github.reneranzinger.audible.list.service.IDataModelService;
import com.github.reneranzinger.audible.list.util.AudibleException;

/**
 * Implementation of the datamodel service. This class is a facade for the
 * SQLite database.
 *
 * @author logan
 *
 */
public class DataModelService implements IDataModelService
{
    private DBInterface m_db = null;
    private List<Category> m_additionalCategories = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    public void openDataStore(String a_filenamePath) throws AudibleException
    {
        try
        {
            DBInterface t_db = new DBInterface(a_filenamePath);
            this.m_db = t_db;
            this.initialize();
        }
        catch (ClassNotFoundException e)
        {
            throw new AudibleException("SQL driver not found", e);
        }
        catch (SQLException e)
        {
            throw new AudibleException("Unable to open database", e);
        }
    }

    /**
     * Additional initializations when database is opened (e.g. additional
     * categories for all books)
     */
    private void initialize()
    {
        Category t_addition = new Category(-1, "All books");
        this.m_additionalCategories.add(t_addition);
    }

    /**
     * {@inheritDoc}
     */
    public List<Category> getCategories() throws AudibleException
    {
        try
        {
            // get the categories form the database
            List<Category> t_results = this.m_db.getCategories();
            // append the additional categories
            t_results.addAll(this.m_additionalCategories);
            return t_results;
        }
        catch (SQLException e)
        {
            throw new AudibleException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Category addCategory(String a_name) throws AudibleException
    {
        try
        {
            this.m_db.addCategory(a_name);
            Category t_category = this.m_db.getCategoryByName(a_name);
            if (t_category == null)
            {
                throw new AudibleException(
                        "Unable to retrieve a category by name (" + a_name + ") after adding.");
            }
            return t_category;
        }
        catch (SQLException e)
        {
            throw new AudibleException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteCategory(Category a_category) throws AudibleException
    {
        try
        {
            this.m_db.deleteCategory(a_category.getId());
        }
        catch (SQLException e)
        {
            throw new AudibleException(e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Category renameCategory(Category a_category, String a_name) throws AudibleException
    {
        try
        {
            this.m_db.renameCategory(a_category.getId(), a_name);
            Category t_category = this.m_db.getCategoryById(a_category.getId());
            if (t_category == null)
            {
                throw new AudibleException("Unable to retrieve a category by ID ("
                        + a_category.getId() + ") after rename.");
            }
            return t_category;
        }
        catch (SQLException e)
        {
            throw new AudibleException(e.getMessage(), e);
        }
    }

}
