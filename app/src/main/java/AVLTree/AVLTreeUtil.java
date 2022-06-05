package AVLTree;

import BinaryTree.BinarySearchTree;
import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import org.checkerframework.checker.units.qual.A;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.*;

public class AVLTreeUtil {
    private AVLTree<String> avlTree;

    public AVLTreeUtil() {
        avlTree = new AVLTree<String>();
    }


    public DelegateTree<Number, Number> createNumberedVerticesEdges() {
        DelegateTree<Number, Number> graph = new DelegateTree<>();
        AVLTree.AVLNode<String> node = (AVLTree.AVLNode<String>) avlTree.addValue("V11");

        return graph;
    }



    public Forest<String, Integer> createVerticesEdges() {
        DelegateForest<String,Integer> graph = new DelegateForest<>();

        AVLTree.AVLNode<String> node = (AVLTree.AVLNode<String>) avlTree.addValue("V11");
        System.out.println("node: " + node.id  + " " + node.toString());

        AVLTree.AVLNode<String> node2 = (AVLTree.AVLNode<String>) avlTree.addValue("V22");
        System.out.println("node2: " + node2.id  + " " + node2.toString());

        AVLTree.AVLNode<String> node3 = (AVLTree.AVLNode<String>) avlTree.addValue("V33");
        System.out.println("node3: " + node3.id  + " " + node3.toString());

        AVLTree.AVLNode<String> node4 = (AVLTree.AVLNode<String>) avlTree.addValue("V44");
        System.out.println("node4: " + node4.id  + " " + node4.toString());
        System.out.println(avlTree.toString());
        AVLTree.AVLNode<String> node5 = (AVLTree.AVLNode<String>) avlTree.addValue("V55");
        System.out.println("node5: " + node5.id  + " " + node5.toString());

        AVLTree.AVLNode<String> node6 = (AVLTree.AVLNode<String>) avlTree.addValue("V66");
        System.out.println("node6: " + node6.id  + " " + node6.toString());

        AVLTree.AVLNode<String> node7 = (AVLTree.AVLNode<String>) avlTree.addValue("V77");
        System.out.println("node7: " + node7.id  + " " + node7.toString());
        AVLTree.AVLNode<String> node8 = (AVLTree.AVLNode<String>) avlTree.addValue("V88");

        AVLTree.AVLNode<String> node9 = (AVLTree.AVLNode<String>) avlTree.addValue("V99");
        System.out.println("node9: " + node9.id  + " " + node9.toString());

        AVLTree.AVLNode<String> node10 = (AVLTree.AVLNode<String>) avlTree.addValue("v100");
        System.out.println("node10: " + node10.id  + " " + node10.toString());

        System.out.println("BFS: " + Arrays.toString(avlTree.getBFS()));
        System.out.println(avlTree.toString());

        System.out.println("DFS Preorder: " + Arrays.toString(avlTree.getDFS(BinarySearchTree.DepthFirstSearchOrder.inOrder)));

        graph.addVertex("V44");
        graph.addEdge("V44V22".hashCode(), "V44", "V22");
        graph.addEdge("V44V88".hashCode(), "V44", "V88");
        graph.addEdge("V22V11".hashCode(), "V22", "V11");
        graph.addEdge("V22V33".hashCode(), "V22", "V33");
        graph.addEdge("V88V66".hashCode(), "V88", "V66");
        graph.addEdge("V88V99".hashCode(), "V88", "V99");
        graph.addEdge("V66V55".hashCode(), "V66", "V55");
        graph.addEdge("V66V77".hashCode(), "V66", "V77");
        graph.addEdge("V99V100".hashCode(), "V99", "V100");
        return graph;
    }

    public String getRandom() {
        int leftLimit = 48, rightLimit = 116, targetStringLength = 3;
        Random random = new Random();
        // Random generator for alpha numeric Strings, limited to 3 characters per String.
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i >= 48 && i <= 110))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public AVLTree<String> createTestTree() {

        AVLTree<String> avlTree = new AVLTree<String>();
        avlTree.addValue("V11");
        avlTree.addValue("V22");
        avlTree.addValue("V33");
        avlTree.addValue("V44");
        avlTree.addValue("V55");
        avlTree.addValue("V66");
        avlTree.addValue("V77");
        avlTree.addValue("V88");
        avlTree.addValue("V99");
        avlTree.addValue("v100");
        return avlTree;
    }
}
