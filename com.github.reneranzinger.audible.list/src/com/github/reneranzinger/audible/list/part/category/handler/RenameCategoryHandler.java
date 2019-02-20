
package com.github.reneranzinger.audible.list.part.category.handler;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.github.reneranzinger.audible.list.part.category.CategoryNameValidator;
import com.github.reneranzinger.audible.list.persist.om.Category;
import com.github.reneranzinger.audible.list.service.IAudibleSelectionService;
import com.github.reneranzinger.audible.list.service.IDataModelService;
import com.github.reneranzinger.audible.list.util.AudibleEvents;
import com.github.reneranzinger.audible.list.util.AudibleException;
import com.github.reneranzinger.audible.list.util.MessageBoxUtils;

/**
 * Handler class to rename a new category.
 *
 * @author logan
 *
 */
public class RenameCategoryHandler
{
    private static final Logger logger = Logger.getLogger(RenameCategoryHandler.class);

    /**
     * Execute method of the handler. Creates a dialog to enter the new name and
     * sets a AudibleEvents.CATEGORY_RENAME event to confirm the changed
     * category
     *
     * @param a_shell
     *            Active shell used for the dialog
     * @param a_eventBroker
     *            Event broker used to send the AudibleEvents.CATEGORY_RENAME
     *            event
     * @param a_modelService
     *            Data model service
     */
    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell a_shell,
            IEventBroker a_eventBroker, IDataModelService a_modelService,
            IAudibleSelectionService a_selectionService)
    {
        logger.info("COMMAND - Renaming a catgory");
        // get last selected category
        Category t_selectedCategory = a_selectionService.getSelectedCategory();
        // check if it is one of the special categories
        if (t_selectedCategory.getId() < 0)
        {
            // not allowed to rename
            MessageBoxUtils.createErrorMessageBox(a_shell, "Renaming not allowed",
                    "It is not permitted to rename the category: " + t_selectedCategory.getName());
            logger.info("Not permitted to rename category - " + t_selectedCategory.getName());
        }
        else
        {
            // create the dialog
            InputDialog t_dialog = null;
            try
            {
                t_dialog = new InputDialog(Display.getCurrent().getActiveShell(), "New Cateogry",
                        "Provide a unique name for the category:", t_selectedCategory.getName(),
                        new CategoryNameValidator(a_modelService.getCategories(),
                                t_selectedCategory.getName()));
            }
            catch (AudibleException e)
            {
                logger.error("Unable to retrieve categories", e);
                MessageBoxUtils.createErrorMessageBox(a_shell, "Error renaming category",
                        "There was an error retrieving all categories from the data store. For more information please check log file.");
                return;
            }
            // if the dialog was created, open it
            if (t_dialog != null)
            {
                // dialog was created check if OK button was pressed
                if (t_dialog.open() == Window.OK)
                {
                    String t_name = t_dialog.getValue().trim();
                    // is it a different name, change it and send event
                    if (!t_selectedCategory.getName().equals(t_name))
                    {
                        try
                        {
                            // save the new category
                            Category t_category = a_modelService.renameCategory(t_selectedCategory,
                                    t_name);
                            // trigger event of category change
                            a_eventBroker.send(AudibleEvents.EVENT_CATEGORY_RENAME, t_category);
                        }
                        catch (Exception e)
                        {
                            logger.error("Category could not be changed in the data store", e);
                            MessageBoxUtils.createErrorMessageBox(a_shell,
                                    "Error renaming category",
                                    "There was an error while changing the category in the data store. For more information please check log file.");
                        }
                    }
                }
            }
        }
        logger.info("COMMAND - Renaming a catgory - Finished");
    }
}