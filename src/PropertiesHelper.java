import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHelper {

    public static String getProperty(String propertiesFileName, String propertyName) {
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(propertiesFileName);
            properties.load(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties.getProperty(propertyName);
    }
}
