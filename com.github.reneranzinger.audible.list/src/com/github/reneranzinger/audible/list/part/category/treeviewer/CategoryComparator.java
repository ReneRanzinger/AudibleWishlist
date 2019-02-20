package com.github.reneranzinger.audible.list.part.category.treeviewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.github.reneranzinger.audible.list.persist.om.Category;

/**
 * Comparator for the category objects. First all database categories ordered by
 * name (ignore case) than the special categories ordered by there IDs.
 *
 * @author logan
 *
 */
public class CategoryComparator extends ViewerComparator
{
    @Override
    public int compare(Viewer a_viewer, Object a_object1, Object a_object2)
    {
        // these 3 if's should not happen but we need to test anyway
        if (a_object1 == null && a_object2 == null)
        {
            return 0;
        }
        if (a_object1 == null)
        {
            return 1;
        }
        if (a_object2 == null)
        {
            return -1;
        }
        // now the real check starts
        if (a_object1 instanceof Category && a_object2 instanceof Category)
        {
            Category t_category1 = (Category) a_object1;
            Category t_category2 = (Category) a_object2;
            if (t_category1.getId() < 0)
            {
                // first one is a special category
                if (t_category2.getId() < 0)
                {
                    // second two
                    return t_category1.getId().compareTo(t_category2.getId());
                }
                else
                {
                    return 1;
                }
            }
            else
            {
                // first one is a category from database
                if (t_category2.getId() < 0)
                {
                    // second two is special category
                    return -1;
                }
                else
                {
                    return this.compareName(t_category1.getName(), t_category2.getName());
                }
            }

        }
        else
        {
            // should not happen
            return 0;
        }
    }

    /**
     * Compare two strings. Deal with null and do the string comparison using
     * ignore case.
     *
     * @param a_name1
     *            First string
     * @param a_name2
     *            Second string
     * @return 1 if only first string is null; -1 if second string is null; 0 if
     *         both are null; otherwise use compareToIgnoreCase from String to
     *         generate the result.
     */
    private int compareName(String a_name1, String a_name2)
    {
        int t_result = 0;
        if (a_name1 == null && a_name2 == null)
        {
            t_result = 0;
        }
        else if (a_name1 == null)
        {
            t_result = 1;
        }
        else if (a_name2 == null)
        {
            t_result = -1;
        }
        else
        {
            t_result = a_name1.compareToIgnoreCase(a_name2);
        }
        return t_result;
    }
}
