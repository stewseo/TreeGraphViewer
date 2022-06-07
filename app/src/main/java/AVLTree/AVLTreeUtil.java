package AVLTree;


import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.DelegateTree;
import edu.uci.ics.jung.graph.Forest;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultGraphType;
import org.jgrapht.graph.builder.GraphBuilder;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.SupplierUtil;

import java.util.*;

public class AVLTreeUtil {
    private AVLTree<String> avlTree;

    public AVLTreeUtil() {
        avlTree = new AVLTree<String>();
    }


    public DelegateTree<Number, Number> createNumberedVerticesEdges() {
        DelegateTree<Number, Number> graph = new DelegateTree<>();
        AVLTree.AVLNode<String> node = avlTree.addMax("V11");

        return graph;
    }


    public  Graph<String, Integer> createForest() {
        GraphBuilder<String, Integer, Graph<String, Integer>> tree =
                GraphTypeBuilder.<String, Integer>forGraphType(DefaultGraphType.dag())
                        .edgeSupplier(SupplierUtil.createIntegerSupplier())
                        .buildGraphBuilder();

        tree.addEdge( "V44", "V22");
        tree.addEdge("V44", "V88");
        tree.addEdge( "V22", "V11");
        tree.addEdge("V22", "V33");
        tree.addEdge( "V88", "V66");
        tree.addEdge( "V88", "V99");
        tree.addEdge( "V66", "V55");
        tree.addEdge( "V66", "V77");
        tree.addEdge( "V99", "V100");

        return tree.build();
    }
    public Forest<String, Integer> createVerticesEdges() {
        DelegateForest<String,Integer> graph = new DelegateForest<>();

        AVLTree.AVLNode<String> node = (AVLTree.AVLNode<String>) avlTree.addMin("V11");
        System.out.println("node: " + node.getValue()  + " " + node.toString());

        AVLTree.AVLNode<String> node2 = (AVLTree.AVLNode<String>) avlTree.addMin("V22");
        System.out.println("node2: " + node2.getValue()  + " " + node2.toString());

        AVLTree.AVLNode<String> node3 = (AVLTree.AVLNode<String>) avlTree.addMin("V33");
        System.out.println("node3: " + node3.getValue()  + " " + node3.toString());

        AVLTree.AVLNode<String> node4 = (AVLTree.AVLNode<String>) avlTree.addMin("V44");
        System.out.println("node4: " + node4.getValue()  + " " + node4.toString());
        System.out.println(avlTree.toString());
        AVLTree.AVLNode<String> node5 = (AVLTree.AVLNode<String>) avlTree.addMin("V55");
        System.out.println("node5: " + node5.getValue()  + " " + node5.toString());

        AVLTree.AVLNode<String> node6 = (AVLTree.AVLNode<String>) avlTree.addMin("V66");
        System.out.println("node6: " + node6.getValue()  + " " + node6.toString());

        AVLTree.AVLNode<String> node7 = (AVLTree.AVLNode<String>) avlTree.addMin("V77");
        System.out.println("node7: " + node7.getValue()  + " " + node7.toString());
        AVLTree.AVLNode<String> node8 = (AVLTree.AVLNode<String>) avlTree.addMin("V88");

        AVLTree.AVLNode<String> node9 = (AVLTree.AVLNode<String>) avlTree.addMin("V99");
        System.out.println("node9: " + node9.getValue()  + " " + node9.toString());

        AVLTree.AVLNode<String> node10 = (AVLTree.AVLNode<String>) avlTree.addMin("v100");
        System.out.println("node10: " + node10.getValue()  + " " + node10.toString());

        System.out.println(avlTree.toString());


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
                .filter(i -> (i > 48 && i <= 114))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public AVLTree<String> createTestTree() {

        AVLTree<String> avlTree = new AVLTree<String>();
        avlTree.addMin("V11");
        avlTree.addMin("V22");
        avlTree.addMin("V33");
        avlTree.addMin("V44");
        avlTree.addMin("V55");
        avlTree.addMin("V66");
        avlTree.addMin("V77");
        avlTree.addMin("V88");
        avlTree.addMin("V99");
        avlTree.addMin("v100");
        return avlTree;
    }
}
