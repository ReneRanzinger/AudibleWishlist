package com.github.reneranzinger.audible.list.persist;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.github.reneranzinger.audible.list.persist.om.AudibleCategory;
import com.github.reneranzinger.audible.list.persist.om.Book;
import com.github.reneranzinger.audible.list.persist.om.Category;
import com.github.reneranzinger.audible.list.persist.om.Person;
import com.github.reneranzinger.audible.list.persist.om.Score;
import com.github.reneranzinger.audible.list.persist.om.Serie;

/**
 * Class that serves as persistent layer for the SQLite database files.
 *
 * @author logan
 *
 */
public class DBInterface
{
    // connection object that can be used to generate statements
    private Connection m_connection = null;

    /**
     * Constructor of the interface. Needs the filename/path of the database
     * file.
     *
     * @param a_databaseFilePath
     *            Filename and path of the database file to be opened
     * @throws ClassNotFoundException
     *             thrown if the database class can not be found
     * @throws SQLException
     *             thrown if the database connection can not be created
     */
    public DBInterface(String a_databaseFilePath) throws ClassNotFoundException, SQLException
    {
        // load driver into memory so it can be found
        Class.forName("org.sqlite.JDBC");
        // database URL
        String t_url = "jdbc:sqlite:" + a_databaseFilePath;
        // create a connection to the database
        this.m_connection = DriverManager.getConnection(t_url);
        // set the connection to support cascade foreign keys
        Statement t_statement = this.m_connection.createStatement();
        t_statement.execute("PRAGMA foreign_keys = ON");
    }

    /**
     * Close the database connection.
     *
     * @throws SQLException
     *             thrown if the database connection can not be closed
     */
    public void close() throws SQLException
    {
        // check if a connection object exists
        if (this.m_connection != null)
        {
            // close the connection
            this.m_connection.close();
        }
    }

    /**
     * Retrieve the list of all categories from the database
     *
     * @return List of categories
     * @throws SQLException
     *             thrown if query can not be generated or executed
     */
    public List<Category> getCategories() throws SQLException
    {
        List<Category> t_resultList = new ArrayList<Category>();
        // create statement and select all categories
        Statement t_statement = this.m_connection.createStatement();
        ResultSet t_resultSet = t_statement.executeQuery("SELECT * FROM category;");
        // create a list with all categories
        while (t_resultSet.next())
        {
            Category t_category = new Category(t_resultSet.getInt("category_id"),
                    t_resultSet.getString("name"));
            t_resultList.add(t_category);
        }
        // close result set and statement
        t_resultSet.close();
        t_statement.close();
        return t_resultList;
    }

    /**
     * Add a new category in the database
     *
     * @param a_name
     *            Name of the category
     * @return Category object
     * @throws SQLException
     *             thrown if the category could not be added to the database or
     *             could not be found afterwards
     */
    public Category addCategory(String a_name) throws SQLException
    {
        // insert the category into the database
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("INSERT INTO category (name) VALUES (?);");
        t_statement.setString(1, a_name);
        t_statement.execute();
        t_statement.close();
        // retrieve the category by name to generate the new category object
        t_statement = this.m_connection.prepareStatement("SELECT * FROM category WHERE name = ?;");
        t_statement.setString(1, a_name);
        ResultSet t_resultSet = t_statement.executeQuery();
        while (t_resultSet.next())
        {
            Category t_category = new Category(t_resultSet.getInt("category_id"),
                    t_resultSet.getString("name"));
            t_resultSet.close();
            t_statement.close();
            return t_category;
        }
        // for some reason there was no category object in the database
        t_resultSet.close();
        t_statement.close();
        throw new SQLException("Category was not entered successfully into the database.");
    }

    /**
     * Delete category from the database (this will not delete books associated
     * with the category)
     *
     * @param a_id
     *            ID of the category to be deleted
     * @throws SQLException
     *             thrown if category can not be deleted
     */
    public void deleteCategory(Integer a_id) throws SQLException
    {
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("DELETE FROM category WHERE category_id = ?;");
        t_statement.setInt(1, a_id);
        t_statement.execute();
        t_statement.close();
    }

    /**
     * Rename a category in the database
     *
     * @param a_id
     *            ID of the category
     * @param a_name
     *            New name of the category
     * @throws SQLException
     *             thrown if the update can not be performed
     */
    public void renameCategory(Integer a_id, String a_name) throws SQLException
    {
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("UPDATE category SET name = ? WHERE category_id = ?");
        t_statement.setString(1, a_name);
        t_statement.setInt(2, a_id);
        t_statement.execute();
        t_statement.close();
    }

