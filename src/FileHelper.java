import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 16-2-6
 * Time: 上午10:21
 */
public class FileHelper {

    private static JFileChooser openfile = new JFileChooser();
    private static JFileChooser openfiles = new JFileChooser();
    private static JFileChooser opendir = new JFileChooser();

    public static File openSelectDir() {
        File dir = null;

        FileFilter filter = new FileNameExtensionFilter("All files", "*.*");
        opendir.addChoosableFileFilter(filter);

        opendir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int ret = opendir.showDialog(null, "Open Directory");

        if (ret == JFileChooser.APPROVE_OPTION) {
            dir = opendir.getSelectedFile();
        }
        return dir;
    }

    public static File[] openSelectFiles() {
        File[] files = null;

        FileFilter filter = new FileNameExtensionFilter("All files", "*.*");
        openfiles.addChoosableFileFilter(filter);

        openfiles.setMultiSelectionEnabled(true);

        int ret = openfiles.showDialog(null, "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            files = openfiles.getSelectedFiles();
        }
        return files;
    }

    public static File openSelectFile() {
        File file = null;

        FileFilter filter = new FileNameExtensionFilter("All files", "*.*");
        openfile.addChoosableFileFilter(filter);

        int ret = openfile.showDialog(null, "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            file = openfile.getSelectedFile();
        }
        return file;
    }

    public static void exportAsFile(List<String> result) {
        File outputFile = openSelectFile();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(outputFile);
            for (int i = 0; i < result.size(); i++) {
                fileWriter.write(result.get(i));
            }
        } catch (IOException e) {

        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {

                }
            }
        }
    }


}
