/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.modelrepository.grafico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.archicontribs.modelrepository.GitHelper;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.editor.utils.FileUtils;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;

import junit.framework.JUnit4TestAdapter;


@SuppressWarnings("nls")
public class GraficoUtilsTests {
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(GraficoUtilsTests.class);
    }
    
    @Before
    public void runOnceBeforeEachTest() {
    }
    
    @After
    public void runOnceAfterEachTest() throws IOException {
        FileUtils.deleteFolder(getTempTestsFolder());
    }
    
    
    @Test
    public void isGitRepository_FileShouldNotBe() throws Exception {
        File tmpFile = File.createTempFile("architest", null);
        assertFalse(GraficoUtils.isGitRepository(tmpFile));
        tmpFile.delete();
    }

    @Test
    public void isGitRepository_EmptyFolderIsNotGitFolder() {
        File tmpFolder = new File(getTempTestsFolder(), "testFolder");
        tmpFolder.mkdirs();
        
        assertFalse(GraficoUtils.isGitRepository(tmpFolder));
    }

    @Test
    public void isGitRepository_HasGitFolder() {
        File tmpFolder = new File(getTempTestsFolder(), "testFolder");
        File gitFolder = new File(tmpFolder, ".git");
        gitFolder.mkdirs();
        
        assertTrue(GraficoUtils.isGitRepository(tmpFolder));
    }
    
    @Test
    public void getLocalGitFolderName_ShouldReturnCorrectName() {
        String repoURL = "https://githosting.org/path/archi-demo-grafico.git";
        assertEquals("archi-demo-grafico", GraficoUtils.getLocalGitFolderName(repoURL));
        
        repoURL = "ssh://githosting.org/path/archi-demo-grafico";
        assertEquals("archi-demo-grafico", GraficoUtils.getLocalGitFolderName(repoURL));
        
        repoURL = "ssh://githosting.org/This_One";
        assertEquals("this_one", GraficoUtils.getLocalGitFolderName(repoURL));        
    }

    @Test
    public void isModelLoaded_IsLoadedInModelsTree() {
        File localGitFolder = new File("/temp/folder");
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setFile(GraficoUtils.getModelFileName(localGitFolder));
        
        IEditorModelManager.INSTANCE.openModel(model);
        assertTrue(GraficoUtils.isModelLoaded(localGitFolder));
    }

    @Test
    public void locateModel_LocateNewModel() {
        File localGitFolder = new File("/temp/folder");
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setFile(GraficoUtils.getModelFileName(localGitFolder));
        
        IEditorModelManager.INSTANCE.openModel(model);
        assertEquals(model, GraficoUtils.locateModel(localGitFolder));
    }

    @Test
    public void getModelFileName_IsCorrect() {
        File localGitFolder = new File("/temp/folder");
        assertEquals(new File(localGitFolder, ".git/temp.archimate"), GraficoUtils.getModelFileName(localGitFolder));
    }
    
    @Test
    public void createNewLocalGitRepository_CreatesNewRepo() throws Exception {
        File localGitFolder = new File(getTempTestsFolder(), "testRepo");
        String URL = "https://www.somewherethereish.net/myRepo.git";
        
        try(Git git = GraficoUtils.createNewLocalGitRepository(localGitFolder, URL)) {
            assertNotNull(git);
            assertEquals("origin", git.getRepository().getRemoteName("refs/remotes/origin/"));
            assertEquals(localGitFolder, git.getRepository().getWorkTree());
            assertFalse(git.getRepository().isBare());
            assertEquals(URL, git.remoteList().call().get(0).getURIs().get(0).toASCIIString());
        }
    }
    
    @Test (expected=IOException.class)
    public void createNewLocalGitRepository_ThrowsExceptionIfNotEmptyDir() throws Exception {
        File tmpFile = File.createTempFile("architest", null, getTempTestsFolder());
        
        // Should throw exception
        GraficoUtils.createNewLocalGitRepository(tmpFile.getParentFile(), "");
    }
    
    @Test
    public void getRepositoryURL_ShouldReturnURL() throws Exception {
        File localGitFolder = new File(getTempTestsFolder(), "testRepo");
        String URL = "https://www.somewherethereish.net/myRepo.git";
        
        try(Git git = GraficoUtils.createNewLocalGitRepository(localGitFolder, URL)) {
            assertNotNull(git);
            assertEquals(URL, GraficoUtils.getRepositoryURL(localGitFolder));
        }
    }

    @Test
    public void getFileContents_IsCorrect() throws Exception {
        File localGitFolder = new File(getTempTestsFolder(), "testRepo");
        String contents = "Hello World!\nTesting.";
        
        try(Repository repo = GitHelper.createNewRepository(localGitFolder)) {
            File file = new File(localGitFolder, "test.txt");
            
            try(FileWriter fw = new FileWriter(file)) {
                fw.write(contents);
                fw.flush();
            }
            
            assertTrue(file.exists());
            
            // Add file to index
            AddCommand addCommand = new AddCommand(repo);
            addCommand.addFilepattern("."); //$NON-NLS-1$
            addCommand.setUpdate(false);
            addCommand.call();
            
            // Commit file
            CommitCommand commitCommand = Git.wrap(repo).commit();
            commitCommand.setAuthor("Test", "Test");
            commitCommand.setMessage("Message");
            commitCommand.call();

            assertEquals(contents, GraficoUtils.getFileContents(localGitFolder, "test.txt", "HEAD"));
        }
    }
    
    // Support
    
    private File getTempTestsFolder() {
        File file = new File(System.getProperty("java.io.tmpdir"), "org.archicontribs.modelrepository.tests.tmp");
        file.deleteOnExit();
        file.mkdirs();
        return file;
    }
    

}
