/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.modelrepository.authentication;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.archicontribs.modelrepository.ModelRepositoryPlugin;
import org.archicontribs.modelrepository.dialogs.UserNamePasswordDialog;
import org.archicontribs.modelrepository.preferences.IPreferenceConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**
 * User Details
 * 
 * @author Phillip Beauvoir
 */
public class UserDetails {

    public static String[] getUserNameAndPasswordFromCredentialsFileOrDialog(File localGitFolder, Shell shell) throws IOException {
        String userName = null;
        String userPassword = null;
        
        // Secure storage
        SimpleCredentialsStorage sc = new SimpleCredentialsStorage(localGitFolder);
        
        if(sc.hasCredentialsFile()) {
            userName = sc.getUserName();
            userPassword = sc.getUserPassword();
        }
        // Ask user
        else {
            UserNamePasswordDialog dialog = new UserNamePasswordDialog(shell);
            if(dialog.open() != Window.OK) {
                return null;
            }
            
            userName = dialog.getUsername();
            userPassword = dialog.getPassword();
            
            // Store credentials if option is set
            if(ModelRepositoryPlugin.INSTANCE.getPreferenceStore().getBoolean(IPreferenceConstants.PREFS_STORE_REPO_CREDENTIALS)) {
                try {
                    sc.store(userName, userPassword);
                }
                catch(NoSuchAlgorithmException | InvalidKeySpecException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return new String[] { userName, userPassword };
    }
}
