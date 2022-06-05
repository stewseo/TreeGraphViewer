package AVLTree;

import org.jgrapht.util.AVLTree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class
AVLTreeTest {
    public AVLTreeTest(){}

        private static final Random RANDOM = new Random(17L);


        public void testEmpty() {
            AVLTree<Integer> tree = new AVLTree<>();

            System.out.println(tree.getSize());
            System.out.println(tree.getRoot());
        }


        public void testOneNode() {
            AVLTree<Integer> tree = new AVLTree<>();
            AVLTree.TreeNode<Integer> node = tree.addMax(1);

            System.out.println((int) node.getValue());
            System.out.println(tree.getRoot());
            System.out.println(node.getRoot());

            System.out.println(node.getTreeMax());
            System.out.println(node.getTreeMin());

            System.out.println(tree.getMin());
            System.out.println(tree.getMax());

            System.out.println(tree.getSize());
        }


        public void testAddMax() {
            final int testNum = 50;
            for (int nodeNum = 0; nodeNum < testNum; nodeNum++) {
                AVLTree<Integer> tree = new AVLTree<>();
                for (int i = 0; i < nodeNum; i++) {
                    tree.addMax(i);
                    diagnostic(tree);
                }

                System.out.println(tree.getSize());
            }
        }


        public void testAddMin() {
            final int testNum = 50;
            for (int nodeNum = 0; nodeNum < testNum; nodeNum++) {
                AVLTree<Integer> tree = new AVLTree<>();
                for (int i = 0; i < nodeNum; i++) {
                    tree.addMin(i);
                    diagnostic(tree);
                }

                System.out.println(tree.getSize());
            }
        }


        public void testMergeAfter() {
            for (int leftSize = 23; leftSize < 25; leftSize++) {
                for (int rightSize = 23; rightSize < 25; rightSize++) {
                    AVLTree<Integer> left = new AVLTree<>();
                    AVLTree<Integer> right = new AVLTree<>();

                    fillNodes(left, 0, leftSize);
                    fillNodes(right, leftSize, leftSize + rightSize);
                    //Append the nodes in the tree after the nodes in this tree.
                    //The result of this operation is stored in this tree.
                    System.out.println("before merge left subtree size: " + left.getSize());
                    System.out.println("before merge right subtree size: " + right.getSize());
                    System.out.println("Left subtree: " + left);
                    System.out.println("Right subtree: " + right);
                    left.mergeAfter(right);

                    testTreeValueRange(left, 0, leftSize + rightSize);
                    diagnostic(left);
                    System.out.println("After merge size: " + left.getSize());
                    System.out.println("Tree Diagnostics: " + left);

                }
            }
        }


        public void testMergeBefore() {
            for (int leftSize = 0; leftSize < 50; leftSize++) {
                for (int rightSize = 0; rightSize < 50; rightSize++) {
                    AVLTree<Integer> left = new AVLTree<>();
                    AVLTree<Integer> right = new AVLTree<>();

                    fillNodes(left, 0, leftSize);
                    fillNodes(right, leftSize, leftSize + rightSize);

                    right.mergeBefore(left);

                    testTreeValueRange(right, 0, leftSize + rightSize);
                    diagnostic(right);
                }
            }
        }


        public void testSplitAfter() {
            for (int treeSize = 1; treeSize < 50; treeSize++) {
                for (int split = 0; split < treeSize; split++) {
                    AVLTree<Integer> tree = new AVLTree<>();
                    List<AVLTree.TreeNode<Integer>> nodes = fillNodes(tree, 0, treeSize);

                    AVLTree.TreeNode<Integer> splitNode = nodes.get(split);

                    AVLTree<Integer> right = tree.splitAfter(splitNode);

                    testTreeValueRange(tree, 0, split + 1);
                    testTreeValueRange(right, split + 1, treeSize);

                    diagnostic(tree);
                    diagnostic(right);
                }
            }
        }


        public void testSplitBefore() {


            for (int treeSize = 23; treeSize < 25; treeSize++) {
                for (int split = 0; split < treeSize; split++) {
                    AVLTree<Integer> tree = new AVLTree<>();
                    List<AVLTree.TreeNode<Integer>> nodes = fillNodes(tree, 0, treeSize);
                    AVLTree.TreeNode<Integer> splitNode = nodes.get(split);
                    System.out.println("Testing AVL tree splitBefore with node value: " + splitNode);
                    AVLTree<Integer> right = tree.splitBefore(splitNode);

                    testTreeValueRange(tree, 0, split);
                    testTreeValueRange(right, split, treeSize);
                    System.out.println("Nodes that were smaller, that will stay in this tree: " + tree);

                    System.out.println("New Tree with nodes greater than: " + right);
                }
            }
        }


        public void testIterator() {
            System.out.println("Testing Iterator: \n");
            for (int treeSize = 1; treeSize < 30; treeSize++) {
                AVLTree<Integer> tree = new AVLTree<>();

                List<AVLTree.TreeNode<Integer>> nodes = fillNodes(tree, 0, treeSize);

                Iterator<AVLTree.TreeNode<Integer>> iterator = tree.nodeIterator();

                for (AVLTree.TreeNode<Integer> expected : nodes) {
                    System.out.println("iterator.hasNext() " + iterator.hasNext());
                    AVLTree.TreeNode<Integer> actual = iterator.next();
                    System.out.println("Expected node: " + expected + "\nActual node: " + actual);
                }
                System.out.println("expected: false. Actual: " + iterator.hasNext());
            }
        }

        private void testTreeValueRange(AVLTree<Integer> tree, int from, int to) {
            Iterator<AVLTree.TreeNode<Integer>> it = tree.nodeIterator();
            for (int i = from; i < to; i++) {
                AVLTree.TreeNode<Integer> node = it.next();
            }
        }

        private List<AVLTree.TreeNode<Integer>> fillNodes(AVLTree<Integer> tree, int from, int to) {
            Deque<AVLTree.TreeNode<Integer>> nodes = new ArrayDeque<>();
            int middle = (from + to) / 2;
            Deque<Integer> minValues =
                    IntStream.range(from, middle).boxed().collect(Collectors.toCollection(ArrayDeque::new));
            Deque<Integer> maxValues =
                    IntStream.range(middle, to).boxed().collect(Collectors.toCollection(ArrayDeque::new));
            for (int i = from; i < to; i++) {
                int rand = RANDOM.nextInt(2);

                if ((rand == 0 && !minValues.isEmpty()) || maxValues.isEmpty()) {
                    nodes.addFirst(tree.addMin(minValues.removeLast()));
                } else {
                    nodes.addLast(tree.addMax(maxValues.removeFirst()));
                }
                diagnostic(tree);
            }
            return new ArrayList<>(nodes);
        }

        void diagnostic(AVLTree<Integer> tree) {
            AVLTree.TreeNode<Integer> root = tree.getRoot();
            if (root != null) {
                AVLTree.TreeNode<Integer> virtualRoot = root.getParent();
                diagnostic(virtualRoot.getLeft());
            }
        }

        DiagnosticInfo diagnostic(AVLTree.TreeNode<Integer> node) {
            if (node == null) {
                return new DiagnosticInfo(null, null);
            }
            DiagnosticInfo leftInfo = diagnostic(node.getLeft());
            DiagnosticInfo rightInfo = diagnostic(node.getRight());

            if (node.getLeft() == null) {
//                System.out.println("left == null. Subtree Min: " + node.getSubtreeMin());
            } else {
//                System.out.println("Parent: " + node.getLeft().getParent());
//
//                System.out.println("subtree min: " + node.getSubtreeMin() + " left subtree min: " + leftInfo.subtreeMin);
//                System.out.println("Predecessor: "+ node.getPredecessor() + " " + leftInfo.subtreeMax);
//                System.out.println("Successor: " + leftInfo.subtreeMax.getSuccessor() + " " + node);
            }

            if (node.getRight() == null) {
//                assert node.getSubtreeMax() == node;
            } else {
//                System.out.println("Parent: " + node.getRight().getParent() + " " + node);
//                System.out.println("Subtree Max: " + node.getSubtreeMax() + " " + rightInfo.subtreeMax);
//                System.out.println("Successor: " + node.getSuccessor() + " " +  rightInfo.subtreeMin);
//                System.out.println("Predecessor: " + rightInfo.subtreeMin.getPredecessor() + " " +  node);
            }

            return new DiagnosticInfo(node.getSubtreeMin(), node.getSubtreeMax());
        }

        private static class DiagnosticInfo {
            AVLTree.TreeNode<Integer> subtreeMin;
            AVLTree.TreeNode<Integer> subtreeMax;

            public DiagnosticInfo(AVLTree.TreeNode<Integer> subtreeMin, AVLTree.TreeNode<Integer> subtreeMax) {
                this.subtreeMin = subtreeMin;
                this.subtreeMax = subtreeMax;
            }
        }

    }

