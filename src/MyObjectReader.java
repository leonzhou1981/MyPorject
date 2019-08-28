import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MyObjectReader {

    public static void main(String[] args) {
        File aFile = new File("d:\\object.txt");
        Stu a = new Stu(1, "aa");
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(aFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(a);
            objectOutputStream.flush();
            objectOutputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Stu {

        private int sno;
        private String name;

        public Stu(int sno, String name) {
            this.sno = sno;
            this.name = name;
        }
    }
}
