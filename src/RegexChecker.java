import java.util.regex.Pattern;

public class RegexChecker {

    public static void main(String[] args) {
        String s = "$P{remark}";
        String regex = "\\$(?i)p\\{(?i)remark\\}";
//        System.out.println(Pattern.matches(regex, s));
//        Pattern p = Pattern.compile(regex);
//        String[] pieces = p.split(s);
//        for (int i = 0; i < pieces.length; i++) {
//            System.out.println(pieces[i]);
//        }
        s = s.replaceAll("\\$(?i)p\\{(?i)" + "remark" + "\\}", "REMARK TEST LINE 1 $30K\nREMARK TEST LINE 2 $30K");
        System.out.println(s);
    }

}
