
package com.github.reneranzinger.audible.list.part;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import com.github.reneranzinger.audible.list.part.category.treeviewer.CategoryComparator;
import com.github.reneranzinger.audible.list.part.category.treeviewer.CategoryContentProvider;
import com.github.reneranzinger.audible.list.part.category.treeviewer.CategoryLabelProvider;
import com.github.reneranzinger.audible.list.persist.om.Category;
import com.github.reneranzinger.audible.list.service.IAudibleSelectionService;
import com.github.reneranzinger.audible.list.service.IDataModelService;
import com.github.reneranzinger.audible.list.util.AudibleEvents;
import com.github.reneranzinger.audible.list.util.AudibleException;

/**
 * Class that contains the functional code for the category display ususal shown
 * on the left side of the application.
 *
 * @author logan
 *
 */
public class CategoryPart
{
    static final Logger logger = Logger.getLogger(CategoryPart.class);

    public static final String POPUP_MENU_ID = "com.github.reneranzinger.audible.list.popupmenu.category";

    private TreeViewer m_treeViewer;
    private String m_filterString = "";
    private Font m_boldFont = null;
    private Image m_icon = null;
    private List<Category> m_categories = new ArrayList<>();

    /**
     * Method used to create all the controls that are displayed in the part.
     *
     * @param a_parent
     *            Part composite that calls this method
     * @param a_menuService
     *            Eclipse menu service used to register the context menu for the
     *            tree control
     * @param m_modelService
     *            Service that provides access to the data used to populate the
     *            tree control
     */
    @PostConstruct
    public void postConstruct(Composite a_parent, EMenuService a_menuService,
            IDataModelService a_modelService, IAudibleSelectionService a_selectionService)
    {
        logger.info("Creating Category list part");
        // create the resources such as fonts and icons
        this.createResources();
        // load the categories from the database
        try
        {
            this.m_categories = a_modelService.getCategories();
        }
        catch (AudibleException e)
        {
            logger.error("Unable to load categories from the datamodel", e);
        }
        a_parent.setLayout(new GridLayout(1, false));
        // add the filter entry
        Text search = new Text(a_parent, SWT.SEARCH | SWT.CANCEL | SWT.ICON_SEARCH);
        search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        search.setMessage("Filter");
        // add listener to communicate with the tree viewer if the filter text
        // is changed
        search.addModifyListener(new ModifyListener()
        {

            @Override
            public void modifyText(ModifyEvent e)
            {
                // when text changes get the changed value and update the tree
                // viewer
                Text source = (Text) e.getSource();
                m_filterString = source.getText();
                m_treeViewer.expandAll();
                m_treeViewer.refresh();
            }
        });
        // if cancel is clicked reset the text
        search.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                if (e.detail == SWT.CANCEL)
                {
                    Text text = (Text) e.getSource();
                    text.setText("");
                }
            }
        });
        // create the tree control and set provider, input and the comparator
        this.m_treeViewer = new TreeViewer(a_parent, SWT.H_SCROLL | SWT.V_SCROLL);
        this.m_treeViewer.getTree()
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        this.m_treeViewer.setContentProvider(new CategoryContentProvider());
        this.m_treeViewer.setLabelProvider(new CategoryLabelProvider(this.m_boldFont, this.m_icon));
        this.m_treeViewer.setInput(this.m_categories.toArray());
        this.m_treeViewer.setComparator(new CategoryComparator());
        // add the filter to tree viewer
        this.m_treeViewer.addFilter(new CategoryFilter());
        // add double click event listener to open the category
        this.m_treeViewer.addDoubleClickListener(new IDoubleClickListener()
        {
            @Override
            public void doubleClick(DoubleClickEvent a_event)
            {
                // find the selected entry in the tree
                IStructuredSelection t_selection = (StructuredSelection) a_event.getSelection();
                if (t_selection != null)
                {
                    if (t_selection.size() > 0)
                    {
                        // go further only if it is a Category
                        Object t_selectionObject = t_selection.getFirstElement();
                        if (t_selectionObject instanceof Category)
                        {
                            Category t_category = (Category) t_selectionObject;
                            a_selectionService.setSelectedCategory(t_category);
                            // TODO open the category
                        }
                    }
                }
            }
        });
        // safe the selection changes
        this.m_treeViewer.addSelectionChangedListener(new ISelectionChangedListener()
        {
            @Override
            public void selectionChanged(SelectionChangedEvent a_event)
            {
                // find the selected entry in the tree
                IStructuredSelection t_selection = (StructuredSelection) a_event.getSelection();
                if (t_selection != null)
                {
                    if (t_selection.size() > 0)
                    {
                        // go further only if it is a Category
                        Object t_selectionObject = t_selection.getFirstElement();
                        if (t_selectionObject instanceof Category)
                        {
                            Category t_category = (Category) t_selectionObject;
                            // save the selected category
                            a_selectionService.setSelectedCategory(t_category);
                        }
                    }
                }
            }
        });
        // context menu
        a_menuService.registerContextMenu(this.m_treeViewer.getControl(),
                CategoryPart.POPUP_MENU_ID);

        logger.info("Creating Category List part - Finished");
    }

    /**
     * Method to create fonts and icons used in the control. This instances are
     * stored in the member variables of the class.
     */
    private void createResources()
    {
        // create the bold font
        FontRegistry t_registry = new FontRegistry();
        String a_fontName = Display.getCurrent().getSystemFont().getFontData()[0].getName();
        this.m_boldFont = t_registry.getBold(a_fontName);
        // create the icon
        try
        {
            ImageDescriptor t_imageDescriptor = ImageDescriptor.createFromURL(new URL(
                    "platform:/plugin/com.github.reneranzinger.audible.list/icons/category24.png"));
            this.m_icon = t_imageDescriptor.createImage();
        }
        catch (MalformedURLException e)
        {
            logger.error("Unable to load image", e);
        }
    }

    /**
     * Method called before the part is disposed. Is used to release all
     * allocated resources such as fonts and images.
     */
    @PreDestroy
    public void preDestroy()
    {
        logger.info("Disposing Category List part");
        this.m_boldFont.dispose();
        this.m_icon.dispose();
        logger.info("Disposing Category List part - Finished");
    }

    /**
     * Class that allows to filter the tree control based on the filter string.
     *
     * @author logan
     *
     */
    private class CategoryFilter extends ViewerFilter
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean select(Viewer a_viewer, Object a_parentElement, Object a_element)
        {
            // check if the object is a category, should always be the case
            if (a_element instanceof Category)
            {
                // test if the filter string is part of the category name
                Category t_category = (Category) a_element;
                if (t_category.getName().toLowerCase().contains(m_filterString.toLowerCase()))
                {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Method is triggered by the AudibleEvents.EVENT_CATEGORY_DELETE event.
     * Will refresh the tree viewer.
     *
     * @param a_category
     *            Category that was delete from the model
     * @param a_modelService
     *            Datamodel service
     */
    @Optional
    @Inject
    public void deleteCategory(
            @UIEventTopic(AudibleEvents.EVENT_CATEGORY_DELETE) Category a_category,
            IDataModelService a_modelService)
    {
        this.refreshCategories(a_modelService);
    }

    /**
     * Method is triggered by the AudibleEvents.EVENT_CATEGORY_ADD event. Will
     * refresh the tree viewer.
     *
     * @param a_category
     *            new Category that was added to the model
     * @param a_modelService
     *            Datamodel service
     */
    @Optional
    @Inject
    public void addCategory(@UIEventTopic(AudibleEvents.EVENT_CATEGORY_ADD) Category a_category,
            IDataModelService a_modelService)
    {
        this.refreshCategories(a_modelService);
        // select category that was added
        this.selectCategoryById(a_category.getId());
    }

    /**
     * Method is triggered by the AudibleEvents.EVENT_CATEGORY_ADD event. Will
     * refresh the tree viewer.
     *
     * @param a_category
     *            category that was renamed
     * @param a_modelService
     *            Datamodel service
     */
    @Optional
    @Inject
    public void renameCategory(
            @UIEventTopic(AudibleEvents.EVENT_CATEGORY_RENAME) Category a_category,
            IDataModelService a_modelService)
    {
        this.refreshCategories(a_modelService);
        // select category that was renamed
        this.selectCategoryById(a_category.getId());
    }

    /**
     * Select a category in the tree viewer based on the ID of the category
     *
     * @param a_id
     *            ID of the category to be selected
     */
    private void selectCategoryById(Integer a_id)
    {
        // iterate over the categories and find the one with the right ID
        for (Category t_category : this.m_categories)
        {
            if (t_category.getId().equals(a_id))
            {
                this.m_treeViewer.setSelection(new StructuredSelection(t_category));
            }
        }
    }

    /**
     * Reloads the categories from the datamodel and populates the tree viewer
     * with the new data. This method is triggered after each change to the
     * datamodel (add, delete, rename).
     *
     * @param a_modelService
     *            Datamodel service
     */
    private void refreshCategories(IDataModelService a_modelService)
    {
        try
        {
            this.m_categories = a_modelService.getCategories();
        }
        catch (AudibleException e)
        {
            logger.error("Unable to load categories from the datamodel", e);
            this.m_categories = new ArrayList<>();
        }
        this.m_treeViewer.setInput(this.m_categories.toArray());
        this.m_treeViewer.refresh();
    }
}