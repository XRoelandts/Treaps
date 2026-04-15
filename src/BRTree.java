import java.util.ArrayList;
import java.util.List;

public class BRTree {
    public static void main(String[] args) {
        int[] keys = {10, 19, 20, 30, 42, 55, 77};

        // Test 1: inRange(15, 20) → {10, 30, 42, 55, 77}
        BRTree t1 = new BRTree();
        for (int k : keys) t1.insert(k);
        t1.inRange(15, 20);
        System.out.println("After inRange(15,20): " + t1.inOrder());

        // Test 2: inRange(0, 2) → {10,19,20,30,42,55,77}
        BRTree t2 = new BRTree();
        for (int k : keys) t2.insert(k);
        t2.inRange(0, 2);
        System.out.println("After inRange(0,2):   " + t2.inOrder());

        // Test 3: inRange(25, 60) → {10, 19, 20, 77}
        BRTree t3 = new BRTree();
        for (int k : keys) t3.insert(k);
        t3.inRange(25, 60);
        System.out.println("After inRange(25,60): " + t3.inOrder());
    }


    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static class Node {
        int key;
        boolean color;
        Node left, right, parent;

        Node(int key, boolean color) {
            this.key = key;
            this.color = color;
        }
    }


    private Node NIL;   // sentinel
    private Node root;

    public BRTree() {
        NIL = new Node(0, BLACK);
        NIL.left = NIL;
        NIL.right = NIL;
        NIL.parent = NIL;
        root = NIL;
    }

    public void inRange(int a, int b) {
        // Collect keys in range via in-order traversal (avoids
        // concurrent-modification issues with the tree structure)
        List<Integer> toDelete = new ArrayList<>();
        collectInRange(root, a, b, toDelete);
        for (int key : toDelete) {
            delete(key);
        }
    }

    public void insert(int key) {
        Node z = new Node(key, RED);
        z.left = z.right = z.parent = NIL;

        Node y = NIL, x = root;
        while (x != NIL) {
            y = x;
            x = (z.key < x.key) ? x.left : x.right;
        }
        z.parent = y;
        if (y == NIL) root = z;
        else if (z.key < y.key) y.left = z;
        else y.right = z;

        insertFixup(z);
    }

    public List<Integer> inOrder() {
        List<Integer> out = new ArrayList<>();
        inOrderHelper(root, out);
        return out;
    }

    private void collectInRange(Node x, int a, int b, List<Integer> out) {
        if (x == NIL) return;
        if (x.key > a) collectInRange(x.left, a, b, out);
        if (x.key >= a && x.key <= b) out.add(x.key);
        if (x.key < b) collectInRange(x.right, a, b, out);
    }

    private void delete(int key) {
        Node z = search(root, key);
        if (z == NIL) return;
        rbDelete(z);
    }

    private Node search(Node x, int key) {
        while (x != NIL && x.key != key) {
            x = (key < x.key) ? x.left : x.right;
        }
        return x;
    }

    private void rbDelete(Node z) {
        Node y = z;
        Node x;
        boolean yOriginalColor = y.color;

        if (z.left == NIL) {
            x = z.right;
            rbTransplant(z, z.right);
        } else if (z.right == NIL) {
            x = z.left;
            rbTransplant(z, z.left);
        } else {
            y = minimum(z.right);
            yOriginalColor = y.color;
            x = y.right;
            if (y.parent == z) {
                x.parent = y;
            } else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            rbTransplant(z, y);
            y.left = z.left;
            y.left.parent = y;
            y.color = z.color;
        }
        if (yOriginalColor == BLACK) {
            deleteFixup(x);
        }
    }

    private void rbTransplant(Node u, Node v) {
        if (u.parent == NIL) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        v.parent = u.parent;
    }

    private Node minimum(Node x) {
        while (x.left != NIL) x = x.left;
        return x;
    }

    private void deleteFixup(Node x) {
        while (x != root && x.color == BLACK) {
            if (x == x.parent.left) {
                Node w = x.parent.right;
                // Case 1
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                // Case 2
                if (w.left.color == BLACK && w.right.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    // Case 3
                    if (w.right.color == BLACK) {
                        w.left.color = BLACK;
                        w.color = RED;
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    // Case 4
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.right.color = BLACK;
                    leftRotate(x.parent);
                    x = root;
                }
            } else {
                // Mirror cases (right sibling)
                Node w = x.parent.left;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if (w.right.color == BLACK && w.left.color == BLACK) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.left.color == BLACK) {
                        w.right.color = BLACK;
                        w.color = RED;
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    w.left.color = BLACK;
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.color = BLACK;
    }

    private void insertFixup(Node z) {
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                Node y = z.parent.parent.right;
                if (y.color == RED) {                    // Case 1
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {           // Case 2
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = BLACK;       // Case 3
                    z.parent.parent.color = RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                Node y = z.parent.parent.left;
                if (y.color == RED) {
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != NIL) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != NIL) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == NIL) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    private void inOrderHelper(Node x, List<Integer> out) {
        if (x == NIL) return;
        inOrderHelper(x.left, out);
        out.add(x.key);
        inOrderHelper(x.right, out);

    }
}