    /**
     * Return category object from database based on the name
     *
     * @param a_name
     *            Name of the category
     * @return Category object from the database matching the name or null if no
     *         category with this name exists
     * @throws SQLException
     *             thrown if the select statement can not be performed
     */
    public Category getCategoryByName(String a_name) throws SQLException
    {
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("SELECT * FROM category WHERE name = ?");
        t_statement.setString(1, a_name);
        ResultSet t_resultSet = t_statement.executeQuery();
        Category t_category = null;
        while (t_resultSet.next())
        {
            t_category = new Category();
            t_category.setId(t_resultSet.getInt("category_id"));
            t_category.setName(t_resultSet.getString("name"));
        }
        t_resultSet.close();
        t_statement.close();
        return t_category;
    }

    /**
     * Return category object from database based on the ID
     *
     * @param a_id
     *            ID of the Category
     * @return Category object from the database with this ID or null
     * @throws SQLException
     *             thrown if the selected statement can not be performed
     */
    public Category getCategoryById(Integer a_id) throws SQLException
    {
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("SELECT * FROM category WHERE category_id = ?");
        t_statement.setInt(1, a_id);
        ResultSet t_resultSet = t_statement.executeQuery();
        Category t_category = null;
        while (t_resultSet.next())
        {
            t_category = new Category();
            t_category.setId(t_resultSet.getInt("category_id"));
            t_category.setName(t_resultSet.getString("name"));
        }
        t_resultSet.close();
        t_statement.close();
        return t_category;
    }

    /**
     * Write the book and all its data into the database. The method will not
     * check for completeness of the provided book objects. Validation is the
     * responsibility of the caller.
     *
     * @param a_book
     *            Filed book information object
     * @return ID of the book in the database
     * @throws SQLException
     *             thrown if any part of the operation fails
     */
    public Integer writeBook(Book a_book) throws SQLException
    {
        // check if book is already there
        Integer t_bookId = this.checkBookByProductId(a_book.getProductId());
        if (t_bookId == null)
        {
            try
            {
                // insert the book into the database and get book ID
                t_bookId = this.writeBookData(a_book);
                // add the audible category association and create the category
                // if necessary
                this.writeAudibleCategory(a_book.getCategories(), t_bookId);
                // create the authors and readers in the database if necessary
                // and add m-to-n relationships to book
                this.writeAuthors(a_book.getAuthor(), t_bookId);
                this.writeReader(a_book.getReader(), t_bookId);
                // write the current score of the book in the database
                if (a_book.getCurrentScore() != null)
                {
                    this.writeScore(a_book.getCurrentScore(), t_bookId);
                }
                if (a_book.getSeries().size() > 0)
                {
                    this.writeSerie(a_book.getSeries(), t_bookId);
                }
            }
            catch (Exception e)
            {
                // if any part of the operation fails we remove the book that
                // will also remove the m-to-n relationships but might leave
                // artifacts in the category and person tables
                if (t_bookId == null)
                {
                    // check just to be safe the ID part did not fail
                    t_bookId = this.checkBookByProductId(a_book.getProductId());
                }
                // if there was an entry, remove it
                if (t_bookId != null)
                {
                    this.removeBook(t_bookId);
                }
                throw e;
            }
        }
        return t_bookId;
    }

    /**
     * Writes all series that the book is associated with into the database and
     * creates the corresponding m-to-n relationships.
     *
     * @param a_series
     *            List of series that the book is associates with
     * @param a_bookId
     *            Id of the book in the database
     * @throws SQLException
     *             thrown if any part of the insert fails
     */
    private void writeSerie(List<Serie> a_series, Integer a_bookId) throws SQLException
    {
        // for each serie
        for (Serie t_serie : a_series)
        {
            // the the ID of the serie or insert the new author
            Integer t_id = this.findSerie(t_serie);
            // add the m-to-n entry for book and author
            PreparedStatement t_statement = this.m_connection.prepareStatement(
                    "INSERT INTO book_has_serie (book_id, serie_id) VALUES (?,?);");
            t_statement.setInt(1, a_bookId);
            t_statement.setInt(2, t_id);
            t_statement.execute();
            t_statement.close();
        }
    }

