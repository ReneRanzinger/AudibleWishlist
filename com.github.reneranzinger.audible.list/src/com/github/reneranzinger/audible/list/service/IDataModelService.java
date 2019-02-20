package com.github.reneranzinger.audible.list.service;

import java.sql.SQLException;
import java.util.List;

import com.github.reneranzinger.audible.list.persist.om.Category;
import com.github.reneranzinger.audible.list.util.AudibleException;

public interface IDataModelService
{
    /**
     * Opens a database using the filename and path of the database file.
     *
     * @param a_filenamePath
     *            Filename and path of the database
     * @throws ClassNotFoundException
     *             SQL driver not found
     * @throws SQLException
     *             Unable to connect to database
     */
    public void openDataStore(String a_filenamePath) throws AudibleException;

    /**
     * Retrieve the list of all categories from the database
     *
     * @return List of categories
     * @throws AudibleException
     *             thrown if query can not be generated or executed
     */
    public List<Category> getCategories() throws AudibleException;

    /**
     * Add a new category to the datastore
     *
     * @param a_name
     *            Name of the new category
     * @return Object of the newly created category
     * @throws AudibleException
     *             thrown if the category can not be entered to the database
     */
    public Category addCategory(String a_name) throws AudibleException;

    /**
     * Delete a category from the datastore
     *
     * @param a_category
     *            Category to be delete from the store
     * @throws AudibleException
     *             thrown if the category can not be deleted from the database
     */
    public void deleteCategory(Category a_category) throws AudibleException;

    /**
     * Rename a category in the datastore
     *
     * @param a_category
     *            Category that should be renamed
     * @param a_name
     *            New name of the category
     * @return new Category with the new name
     * @throws AudibleException
     *             thrown if the category can not be renamed in the database
     */
    public Category renameCategory(Category a_category, String a_name) throws AudibleException;
}