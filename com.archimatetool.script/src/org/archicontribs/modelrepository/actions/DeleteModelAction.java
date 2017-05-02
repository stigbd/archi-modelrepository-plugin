package org.archicontribs.modelrepository.actions;

import org.archicontribs.modelrepository.IModelRepositoryImages;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

public class DeleteModelAction extends AbstractModelAction {
	
	private IWorkbenchWindow fWindow;

    public DeleteModelAction(IWorkbenchWindow window) {
        fWindow = window;
        setImageDescriptor(IModelRepositoryImages.ImageFactory.getImageDescriptor(IModelRepositoryImages.ICON_DELETE_16));
        setText("Remove Bookmark");
        setToolTipText("Remove a Bookmark and delete local copy");
    }

    @Override
    public void run() {
    	boolean confirmed = MessageDialog.openConfirm(fWindow.getShell(), "Confirm", "Are you sure you want to remove this bookmark. This can't be undone!");
    	
    	if(confirmed) {
    		// TODO
    		MessageDialog.openInformation(fWindow.getShell(), this.getText(), "Model deleted!");
    	}
    }
}
