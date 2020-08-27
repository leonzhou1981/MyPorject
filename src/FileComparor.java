import java.io.File;

public class FileComparor extends AbstractFileHelper {

    private File baseFileRoot;
    private File compareFileRoot;
    private boolean exchangeFlag = false;

    public FileComparor() {

    }

    @Override
    protected boolean isSpecifiedFormat(File aFile) {
        return (!aFile.getName().endsWith(".log"))
            &&
            /*(aFile.getName().endsWith(".sh")
                || aFile.getName().endsWith(".bat")
                || aFile.getName().endsWith(".jar")
                || aFile.getName().endsWith(".ear")
                || aFile.getName().endsWith(".war")
                || aFile.getName().endsWith(".xml")
                || aFile.getName().endsWith(".properties")
            )
            && */
            (
            !(
                aFile.getAbsolutePath().contains("\\tmp\\vfs\\")
                    || aFile.getAbsolutePath().contains("\\smartclient\\")
                    || aFile.getAbsolutePath().contains("\\standalone\\userhomes\\")
                    || aFile.getAbsolutePath().contains("\\standalone\\userhome\\")
                    || aFile.getAbsolutePath().contains("\\standalone\\log\\")
                    || aFile.getAbsolutePath().contains("\\configuration\\standalone_xml_history\\")));
    }

    @Override
    protected void doSpecifiedThingsForFile(File aFile) {
        File baseFile = aFile;
        String baseFilePath = aFile.getAbsolutePath();
        String compareFilePath = baseFilePath.replace(baseFileRoot.getAbsolutePath(), compareFileRoot.getAbsolutePath());
        File compareFile = new File(compareFilePath);
        if (compareFile.exists()) {
            if (!exchangeFlag) {
                if (compareFile.length() != baseFile.length()) {
                    System.out.println("File " + compareFilePath + " has been modified...");
                } else {
                    String compareFile_md5 = MD5Util.getMd5ByFile(compareFile);
                    String baseFile_md5 = MD5Util.getMd5ByFile(baseFile);
                    if (!compareFile_md5.equals(baseFile_md5)) {
                        System.out.println("File " + compareFilePath + " has been modified...");
                    }
                }
            }
        } else {
            if (!exchangeFlag) {
                System.out.println("File " + compareFilePath + " has been removed...");
            } else {
                System.out.println("File " + baseFilePath + " has been added...");
            }
        }
    }

    public static void main(String[] args) {
        FileComparor fileComparor = new FileComparor();
        fileComparor.baseFileRoot = fileComparor.openSelectDir();
        System.out.println("Select Base Dir as: " + fileComparor.baseFileRoot);
        fileComparor.compareFileRoot = fileComparor.openSelectDir();
        System.out.println("Select Compare Dir as: " + fileComparor.compareFileRoot);
        fileComparor.iterateDirectoryAndFiles(fileComparor.baseFileRoot);
        //exchange roots for newly-added files
        File tempRoot = fileComparor.baseFileRoot;
        fileComparor.baseFileRoot = fileComparor.compareFileRoot;
        fileComparor.compareFileRoot = tempRoot;
        fileComparor.exchangeFlag = true;
        System.out.println("--------------------------------------------------------");
        fileComparor.iterateDirectoryAndFiles(fileComparor.baseFileRoot);
    }
}
