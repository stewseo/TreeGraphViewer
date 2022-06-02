package LCA_and_ShortestPath.app;

public class BinaryTree {
    static class Node {
        int data;
        Node left, right;

        public Node(int item) {
            data = item;
            left = right = null;
        }
    }

        Node root;

    Node findLCA(int n1, int n2) {
            return findLCA(root, n1, n2);
        }

        Node findLCA(Node node, int n1, int n2) {
            // Base case
            if (node == null) {
                return null;
            }

            if (node.data == n1 || node.data == n2) {
                return node;
            }


            Node left_lca = findLCA(node.left, n1, n2);
            Node right_lca = findLCA(node.right, n1, n2);


            if (left_lca != null && right_lca != null)
                return node;

            return (left_lca != null) ? left_lca : right_lca;
        }
    static boolean v1 = false, v2 = false;


    Node findLCAUtil(Node node, int n1, int n2) {
        // Base case
        if (node == null)
            return null;

        Node temp = null;

        if (node.data == n1) {
            v1 = true;
            temp = node;
        }
        if (node.data == n2) {
            v2 = true;
            temp = node;
        }
        Node left_lca = findLCAUtil(node.left, n1, n2);
        Node right_lca = findLCAUtil(node.right, n1, n2);

        if (temp != null) {
            return temp;
        }

        if (left_lca != null && right_lca != null)
            return node;

        return (left_lca != null) ? left_lca : right_lca;
    }

    Node findLCABoolean(int n1, int n2) {
        // Initialize n1 and n2 as not visited
        v1 = false;
        v2 = false;


        Node lca = findLCAUtil(root, n1, n2);

        if (v1 && v2)
            return lca;

        return null;
    }

}
