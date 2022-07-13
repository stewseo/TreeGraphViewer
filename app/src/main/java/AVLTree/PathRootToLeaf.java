package AVLTree;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PathRootToLeaf {

        public static void printPathToLeaf(AVLTree.TreeNode<Integer> curr, LinkedHashMap<AVLTree.TreeNode<Integer>, AVLTree.TreeNode<Integer>> parent) {

            System.out.println();
        }

        public static void printPathToLeaf(AVLTree.TreeNode<Integer> root) {
            if (root == null) {
                return;
            }
            int count = 0;
            ArrayDeque<AVLTree.TreeNode<Integer>> stack=new ArrayDeque<>();
            stack.addFirst(root);

            LinkedHashMap<AVLTree.TreeNode<Integer>, AVLTree.TreeNode<Integer>> parent = new LinkedHashMap<>();

            parent.put(root,null);

            while (!stack.isEmpty()) {
                AVLTree.TreeNode<Integer> current = stack.pollFirst();

                if (current.left==null && current.right==null) {
                    ArrayDeque<AVLTree.TreeNode<Integer>> s = new ArrayDeque<>() ;
                    AVLTree.TreeNode<Integer> curr = current;
                    while (curr!=null) {
                        s.offerFirst(curr);
                        curr = parent.get(curr);
                    }

                    while (!s.isEmpty()) {
                        curr = s.removeFirst();
                        System.out.print(curr.getValue()+" ");
                    }
                }

                if (current.right!=null) {
                    parent.put(current.right, current);
                    stack.push(current.right);
                }
                if (current.left!=null) {
                    parent.put(current.left, current);
                    stack.push(current.left);
                }
            }
            System.out.println("count: " + count);
        }

        public static void main(String args[]) {
            Node root=new Node(10);
            AVLTree<Integer> tree = new AVLTree<Integer>();
            Deque<Integer> minValues = IntStream.range(1, 512).boxed().collect(Collectors.toCollection(ArrayDeque::new));
//            System.out.println(minValues.size());
            int[] n = new int[]{4,6,5,3,2,1,77,626,88,55,886,553,422,33};
            minValues.forEach(tree::addMax);
//            while(!tree.isEmpty()) {
//                tree.removeMax();
//            }
//            System.out.println(minValues.size());
            AVLTree.TreeNode<Integer> avlRoot = tree.getRoot();
//            root.left = new Node(8);
//            root.right = new Node(2);
//            root.left.left = new Node(3);
//            root.left.right = new Node(5);
//            root.right.left = new Node(2);

//            printPathToLeaf(root);
            printPathToLeaf(avlRoot);
        }
    }

    class Node {
        int data;
        Node left, right;
        Node(int data)
        {
            left=right=null;
            this.data=data;
        }
    };


