public class EncryptedBinaryTree {
    public static void main(String[] args) {

        int[] bt1 = {-2, -2, -1, -2, -1};
        System.out.println(findElement(bt1, 1));

        int[] bt2 = {-2, -2, -2, -2, -2, -2, -2};
        System.out.println(findElement(bt2, 4));
        System.out.println(findElement(bt2, 7));
        System.out.println(findElement(bt2, 99));
    }

    public static boolean findElement(int[] bt, int t) {
        if (bt == null || bt.length == 0) return false;
        // Start DFS from root (index 0) with unencrypted value 1
        return dfs(bt, 0, 1, t);
    }

    private static boolean dfs(int[] bt, int idx, int val, int target) {
        // Out-of-bounds or empty node
        if (idx >= bt.length || bt[idx] == -1) return false;

        // bt[idx] == -2 means this is a valid (non-empty) node
        if (val == target) return true;

        int leftIdx = 2 * idx + 1;
        int rightIdx = 2 * idx + 2;

        boolean foundLeft = dfs(bt, leftIdx, 3 * val + 1, target);
        boolean foundRight = dfs(bt, rightIdx, 2 * val + 5, target);

        return foundLeft || foundRight;
    }
}
