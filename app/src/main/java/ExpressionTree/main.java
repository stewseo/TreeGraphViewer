package ExpressionTree;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.util.AVLTree;
import org.jgrapht.util.SupplierUtil;

import java.util.Random;
import java.util.Stack;

public class main {
    public static void main(String[] args) {
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
    }
}
