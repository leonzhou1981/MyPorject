

public class RegexHelper {

    public static void main(String[] args) {
//        System.out.println("\"\\n\".matches(\"\\n\") = " + "\n".matches("\n"));
        System.out.println("\"\\n\\n\".matches(\"\\n\") = " + "\n\n\n".matches("[\\s\\S]*[\n][\\s\\S]*"));
//        System.out.println("\"\\n\\n\".matches(\"\\n\\n\") = " + "\n\n".matches("\n\n"));
//        System.out.println("\"\\r\\n\".matches(\"\\n\") = " + "\r\n".matches("\n"));
//        System.out.println("\"\\r\\n\".matches(\"\\r\") = " + "\r\n".matches("\r"));
        System.out.println("\n\n".replace("\n","a"));
    }
}
