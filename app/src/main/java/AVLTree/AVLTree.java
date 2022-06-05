package AVLTree;

import BinaryTree.BinarySearchTree;
import edu.uci.ics.jung.samples.TreeCollapseDemo;
import view.AVLTreeInsert_Collapse_View;

import java.util.ArrayList;
import java.util.List;


public class AVLTree<T extends Comparable<T>> extends BinarySearchTree<T> {

    public enum Balance {
        LEFT_LEFT, LEFT_RIGHT, RIGHT_LEFT, RIGHT_RIGHT
    }

    static AVLTreeInsert_Collapse_View treemain;

    public AVLTree() {
            TreeCollapseDemo treemain = new TreeCollapseDemo();
            this.creator = new INodeCreator<T>() {

                @Override
                public Node<T> createNewNode(Node<T> parent, T id) {
                    return (new AVLNode<T>(parent, id));
                }
            };
    }

    public AVLTree(INodeCreator<T> creator) {
            super(creator);
    }

    @Override
    public Node<T> addValue(T id) {
        Node<T> nodeToReturn = super.addValue(id);

        AVLNode<T> nodeAdded = (AVLNode<T>) nodeToReturn;

        nodeAdded.updateHeight();

        balanceAfterInsert(nodeAdded);

        nodeAdded = (AVLNode<T>) nodeAdded.parent;
        while (nodeAdded != null) {
            int h1 = nodeAdded.height;

            nodeAdded.updateHeight();
            balanceAfterInsert(nodeAdded);

            // If height before and after balance is the same, stop going up the tree
            int h2 = nodeAdded.height;
            if (h1 == h2)
                break;

            nodeAdded = (AVLNode<T>) nodeAdded.parent;
        }
        if(root == null){
            root = nodeToReturn;
        }
        return nodeToReturn;
    }

    public AVLNode<T> getMax(){return (AVLNode<T>) super.getGreatest(root);}
    public AVLNode<T> getMin(){return (AVLNode<T>) super.getLeast(root);}

    private int balanceFactor;
    private Balance balance;

    private void balanceAfterInsert(AVLNode<T> node) {
        balanceFactor = node.getBalanceFactor();

        if (balanceFactor > 1 || balanceFactor < -1) {
            AVLNode<T> child = null;
            balance = null;

            StringBuilder balanceAfterInsert = new StringBuilder(" Balance Factor is over threshhold of -1,0,1");
            if (balanceFactor < 0) {

                child = (AVLNode<T>) node.lesser;
                balanceFactor = child.getBalanceFactor();

                if (balanceFactor < 0) {
                    balance = Balance.LEFT_LEFT;
                    balanceAfterInsert.append(balance).append(" ");
                }
                else {
                    balance = Balance.LEFT_RIGHT;
                    balanceAfterInsert.append(balance).append(" ");
                }
            } else {
                child = (AVLNode<T>) node.greater;
                balanceFactor = child.getBalanceFactor();
                if (balanceFactor < 0) {
                    balanceAfterInsert.append(balance).append(" greater child must be balanced");
                    balance = Balance.RIGHT_LEFT;
                }
                else {
                    balanceAfterInsert.append(balance).append(" greater child must be balanced");
                    balance = Balance.RIGHT_RIGHT;
                }
            }
            if (balance == Balance.LEFT_RIGHT) {
                rotateLeft(child);
                rotateRight(node);
            } else if (balance == Balance.RIGHT_LEFT) {
                rotateRight(child);
                rotateLeft(node);
            } else if (balance == Balance.LEFT_LEFT) {
                rotateRight(node);
            } else {
                rotateLeft(node);
            }
            child.updateHeight();
            node.updateHeight();
        }
    }
    public int getBalanceFactor(){
        return balanceFactor;
    }

    public Balance getBalance(){

        return balance;
    }

