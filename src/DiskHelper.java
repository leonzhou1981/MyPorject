import java.io.File;
import java.util.Map;
import java.util.TreeMap;

public class DiskHelper {

    public static void main(String[] args) {
        getHdInfo("D:\\changecollector");
    }

    public static void getHdInfo(String dir) {
        File fdir = new File(dir);
        System.out.println(Math.ceil(fdir.getFreeSpace()/1024/1024/1024));
    }

    public static Map<String, String> getHdInfo() {

        Map map = new TreeMap<String, String>();

        File[] roots = File.listRoots();
        double unit = Math.pow(1024, 3);

        for (int i = 0; i < roots.length; i++) {

            String hd = roots[i].getPath();

            double freespace = roots[i].getFreeSpace() / unit;

            freespace = Math.ceil((freespace * 10)) / 10;

            map.put(hd, String.valueOf(freespace));
        }

        return map;
    }

}
