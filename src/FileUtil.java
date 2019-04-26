import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * User: lzhou
 * Date: Sep 22, 2009
 * Time: 1:44:45 PM
 */
public class FileUtil {

    public static final String line_separator = System.getProperty("line.separator");
    public static final String file_separator = System.getProperty("file.separator");

    public static void iterateAllFilesUnderOneDirectory(String path, IFileAction ifa) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                ifa.doProcess(file);
                if (file.isDirectory()) {
                    ifa.doDirectoryProcess(file);
                    String[] files = file.list();
                    for (String subFile : files) {
                        iterateAllFilesUnderOneDirectory(path + "\\" + subFile, ifa);
                    }
                } else {
                    ifa.doFileProcess(file);
                }
            }
        }
    }

    public static File openFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            File dirs = file.getParentFile();
            dirs.mkdirs();
            file.createNewFile();
        }
        return file;
    }

    public static List<String> readTextFile(File file) throws IOException {
        List<String> manyLines = new LinkedList<String>();
        if (file != null && file.exists()) {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while (line != null) {
                manyLines.add(line);
                line = br.readLine();
            }
            fr.close();
        }
        return manyLines;
    }

    public static void writeTextFile(File file, List<String> manyLines) throws IOException {
        if (file != null && file.exists()) {
            FileWriter fw = new FileWriter(file);
            if (manyLines != null && manyLines.size() > 0) {
                for (String line : manyLines) {
                    fw.write(line);
                    fw.write(line_separator);
                }
            }
            fw.close();
        }
    }

    public static void main(String[] args) {
        try {
            openFile("C:\\1\\2\\3.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
