package AVLTree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class
AVLTreeTest {

    public AVLTreeTest(){}

        private static final Random RANDOM = new Random(17L);


        public void testEmpty() {
            AVLTree<Integer> tree = new AVLTree<Integer>();
            System.out.println(tree.getRoot());
        }


        public void testOneNode() {
            AVLTree<Integer> tree = new AVLTree<Integer>();
            AVLTree.AVLNode<Integer> node =
                    tree.addMax(66);

            System.out.println((int) node.getValue());
            System.out.println(tree.getRoot());

            System.out.println(node.getTreeMax());
            System.out.println(node.getTreeMin());

            System.out.println(tree.getRoot().getTreeMax());
            System.out.println(tree.getRoot().getTreeMin());

//            System.out.println(tree.get());
        }

        public void testAddMax() {
            final int testNum = 50;
            for (int nodeNum = 0; nodeNum < testNum; nodeNum++) {
                AVLTree<Integer> tree = new AVLTree<Integer>();
                for (int i = 0; i < nodeNum; i++) {
                    tree.addMax(i);
                    diagnostic(tree);
                }

            }
        }

        public void testAddMin() {
            final int testNum = 50;
            for (int nodeNum = 0; nodeNum < testNum; nodeNum++) {
                AVLTree<Integer> tree = new AVLTree<Integer>();
                for (int i = 0; i < nodeNum; i++) {
                    tree.addMin(i);
                    diagnostic(tree);
                }

            }
        }


        public void testMergeAfter() {
            for (int leftSize = 23; leftSize < 25; leftSize++) {
                for (int rightSize = 23; rightSize < 25; rightSize++) {
                    AVLTree<Integer> left = new AVLTree<Integer>();
                    AVLTree<Integer> right = new AVLTree<Integer>();

                    fillNodes(left, 0, leftSize);
                    fillNodes(right, leftSize, leftSize + rightSize);

                    left.mergeAfter(right);

                    testTreeValueRange(left, 0, leftSize + rightSize);
                    diagnostic(left);

                }
            }
        }

//
        public void testMergeBefore() {
            for (int leftSize = 0; leftSize < 50; leftSize++) {
                for (int rightSize = 0; rightSize < 50; rightSize++) {
                    AVLTree<Integer> left = new AVLTree<Integer>();
                    AVLTree<Integer> right = new AVLTree<Integer>();

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
                    AVLTree<Integer> tree = new AVLTree<Integer>();

                    List<AVLTree.AVLNode<Integer>> nodes = fillNodes(tree, 0, treeSize);

                    AVLTree.AVLNode<Integer> splitNode = nodes.get(split);

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
                    AVLTree<Integer> tree = new AVLTree<Integer>();
                    List<AVLTree.AVLNode<Integer>> nodes = fillNodes(tree, 0, treeSize);
                    AVLTree.AVLNode<Integer> splitNode = nodes.get(split);
                    AVLTree<Integer> left = tree.splitBefore(splitNode);
                    testTreeValueRange(tree, 0, split);
                    testTreeValueRange(left, split, treeSize);

                }
            }
        }


        public void testIterator() {
            for (int treeSize = 1; treeSize < 30; treeSize++) {
                AVLTree<Integer> tree = new AVLTree<Integer>();

                List<AVLTree.AVLNode<Integer>> nodes = fillNodes(tree, 0, treeSize);
                Iterator<AVLTree.AVLNode<Integer>> iterator = tree.nodeIterator();

                for (AVLTree.AVLNode<Integer> expected : nodes) {
                    AVLTree.AVLNode<Integer> actual = iterator.next();
                }
            }
        }

        private void testTreeValueRange(AVLTree<Integer> tree, int from, int to) {
            Iterator<AVLTree.AVLNode<Integer>> it = tree.nodeIterator();
            for (int i = from; i < to; i++) {
                AVLTree.AVLNode<Integer> node = it.next();
            }
        }

        private List<AVLTree.AVLNode<Integer>> fillNodes(AVLTree<Integer> tree, int from, int to) {
            Deque<AVLTree.AVLNode<Integer>> nodes = new ArrayDeque<>();
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
            AVLTree.AVLNode<Integer> root = tree.getRoot();
            if (root != null) {
                AVLTree.AVLNode<Integer> virtualRoot = root.getParent();
                diagnostic(virtualRoot.getLeft());
            }
        }

        DiagnosticInfo diagnostic(AVLTree.AVLNode<Integer> node) {
            if (node == null) {
                return new DiagnosticInfo(null, null);
            }
            DiagnosticInfo leftInfo = diagnostic(node.getLeft());
            DiagnosticInfo rightInfo = diagnostic(node.getRight());

            return null;
//            return new DiagnosticInfo(node.(), node.getSubtreeMax());
        }

        private static class DiagnosticInfo {
            AVLTree.AVLNode<Integer> subtreeMin;
            AVLTree.AVLNode<Integer> subtreeMax;

            public DiagnosticInfo(AVLTree.AVLNode<Integer> subtreeMin, AVLTree.AVLNode<Integer> subtreeMax) {
                this.subtreeMin = subtreeMin;
                this.subtreeMax = subtreeMax;
            }
        }

    }

