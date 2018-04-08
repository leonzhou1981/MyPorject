import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 16-9-22
 * Time: 下午1:40
 */
public class MD5Checksum {

    private static byte[] createChecksum(String filename) {
        InputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead = -1;

            while ((numRead = fis.read(buffer)) != -1) {
                complete.update(buffer, 0, numRead);
            }
            return complete.digest();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5Checksum(String filename) {

        if (!new File(filename).isFile()) {
            System.out.println("Error: " + filename
                    + " is not a valid file.");
            return null;
        }
        byte[] b = createChecksum(filename);
        if (null == b) {
            System.out.println("Error:create md5 string failure!");
            return null;
        }
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < b.length; i++) {
            result.append(Integer.toString((b[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return result.toString();

    }

    public static void main(String args[]) {
        try {
            long beforeTime = System.currentTimeMillis();
            String path = "C:\\Users\\user\\Desktop\\work_shedule.txt";
            String before = "999E42920C54CF7D66190731CD54F0E6".toLowerCase();
            String md5 = getMD5Checksum(path);
            System.out.println(md5);
            System.out.println(md5.equals(before));

            File file = new File(path);

            System.out.println(path + "'s size is : " + file.length() + " bytes, it consumes " + (System.currentTimeMillis() - beforeTime) + " ms.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
