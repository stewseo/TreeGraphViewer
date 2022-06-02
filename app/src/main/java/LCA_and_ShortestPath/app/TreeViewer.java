package LCA_and_ShortestPath.app;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.TreeDynamicConnectivity;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.AVLTree;
import org.jgrapht.util.SupplierUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;


@SuppressWarnings("serial")
public class TreeViewer<T extends Comparable<T>> extends JFrame {

        static private int CANVAS_HEIGHT = 600;
        static private int CANVAS_WIDTH = 1000;

        private int rootY = 10;
        private int NODE_SIZE = 25;
        private int ROW_HEIGHT = 50;
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        public Object drawTree(BinaryTree.Node root, int depth, int index) {
            if (root == null) {
                return null;
            }

            // draw root

            /*
             * leftChildIndex = parentIndex * 2 - 1
             * rightChildIndex = parentIndex *2
             * x = index * canvasWidth / (2^depth + 1)
             * y = depth * canvasHeight / treeDepth
             */

            int myX = (int) ((CANVAS_WIDTH * (index)) / (Math.pow(2, depth - 1) + 1));

            Object rootVertex = graph.insertVertex(parent, null, root.data,
                    myX, depth * ROW_HEIGHT + rootY, NODE_SIZE, NODE_SIZE);


            Object rightChildVertex = drawTree(root.right, depth + 1,
                    index * 2);

            if (rightChildVertex != null) {// edge
                graph.insertEdge(parent, null, "R", rootVertex, rightChildVertex,
                        "startArrow=none;endArrow=none;strokeWidth=1;strokeColor=green");
            }

            Object leftChildVertex = drawTree(root.left, depth + 1,
                    index * 2 - 1);

            if (leftChildVertex != null) { // edge
                graph.insertEdge(parent, null, "L", rootVertex, leftChildVertex,
                        "startArrow=none;endArrow=none;strokeWidth=1;strokeColor=green");
            }
            return rootVertex;

        }

        public void update(BinaryTree.Node root) {

            graph.getModel().beginUpdate();

            try {

                Object[] cells = graph.getChildCells(parent, true, false);
                graph.removeCells(cells, true);
                drawTree(root, 1, 1);

            } finally {
                graph.getModel().endUpdate();
            }
        }

        public TreeViewer(BinaryTree.Node root) {

            this.update(root);

            mxGraphComponent graphComponent = new mxGraphComponent(graph);

            getContentPane().add(graphComponent);
        }

