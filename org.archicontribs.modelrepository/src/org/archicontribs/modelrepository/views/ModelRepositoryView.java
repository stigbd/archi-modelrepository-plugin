/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.modelrepository.views;

import java.io.File;

import org.archicontribs.modelrepository.ModelRepositoryPlugin;
import org.archicontribs.modelrepository.actions.AbstractModelAction;
import org.archicontribs.modelrepository.actions.CloneModelAction;
import org.archicontribs.modelrepository.actions.CommitModelAction;
import org.archicontribs.modelrepository.actions.DeleteModelAction;
import org.archicontribs.modelrepository.actions.OpenModelAction;
import org.archicontribs.modelrepository.actions.PropertiesAction;
import org.archicontribs.modelrepository.actions.PushModelAction;
import org.archicontribs.modelrepository.actions.RefreshModelAction;
import org.archicontribs.modelrepository.actions.SaveModelAction;
import org.eclipse.help.HelpSystem;
import org.eclipse.help.IContext;
import org.eclipse.help.IContextProvider;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;


/**
 * Model Repository ViewPart for managing models
 */
public class ModelRepositoryView
extends ViewPart
implements IContextProvider, ITabbedPropertySheetPageContributor {

	public static String ID = ModelRepositoryPlugin.PLUGIN_ID + ".modelRepositoryView"; //$NON-NLS-1$
    public static String HELP_ID = ModelRepositoryPlugin.PLUGIN_ID + ".modelRepositoryViewHelp"; //$NON-NLS-1$
    
    /**
     * The Repository Viewer
     */
    private GitRepositoryTreeViewer fTreeViewer;
    
    /*
     * Actions
     */
    protected AbstractModelAction fActionClone;
    
    protected AbstractModelAction fActionOpen;
    protected AbstractModelAction fActionRefresh;
    protected AbstractModelAction fActionDelete;
    
    protected AbstractModelAction fActionSave;
    protected AbstractModelAction fActionCommit;
    protected AbstractModelAction fActionPush;
    
    protected AbstractModelAction fActionProperties;
    

    @Override
    public void createPartControl(Composite parent) {
        // Create the Tree Viewer first
        fTreeViewer = new GitRepositoryTreeViewer(ModelRepositoryPlugin.INSTANCE.getUserModelRepositoryFolder(), parent);
        
        makeActions();
        registerGlobalActions();
        hookContextMenu();
        //makeLocalMenuActions();
        makeLocalToolBarActions();
        
        // Register us as a selection provider so that Actions can pick us up
        getSite().setSelectionProvider(getViewer());
        
        /*
         * Listen to Selections to update local Actions
         */
        getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                updateActions(event.getSelection());
            }
        });
        
        /*
         * Listen to Double-click Action
         */
        getViewer().addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                //handleDoubleClickAction();
            }
        });

        // Register Help Context
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getViewer().getControl(), HELP_ID);
    }
    
    /**
     * Make local actions
     */
    protected void makeActions() {
        fActionClone = new CloneModelAction(getViewSite().getWorkbenchWindow());
        
        fActionOpen = new OpenModelAction(getViewSite().getWorkbenchWindow());
        fActionOpen.setEnabled(false);
        
        fActionRefresh = new RefreshModelAction(getViewSite().getWorkbenchWindow());
        fActionRefresh.setEnabled(false);
        
        fActionDelete = new DeleteModelAction(getViewSite().getWorkbenchWindow());
        fActionDelete.setEnabled(false);
        
        fActionSave = new SaveModelAction(getViewSite().getWorkbenchWindow());
        fActionSave.setEnabled(false);
        
        fActionCommit = new CommitModelAction(getViewSite().getWorkbenchWindow());
        fActionCommit.setEnabled(false);
        
        fActionPush = new PushModelAction(getViewSite().getWorkbenchWindow());
        fActionPush.setEnabled(false);
        
        fActionProperties = new PropertiesAction();
        fActionProperties.setEnabled(false);
        
        // Register the Keybinding for actions
//        IHandlerService service = (IHandlerService)getViewSite().getService(IHandlerService.class);
//        service.activateHandler(fActionRefresh.getActionDefinitionId(), new ActionHandler(fActionRefresh));
    }

    /**
     * Register Global Action Handlers
     */
    private void registerGlobalActions() {
        IActionBars actionBars = getViewSite().getActionBars();
        
        // Register our interest in the global menu actions
        actionBars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), fActionProperties);
    }

    /**
     * Hook into a right-click menu
     */
    protected void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#RepoViewerPopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
            }
        });
        
        Menu menu = menuMgr.createContextMenu(getViewer().getControl());
        getViewer().getControl().setMenu(menu);
        
        getSite().registerContextMenu(menuMgr, getViewer());
    }
    
    /**
     * Make Any Local Bar Menu Actions
     */
