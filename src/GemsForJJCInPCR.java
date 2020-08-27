public class GemsForJJCInPCR {

    public static void main(String[] args) {
        if (args != null) {
            int position = 6666;
            int totGems = 0;
            if (position > 15000) {
                totGems = (15001 - 12001) / 100 * 45
                    + ((12001 - 8001) / 100) * 95 + (8001 - 8000) * 95 + (8000 - 4001) * 1
                    + (4001 - 2001) * 3 + (2001 - 1001) * 5 + (1001 - 501) * 7 + (501 - 201) * 13
                    + (201 - 101) * 35 + (101 - 11) * 60 + (11 - 1) * 550;
            } else if (position > 12000) {
                totGems = (position - 12001) / 100 * 45
                    + ((12001 - 8001) / 100) * 95 + (8001 - 8000) * 95 + (8000 - 4001) * 1
                    + (4001 - 2001) * 3 + (2001 - 1001) * 5 + (1001 - 501) * 7 + (501 - 201) * 13
                    + (201 - 101) * 35 + (101 - 11) * 60 + (11 - 1) * 550;
            } else if (position > 8000) {
                totGems = ((position - 8001) / 100) * 95 + (8001 - 8000) * 95 + (8000 - 4001) * 1
                    + (4001 - 2001) * 3 + (2001 - 1001) * 5 + (1001 - 501) * 7 + (501 - 201) * 13
                    + (201 - 101) * 35 + (101 - 11) * 60 + (11 - 1) * 550;
            } else if (position > 4000) {
                totGems = (position - 4001) * 1
                    + (4001 - 2001) * 3 + (2001 - 1001) * 5 + (1001 - 501) * 7 + (501 - 201) * 13
                    + (201 - 101) * 35 + (101 - 11) * 60 + (11 - 1) * 550;
            } else if (position > 2000) {
                totGems = (position - 2001) * 3 + (2001 - 1001) * 5 + (1001 - 501) * 7 + (501 - 201) * 13
                    + (201 - 101) * 35 + (101 - 11) * 60 + (11 - 1) * 550;
            } else if (position > 1000) {
                totGems = (position - 1001) * 5 + (1001 - 501) * 7 + (501 - 201) * 13
                    + (201 - 101) * 35 + (101 - 11) * 60 + (11 - 1) * 550;
            } else if (position > 500) {
                totGems = (position - 501) * 7 + (501 - 201) * 13
                    + (201 - 101) * 35 + (101 - 11) * 60 + (11 - 1) * 550;
            } else if (position > 200) {
                totGems = (position - 201) * 13
                    + (201 - 101) * 35 + (101 - 11) * 60 + (11 - 1) * 550;
            } else if (position > 100) {
                totGems = (position - 101) * 35 + (101 - 11) * 60 + (11 - 1) * 550;
            } else if (position > 10) {
                totGems = (position - 11) * 60 + (11 - 1) * 550;
            } else if (position > 0) {
                totGems = (position - 1) * 550;
            }
            System.out.println("The position is " + position + ", left " + totGems + " gems.");
        }
    }

}
