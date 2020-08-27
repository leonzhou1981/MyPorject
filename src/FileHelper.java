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
public class FileHelper extends AbstractFileHelper {

    public void exportAsFile(List<String> result) {
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

    @Override
    protected boolean isSpecifiedFormat(File aFile) {
        return false;
    }

    @Override
    protected void doSpecifiedThingsForFile(File aFile) {

    }

    public static void main(String[] args) {
        String tmpPath = "D:\\WEB\\VAADIN\\tmff-web\\tmp";
        String tmpFile = tmpPath + "/kewill-kff-framework-configuration.jar";
        new File(tmpFile).delete();
    }
}
