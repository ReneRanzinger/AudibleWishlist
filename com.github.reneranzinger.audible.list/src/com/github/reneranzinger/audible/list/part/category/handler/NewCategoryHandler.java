
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
import com.github.reneranzinger.audible.list.service.IDataModelService;
import com.github.reneranzinger.audible.list.util.AudibleEvents;
import com.github.reneranzinger.audible.list.util.AudibleException;
import com.github.reneranzinger.audible.list.util.MessageBoxUtils;

/**
 * Handler class to add a new category to the database.
 *
 * @author logan
 *
 */
public class NewCategoryHandler
{
    private static final Logger logger = Logger.getLogger(NewCategoryHandler.class);

    /**
     * Execute method of the handler. Creates a dialog to enter the name and
     * sets a AudibleEvents.CATEGORY_ADD event to confirm the newly created
     * category
     *
     * @param a_shell
     *            Active shell used for the dialog
     * @param a_eventBroker
     *            Event broker used to send the AudibleEvents.CATEGORY_ADD event
     * @param a_modelService
     *            Data model service
     */
    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell a_shell,
            IEventBroker a_eventBroker, IDataModelService a_modelService)
    {
        logger.info("COMMAND - Creating a new catgory");
        // create the dialog
        InputDialog t_dialog = null;
        try
        {
            t_dialog = new InputDialog(Display.getCurrent().getActiveShell(), "New Cateogry",
                    "Provide a unique name for the category:", null,
                    new CategoryNameValidator(a_modelService.getCategories()));
        }
        catch (AudibleException e)
        {
            logger.error("Unable to retrieve categories", e);
            MessageBoxUtils.createErrorMessageBox(a_shell, "Error creating new category",
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
                try
                {
                    // save the new category
                    Category t_category = a_modelService.addCategory(t_name);
                    // trigger event of category change
                    a_eventBroker.send(AudibleEvents.EVENT_CATEGORY_ADD, t_category);
                }
                catch (Exception e)
                {
                    logger.error("Category could not be added to data store", e);
                    MessageBoxUtils.createErrorMessageBox(a_shell, "Error creating new category",
                            "There was an error while adding the new category in the data store. For more information please check log file.");
                }
            }
        }
        logger.info("COMMAND - Creating a new catgory - Finished");
    }
}