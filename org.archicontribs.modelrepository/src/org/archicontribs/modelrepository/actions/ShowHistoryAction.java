/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.modelrepository.actions;

import java.io.IOException;

import org.archicontribs.modelrepository.IModelRepositoryImages;
import org.archicontribs.modelrepository.grafico.GraficoUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Open Model Action
 * 
 * @author Jean-Baptiste Sarrodie
 * @author Phillip Beauvoir
 * @author Stig B. Dørmænen
 */
public class ShowHistoryAction extends AbstractModelAction {
	
	private IWorkbenchWindow fWindow;

    public ShowHistoryAction(IWorkbenchWindow window) {
        fWindow = window;
        setImageDescriptor(IModelRepositoryImages.ImageFactory.getImageDescriptor(IModelRepositoryImages.SHOW_HISTORY_16));
        setText("Show history");
        setToolTipText("Show a list of changes");
    }

    @Override
    public void run() {
        MessageDialog.openInformation(fWindow.getShell(),
                "Show History",
                "Not implemented yet.");
    }
}
