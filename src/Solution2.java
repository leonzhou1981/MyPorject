/**
 * Created with IntelliJ IDEA.
 * User: Leon
 * Date: 17-4-26
 * Time: 下午2:19
 */
public class Solution2 {

    public String solution(int[] A) {
        String result = "";
        if (A != null && A.length > 0) {
            long total = sum(A, 0, A.length);
            sort(A);
            if (total == 0) {
                result = printTricoloringInOneColor(A);
            } else if  (total % 3 == 0) {
                result = printTricoloringInThreeColor(A);
            } else {
                result = "impossible";
            }
        } else {
            result = "impossible";
        }
        return result;
    }

    private int[] sort(int[] A) {
        for (int i = 0; i < A.length - 1; i++) {
            for (int j = 1; j < A.length; j++) {
                if (A[i] > A[j]) {
                    int temp = A[j];
                    A[j] = A[i];
                    A[i] = temp;
                }
            }
        }
        return A;
    }

    private String printTricoloringInThreeColor(int[] A) {
        long rTotal = 0;
        long gTotal = 0;
        long bTotal = 0;
        String result = "";
        for (int i = 0; i < A.length; i++) {
            rTotal = sum(A, 0, i);
            gTotal = sum(A, i, A.length);
            if (rTotal * 2 == gTotal) {
                for (int j = i; i < A.length; j++) {
                    gTotal = sum(A, i, j);
                    bTotal = sum(A, j, A.length);
                    if (gTotal == bTotal) {
                        result = printTricoloringInThreeColor(A.length, i, j);
                    }
                }
            }
        }
        if ("".equals(result)) {
            result = "impossible";
        }
        return result;
    }

    private String printTricoloringInThreeColor(int length, int i, int j) {
        StringBuffer sb = new StringBuffer();
        for (int k = 0; k < length; k++) {
            if (k < i) {
                sb.append("R");
            } else if (k >= i && k < j) {
                sb.append("G");
            } else {
                sb.append("B");
            }
        }
        return sb.toString();
    }

    private String printTricoloringInTwoColor(int[] A) {
        long rTotal = 0;
        long gTotal = 0;
        String result = "";
        for (int i = 0; i < A.length; i++) {
            rTotal = sum(A, 0, i);
            gTotal = sum(A, i, A.length);
            if (rTotal == gTotal) {
                result = printTricoloringInTwoColor(A.length, i);
            }
        }
        if ("".equals(result)) {
            result = "impossible";
        }
        return result;
    }

    private String printTricoloringInTwoColor(int length, int i) {
        StringBuffer sb = new StringBuffer();
        for (int k = 0; k < length; k++) {
            if (k < i) {
                sb.append("R");
            } else {
                sb.append("G");
            }
        }
        return sb.toString();
    }

    private String printTricoloringInOneColor(int[] A) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < A.length; i++) {
            sb.append("R");
        }
        return sb.toString();
    }

    private long sum(int[] A, int start, int end) {
        long sumOfArray = 0;
        if (A != null && A.length > 0) {
            for (int i = start; i < end; i++) {
                sumOfArray += A[i];
            }
        } 
        return sumOfArray;
    }

    public static void main(String[] args) {
        int[] A = new int[] {3, 6, 9};
        Solution2 solution = new Solution2();
        System.out.println(solution.solution(A));

    }

}
