import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Finder {

    public static void main(String[] args) {
        String projectPath = "D:\\WEB\\VAADIN\\tmff-web";
        List<String> lstResult = new ArrayList<>();
        System.out.println("------------------Begin------------------");
        FileUtil.iterateAllFilesUnderOneDirectory(projectPath, new IFileAction() {
            @Override
            public void doFileProcess(File file) {
                String filePath = file.getAbsolutePath();
                if (filePath.toLowerCase().contains(File.separator + "server" + File.separator)
                    || filePath.toLowerCase().contains(File.separator + "business" + File.separator)
                    || filePath.contains(File.separator + "Swings-Runtime" + File.separator)
                    || filePath.contains(File.separator + "smarty-client-mngr" + File.separator)
                    || filePath.contains(File.separator + "workflow" + File.separator)
                    || filePath.contains(File.separator + "wfmonitor" + File.separator)
                    || filePath.contains(File.separator + "wfsetup" + File.separator)) {
                    return;
                }
                if (file.getName().endsWith(".java") && !file.getName().contains("Robot")) {
                    try {
                        List<String> lstContent = FileUtil.readTextFile(file);
                        boolean printFileName = false;
                        boolean inBlockComment = false;
                        int lineCount = 0;
                        for (String line : lstContent) {
                            if (line != null) {
                                lineCount++;
                                if (!inBlockComment) {
                                    if (line.trim().contains("/*")) {
                                        inBlockComment = true;
                                    }
                                } else {
                                    if (line.trim().contains(("*/"))) {
                                        inBlockComment = false;
                                    }
                                }
                                if (!inBlockComment && !line.trim().startsWith("//")) {
                                    if (matchConditions(line)) {
                                        if (!printFileName) {
                                            lstResult.add(filePath);
                                            printFileName = true;
                                        }
                                        lstResult.add(lineCount + " : " + line.trim());
                                    }
                                }
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
            System.out.println("The result file is stored at " + projectPath + File.separator + "result.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean matchConditions(String line) {
        return line.contains("WinHelper.setScreenCenter(");
//        return line.contains(".startup(") && !line.contains("super.startup(");
        /*return (line.contains(" getInstance"))
            && line.toLowerCase().contains("cache")
            && !line.contains(" final ")
            && !line.contains("KFFServerCacheMap")
            && !line.contains("KFFCacheMap")
            && !line.contains("KFFClientCacheMap")
            && !line.contains("ALSLogger")
            && !line.contains("//");*/
    }

}
