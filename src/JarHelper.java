import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.SwingUtilities;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 16-11-17
 * Time: 下午4:49
 */
public class JarHelper {

    private static final String path = "D:\\WEB\\VAADIN\\tmff-web\\build-artifacts";
    private static final String tmpPath = "D:\\WEB\\VAADIN\\tmff-web\\tmp";

    public static void main(String[] args) {
        try {
            //
            JarFile earJar = new JarFile(path + "/tmff-business-app.ear");
            JarEntry jarEntry = earJar.getJarEntry("lib/kewill-kff-framework-configuration.jar");
            InputStream jarInputStream = earJar.getInputStream(jarEntry);

            if (!new File(tmpPath).exists()) {
                new File(tmpPath).mkdirs();
            }
            final String tmpFile = tmpPath + "/kewill-kff-framework-configuration.jar";
            FileOutputStream fos = new FileOutputStream(tmpFile, true);
            int i = 0;
            while ((i = jarInputStream.read()) != -1) {
                fos.write(i);
            }
            fos.flush();
            fos.close();
            earJar.close();

            //
            JarFile configurationJar = new JarFile(tmpFile);
            JarEntry sysconfigEntry = configurationJar.getJarEntry("config/sysconfig.properties");
            InputStream sysconfigInputStream = configurationJar.getInputStream(sysconfigEntry);

            Properties sysconfigProp = new Properties();
            sysconfigProp.load(sysconfigInputStream);
            String als_release = sysconfigProp.getProperty("als_release");
            System.out.println(als_release);

            //
            configurationJar.close();
            Runnable runnable = new Runnable() {
                public void run() {
                    if (new File(tmpFile).exists() && new File(tmpFile).isFile()) {
                        new File(tmpFile).delete();
                    }
                }
            };
            SwingUtilities.invokeLater(runnable);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }


    }
}