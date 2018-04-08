/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 17-4-26
 * Time: 下午2:19
 */
public class Solution1 {

    public int solution(int[] A) {
        if (A != null && A.length > 0) {
            int len = A.length;
            for (int i = 0; i < len; i++) {
                if (sumLeft(A, i) == sumRight(A, i, len)) {
                    return i;
                }
            }
        }
        return -1;
        // write your code in Java SE 8
    }

    private int sumLeft(int[] A, int i) {
        int sum = 0;
        for (int k = 0; k < i; k++) {
            sum += A[k];
        }
        return sum;
    }

    private int sumRight(int[] A, int i, int len) {
        int sum = 0;
        for (int k = len - 1; k > i; k--) {
            sum += A[k];
        }
        return sum;
    }

    public static void main(String[] args) {
        int[] A = new int[]{2, -7, 1, 5, 2, -4, 3, 0,2};
        Solution1 s = new Solution1();
        System.out.println(s.solution(A));
    }
}