        public static void main(String[] args) throws IOException {
            TreeDynamicConnectivity<Character> treeDynamicConnectivity = new TreeDynamicConnectivity<>();

            BinaryTree tree = new BinaryTree();
            tree.root = new BinaryTree.Node(15);
            tree.root.left = new BinaryTree.Node(8);
            tree.root.right = new BinaryTree.Node(30);
            tree.root.left.left = new BinaryTree.Node(5);
            tree.root.left.right = new BinaryTree.Node(12);
            tree.root.left.right.left = new BinaryTree.Node(10);
            tree.root.left.right.right = new BinaryTree.Node(13);
            tree.root.right.left = new BinaryTree.Node(20);
            tree.root.right.right = new BinaryTree.Node(36);
            tree.root.left.left.left = new BinaryTree.Node(3);
            tree.root.left.left.right = new BinaryTree.Node(7);
            tree.root.right.right.left = new BinaryTree.Node(32);
            tree.root.right.right.right = new BinaryTree.Node(39);
            tree.root.left.left.left.left = new BinaryTree.Node(1);
            tree.root.left.left.left.right = new BinaryTree.Node(4);
            tree.root.right.right.right.left = new BinaryTree.Node(37);
            tree.root.right.right.right.right = new BinaryTree.Node(42);

            System.out.println("LCA(1, 42) = " + tree.findLCA(1, 42).data);
            System.out.println("LCA(13, 10) = " + tree.findLCA(13, 10).data);
            System.out.println("LCA(13, 4) = " + tree.findLCA(13, 4).data);
            System.out.println("LCA(2, 4) = " + tree.findLCA(2, 4).data);

            BinaryTree.Node lcaWithBooleanPointers = tree.findLCABoolean(4, 5);
            if (lcaWithBooleanPointers != null)
                System.out.println("LCA(4, 5) = " + lcaWithBooleanPointers.data);
            else
                System.out.println("Keys are not present");

            lcaWithBooleanPointers = tree.findLCABoolean(4, 10);
            if (lcaWithBooleanPointers != null)
                System.out.println("Find LCA(4, 10) = " + lcaWithBooleanPointers.data);
            else
                System.out.println("Keys are not present");


            JFrame frame = new TreeViewer<Integer>(tree.root);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
            frame.setVisible(true);

            String s = "B2^4A*C*S";
            Precedence prec = new Precedence();
            char[] expr2 = {'b','2','^','A','*','C','S'};
            char[] expr = prec.infixToPostfix(s).toCharArray();

            AVLTree<Character> bst = new AVLTree<Character>();//use AVL tree class

            Random randomGenerator = new Random();

            for (int i = 0; i < expr2.length; i++) {
                if(expr2[i] == '^' || expr2[i] == '*') {
                    bst.addMax(expr2[i]);
                }else{
                    bst.addMin(expr2[i]);
                }
            }

            Stack<ExpressionTree.Node<Character>> stack = new Stack<>();

            for (int i = 0; i < expr.length; i++) {
                char c = expr[i];

                if (ExpressionTree.isOperator(c)) {
                    ExpressionTree.Node<Character> r = stack.pop();
                    ExpressionTree.Node<Character> l = stack.pop();
                    ExpressionTree.Node<Character> node = new ExpressionTree.Node<Character>(c,null, l, r);
                    stack.push(node);
                }
                else{
                    stack.push(new ExpressionTree.Node<Character>(c));
                }
            }

            ExpressionTree<Character> ex = new  ExpressionTree<Character>();
            ex.inorder(stack.peek());

            Graph<Character, DefaultWeightedEdge> g = GraphTypeBuilder.undirected().weighted(true).allowingMultipleEdges(false)
                    .allowingSelfLoops(false).vertexSupplier(SupplierUtil.createSupplier(Character.class))
                    .edgeSupplier(SupplierUtil.createDefaultWeightedEdgeSupplier()).buildGraph();


         while(!stack.isEmpty()) {
             ExpressionTree.Node<Character> current = stack.pop();
             while(current != null) {
                 g.addVertex(current.ch);

                 if(current.getLeft()!=null) {
                     g.addVertex(current.getLeft().ch);
                     g.addEdge(current.ch, current.left.ch);
                 }

                 if(current.getRight() != null){
                     g.addVertex(current.getRight().ch);
                     g.addEdge(current.ch, current.right.ch);
                 }

                 if (current.getLeft() == null) {
                     current = current.right;
                 }
                 else {
                     ExpressionTree.Node<Character> pred = current.left;
                     //predecessor
                     while (pred.getRight() != null && pred.getRight() != current) {
                         pred = pred.getRight();
                     }
                     if (pred.getRight() == null) {
                         pred.right = current;
                         current = current.getLeft();
                     } else {
                         pred.right = null;
                         current = current.getRight();
                     }
                 }
             }
            };
            System.out.print(g.vertexSet());

            System.out.print("vertices in graph: "+Arrays.toString(g.vertexSet().toArray()));

            JGraphXAdapter<Character, DefaultWeightedEdge> graphAdapter = new JGraphXAdapter<Character, DefaultWeightedEdge>(g);
            mxIGraphLayout layout = new mxHierarchicalLayout(graphAdapter);

            layout.execute(graphAdapter.getDefaultParent());

            BufferedImage image =
                    mxCellRenderer.createBufferedImage(graphAdapter, null, 2, Color.WHITE, true, null);
            File imgFile = new File("src/test/resources/testTree.png");
            ImageIO.write(image, "PNG", imgFile);


            //Traverses the tree up until the virtual root and splits it into two parts.
            //The algorithm is described in Donald E. Knuth. The art of computer programming. Second Edition. Volume 3 / Sorting and Searching, p. 474.
            //Params:
            //left – a left subtree
            //right – a right subtree
            //p – next parent node
            //leftMove – true if we're moving from the left child, false otherwise.
//            bst.splitAfter()



        }
    }

