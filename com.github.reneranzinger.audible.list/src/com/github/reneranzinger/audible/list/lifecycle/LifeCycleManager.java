package com.github.reneranzinger.audible.list.lifecycle;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.FrameworkUtil;

import com.github.reneranzinger.audible.list.log.AudibleListLogConfigurator;
import com.github.reneranzinger.audible.list.service.IAudibleSelectionService;
import com.github.reneranzinger.audible.list.service.IDataModelService;
import com.github.reneranzinger.audible.list.service.impl.AudibleSelectionService;
import com.github.reneranzinger.audible.list.service.impl.DataModelService;

/**
 * Class that is used to execute certain functions as part of the application
 * life cycle, such as registration of services and display of splash screen.
 *
 * @author logan
 *
 */
@SuppressWarnings("restriction")
public class LifeCycleManager
{
    private static final Logger logger = Logger.getLogger(LifeCycleManager.class);

    /**
     * Function to be called after the context is created. This function is used
     * to do all initial steps before the window is created.
     *
     * @param display
     *            Display that can be used to create dialogs
     * @param a_eclipseContext
     *            Context of the eclipse application, used to register services
     */
    @PostContextCreate
    void postContextCreate(Display a_display, IEclipseContext a_eclipseContext)
    {
        // start logging
        AudibleListLogConfigurator.startLog();
        logger.info("Starting Application....");
        logger.info("Registering model service ....");
        this.prepareService(a_eclipseContext);
        logger.info("Registering model service finished");
        // open database
        try
        {
            IDataModelService dataModelService = a_eclipseContext.get(IDataModelService.class);
            dataModelService.openDataStore(
                    "D:\\Java\\workspace-Audible\\com.github.reneranzinger.audible.list\\database\\audible.sqlite3");
        }
        catch (Exception e)
        {
            logger.fatal("Unable to open database.");
            System.exit(-1);
        }
    }

    /**
     * Create services (DataModelService, Selection Service) and register them
     * as osgi services
     *
     * @param eclipseContext
     *            Eclipse context of the application
     */
    private void prepareService(IEclipseContext a_eclipseContext)
    {
        // create the service instances
        IDataModelService t_dataModelService = ContextInjectionFactory.make(DataModelService.class,
                a_eclipseContext);
        IAudibleSelectionService t_selectionService = ContextInjectionFactory
                .make(AudibleSelectionService.class, a_eclipseContext);
        logger.info(
                "Registering service in osgi service layer : " + IDataModelService.class.getName());
        FrameworkUtil.getBundle(this.getClass()).getBundleContext()
                .registerService(IDataModelService.class, t_dataModelService, null);
        FrameworkUtil.getBundle(this.getClass()).getBundleContext()
                .registerService(IAudibleSelectionService.class, t_selectionService, null);
    }
}