    /**
     * Attempts to find the series in the database and if not successful inserts
     * it.
     *
     * @param a_serie
     *            Serie to find or insert
     * @return Id of the series in the database
     * @throws SQLException
     *             thrown if the select or insert statement fails
     */
    private Integer findSerie(Serie a_serie) throws SQLException
    {
        // try to find the series with this name in the database
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("SELECT serie_id FROM serie WHERE name = ?");
        t_statement.setString(1, a_serie.getName());
        ResultSet t_resultSet = t_statement.executeQuery();
        while (t_resultSet.next())
        {
            // success, get ID and return it
            Integer t_id = t_resultSet.getInt("serie_id");
            t_resultSet.close();
            t_statement.close();
            return t_id;
        }
        // failed, series does not exist
        t_resultSet.close();
        t_statement.close();
        // insert the series
        t_statement = this.m_connection.prepareStatement(
                "INSERT INTO serie (name, url) VALUES (?,?);", Statement.RETURN_GENERATED_KEYS);
        t_statement.setString(1, a_serie.getName());
        t_statement.setString(2, a_serie.getUrl());
        t_statement.execute();
        // get the create ID
        t_resultSet = t_statement.getGeneratedKeys();
        if (t_resultSet.next())
        {
            Integer t_id = t_resultSet.getInt(1);
            t_resultSet.close();
            t_statement.close();
            return t_id;
        }
        else
        {
            t_resultSet.close();
            t_statement.close();
            throw new SQLException("Insert of series successful but unable to retrieve ID.");
        }
    }

    /**
     * Delete a book from the database based on its ID
     *
     * @param a_bookId
     *            ID of the book in the database
     * @throws SQLException
     *             thrown if the delete statement fails
     */
    public void removeBook(Integer a_bookId) throws SQLException
    {
        // delete a book from the table
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("DELETE FROM book WHERE book_id = ?;");
        t_statement.setInt(1, a_bookId);
        t_statement.execute();
        t_statement.close();
    }

    private void writeScore(Score a_currentScore, Integer a_bookId) throws SQLException
    {
        // write the score into the database
        PreparedStatement t_statement = this.m_connection.prepareStatement(
                "INSERT INTO book_has_score (book_id, voters, score, date) VALUES (?,?,?,?);");
        t_statement.setInt(1, a_bookId);
        t_statement.setInt(2, a_currentScore.getVoters());
        t_statement.setDouble(3, a_currentScore.getScore());
        t_statement.setDate(4, new java.sql.Date(a_currentScore.getDate().getTime()));
        t_statement.execute();
        t_statement.close();
    }

    /**
     * Find the application category in the database or creates it if necessary.
     *
     * @param a_category
     *            Name of the category
     * @return ID of the category in the database
     * @throws SQLException
     *             thrown if any part of the finding or creating of the category
     *             fails
     */
    public Integer findCategory(String a_category) throws SQLException
    {
        // try to find the category with this name in the database
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("SELECT category_id FROM category WHERE name = ?");
        t_statement.setString(1, a_category);
        ResultSet t_resultSet = t_statement.executeQuery();
        while (t_resultSet.next())
        {
            // success, get ID and return it
            Integer t_id = t_resultSet.getInt("category_id");
            t_resultSet.close();
            t_statement.close();
            return t_id;
        }
        // failed, category does not exist
        t_resultSet.close();
        t_statement.close();
        // insert the person
        t_statement = this.m_connection.prepareStatement("INSERT INTO category (name) VALUES (?);",
                Statement.RETURN_GENERATED_KEYS);
        t_statement.setString(1, a_category);
        t_statement.execute();
        // get the create ID
        t_resultSet = t_statement.getGeneratedKeys();
        if (t_resultSet.next())
        {
            Integer t_id = t_resultSet.getInt(1);
            t_resultSet.close();
            t_statement.close();
            return t_id;
        }
        else
        {
            t_resultSet.close();
            t_statement.close();
            throw new SQLException("Insert of category successful but unable to retrieve ID.");
        }
    }

    /**
     * Writes the list of readers for a book into the database and adds the
     * m-to-n relationship.
     *
     * @param a_reader
     *            List of readers
     * @param a_bookId
     *            Database ID of the book that the readers are associated with
     * @throws SQLException
     *             thrown if any part of SQL operation can not be executed
     */
    private void writeReader(List<Person> a_reader, Integer a_bookId) throws SQLException
    {
        // for each author
        for (Person t_person : a_reader)
        {
            // the the ID of the author or insert the new author
            Integer t_id = this.findPerson(t_person);
            // add the m-to-n entry for book and author
            PreparedStatement t_statement = this.m_connection.prepareStatement(
                    "INSERT INTO book_has_reader (book_id, person_id) VALUES (?,?);");
            t_statement.setInt(1, a_bookId);
            t_statement.setInt(2, t_id);
            t_statement.execute();
            t_statement.close();
        }
    }

