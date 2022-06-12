package AVLTree;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
            final int testNum = 700;
            for (int nodeNum = 0; nodeNum < testNum; nodeNum++) {
                AVLTree<Integer> tree = new AVLTree<Integer>();
                for (int i = 0; i < nodeNum; i++) {
                    tree.addMax(i);
                    diagnostic(tree);
                }
                assert nodeNum == tree.getSize();

                if(nodeNum == 699) {
                    System.out.println("passed all diagnostics. tree.getSize: " + tree.getSize() + " tree.max: " + tree.getMax().getValue() + " tree.getRoot.getMax: " + tree.getRoot().getTreeMax().getValue());
                }
            }
        }

        public void testInsert() {
            AVLTree<Integer> tree = new AVLTree<Integer>();
//            System.out.println(" adding 20");
//            tree.addMax(20);
//            printBFSandDFSInorder(tree);
//            System.out.println(" adding 4");
//            tree.addMin(4);
//            printBFSandDFSInorder(tree);


            // insert 15:
            //   20+      20++         20++      15
            //  /        /            /         /  \
            // 4     => 4-     =>   15+     => 4    20
            //           \         /
            //            15      4


            AVLTree<Integer> previousTree = tree;
            //current tree, expected size, expected root value
//            validateSizeAndRoot(tree, 2, 20);
//            System.out.println("\nadding 15");
//            AVLTree.AVLNode<Integer> added = tree.insert(15);
            //prints inorder and level order traversals for tree before and after size change
//            printBFSandDFSInorder(tree);
//            validateSizeAndRoot(tree, 3, 15);
            //current tree, expected root location, expected added value node location, added value
//            validateTraversalOrder(tree, 2, 2, 15);

            // insert 15 (not showing the split merge steps):
            //     20+          20++           20++         9
            //    /  \         /  \           /  \         / \
            //   4    26 =>   4-   26 =>     9+   26 =>   4+  20
            //  / \          / \            / \          /   /  \
            // 3   9        3   9-         4+  15       3  15    26
            //                   \       /
            //                    15    3
//            tree = new AVLTree<Integer>();
//            System.out.print("\n\n Add max 20");
//            tree.addMax(20);
//            System.out.print("\n\n Add max 26");
//            tree.addMax(26);
//            System.out.print("\n\n Add min 9");
//            tree.addMin(9);
//            System.out.print("\n\n Add min 4");
//            tree.addMin(4);
//            System.out.print("\n\n Add min 3");
//            tree.addMin(3);
//
//            previousTree = tree;
//            validateSizeAndRoot(previousTree,5, 20);

//            tree.insert(15);
//            printBFSandDFSInorder(tree, previousTree);
//            validateSizeAndRoot(tree, 6, 9);
//
//            validateTraversalOrder(tree, 3, 4, 15);

            // insert 15 to tree bfs traversal: 20, 4, 26, 3, 9, 21, 30, 2, 7, 11
            // splitBefore(20):
            // current tree:            right reference:
            //
            //         4                   26
            //       /   \               /    \
            //      3      9            21     30
            //     / \      \          /
            //    2   7      11       20

            // addMax(15):           mergeAfter(right):
            //                     before first right rotation:         after first right rotation:       after second right rotation:
            //                    bfs:4,3,20,2,9,26,7,11,21,30,15      bfs:4,3,9,2,7,20,11,26,15,21,30   bfs:9,4,20,3,7,11,26,2,15,21,30
            //
            //         4                  __4++__                           __4+__                      ___9___
            //       /   \               /       \                        /      \                     /       \
            //      3      9    =>      3        20          =>          3        9                   4        20
            //     / \      \          / \         \                    / \        \                 /  \     /   \
            //    2   7      11       2   9         26                 2   7        20              3    7  11     26
            //                 \         / \       /  \                      \        \            /          \   /  \
            //                  15      7   11    21   30                     11       26         2           15 21   30
            //                                \                                 \     /  \
            //                                 15                                15  21   30
//            tree = new AVLTree<Integer>();
//            int[] caseRightDoubleHeavy = new int[]{30,26,21,20,11,9,7,4,3,2};
//            Arrays.stream(caseRightDoubleHeavy).forEach(tree::addMin);
//            //@param tree, expected tree.size, expected tree.getRoot() value
//            validateSizeAndRoot(tree, 10, 20);
//            tree.insert(15);
//            validateSizeAndRoot(tree, 11, 9);
//            //@param tree, inserted value, expected BFS root location, expected BFS added node location
//            validateTraversalOrder(tree, 15, 1, 9);

            //   20+      20++        20++      8
            //  /        /           /         / \
            // 4     => 4-     =>   8+     => 4   20
            //           \         /
            //            8       4
//            tree = new AVLTree<Integer>();
//            int[] twoA = new int[]{20,4};
//            Arrays.stream(twoA).forEach(tree::addMin);
//            validateSizeAndRoot(tree, 2, 20);
//            tree.insert(8);
//            validateSizeAndRoot(tree, 3, 8);

            //bfs 4, 3, 7, 2      right bfs 20, 11, 26, 9, 21, 30
            //        4                    20
            //      /   \                 /  \
            //     3     7               11   26
            //    /                     / \  /  \
            //   2                     9    21   30

            //                      Left Double Heavy
            // addMax(8):           mergeAfter(right):
            //                    bfs:20,9,26,4,11,21,30,3,7,2
            //         4                  __20++__                         __4+__                       ___9___
            //       /   \               /       \                        /      \                     /       \
            //      3      7   =>       9        26          =>          3        9                   4        20
            //     / \      \          / \         \                    / \        \                 /  \     /   \
            //    2   7      8        2   9         26                 2   7        20              3    7  11     26
            //                            / \       /  \                      \        \            /          \   /  \
            //                           7   11    21   30                     11       26         2           15 21   30
            //                                \                                 \      /  \
            //                                 15                                15   21   30

//            tree = new AVLTree<Integer>();
//            int[] caseRightDoubleHeavy = new int[]{30,26,21,20,11,9,7,4,3,2};
//            Arrays.stream(caseRightDoubleHeavy).forEach(tree::addMin);
//            validateSizeAndRoot(tree, 10, 20);
//            tree.insert(8);
//            validateSizeAndRoot(tree, 11, 9);
//            validateTraversalOrder(tree, 15, 1, 9);

            //    2          2            4
            //   / \          \          / \
            //  1   4    =>    4    =>  2   5
            //     / \        / \        \
            //    3   5      3   5        3
//            tree = new AVLTree<Integer>();
//            int[] caseOneDelete = new int[]{1,2,3,4,5};
//            Arrays.stream(caseOneDelete).forEach(tree::addMax);
//            System.out.println("REMOVING");
//            tree.removeMin();

            //bfs 8,4,A,2,6,9,C,1,3,5,7,B,D   bfs 8,4,A,2,6,9,C,3,5,7,B,D  bfs 8,4,A,3,6,9,C,5,7,B,D
            // removeMin() (1)                 removeMin() (2)              removeMin() (3) -> double right heavy
            //         ____8____                   ___8___                 ___8___
            //        /          \                /       \               /       \
            //     __4__        __A__            4         A             4         A
            //    /     \       /    \         /   \      /  \          / \       / \
            //   2       6     9     _C_ =>   2     6    9    C    => 3    6     9   C
            //  / \     / \         /   \      \   /  \      / \          /  \  /   /  \
            // 1   3   5   7       B     D      3 5    7    B   D        5    7    B    D
            //
            // bfs rotateLeft(node)           bfs after rotateLeft      bfs after 2nd rotateLeft
            // 8,4,A,6,9,C,5,7,B,D             8,4,A,5,9,C,B,D          l8,6,A,4,7,9,C,5,B,D
            //         ____8____                   ___8___                 ___8___
            //        /          \                /       \               /       \
            //     __4__        __A__            4         A             6         A
            //          \       /    \             \     /  \           / \       / \
            //           6     9     _C_ =>         5   9    C    =>   4   7     9   C
            //          / \         /   \                   / \           /         /  \
            //         5   7       B     D                 B   D         5         B    D
            AVLTree<String> tree2 = new AVLTree<String>();
            tree2.addMin("1");
            tree2.addMax("D");
            tree2.insert("2");
            tree2.insert("3");
            tree2.insert("4");
            tree2.insert("5");
            tree2.insert("6");
            tree2.insert("7");
            tree2.insert("8");
            tree2.insert("9");
            tree2.insert("A");
            tree2.insert("B");
            tree2.insert("C");

            System.out.println("REMOVING");
            tree2.removeMin();
            tree2.removeMin();
            tree2.removeMin();
        }

    private void validateTraversalOrder(AVLTree<Integer> tree, int rootLocation, int insertedNodeLocation, int insertedValue) {
        AtomicInteger count = new AtomicInteger(1);

        tree.getBFS(tree.getRoot()).forEach(node->{
            if(count.get() == rootLocation) {
                assert node.equals(tree.getRoot().getValue());
            }
            if(count.getAndIncrement() == insertedNodeLocation) {
                assert node.equals(insertedValue);
            }
        });

            System.out.println("inorder root: " + rootLocation);
            System.out.println("inorder added node: " + insertedNodeLocation);
    }

    private void validateSizeAndRoot(AVLTree<Integer> actualTree, int expectedSize, int expectedRootValue) {
        assert actualTree.getSize() == expectedSize;
        assert actualTree.getRoot().getValue() == expectedRootValue;
    }

    private void treeDiagnostics(AVLTree<Integer> tree, int previousSize, int previousRootValue) {
            assert previousSize-1 == tree.getSize();
    }

    private void printBFSandDFSInorder(AVLTree<Integer> tree) {
        System.out.println("\nBFS: "+ tree.getBFS(tree.getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("DFS inorder: " + tree.getDFS(tree.getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    public void testDelete() {
    }

    public void testAddMin() {
            final int testNum = 700;
            for (int nodeNum = testNum; nodeNum >= 0; nodeNum--) {
                AVLTree<Integer> tree = new AVLTree<Integer>();
                for (int i = 0; i < nodeNum; i++) {
                    tree.addMin(i);
                    diagnostic(tree);
                }
                assert nodeNum == tree.getSize();

                if(nodeNum == 1) {
                    System.out.println("tree.getSize: " + tree.getSize() + " tree.getMin: " + tree.getMin().getValue() + "\ntree.getRoot.getMin: " + tree.getRoot().getTreeMin().getValue());
                }
            }
        }


        //TODO: Test Ancestry Predecessor, Successor, Lowest common ancestor, Greatest common ancestor,


        public void testMergeAfter() {
            for (int leftSize = 0; leftSize < 50; leftSize++) {
                for (int rightSize = 0; rightSize < 50; rightSize++) {
                    AVLTree<Integer> left = new AVLTree<Integer>();
                    AVLTree<Integer> right = new AVLTree<Integer>();

                    fillNodes(left, 0, leftSize);
                    fillNodes(right, leftSize, leftSize + rightSize);


                    if(leftSize == 49 && rightSize == 49) {

                        System.out.println("left size before merge: " + left.getSize() + " right size before merge: " + right.getSize());
                        System.out.println("merging after:  " + right.getRoot());
                        left.nodeIterator().forEachRemaining(e->System.out.print(e.getValue() + ", "));

                        left.mergeAfter(right);

                        left.nodeIterator().forEachRemaining(e->System.out.print(e.getValue() + ", "));
                        System.out.println("left size after merge: " + left.getSize() + " right size after merge: " + right.getSize());

                    }
                    else{
                        left.mergeAfter(right);
                    }
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

                    if(leftSize == 49 && rightSize == 49) {
                        System.out.println("left size before merge: " + left.getSize() + " right size before merge: " + right.getSize());
                        System.out.println("merging before:  " + left.getRoot());
                        right.nodeIterator().forEachRemaining(e->System.out.print(e.getValue() + ", "));
                        right.mergeBefore(left);
                        right.nodeIterator().forEachRemaining(e->System.out.print(e.getValue() + ", "));
                        System.out.println("left size after merge: " + left.getSize() + " right size after merge: " + right.getSize());
                    }
                    else{
                        right.mergeBefore(left);

                    }


                    testTreeValueRange(right, 0, leftSize + rightSize);
                    diagnostic(right);
                }
            }
        }


        public void testSplitAfter() {
            for (int treeSize = 1; treeSize < 50; treeSize+=2) {
                for (int split = 0; split < treeSize; split+=2) {
                    AVLTree<Integer> tree = new AVLTree<Integer>();

                    List<AVLTree.AVLNode<Integer>> nodes = fillNodes(tree, 0, treeSize);

                    AVLTree.AVLNode<Integer> splitNode = nodes.get(split);
                    System.out.println("\n\ntree size before split: " + tree.getSize());
                    tree.nodeIterator().forEachRemaining(e->System.out.print(e.getValue() + ", "));

                    AVLTree<Integer> right = tree.splitAfter(splitNode);
                    System.out.println("\n\ntree size after split: " + tree.getSize());
                    tree.nodeIterator().forEachRemaining(e->System.out.print(e.getValue() + ", "));

                    System.out.println("\n\nright size after split: " + right.getSize());
                    right.nodeIterator().forEachRemaining(e->System.out.print(e.getValue() + ", "));


                    testTreeValueRange(tree, 0, split + 1);
                    testTreeValueRange(right, split + 1, treeSize);

                    diagnostic(tree);
                    diagnostic(right);
                }
            }
        }


        public void testSplitBefore() {

            for (int treeSize = 1; treeSize < 50; treeSize++) {
                for (int split = 0; split < treeSize; split++) {
                    AVLTree<Integer> tree = new AVLTree<Integer>();
                    List<AVLTree.AVLNode<Integer>> nodes = fillNodes(tree, 0, treeSize);

                    AVLTree.AVLNode<Integer> splitNode = nodes.get(split);

                    if(treeSize == 49 && split == 41) {

                        System.out.println("\n\ntree size: " + tree.getSize());
                        tree.nodeIterator().forEachRemaining(e -> System.out.print(e.getValue() + ", "));

                        AVLTree<Integer> right = tree.splitBefore(splitNode);
                        System.out.println("\n\ntree.splitBefore: " + tree.getSize());
                        tree.nodeIterator().forEachRemaining(e -> System.out.print(e.getValue() + ", "));

                        System.out.println("\n\nright size: " + right.getSize());
                        right.nodeIterator().forEachRemaining(e -> System.out.print(e.getValue() + ", "));
                    }
                    else {
                        AVLTree<Integer> right = tree.splitBefore(splitNode);


                        testTreeValueRange(tree, 0, split);
                        testTreeValueRange(right, split, treeSize);

                        diagnostic(tree);
                        diagnostic(right);

                    }
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
            if(tree.isEmpty()){
                return;
            }
            assert (to - from) == tree.getSize();

            Iterator<AVLTree.AVLNode<Integer>> it = tree.nodeIterator();
            for (int i = from; i < to; i++) {
                assert it.hasNext();

                AVLTree.AVLNode<Integer> node = it.next();

                assert i == node.getValue();
            }
        }

        private List<AVLTree.AVLNode<Integer>> fillNodes(AVLTree<Integer> tree, int from, int to) {

            Deque<AVLTree.AVLNode<Integer>> nodes = new ArrayDeque<>();
            int middle = (from + to) / 2;

            Deque<Integer> minValues = IntStream.range(from, middle).boxed().collect(Collectors.toCollection(ArrayDeque::new));
            Deque<Integer> maxValues = IntStream.range(middle, to).boxed().collect(Collectors.toCollection(ArrayDeque::new));

            for (int i = from; i < to; i+=2) {
                int rand = RANDOM.nextInt(2);
                if ((rand == 0 && !minValues.isEmpty()) || maxValues.isEmpty()) {

                    nodes.addFirst(tree.addMin(minValues.removeLast()));
                    assert Objects.equals(nodes.peekFirst(), tree.getMin());
                } else {
                    nodes.addLast(tree.addMax(maxValues.removeFirst()));
                    assert Objects.equals(nodes.peekLast(), tree.getMax());
                }
                diagnostic(tree);
            }
            return new ArrayList<>(nodes);
        }

        void diagnostic(AVLTree<Integer> tree) {
            AVLTree.AVLNode<Integer> root = tree.getRoot();
            if (root != null) {
                AVLTree.AVLNode<Integer> virtualRoot = root.getParent();

                assert virtualRoot.getLeft().equals(root);

                diagnostic(virtualRoot.left);
            }
        }

        DiagnosticInfo diagnostic(AVLTree.AVLNode<Integer> node) {

            if (node == null) {
                //subtree min,max = null, height,size = 0
                return new DiagnosticInfo(null, null, 0, 0);
            }
            //case left right subtree diagnostics == or equals parent tree diagnostics
            DiagnosticInfo leftInfo = diagnostic(node.getLeft());
            DiagnosticInfo rightInfo = diagnostic(node.getRight());

            //case subtree height: leftInfo.height + rightInfo.height + 1 is == to node.getHeight()
           assert node.getHeight() == Math.max(leftInfo.height, rightInfo.height) + 1;
            //case subtree size: leftInfo size + rightInfo size + 1 is == to node.getSubTreeSize()
           assert node.getSubtreeSize() == (leftInfo.size + rightInfo.size + 1);
            //case subtree balance: node.getLeftHeight - node.getRightHeight is < 2.
           assert Math.abs(node.getLeftHeight()-node.getRightHeight()) < 2;

           //cases when left child is null
           if (node.getLeft() == null) {
                //case node is subtree min: if node.getLeft returns null, node.getSubtreeMin will equals node
                assert node.getSubtreeMin().equals(node);
            }
            //cases when left child is not null
            else {
                //case parent of left: parent of left equals node
                assert node.getLeft().getParent().equals(node);

                //case subtree min: node subtreeMin and left child subTreeMin will be equals
                assert node.getSubtreeMin().equals(leftInfo.subtreeMin);

                //case node Predecessor/ leftInfo subtreeMax: node.getPredecessor and leftInfo subtreeMax will be equals

                assert node.getPredecessor().equals(leftInfo.subtreeMax);

                //case left subtreeMax successor: leftInfo.subTreeMax.getSuccessor and node will be equals
                assert leftInfo.subtreeMax.getSuccessor().equals(node);
            }
            //cases when right child is null
            if (node.getRight() == null) {
                //case node == subtreeMax: when right == null, node.subtreeMax and node will be equals
                assert node.getSubtreeMax() == node;
            }
            //cases when right child is not null
            else {
                //case node is node.right.parent: node.getRight.getParent and node will be equals
                assert node.getRight().getParent().equals(node);
                //case subtree max: node.getSubtreeMax and rightInfo.subTreeMax will be equals
                assert node.getSubtreeMax().equals(rightInfo.subtreeMax);
                //case successor is rightInfo.subTreeMin: node.getSuccessor and rightInfo.subtreeMin will be equals
                assert node.getSuccessor().equals(rightInfo.subtreeMin);
                //case right.subTreeMin.predecessor: rightInfo.subtreeMin.predecessor and node will be equals
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

