import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 16-9-22
 * Time: 下午1:55
 */
public class MD5Checksum2 {

    private static Logger LOGGER = Logger.getLogger(MD5Checksum2.class);
    private static JFileChooser openfile = new JFileChooser();

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

    public static void main(String[] args) {
        try {
            String md5 = DigestUtils.md5Hex(new FileInputStream(openSelectFile()));
            System.out.println(md5);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
