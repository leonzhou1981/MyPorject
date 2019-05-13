import java.util.List;

public class TreeUtil {

    public TreeUtil() {

    }

    //Tree Node
    static class TreeNode {
        String key;
        List<String> lstChild;

        public TreeNode(String key) {
            this.key = key;
        }

        public void addNode(TreeNode node) {


        }
    }

    private TreeNode root;

    public static TreeUtil getInstance() {
        return new TreeUtil();
    }

    private TreeNode getRoot() {
        if (root == null) {
            root = new TreeNode("");
        }
        return root;
    }

    public static void main(String[] args) {
        TreeNode root = TreeUtil.getInstance().getRoot();
        root.addNode(new TreeNode("A"));
    }




}
