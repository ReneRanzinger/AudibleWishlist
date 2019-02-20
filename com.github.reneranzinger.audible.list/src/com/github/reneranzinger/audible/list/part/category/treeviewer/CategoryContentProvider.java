package com.github.reneranzinger.audible.list.part.category.treeviewer;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Simple content provider for the tree control.
 *
 * @author logan
 *
 */
public class CategoryContentProvider implements ITreeContentProvider
{

    @Override
    public Object[] getElements(Object a_inputElement)
    {
        return (Object[]) a_inputElement;
    }

    @Override
    public Object[] getChildren(Object a_parentElement)
    {
        return null;
    }

    @Override
    public Object getParent(Object a_element)
    {
        return null;
    }

    @Override
    public boolean hasChildren(Object a_element)
    {
        return false;
    }
}
