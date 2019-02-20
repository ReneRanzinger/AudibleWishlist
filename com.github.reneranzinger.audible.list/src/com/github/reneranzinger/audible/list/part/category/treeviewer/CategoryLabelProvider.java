package com.github.reneranzinger.audible.list.part.category.treeviewer;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.github.reneranzinger.audible.list.persist.om.Category;

/**
 * Class for the label provider of categories in the explorer tree control.
 *
 * @author logan
 *
 */
public class CategoryLabelProvider extends LabelProvider implements IFontProvider
{
    static final Logger logger = Logger.getLogger(CategoryLabelProvider.class);

    private Font m_fontDatabaseCategory = null;
    private Image m_icon = null;

    /**
     * Constructors the resources from the calling class
     *
     * @param a_fontDatabaseCategory
     *            Font used to display the database categories
     * @param a_icon
     *            Icon for the database categories
     */
    public CategoryLabelProvider(Font a_fontDatabaseCategory, Image a_icon)
    {
        this.m_fontDatabaseCategory = a_fontDatabaseCategory;
        this.m_icon = a_icon;
    }

    /**
     * Get the font to be used for the labels
     *
     * @return Front to be used
     */
    @Override
    public Font getFont(Object a_object)
    {
        // only categories form the database will be bold
        if (a_object instanceof Category)
        {
            Category t_category = (Category) a_object;
            if (t_category.getId() >= 0)
            {
                return this.m_fontDatabaseCategory;
            }
        }
        return null;
    }

    /**
     * Get text to be displayed for the categories objects
     *
     * @return Text to be displayed
     */
    @Override
    public String getText(Object a_object)
    {
        // if its a category get its name, the alternative should not happen
        if (a_object instanceof Category)
        {
            Category t_category = (Category) a_object;
            return t_category.getName();
        }
        return a_object.toString();
    }

    /**
     * Provide the image to be displayed as icon
     *
     * @return Image object
     */
    @Override
    public Image getImage(Object a_object)
    {
        // if its a category get the icon
        if (a_object instanceof Category)
        {
            Category t_category = (Category) a_object;
            // only if the category comes from the database give a icon
            if (t_category.getId() >= 0)
            {
                return this.m_icon;
            }
        }
        return null;
    }

}