package AVLTree;


import java.util.Arrays;

public class TreeMain {

    public static void main(String[] args) {
        AVLTree<Integer> avlt = new AVLTree<Integer>();
        avlt.addMax(5);
        avlt.addMax(5);
        avlt.addMax(5);
        avlt.addMax(5);
        avlt.addMax(5);
        avlt.addMax(5);
        avlt.addMax(5);
        AVLTreeTest ss = new AVLTreeTest();
        ss.testOneNode();;
        ss.testAddMax();


    }


    public static void testAddMin() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        org.jgrapht.util.AVLTree<Integer> testTree = new org.jgrapht.util.AVLTree<Integer>();
        final int testNum = 50;
        for (int nodeNum = 0; nodeNum < testNum; nodeNum++) {
            for (int i = 0; i < nodeNum; i++) {
                testTree.addMin(i);
                tree.addMin(i);
            }
        }
        testTree.iterator().forEachRemaining(System.out::print);
    }
}
