package AVLTree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AVLTreeTest {

    public AVLTreeTest(){}

        private static final Random RANDOM = new Random(17L);


        public void testEmpty() {
            AVLTree<Integer> tree = new AVLTree<Integer>();
            System.out.println(tree.getRoot());
        }


        public void testOneNode() {
            AVLTree<Integer> tree = new AVLTree<Integer>();
            AVLTree.AVLNode<Integer> node = tree.addMax(1);

            String rootDiagnosticTest = "\nTesting AVLTree getRoot, AVLTree.AVLNode getRoot, getTreeMax, getTreeMin, getHeight, getMin, getMax, getSize\n";
            StringBuilder testResult = new StringBuilder(rootDiagnosticTest);


            rootDiagnosticTest = "Testing value\n";
            String expected = "expected value: 1";
            String actual = " actual value: " + node.getValue() + "\n";
            testResult.append(rootDiagnosticTest).append(expected).append(actual);
            //test getValue  from AVLTree.AVLNode equals 1
            if(node.getValue() != 1){
                testResult.append(".....Failed\n\n");
            }
            else{
                testResult.append(".....Passed\n\n");
            }

            //test getTreeMin from AVLTree.AVLNode equals node
            //test getHeight from AVLTree.AVLNode equals node.getHeight

            rootDiagnosticTest = "Testing virtualRoot.left";
            expected = "expected virtual root left: " + tree.getRoot().getValue();
            actual = " actual virtual root left: " + node.getValue() + "\n";
            testResult.append(rootDiagnosticTest).append(expected).append(actual);

            //test values are equal: virtual root.left, node
            if (!node.equals(tree.getRoot())) {
                testResult.append(".....Failed\n\n");
            }
            else {
                testResult.append(".....Passed\n\n");
            }

            rootDiagnosticTest = "Testing while ((current = parent) != null){ return current.left }\n\n";
            expected = "expected AVLNode getRoot: " + node.getRoot().getValue();
            actual = " actual AVLNode getRoot: " + node.getValue() + "\n";
            testResult.append(rootDiagnosticTest).append(expected).append(actual);
            //test values are equal: AVLTree.AVLNode.getRoot, node
            if (!node.equals(tree.getRoot().getRoot())) {
                testResult.append(".....Failed\n\n");
            }
            else {
                testResult.append(".....Passed\n\n");
            }

            rootDiagnosticTest = "Testing AVLNode treeMax value\n";
            expected = "expected AVLNode treeMax: " + node.getTreeMax().getValue();
            actual = " actual AVLNode treeMax: " + node.getValue() + "\n";
            testResult.append(rootDiagnosticTest).append(expected).append(actual);
            //test values are equal: AVLTree.getTreeMax, node
            if (!node.equals(node.getTreeMax())) {
                testResult.append(".....Failed\n\n");
            } else {
                testResult.append(".....Passed\n\n");
            }

            rootDiagnosticTest = "Testing TreeMin value\n";
            expected = "expected TreeMin value: " + node.getTreeMin().getValue();
            actual = " actual TreeMin: " + node.getValue() + "\n";
            testResult.append(rootDiagnosticTest).append(expected).append(actual);
            //test values are equal: AVLTree.getTreeMin, node
            if (!node.equals(node.getTreeMin())) {
                testResult.append(".....Failed\n\n");
            } else {
                testResult.append(".....Passed\n\n");
            }

            rootDiagnosticTest = "Testing Height\n";
            expected = "expected Height: 1 ";
            actual = " actual Height: " + node.getHeight() + "\n";
            testResult.append(rootDiagnosticTest).append(expected).append(actual);
            //test height is equal: 1, node.height
            if (node.getHeight() != 1) {
                testResult.append(".....Failed\n\n");
            } else {
                testResult.append(".....Passed\n\n");
            }

            rootDiagnosticTest = "Testing min\n";
            expected = "expected min: " + tree.getMin().getValue();
            actual = " actual min: " + node.getValue() + "\n";
            testResult.append(rootDiagnosticTest).append(expected).append(actual);
            //test min is equal: tree.min, node
            if (!tree.getMin().equals(node)) {
                testResult.append(".....Failed\n\n");
            } else {
                testResult.append(".....Passed\n\n");
            }

            rootDiagnosticTest = "Testing max\n";
            expected = "expected max: " + tree.getMax().getValue();
            actual = " actual max: " + node.getValue() + "\n";
            testResult.append(rootDiagnosticTest).append(expected).append(actual);
            //test max is equal: tree.max, node
            if (!tree.getMax().equals(node)) {
                testResult.append(".....Failed\n\n");
            } else {
                testResult.append(".....Passed\n\n");
            }

            rootDiagnosticTest = "Testing size\n";
            expected = "expected size: 1";
            actual = " actual size: " + tree.getSize() + "\n";
            testResult.append(rootDiagnosticTest).append(expected).append(actual);
            //test size is equal: tree.size, node
            if (tree.getSize() != 1) {
                testResult.append(".....Failed\n\n");
            } else {
                testResult.append(".....Passed\n\n");
            }
            System.out.println(testResult.toString());

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
            final int testNum = 5000;
            for (int nodeNum = 0; nodeNum < testNum; nodeNum++) {
                AVLTree<Integer> tree = new AVLTree<Integer>();
                for (int i = 0; i < nodeNum; i++) {
                    tree.addMin(i);
                    diagnostic(tree);
                }
                System.out.println("expected: " + nodeNum +  "actual: " + tree.getSize());
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

            for (int treeSize = 10; treeSize < 12; treeSize++) {
                for (int split = 0; split < treeSize; split++) {
                    AVLTree<Integer> tree = new AVLTree<Integer>();
                    List<AVLTree.AVLNode<Integer>> nodes = fillNodes(tree, 0, treeSize);
                    System.out.println("tree before split: ");
                    System.out.println(tree + " ,");

                    AVLTree.AVLNode<Integer> splitNode = nodes.get(split);
                    AVLTree<Integer> right = tree.splitBefore(splitNode);
                    System.out.println("tree after split: ");
                    System.out.println(tree + " ,");

//                    testTreeValueRange(tree, 0, split);

                    System.out.println("\nright after split: ");
//                    testTreeValueRange(right, split, treeSize);
                    System.out.println(right + " ,");


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
                if(!virtualRoot.getLeft().equals(root)){
                    System.out.println(" FAILED. virtual root is not parent of root: " + root);
                }
                diagnostic(virtualRoot.getLeft());
            }
        }

        DiagnosticInfo diagnostic(AVLTree.AVLNode<Integer> node) {

            if (node == null) {
                return new DiagnosticInfo(null, null, 0, 0);
            }
            DiagnosticInfo leftInfo = diagnostic(node.getLeft());
            DiagnosticInfo rightInfo = diagnostic(node.getRight());

           if(node.getHeight() != Math.max(leftInfo.height, rightInfo.height) + 1) {

           }
           if (node.getSubtreeSize() != leftInfo.size + rightInfo.size + 1) {

           }

            if(Math.abs(node.getLeftHeight()-node.getRightHeight()) > 2) {

            }

            if (node.getLeft() == null) {
                if(node.getSubtreeMin() != node){

                }
            } else {
                if(!node.getLeft().getParent().equals(node)){

                }

                if(node.getSubtreeMin() != leftInfo.subtreeMin) {

                }
                assert node.getPredecessor().equals(leftInfo.subtreeMax);

                assert leftInfo.subtreeMax.getSuccessor().equals(node);
            }

            if (node.getRight() == null) {
                assert node.getSubtreeMax() == node;

            } else {
                assert node.getRight().getParent().equals(node);
                assert node.getSubtreeMax().equals(rightInfo.subtreeMax);
                assert node.getSuccessor().equals(rightInfo.subtreeMin);
                assert rightInfo.subtreeMin.predecessor.equals(node);
            }
            return new DiagnosticInfo(node.getSubtreeMin(), node.getSubtreeMax(), node.getHeight(), node.getSubtreeSize());
        }

        private static class DiagnosticInfo {
            AVLTree.AVLNode<Integer> subtreeMin;
            AVLTree.AVLNode<Integer> subtreeMax;
            int height;
            int size;

            public DiagnosticInfo(AVLTree.AVLNode<Integer> subtreeMin, AVLTree.AVLNode<Integer> subtreeMax, int height, int size) {
                this.subtreeMin = subtreeMin;
                this.subtreeMax = subtreeMax;
                this.height = height;
                this.size = size;
            }
        }

    }

