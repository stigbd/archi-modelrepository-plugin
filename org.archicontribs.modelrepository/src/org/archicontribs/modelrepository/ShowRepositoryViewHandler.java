/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.modelrepository;

import org.archicontribs.modelrepository.views.ModelRepositoryView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.archimatetool.editor.ui.services.ViewManager;


/**
 * Show Scripts View
 * 
 * @author Phillip Beauvoir
 */
public class ShowRepositoryViewHandler extends AbstractHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        ViewManager.toggleViewPart(ModelRepositoryView.ID, true);
        return null;
    }


}
