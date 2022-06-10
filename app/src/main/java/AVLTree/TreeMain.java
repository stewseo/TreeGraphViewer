package AVLTree;
import org.checkerframework.checker.units.qual.A;
import  org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.jgrapht.traverse.LexBreadthFirstIterator;

import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreeMain {
    private static final Random RANDOM = new Random(17L);
    public static void main(String[] args) {
        AVLTreeUtil avlUtil = new AVLTreeUtil();
        AVLTree<Integer> tree = new AVLTree<Integer>();
        AVLTreeTest treeTest = new AVLTreeTest();
//        treeTest.testOneNode();
//        treeTest.testAddMin();
//        System.out.println("test add max ");
//        treeTest.testAddMax();
//        System.out.println("test add merge after ");
//        treeTest.testMergeAfter();
//        treeTest.testMergeBefore();
//        treeTest.testSplitAfter();
        treeTest.testInsert();
//        List<AVLTree.AVLNode<Integer>> nodeList = fillNodes(tree, 0, 100);
//        assert nodeList.size() == 10;
//        assert tree.getSize() == nodeList.size();

//        System.out.println(tree.getRoot().getValue());
//        System.out.println("left " + tree.getRoot().getLeft().getValue());
//        System.out.println("right " + tree.getRoot().getLeft().getValue());
//        System.out.println("left left " + tree.getRoot().getLeft().getLeft().getValue());
//        System.out.println("left right " + tree.getRoot().getLeft().getRight().getValue());
//        System.out.println("right right " + tree.getRoot().getRight().getRight().getValue());
//        System.out.println("right left " + tree.getRoot().getRight().getLeft().getValue());
//        System.out.println("left left left " + tree.getRoot().getLeft().getLeft().getLeft().getValue());
//        System.out.println("right right right " + tree.getRoot().getRight().getRight().getRight().getValue());
//        System.out.println("right right left " + tree.getRoot().getRight().getRight().getLeft().getValue());

//        AVLTree.AVLNode<Integer> testNode = tree.getRoot();
//        while(testNode.getPredecessor() != null){
//            System.out.println("current node: " + testNode + "\npredecessor: " + testNode.getPredecessor());
//            testNode = testNode.getPredecessor();
//        }
//
//        AVLTree.AVLNode<Integer> testSuccessor = tree.getRoot();
//        while(testSuccessor.getSuccessor() != null){
//            System.out.println("current node: " + testSuccessor  + "\nsuccessor:" + testSuccessor.getSuccessor());
//            testSuccessor = testSuccessor.getSuccessor();
//        }

        Iterator<AVLTree.AVLNode<Integer>> it = tree.nodeIterator();
        assert it != null;
        int count = 1;
        while(it.hasNext()){
            AVLTree.AVLNode<Integer> current = it.next();
            if(count == 1){
                System.out.println("expected min: 47 actual: " + tree.getMin().getValue());
            }
            if(count == tree.getSize()){
                System.out.println("expected max: 56 actual: " + tree.getMax().getValue());
            }
            if(count++ > tree.getSize()){
                System.out.println(" Iteration: " + count + " nodes in tree: " + tree.getSize());
            }
            System.out.println(current);
        }

//        treeTest.testSplitBefore();

    }
    private static List<AVLTree.AVLNode<Integer>> fillNodes(AVLTree<Integer> tree, int from, int to) {
        Deque<AVLTree.AVLNode<Integer>> nodes = new ArrayDeque<>();
        int middle = (from + to) / 2;
        //create value range
        Deque<Integer> minValues = IntStream.range(from, middle).boxed().collect(Collectors.toCollection(ArrayDeque::new));
        Deque<Integer> maxValues = IntStream.range(middle, to).boxed().collect(Collectors.toCollection(ArrayDeque::new));
        for (int i = from; i < to; i+=10) {
            //add min or max value to the tree and array list
            int rand = RANDOM.nextInt(2);
            if ((rand == 0 && !minValues.isEmpty()) || maxValues.isEmpty()) {
                nodes.addFirst(tree.addMin(minValues.removeLast()));
                assert Objects.equals(nodes.peekFirst(), tree.getMin());
            } else {
                nodes.addLast(tree.addMax(maxValues.removeFirst()));
                assert Objects.equals(nodes.peekLast(), tree.getMax());
            }
        }
        return new ArrayList<>(nodes);
    }
}