    @Override
    protected Node<T> removeValue(T value) {
            // Find node to remove
            Node<T> nodeToRemoved = this.getNode(value);
            if (nodeToRemoved==null)
                return null;

            // Find the replacement node
            Node<T> replacementNode = this.getSuccessor(nodeToRemoved);

            // Find the parent of the replacement node to re-factor the height/balance of the tree
            AVLNode<T> nodeToRefactor = null;
            if (replacementNode != null)
                nodeToRefactor = (AVLNode<T>) replacementNode.parent;
            if (nodeToRefactor == null)
                nodeToRefactor = (AVLNode<T>) nodeToRemoved.parent;
            if (nodeToRefactor != null && nodeToRefactor == nodeToRemoved)
                nodeToRefactor = (AVLNode<T>) replacementNode;

            // Replace the node
            replaceNodeWithNode(nodeToRemoved, replacementNode);

            // Re-balance the tree all the way up the tree
            while (nodeToRefactor != null) {
                nodeToRefactor.updateHeight();
                balanceAfterDelete(nodeToRefactor);

                nodeToRefactor = (AVLNode<T>) nodeToRefactor.parent;
            }

            return nodeToRemoved;
        }


        private void balanceAfterDelete(AVLNode<T> node) {
            int b = node.getBalanceFactor();
            if (b == -2 || b == 2) {
                if (b == -2) {
                    AVLNode<T> ll = (AVLNode<T>) node.lesser.lesser;
                    int lesser = (ll != null) ? ll.height : 0;
                    AVLNode<T> lr = (AVLNode<T>) node.lesser.greater;
                    int greater = (lr != null) ? lr.height : 0;
                    if (lesser >= greater) {
                        rotateRight(node);
                        node.updateHeight();
                        if (node.parent != null)
                            ((AVLNode<T>) node.parent).updateHeight();
                    } else {
                        rotateLeft(node.lesser);
                        rotateRight(node);

                        AVLNode<T> p = (AVLNode<T>) node.parent;
                        if (p.lesser != null)
                            ((AVLNode<T>) p.lesser).updateHeight();
                        if (p.greater != null)
                            ((AVLNode<T>) p.greater).updateHeight();
                        p.updateHeight();
                    }
                } else if (b == 2) {
                    AVLNode<T> rr = (AVLNode<T>) node.greater.greater;
                    int greater = (rr != null) ? rr.height : 0;
                    AVLNode<T> rl = (AVLNode<T>) node.greater.lesser;
                    int lesser = (rl != null) ? rl.height : 0;
                    if (greater >= lesser) {
                        rotateLeft(node);
                        node.updateHeight();
                        if (node.parent != null)
                            ((AVLNode<T>) node.parent).updateHeight();
                    } else {
                        rotateRight(node.greater);
                        rotateLeft(node);

                        AVLNode<T> p = (AVLNode<T>) node.parent;
                        if (p.lesser != null)
                            ((AVLNode<T>) p.lesser).updateHeight();
                        if (p.greater != null)
                            ((AVLNode<T>) p.greater).updateHeight();
                        p.updateHeight();
                    }
                }
            }
        }


        @Override
        protected boolean validateNode(Node<T> node) {
            boolean bst = super.validateNode(node);
            if (!bst) {
                return false;
            }
            AVLNode<T> avlNode = (AVLNode<T>) node;
            int balanceFactor = avlNode.getBalanceFactor();
            if (balanceFactor > 1 || balanceFactor < -1) {
//                System.out.println("balanceFactor False");
                return false;
            }
            if (avlNode.isLeaf()) {
                if (avlNode.height != 1) {
                    return false;
                }
            } else {
                AVLNode<T> avlNodeLesser = (AVLNode<T>) avlNode.lesser;
                int lesserHeight = 1;
                if (avlNodeLesser != null)
                    lesserHeight = avlNodeLesser.height;

                AVLNode<T> avlNodeGreater = (AVLNode<T>) avlNode.greater;
                int greaterHeight = 1;
                if (avlNodeGreater != null)
                    greaterHeight = avlNodeGreater.height;

                if (avlNode.height == (lesserHeight + 1) || avlNode.height == (greaterHeight + 1)) {
                    return true;
                }
                return false;
            }

            return true;
        }


