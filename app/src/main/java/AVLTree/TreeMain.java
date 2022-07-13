package AVLTree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TreeMain {
    private static final Random RANDOM = new Random(17L);
    public static void main(String[] args) {
        GraphUtil avlUtil = new GraphUtil();
        AVLTree<Integer> tree = new AVLTree<Integer>();
        AVLTreeTest treeTest = new AVLTreeTest();
        treeTest.testAddMinMax(true);
        treeTest.testAddMinMax(false);
        treeTest.testLeftDoubleHeavyAddRemoveMin();
        treeTest.testInsert();

    }
    private static List<AVLTree.TreeNode<Integer>> fillNodes(AVLTree<Integer> tree, int from, int to) {
        Deque<AVLTree.TreeNode<Integer>> nodes = new ArrayDeque<>();
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
