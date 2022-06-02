package LCA_and_ShortestPath.app;

import java.util.Stack;
import java.util.stream.IntStream;

/*
Binary expression where the leaves are operands, including constants or variable names.
Root nodes are operators "+", "-", "/", "*", "^"
 */
public class ExpressionTree<T> {
    Node<T> root;

    public ExpressionTree() {
    }

    public ExpressionTree(Node<T> root) {
        this.root = root;
    }


    //Nodes of the expression tree
    static class Node<T>  {
        T data;
        //The operator or operand value
        char ch;
        //left and right children of a node
        Node<T> left, right;


        //constructor for a leaf node of the tree
        Node(char c) {
            this(c, null, null,null);
        }

        //Constrcutor accept a Type, left child of Type, right child of Type
        public Node(char c, Node<T> l, Node<T> r) {
            this(c, null, l, r);
        }

        //constructor that would be for all instance variables that define this Node.
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
    //method to check if the char is an operator
    public static <T> boolean isOperator(T c) {

        if(c.getClass() == Character.class) {
            char ch = (Character) c;
            return (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '^' || ch == '~' || ch == '|' || ch == '&');
        }
        return false;
    }
    //print the postfix expression
    public void postOrder(Node<T> root) {
        if(root == null){
            return;
        }
        postOrder(root.left);
        postOrder(root.right);
        System.out.print(root.ch);
    }
    //print the infix expression
    public void inorder(Node<T> root) {
        if(root == null){
            return;
        }
        //print parenthesis if the character at the current node is an operator
        if(isOperator(root.ch)){
            System.out.print("(");
        }
        //traverse and print the tree inorder. Which will print the infix expression
        inorder(root.left);
        System.out.print(root.ch);
        inorder(root.right);

        //close parenthesis of current sub equation
        if(isOperator(root.ch)){
            System.out.print(")");
        }
    }
    public Node<T> construct(T postfix) {
        //check that the parameter we are constructing a tree from has content
        if(postfix == null){
            return null;
        }
        //this stack will create tree pointers by popping the most recent operands and binding them as l,r leaf nodes to a root operator
        Stack<Node<T>> stack = new Stack<>();

        //Using generics for Model oop practice, but we need access to class String's toCharArray method
        if(postfix.getClass() == String.class) {
            String s = (String) postfix;

            if(s.length() == 0) {
                return null;
            }
            //length of the parameter expression string
            int n = s.length();

            //iterate each char postfix expression String
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
                    //push leaf node containining operand to the stack
                    stack.push(new Node<T>(postFixCharArray[i]));
                }
            });
            return stack.peek();
        };
        return null;
    }
}