//    protected void makeLocalMenuActions() {
//        IActionBars actionBars = getViewSite().getActionBars();
//
//        // Local menu items go here
//        IMenuManager manager = actionBars.getMenuManager();
//        manager.add(new Action("&View Management...") {
//            public void run() {
//                MessageDialog.openInformation(getViewSite().getShell(),
//                        "View Management",
//                        "This is a placeholder for the View Management Dialog");
//            }
//        });
//    }

    /**
     * Make Local Toolbar items
     */
    protected void makeLocalToolBarActions() {
        IActionBars bars = getViewSite().getActionBars();
        IToolBarManager manager = bars.getToolBarManager();

        manager.add(new Separator(IWorkbenchActionConstants.NEW_GROUP));
        manager.add(fActionClone);
        manager.add(new Separator());
        manager.add(fActionDelete);
        manager.add(fActionRefresh);
        manager.add(fActionOpen);
        manager.add(new Separator());
        manager.add(fActionSave);
        manager.add(fActionCommit);
        manager.add(fActionPush);
    }
    
    /**
     * Update the Local Actions depending on the selection 
     * @param selection
     */
    public void updateActions(ISelection selection) {
        File file = (File)((IStructuredSelection)selection).getFirstElement();
        //boolean isEmpty = selection.isEmpty();
        
        // Actions that need a git repository
        fActionRefresh.setGitRepository(file);
        fActionOpen.setGitRepository(file);
        fActionDelete.setGitRepository(file);
        
        // TODO: Actions that should in fact be bounded to an ArchimateModel and not a git repository 
        fActionSave.setGitRepository(file);
        fActionCommit.setGitRepository(file);
        fActionPush.setGitRepository(file);
        
        fActionProperties.setGitRepository(file);
    }
    
    protected void fillContextMenu(IMenuManager manager) {
        boolean isEmpty = getViewer().getSelection().isEmpty();

        manager.add(fActionClone);

        if(!isEmpty) {
            manager.add(new Separator());
            manager.add(fActionDelete);
            manager.add(fActionRefresh);
            manager.add(fActionOpen);
            manager.add(new Separator());
            manager.add(fActionSave);
            manager.add(fActionCommit);
            manager.add(fActionPush);
            manager.add(new Separator());
            manager.add(fActionProperties);
        }
    }

    /**
     * @return The Viewer
     */
    public TreeViewer getViewer() {
        return fTreeViewer;
    }
    
    @Override
    public void setFocus() {
        if(getViewer() != null) {
            getViewer().getControl().setFocus();
        }
    }
    
    @Override
    public String getContributorId() {
        return ModelRepositoryPlugin.PLUGIN_ID;
    }
    
    @Override
    public <T> T getAdapter(Class<T> adapter) {
        /*
         * Return the PropertySheet Page
         */
        if(adapter == IPropertySheetPage.class) {
            return adapter.cast(new TabbedPropertySheetPage(this));
        }
        
        return super.getAdapter(adapter);
    }


    // =================================================================================
    //                       Contextual Help support
    // =================================================================================
    
    public int getContextChangeMask() {
        return NONE;
    }

    public IContext getContext(Object target) {
        return HelpSystem.getContext(HELP_ID);
    }

    public String getSearchExpression(Object target) {
        return "Repository View";
    }
}
