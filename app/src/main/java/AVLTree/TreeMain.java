package AVLTree;
import org.checkerframework.checker.units.qual.A;
import  org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.jgrapht.traverse.LexBreadthFirstIterator;

import org.jgrapht.traverse.BreadthFirstIterator;

import java.util.Arrays;
import java.util.Iterator;

public class TreeMain {

    public static void main(String[] args) {
        AVLTreeUtil avlUtil = new AVLTreeUtil();
        AVLTree<Integer> tree = new AVLTree<Integer>();
        AVLTreeTest treeTest = new AVLTreeTest();
        treeTest.testOneNode();


    }

}