    /**
     * Writes the list of authors for a book into the database and adds the
     * m-to-n relationship.
     *
     * @param a_author
     *            List of authors
     * @param a_bookId
     *            Database ID of the book that the authors are associated with
     * @throws SQLException
     *             thrown if any part of SQL operation can not be executed
     */
    private void writeAuthors(List<Person> a_author, Integer a_bookId) throws SQLException
    {
        // for each author
        for (Person t_person : a_author)
        {
            // the the ID of the author or insert the new author
            Integer t_id = this.findPerson(t_person);
            // add the m-to-n entry for book and author
            PreparedStatement t_statement = this.m_connection.prepareStatement(
                    "INSERT INTO book_has_author (book_id, person_id) VALUES (?,?);");
            t_statement.setInt(1, a_bookId);
            t_statement.setInt(2, t_id);
            t_statement.execute();
            t_statement.close();
        }
    }

    /**
     * Finds a person in the database or inserts an new entry for the person
     *
     * @param a_person
     *            Person (name) to be found in the database
     * @return Database ID of the person
     * @throws SQLException
     *             thrown if the find or insert statement fails
     */
    private Integer findPerson(Person a_person) throws SQLException
    {
        // try to find the person with this name in the database
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("SELECT person_id FROM person WHERE name = ?");
        t_statement.setString(1, a_person.getName());
        ResultSet t_resultSet = t_statement.executeQuery();
        while (t_resultSet.next())
        {
            // success, get ID and return it
            Integer t_id = t_resultSet.getInt("person_id");
            t_resultSet.close();
            t_statement.close();
            return t_id;
        }
        // failed, category does not exist
        t_resultSet.close();
        t_statement.close();
        // insert the person
        t_statement = this.m_connection.prepareStatement("INSERT INTO person (name) VALUES (?);",
                Statement.RETURN_GENERATED_KEYS);
        t_statement.setString(1, a_person.getName());
        t_statement.execute();
        // get the create ID
        t_resultSet = t_statement.getGeneratedKeys();
        if (t_resultSet.next())
        {
            Integer t_id = t_resultSet.getInt(1);
            t_resultSet.close();
            t_statement.close();
            return t_id;
        }
        else
        {
            t_resultSet.close();
            t_statement.close();
            throw new SQLException("Insert of person successful but unable to retrieve ID.");
        }
    }

    /**
     * Write
     *
     * @param a_categories
     * @param a_bookId
     * @throws SQLException
     */
    private void writeAudibleCategory(List<AudibleCategory> a_categories, Integer a_bookId)
            throws SQLException
    {
        for (AudibleCategory t_audibleCategory : a_categories)
        {
            Integer t_id = this.findAudibleCategory(t_audibleCategory);
            PreparedStatement t_statement = this.m_connection.prepareStatement(
                    "INSERT INTO book_has_audible_category (book_id, audible_category_id) VALUES (?,?);");
            t_statement.setInt(1, a_bookId);
            t_statement.setInt(2, t_id);
            t_statement.execute();
            t_statement.close();
        }
    }

    /**
     * Find an audible category in the database or insert if necessary.
     *
     * @param a_audibleCategory
     *            Audible category to find
     * @return Id of the category in the database
     * @throws SQLException
     *             thrown if the finding or inserting fails
     */
    private Integer findAudibleCategory(AudibleCategory a_audibleCategory) throws SQLException
    {
        // try to find the category with this name in the database
        PreparedStatement t_statement = this.m_connection.prepareStatement(
                "SELECT audible_category_id FROM audible_category WHERE name = ?");
        t_statement.setString(1, a_audibleCategory.getName());
        ResultSet t_resultSet = t_statement.executeQuery();
        while (t_resultSet.next())
        {
            // success, get ID and return it
            Integer t_id = t_resultSet.getInt("audible_category_id");
            t_resultSet.close();
            t_statement.close();
            return t_id;
        }
        // failed, category does not exist
        t_resultSet.close();
        t_statement.close();
        // insert the category
        t_statement = this.m_connection.prepareStatement(
                "INSERT INTO audible_category (name) VALUES (?);", Statement.RETURN_GENERATED_KEYS);
        t_statement.setString(1, a_audibleCategory.getName());
        t_statement.execute();
        // get the create ID
        t_resultSet = t_statement.getGeneratedKeys();
        if (t_resultSet.next())
        {
            Integer t_id = t_resultSet.getInt(1);
            t_resultSet.close();
            t_statement.close();
            return t_id;
        }
        else
        {
            t_resultSet.close();
            t_statement.close();
            throw new SQLException(
                    "Insert of audible category successful but unable to retrieve ID.");
        }
    }

