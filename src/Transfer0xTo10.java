import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 15-8-4
 * Time: 下午5:53
 */
public class Transfer0xTo10 {

    private final static List UNICODE_RANGE = new ArrayList();

    static {
//        UNICODE_RANGE.add("8531,8578");
//        UNICODE_RANGE.add("8592,8607");
//        UNICODE_RANGE.add("9312,9455");
//        UNICODE_RANGE.add("9728,9747");
//        UNICODE_RANGE.add("9754,9839");
//        UNICODE_RANGE.add("12832,12867");
//        UNICODE_RANGE.add("12928,13003");
//        UNICODE_RANGE.add("13008,13174");
//        UNICODE_RANGE.add("13179,13277");
//        UNICODE_RANGE.add("13280,13310");

        UNICODE_RANGE.add("12288,12288");
    }

    public static void main(String[] args) {
        printSpecialWords();
    }

    private static void transfer16To10(long number) {
        System.out.println(number);
    }

    private static void printSpecialWords() {
        for (Iterator it = UNICODE_RANGE.iterator(); it.hasNext();) {
            String pair = (String) it.next();
            int pos = pair.indexOf(",");
            int sInt1 = Integer.parseInt(pair.substring(0, pos));
            int sInt2 = Integer.parseInt(pair.substring(pos + 1, pair.length()));
            for (int sInt = sInt1; sInt <= sInt2; sInt++) {
                char sWord = (char) sInt;
                System.out.print(sWord);
            }
            System.out.println();
        }
    }
}
