import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: lzhou
 * Date: Dec 15, 2009
 * Time: 9:51:47 PM
 */
public class StringUtil {

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static List<String> split(String s, String splitor) {
        List<String> result = new ArrayList<String>();
        if (s != null) {
            int pos = s.indexOf(splitor);
            while (pos != -1) {
                result.add(s.substring(0, pos));
                s = s.substring(pos + 1);
                pos = s.indexOf(splitor);
            }
        }
        return result;
    }

    public static String combine(List<String> ss, int maxSize) {
        StringBuffer result = new StringBuffer();
        if (ss != null && ss.size() > 0) {
            for (int i = 0; i < ss.size(); i++) {
                String s = ss.get(i);
                if ((result.length() + s.length()) < maxSize) {
                    result.append(ss.get(i));
                } else {
                    break;
                }
            }
        }
        return result.toString();
    }

    public static String getFirstNumberAsString(String s, int minSize, int maxSize) {
        List<String> numbers = new ArrayList<String>();
        if (s != null) {
            char[] chars = s.toCharArray();
            int phase = 0;
            for (int i = 0; i < chars.length; i++) {
                if (phase == 0) {
                    if (chars[i] > 47 && chars[i] < 58) {
                        numbers.add(0, String.valueOf(chars[i]));
                        phase++;
                    }
                } else if (phase > 0) {
                    if (chars[i] > 47 && chars[i] < 58) {
                        numbers.add(phase, String.valueOf(chars[i]));
                        phase++;
                    } else {
                        break;
                    }
                }
            }
            if (numbers.size() < minSize || numbers.size() > maxSize) {
                numbers = new ArrayList<String>();
            }
        }
        return StringUtil.combine(numbers, 1000);
    }

    public static Integer getFirstNumberAsInteger(String s, int minSize, int maxSize) {
        String strNum = getFirstNumberAsString(s, minSize, maxSize);
        try {
            return Integer.valueOf(strNum);
        } catch (NumberFormatException e) {
            return new Integer(-1);
        }
    }

    public static String pickupWord(String source, String prefix, String suffix) {
        String word = null;
        if (source != null) {
            if (prefix != null && suffix != null) {
                int i_prefix = source.indexOf(prefix);
                int i_suffix = source.lastIndexOf(suffix);
                if (i_prefix < i_suffix) {
                    if (i_prefix > -1 && i_suffix > -1) {
                        word = source.substring(i_prefix + prefix.length(), i_suffix);
                    } else if (i_prefix > -1) {
                        word = source.substring(i_prefix + prefix.length());
                    } else if (i_suffix > -1) {
                        word = source.substring(0, i_suffix);
                    }
                }
            } else if (prefix != null) {
                int i_prefix = source.indexOf(prefix);
                if (i_prefix > -1) {
                    word = source.substring(i_prefix + prefix.length());
                }
            } else if (suffix != null) {
                int i_suffix = source.indexOf(suffix);
                if (i_suffix > -1) {
                    word = source.substring(0, i_suffix);
                }
            }
        }
        return word;
    }

    public static String[] convertListToArray(List<String> lstStr) {
        if (lstStr != null && lstStr.size() > 0) {
            int size = lstStr.size();
            String[] arrayStr = new String[size];
            for (int i = 0; i < lstStr.size(); i++) {
                arrayStr[i] = lstStr.get(i);
            }
            return arrayStr;
        } else {
            return null;
        }
    }

    public static String replaceAll(String source, String to_be_replaced, String replace_as) {
        if (source != null) {
            while (source.indexOf(to_be_replaced) > 0) {
                source = source.substring(0, source.indexOf(to_be_replaced)) + replace_as + source.substring(source.indexOf(to_be_replaced) + to_be_replaced.length());
            }
        }
        return source;
    }

    public static void main(String[] args) {
        System.out.println(pickupWord("ABC", "A", "C"));
        System.out.println(pickupWord("ABC", "AB", null));
        System.out.println(pickupWord("ABC", null, "C"));
    }
}
