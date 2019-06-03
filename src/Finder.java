import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Finder {

    public static void main(String[] args) {
        String projectPath = "D:\\WEB\\TRUNK\\tmff-web";
        List<String> lstResult = new ArrayList<>();
        System.out.println("------------------Begin------------------");
        FileUtil.iterateAllFilesUnderOneDirectory(projectPath, new IFileAction() {
            @Override
            public void doFileProcess(File file) {
                String filePath = file.getAbsolutePath();
                if (filePath.toLowerCase().contains(File.separator + "server" + File.separator)
                    || filePath.toLowerCase().contains(File.separator + "business" + File.separator)
                    || filePath.contains(File.separator + "Swings-Runtime" + File.separator)
                    || filePath.contains(File.separator + "smarty-client-mngr" + File.separator)) {
                    return;
                }
                if (file.getName().endsWith(".java") || file.getName().endsWith(".xml")) {
                    try {
                        List<String> lstContent = FileUtil.readTextFile(file);
                        boolean printFileName = false;
                        for (String line : lstContent) {
                            if (line.contains(" BusinessAction ")) {
                                break;
                            }

//                            if (line != null && (line.contains(" static ") || line.contains(" getInstance"))
                            if (line != null && (line.contains(" getInstance"))
                                && line.toLowerCase().contains("cache")
                                && !line.contains(" final ")
                                && !line.contains("KFFServerCacheMap")
                                && !line.contains("KFFCacheMap")
                                && !line.contains("KFFClientCacheMap")
                                && !line.contains("ALSLogger")
                                && !line.contains("//")
                                //&& ((line.contains("(") && line.contains("=")) || (!line.contains("(") && !line.contains("=")))
                            ) {
                                if (!printFileName) {
                                    lstResult.add(filePath);
                                    printFileName = true;
                                }
                                lstResult.add(line);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void doDirectoryProcess(File file) {

            }

            @Override
            public void doProcess(File file) {

            }
        });
        System.out.println("-------------------End-------------------");
        try {
            FileUtil.writeTextFile(new File(projectPath + File.separator + "result.txt"), lstResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
