import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 17-4-26
 * Time: 下午2:44
 */
public class Solution {

    public int solution(int[] A) {
        int max_len = 1;
        Map<Integer, Integer> mSelect = new HashMap<Integer, Integer>();
        if (A != null && A.length > 1) {
            for (int i = 0; i < A.length; i++) {
                if (mSelect.containsKey(A[i])) {
                    mSelect.put(A[i], mSelect.get(A[i]) + 1);
                } else {
                    mSelect.put(A[i], 1);
                }
                if (mSelect.containsKey(A[i] + 1)) {
                    mSelect.put(A[i] + 1, mSelect.get(A[i] + 1) + 1);
                } else {
                    mSelect.put(A[i] + 1, 1);
                }
//                if (mSelect.containsKey(A[i] - 1)) {
//                    mSelect.put(A[i] - 1, mSelect.get(A[i] - 1) + 1);
//                } else {
//                    mSelect.put(A[i] - 1, 1);
//                }
            }
            if (mSelect.keySet().size() > 0) {
                for (Iterator it = mSelect.keySet().iterator(); it.hasNext();) {
                    Integer key = (Integer) it.next();
                    Integer value = mSelect.get(key);
                    if (value > max_len ) {
                        max_len = value;
                    }
                }
            }
        }
        return max_len;
    }

    public static void main(String[] args) {
        int[] A = new int[] {6,10,6,9,7,8};
        Solution s = new Solution();
        System.out.println(s.solution(A));
    }


//    public String solution(String S) {
//
//        String lineSeparator = System.getProperty("line.separator");
//        if (!S.contains(lineSeparator)) {
//            lineSeparator = "\n";
//        }
//        int music_size = 0;
//        int images_size = 0;
//        int movies_size = 0;
//        int other_size = 0;
//        if (S != null && S.length() > 0) {
//            int i = 0;
//            while (i < S.length()) {
//                String line = "";
//                String fileType = "";
//                int fileSize = 0;
//                if (S.substring(i, S.length() - 1).contains(lineSeparator)) {
//                    int line_end = S.substring(i, S.length() - 1).indexOf(lineSeparator);
//                    line = S.substring(i, line_end + i);
//                    i = i + line_end + 1;
//                } else {
//                    line = S.substring(i, S.length());
//                    i = S.length();
//                }
//                fileType = getFileType(line);
//                fileSize = getFileSize(line);
//                if ("mp3,aac,flac".contains(fileType)) {
//                    music_size += fileSize;
//                } else if ("jpg,bmp,gif".contains(fileType)) {
//                    images_size += fileSize;
//                } else if ("mp4,avi,mkv".contains(fileType)) {
//                    movies_size += fileSize;
//                } else {
//                    other_size += fileSize;
//                }
//            }
//        }
//
//        return "music " + music_size + "b" + lineSeparator
//                + "images " + images_size + "b" + lineSeparator
//                + "movies " + movies_size + "b" + lineSeparator
//                + "other " + other_size + "b";
//    }
//
//    private String getFileType(String line) {
//        String fileType = "";
//        if (line != null && line.length() > 0) {
//            int last_dot_pos = line.lastIndexOf(".");
//            int last_space_pos = line.lastIndexOf(" ");
//            if (last_dot_pos != -1 && last_space_pos != -1) {
//                fileType = line.substring(last_dot_pos + 1, last_space_pos);
//            }
//        }
//        return fileType;
//    }
//
//    private int getFileSize(String line) {
//        int fileSize = 0;
//        if (line != null && line.length() > 0) {
//            int last_space_pos = line.lastIndexOf(" ");
//            if (last_space_pos != -1) {
//                String strfileSize = line.substring(last_space_pos + 1, line.length() - 1); //remove last b
//                fileSize = Integer.valueOf(strfileSize);
//            }
//        }
//        return fileSize;
//    }
//
//    public static void main(String[] args) {
//        String S = "my.song.mp3 11b\n" +
//                "greatSong.flac 1000b\n" +
//                "not3.txt 5b\n" +
//                "video.mp4 200b\n" +
//                "game.exe 100b\n" +
//                "mov!e.mkv 10000b";
//        Solution s = new Solution();
//        System.out.println(s.solution(S));
//    }

//    public int solution(String E, String L) {
//        int cost = 0;
//        int initial_fee = 2;
//        int first_hour_fee = 3;
//        int after_first_hour_fee = 4;
//
//        int startHour = new Integer(E.substring(0, 2));
//        int startMinute = new Integer(E.substring(3));
//        int endHour = new Integer(L.substring(0, 2));
//        int endMinute = new Integer(L.substring(3));
//
//        int totalMinute = (endHour - startHour) * 60 + (endMinute - startMinute);
//        cost = initial_fee + (totalMinute > 0 ? first_hour_fee : 0) + calRestFee(totalMinute, after_first_hour_fee);
//
//        return cost;
//    }
//
//    private int calRestFee(int totalMinute, int after_first_hour_fee) {
//        int restFee = 0;
//        if (totalMinute - 60 > 0) {
//            int restHours = (totalMinute - 60) / 60;
//            if (restHours * 60 == totalMinute - 60) {
//                restFee = after_first_hour_fee * restHours;
//            } else {
//                restFee = after_first_hour_fee * (restHours + 1);
//            }
//        }
//        return restFee;
//    }
//
//    public static void main(String[] args) {
//        Solution s = new Solution();
//        System.out.println(s.solution("11:43", "11:42"));
//    }


//    public boolean solution(int[] A) {
//        boolean result = false;
//
//        int len = A.length;
//        boolean hasDecreasing = false;
//        for (int i = 0; i < len - 1; i++) {
//            for (int j = 1; j < len - i; j++) {
//                //find first decreasing element, swap it with next one by one
//                if (A[i] > A[i + j]) {
//                    hasDecreasing = true;
//                    int temp = A[i];
//                    A[i] = A[i + j];
//                    A[i + j] = temp;
//                    //check if the array is in non-decreasing order
//                    boolean checkFailed = false;
//                    for (int k = 0; k < len - 1; k++) {
//                        if (A[k] > A[k + 1]) {
//                            checkFailed = true;
//                            //if there is still something in decreasing order, swap it back, and continue to try
//                            int temp2 = A[i];
//                            A[i] = A[i + j];
//                            A[i + j] = temp2;
//                            break;
//                        }
//                    }
//                    if (!checkFailed) result = true;  //check finished on the whole array, return true
//                }
//            }
//
//        }
//        if (!hasDecreasing || len == 1) result = true; //in non-decreasing at beginning, or only one element, return true
//
//        return result;
//    }
//
//    public static void main(String[] args) {
//        int[] A = new int[]{1, 3, 11, 5, 7, 8, 9, 3,22};
//        Solution s = new Solution();
//        System.out.println(s.solution(A));
//    }

}