    /**
     * Write information of a book in the book table in the database. This
     * method does not check if the book already exists (unique constrains).
     * This is the responsibility of the caller.
     *
     * @param a_book
     *            Book to be wrote into the database
     * @return ID of the book in the database
     * @throws SQLException
     *             thrown if the insert fails
     */
    private Integer writeBookData(Book a_book) throws SQLException
    {
        PreparedStatement t_statement = this.m_connection.prepareStatement(
                "INSERT INTO book (title, url, release_date, length, product_id, description, image_url, flag) VALUES (?,?,?,?,?,?,?,?);",
                Statement.RETURN_GENERATED_KEYS);
        t_statement.setString(1, a_book.getTitle());
        t_statement.setString(2, a_book.getUrl());
        t_statement.setDate(3, new java.sql.Date(a_book.getReleaseDate().getTime()));
        t_statement.setInt(4, a_book.getDurationMin());
        t_statement.setString(5, a_book.getProductId());
        t_statement.setString(6, a_book.getDescription());
        t_statement.setString(7, a_book.getImageUrl());
        if (a_book.getFlag() == null)
        {
            t_statement.setNull(8, Types.INTEGER);
        }
        else
        {
            t_statement.setInt(8, a_book.getFlag());
        }
        t_statement.execute();
        ResultSet t_resultSet = t_statement.getGeneratedKeys();
        if (t_resultSet.next())
        {
            Integer t_id = t_resultSet.getInt(1);
            t_resultSet.close();
            t_statement.close();
            return t_id;
        }
        else
        {
            t_resultSet.close();
            t_statement.close();
            throw new SQLException("Insert of book successful but unable to retrieve ID.");
        }
    }

    /**
     * Check if a book with this URL already exists in the database.
     *
     * @param a_url
     *            URL of the book
     * @return ID of the book or null if no book was found
     * @throws SQLException
     *             thrown if the select statement fails
     */
    private Integer checkBookByProductId(String a_id) throws SQLException
    {
        Integer t_result = null;
        // look for the book by its URL
        PreparedStatement t_statement = this.m_connection
                .prepareStatement("SELECT book_id FROM book WHERE product_id = ?");
        t_statement.setString(1, a_id);
        ResultSet t_resultSet = t_statement.executeQuery();
        // was an entry found
        if (t_resultSet.next())
        {
            // yes, get its ID
            t_result = t_resultSet.getInt("book_id");

        }
        t_resultSet.close();
        t_statement.close();
        return t_result;
    }

    /**
     * Adds the association of a book with an application category
     *
     * @param a_categoryId
     *            ID of Application category in the database
     * @param a_bookId
     *            ID of the book in the database
     * @throws SQLException
     *             thrown if any part of the insert operation fails
     */
    public void addBookToCategory(Integer a_bookId, Integer a_categoryId) throws SQLException
    {
        // check if this m-to-n already present to avoid violating the unique
        // constrain
        PreparedStatement t_statement = this.m_connection.prepareStatement(
                "SELECT * FROM category_has_book WHERE book_id = ? aND category_id = ?;");
        t_statement.setInt(1, a_bookId);
        t_statement.setInt(2, a_categoryId);
        ResultSet t_resultSet = t_statement.executeQuery();
        // was an entry found
        if (!t_resultSet.next())
        {
            t_resultSet.close();
            t_statement.close();
            // no, insert it
            // add the m-to-n entry for book and category
            t_statement = this.m_connection.prepareStatement(
                    "INSERT INTO category_has_book (book_id, category_id) VALUES (?,?);");
            t_statement.setInt(1, a_bookId);
            t_statement.setInt(2, a_categoryId);
            t_statement.execute();
        }
        else
        {
            t_resultSet.close();
        }
        t_statement.close();
    }
}
