package view;

import AVLTree.AVLTree;
import AVLTree.AVLTreeTest;

import javax.swing.*;
import java.awt.*;

public class TreeMain {
    public static void main(String[] args) {
        AVLTreeTest avlTreeTest = new AVLTreeTest();
//        avlTreeTest.testAddMin();
//        avlTreeTest.testAddMax();
//        avlTreeTest.testEmpty();
//        avlTreeTest.testIterator();
        avlTreeTest.testSplitBefore();
//        avlTreeTest.testSplitAfter();
        avlTreeTest.testMergeAfter();
//        avlTreeTest.testOneNode();

        AVLTree<Integer> avltree = new AVLTree<Integer>();
        avltree.addValue(5);

        JFrame frame = new JFrame();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        content.add(new AVLTreeInsert_Collapse_View());
        frame.pack();
        frame.setVisible(true);
    }
}
