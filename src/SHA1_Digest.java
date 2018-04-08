import sun.misc.BASE64Encoder;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 14-11-13
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
public class SHA1_Digest {

    public static void main(String[] args) {
        JFileChooser fileopen = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("Any files", "*");
        fileopen.addChoosableFileFilter(filter);

        int ret = fileopen.showDialog(null, "Open file");

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileopen.getSelectedFile();
            System.out.println(generateSHA1_digest(file));
        }
    }

    private static String generateSHA1_digest(File file) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            InputStream is = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                baos.write(ch);
            }
            byte[] bytes = baos.toByteArray();
            sha1.update(bytes);
            byte[] hash = sha1.digest();
            String result = "";
            for (int i = 0; i < hash.length; i++) {
                int v = hash[i] & 0xFF;
                if (v < 16) result += "0";
                result += Integer.toString(v, 16);
            }
            return new BASE64Encoder().encode(result.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }


}