        @Override
        public String toString() {
            return AVLTreePrinter.getString(this);
        }

        public static class AVLNode<T extends Comparable<T>> extends Node<T> {

            protected int height = 1;

            public T getValue(){
                return super.id;
            }

            public AVLNode(Node<T> parent, T value) {
                super(parent, value);
            }
            protected boolean isLeaf() {
                return ((lesser == null) && (greater == null));
            }

            StringBuilder sbUpdateHeight;

            protected int updateHeight() {
                int lesserHeight = 0;
                sbUpdateHeight = new StringBuilder();
                if (lesser != null) {
                    AVLNode<T> lesserAVLNode = (AVLNode<T>) lesser;
                    sbUpdateHeight.append(lesserAVLNode.height).append(" ");
                    lesserHeight = lesserAVLNode.height;
                }
                int greaterHeight = 0;
                if (greater != null) {
                    AVLNode<T> greaterAVLNode = (AVLNode<T>) greater;
                    greaterHeight = greaterAVLNode.height;
                    sbUpdateHeight.append(greater.id.toString()).append(" ").append(greaterHeight);
                }

                if (lesserHeight > greaterHeight) {
                    height = lesserHeight + 1;
                } else {
                    height = greaterHeight + 1;
                }
                return height;
            }
            StringBuilder sbBalanceFactor;
            protected int getBalanceFactor() {
                sbBalanceFactor = new StringBuilder();

                int lesserHeight = 0;
                if (lesser != null) {
                    sbBalanceFactor.append(lesser.id).append(" ").append(lesserHeight).append(" ");
                    AVLNode<T> lesserAVLNode = (AVLNode<T>) lesser;
                    lesserHeight = lesserAVLNode.height;
                }
                int greaterHeight = 0;
                if (greater != null) {

                    AVLNode<T> greaterAVLNode = (AVLNode<T>) greater;
                    sbBalanceFactor.append(greater.id).append(" ").append(greaterHeight);
                    greaterHeight = greaterAVLNode.height;
                }
                return greaterHeight - lesserHeight;
            }
            public int getHeight(){
                return height;
            }

            @Override
            public String toString() {
                return "value=" + id + " height=" + height + " parent=" + ((parent != null) ? parent.id : "NULL")
                        + " lesser=" + ((lesser != null) ? lesser.id : "NULL") + " greater="
                        + ((greater != null) ? greater.id : "NULL");
            }
        }

        protected static class AVLTreePrinter {

            public static <T extends Comparable<T>> String getString(AVLTree<T> tree) {
                if (tree.root == null)
                    return "Tree has no nodes.";
                return getString((AVLNode<T>) tree.root, "", true);
            }

            public static <T extends Comparable<T>> String getString(AVLNode<T> node) {
                if (node == null)
                    return "Sub-tree has no nodes.";
                return getString(node, "", true);
            }

            private static <T extends Comparable<T>> String getString(AVLNode<T> node, String prefix, boolean isTail) {
                StringBuilder builder = new StringBuilder();

                builder.append(prefix + (isTail ? "└── " : "├── ") + "(" + node.height + ") " + node.id + "\n");
                List<Node<T>> children = null;
                if (node.lesser != null || node.greater != null) {
                    children = new ArrayList<Node<T>>(2);
                    if (node.lesser != null)
                        children.add(node.lesser);
                    if (node.greater != null)
                        children.add(node.greater);
                }
                if (children != null) {
                    for (int i = 0; i < children.size() - 1; i++) {
                        builder.append(getString((AVLNode<T>) children.get(i), prefix + (isTail ? "    " : "│   "), false));
                    }
                    if (children.size() >= 1) {
                        builder.append(getString((AVLNode<T>) children.get(children.size() - 1), prefix + (isTail ? "    " : "│   "), true));
                    }
                }

                return builder.toString();
            }
        }
    }

