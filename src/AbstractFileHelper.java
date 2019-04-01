import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public abstract class AbstractFileHelper {

    protected File openSelectDir() {
        JFileChooser opendir = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("All Files", "*.*");
        opendir.addChoosableFileFilter(filter);

        opendir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int ret = opendir.showDialog(null, "Open Directory");

        File dir = null;
        if (ret == JFileChooser.APPROVE_OPTION) {
            dir = opendir.getSelectedFile();
        }
        return dir;
    }

    public File[] openSelectFiles() {
        JFileChooser openfiles = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("All files", "*.*");
        openfiles.addChoosableFileFilter(filter);

        openfiles.setMultiSelectionEnabled(true);

        int ret = openfiles.showDialog(null, "Open file");

        File[] files = null;
        if (ret == JFileChooser.APPROVE_OPTION) {
            files = openfiles.getSelectedFiles();
        }
        return files;
    }

    public File openSelectFile() {
        JFileChooser openfile = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("All files", "*.*");
        openfile.addChoosableFileFilter(filter);

        int ret = openfile.showDialog(null, "Open file");

        File file = null;
        if (ret == JFileChooser.APPROVE_OPTION) {
            file = openfile.getSelectedFile();
        }
        return file;
    }

    private void iterateDirectoryAndFiles(File aDirectoryOrFile) {
        if (aDirectoryOrFile.isDirectory()) {
            File[] subDirectoryAndFiles = aDirectoryOrFile.listFiles();
            for (int i = 0; i < subDirectoryAndFiles.length; i++) {
                File subDirectoryAndFile = subDirectoryAndFiles[i];
                if (subDirectoryAndFile.isFile() && isSpecifiedFormat(subDirectoryAndFile)) {
                    doSpecifiedThingsForFile(subDirectoryAndFile);
                } else if (subDirectoryAndFile.isDirectory()) {
                    iterateDirectoryAndFiles(subDirectoryAndFile);
                }
            }
        }
    }

    protected abstract boolean isSpecifiedFormat(File aFile);

    protected abstract void doSpecifiedThingsForFile(File aFile);
}
