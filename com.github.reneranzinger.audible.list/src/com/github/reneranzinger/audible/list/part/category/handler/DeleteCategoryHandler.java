
package com.github.reneranzinger.audible.list.part.category.handler;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.github.reneranzinger.audible.list.persist.om.Category;
import com.github.reneranzinger.audible.list.service.IAudibleSelectionService;
import com.github.reneranzinger.audible.list.service.IDataModelService;
import com.github.reneranzinger.audible.list.util.AudibleEvents;
import com.github.reneranzinger.audible.list.util.MessageBoxUtils;

public class DeleteCategoryHandler
{
    private static final Logger logger = Logger.getLogger(NewCategoryHandler.class);

    /**
     * Execute method of the handler. Creates a confirmation dialog to ask user
     * to confirm the deletion of the last selected category. If the deletion is
     * confirmed the category is removed from the model and a
     * AudibleEvents.CATEGORY_DELETE event is send.
     *
     * @param a_shell
     *            Active shell used for the dialog
     * @param a_eventBroker
     *            Event broker used to send the AudibleEvents.CATEGORY_ADD event
     * @param a_modelService
     *            Data model service
     * @param a_selectionService
     *            Audible selection service to retrieve last selected category
     */
    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell a_shell,
            IEventBroker a_eventBroker, IDataModelService a_modelService,
            IAudibleSelectionService a_selectionService)
    {
        logger.info("COMMAND - Deleting a catgory");
        // get last selected category
        Category t_selectedCategory = a_selectionService.getSelectedCategory();
        // check if it is one of the special categories
        if (t_selectedCategory.getId() < 0)
        {
            // not allowed to delete
            MessageBoxUtils.createErrorMessageBox(a_shell, "Deleting not allowed",
                    "It is not permitted to delete the category: " + t_selectedCategory.getName());
            logger.info("Not permitted to delete category - " + t_selectedCategory.getName());
        }
        else
        {
            // create confirmation dialog
            int t_response = MessageBoxUtils.createConfirmationMessageBox(a_shell,
                    "Deleting category", "Are you sure that you want to delete category: "
                            + t_selectedCategory.getName());
            // has the user selected YES?
            if (t_response == SWT.YES)
            {
                try
                {
                    // delete the category
                    a_modelService.deleteCategory(t_selectedCategory);
                    // trigger event of category change
                    a_eventBroker.send(AudibleEvents.EVENT_CATEGORY_DELETE, t_selectedCategory);
                }
                catch (Exception e)
                {
                    logger.error("Category could not be delete from the data store", e);
                    MessageBoxUtils.createErrorMessageBox(a_shell, "Error delete category",
                            "There was an error while deleting the category from the data store. For more information please check log file.");
                }
            }
        }
        logger.info("COMMAND - Deleting a catgory - Finished");
    }

}