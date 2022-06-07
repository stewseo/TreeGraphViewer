package ExpressionTree;

import java.util.Stack;
import java.util.stream.IntStream;

/*
Push operands to a  stack. When a node containing a boolean or algebraic expression is visited,  pop the top 2 items off that stack.
Link the operands as leafs, and subtrees as children.
Notifications and dialogues to replace comments
 */
public class ExpressionTree<T> {
    Node<T> root;

    public ExpressionTree() {
    }

    public ExpressionTree(Node<T> root) {
        this.root = root;
    }


    static class Node<T>  {
        T data;
        char ch;
        Node<T> left, right;

        Node(char c) {
            this(c, null, null,null);
        }

        public Node(char c, Node<T> l, Node<T> r) {
            this(c, null, l, r);
        }

        Node(char c, T data, Node<T> left, Node<T> right) {
            ch = c;
            this.data = data;
            this.left = left;
            this.right = right;
        }
        public Node<T> getLeft(){
            return left;
        }
        public Node<T> getRight(){
            return right;
        }
        public char getChar(){
            return this.ch;
        }
        public Node<T> getRoot() {
            return this;
        }

    }
    public static <T> boolean isOperator(T c) {

        if(c.getClass() == Character.class) {
            char ch = (Character) c;
            return (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '~' || ch == '|' || ch == '&');
        }
        return false;
    }
    public void postOrder(Node<T> root) {
        if(root == null){
            return;
        }
        postOrder(root.left);
        postOrder(root.right);
        System.out.print(root.ch);
    }
    public void inorder(Node<T> root) {
        if(root == null){
            return;
        }
        if(isOperator(root.ch)){
            System.out.print("(");
        }
        inorder(root.left);
        System.out.print(root.ch);
        inorder(root.right);

        if(isOperator(root.ch)){
            System.out.print(")");
        }
    }
    public Node<T> construct(T postfix) {
        if(postfix == null){
            return null;
        }
        Stack<Node<T>> stack = new Stack<>();

        if(postfix.getClass() == String.class) {
            String s = (String) postfix;

            if(s.length() == 0) {
                return null;
            }
            int n = s.length();

            char[] postFixCharArray = s.toCharArray();

            IntStream.range(0, s.length()).forEach(i -> {

                if (isOperator(postFixCharArray[i])) {
                    System.out.println(postFixCharArray[i]);
                    Node<T> r = stack.pop();
                    Node<T> l = stack.pop();
                    Node<T> node = new Node<T>(postFixCharArray[i],null, l, r);
                    stack.push(node);
                }
                else{
                    stack.push(new Node<T>(postFixCharArray[i]));
                }
            });
            return stack.peek();
        };
        return null;
    }
}

