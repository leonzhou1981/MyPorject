import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 15-11-10
 * Time: 上午10:13
 */
public class LineCounter {

    private static long countLines = 0;
    private static long countLinesAtClient = 0;
    private static long countLinesAtCommon = 0;
    private static long countLinesAtServer = 0;

    public static void main(String[] args) {
        countLines = 0;

        JFileChooser fileopen = new JFileChooser();
        fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        FileFilter filter = new FileNameExtensionFilter("All files", "*.*");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(null, "Select Directory");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            iterateInFolder(file);
            System.out.println("There are " + countLines + " lines under " + file.getAbsolutePath());
            System.out.println("There are " + countLinesAtClient + " lines under " + file.getAbsolutePath() + " at client side.");
            System.out.println("There are " + countLinesAtServer + " lines under " + file.getAbsolutePath() + " at server side.");
            System.out.println("There are " + countLinesAtCommon + " lines under " + file.getAbsolutePath() + " at both client and server side.");
        }
    }

    private static void iterateInFolder(File root) {
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            for (File sub : files) {
                iterateInFolder(sub);
            }
        } else {
            String fileName = root.getAbsolutePath();
            if (fileName.contains("Desktop") || fileName.contains("gui")) {
                if (fileName.contains("src") && (fileName.endsWith(".java"))) {
                    countLines += countLinesInJavaFile(root);
                    countLinesAtClient += countLinesInJavaFile(root);
                }
                if (fileName.contains("src") && fileName.endsWith(".xml")) {
                    countLines += countLinesInXMLFile(root);
                    countLinesAtClient += countLinesInXMLFile(root);
                }
                if (fileName.contains("src") && fileName.endsWith(".properties")) {
                    countLines += countLinesInPropertiesFile(root);
                    countLinesAtClient += countLinesInPropertiesFile(root);
                }
            } else if (fileName.contains("Business") || fileName.contains("server")) {
                if (fileName.contains("src") && (fileName.endsWith(".java"))) {
                    countLines += countLinesInJavaFile(root);
                    countLinesAtServer += countLinesInJavaFile(root);
                }
                if (fileName.contains("src") && fileName.endsWith(".xml")) {
                    countLines += countLinesInXMLFile(root);
                    countLinesAtServer += countLinesInXMLFile(root);
                }
                if (fileName.contains("src") && fileName.endsWith(".properties")) {
                    countLines += countLinesInPropertiesFile(root);
                    countLinesAtServer += countLinesInPropertiesFile(root);
                }
            } else {
                if (fileName.contains("src") && (fileName.endsWith(".java"))) {
                    countLines += countLinesInJavaFile(root);
                    countLinesAtCommon += countLinesInJavaFile(root);
                }
                if (fileName.contains("src") && fileName.endsWith(".xml")) {
                    countLines += countLinesInXMLFile(root);
                    countLinesAtCommon += countLinesInXMLFile(root);
                }
                if (fileName.contains("src") && fileName.endsWith(".properties")) {
                    countLines += countLinesInPropertiesFile(root);
                    countLinesAtCommon += countLinesInPropertiesFile(root);
                }
            }
        }
    }

    private static long countLinesInPropertiesFile(File file) {
        long countLines = 0;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line = "";
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (!"".equals(line)) {
                    if (!line.startsWith("#")) {
                        countLines++;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return countLines;
    }

    private static long countLinesInXMLFile(File file) {
        long countLines = 0;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line = "";
            boolean inBlockComment = false;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (!"".equals(line)) {
                    if (line.startsWith("<!--")) {
                        inBlockComment = true;
                    }
                    if (line.endsWith("-->")) {
                        inBlockComment = false;
                        continue;
                    }
                    if (!inBlockComment) {
                        countLines++;
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return countLines;
    }

    private static long countLinesInJavaFile(File file) {
        long countLines = 0;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line = "";
            boolean inBlockComment = false;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (!"".equals(line)) {
                    if (!line.startsWith("//")) {
                        if (line.startsWith("/*")) {
                            inBlockComment = true;
                        }
                        if (line.endsWith("*/")) {
                            inBlockComment = false;
                            continue;
                        }
                        if (!inBlockComment) {
                            countLines++;
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return countLines;
    }
}
