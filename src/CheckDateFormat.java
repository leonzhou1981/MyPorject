import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 16-3-1
 * Time: 上午10:31
 */
public class CheckDateFormat {

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");
        System.out.println(new Date());
        System.out.println(sdf.format(new Date()));
//        GregorianCalendar gc = new GregorianCalendar();
//        gc.setTime(new Date());
//        gc.set(5, -1);//minus one day
//        System.out.println(gc.getTime());
//        System.out.println(sdf.format(gc.getTime()));
//        System.out.println(gc.isLeapYear(2016));
    }

}
