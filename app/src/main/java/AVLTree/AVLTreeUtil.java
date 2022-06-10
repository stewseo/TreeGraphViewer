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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

        tree.addEdge( "V444", "V222");
        tree.addEdge( "V444", "V888");
        tree.addEdge( "V222", "V111");
        tree.addEdge( "V222", "V333");
        tree.addEdge( "V888", "V666");
        tree.addEdge( "V888", "V991");
        tree.addEdge( "V666", "V555");
        tree.addEdge( "V666", "V777");
        tree.addEdge( "V991", "V999");

        return tree.build();
    }
    public Forest<String, Integer> createVerticesEdges() {
        DelegateForest<String,Integer> graph = new DelegateForest<>();

        AVLTree.AVLNode<String> node = (AVLTree.AVLNode<String>) avlTree.addMin("V11");
        AVLTree.AVLNode<String> node2 = (AVLTree.AVLNode<String>) avlTree.addMin("V22");
        AVLTree.AVLNode<String> node3 = (AVLTree.AVLNode<String>) avlTree.addMin("V33");
        AVLTree.AVLNode<String> node4 = (AVLTree.AVLNode<String>) avlTree.addMin("V44");
        AVLTree.AVLNode<String> node5 = (AVLTree.AVLNode<String>) avlTree.addMin("V55");
        AVLTree.AVLNode<String> node6 = (AVLTree.AVLNode<String>) avlTree.addMin("V66");
        AVLTree.AVLNode<String> node7 = (AVLTree.AVLNode<String>) avlTree.addMin("V77");
        AVLTree.AVLNode<String> node8 = (AVLTree.AVLNode<String>) avlTree.addMin("V88");
        AVLTree.AVLNode<String> node9 = (AVLTree.AVLNode<String>) avlTree.addMin("V99");
        AVLTree.AVLNode<String> node10 = (AVLTree.AVLNode<String>) avlTree.addMin("v100");

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
        int leftLimit = 49, rightLimit = 57, targetStringLength = 3;
        Random random = new Random();
        String suffix = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i > 49 && i <= 57))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return "V".concat(suffix);
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

    private static final Random RANDOM = new Random(17L);

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
        }
        return new ArrayList<>(nodes);
    }
}
