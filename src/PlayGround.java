import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 16-2-5
 * Time: 下午2:10
 */
public class PlayGround {

    public static void main(String[] args) {
        StringBuffer sb = new StringBuffer("ABC");
        List list = new ArrayList();
        list.add(sb);
        sb = new StringBuffer("");
        System.out.println(list.get(0));

        Pattern p_date = Pattern.compile("^--[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}$");
        if (p_date.matcher("--2015/12/09").matches()) {
            System.out.println("Matched");
        } else {
            System.out.println("Failed");
        }

    }
}
