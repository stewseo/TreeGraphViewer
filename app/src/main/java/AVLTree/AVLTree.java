package AVLTree;

import BinaryTree.BinaryNode;
import BinaryTree.BinarySearchTree;
import java.util.*;
import java.util.function.Consumer;

/*
Implementation of JUNG and JGraphT's AVL tree with Module12 BST
AVLTree Author:
 * @Timofey Chudakov
BinarySearchTree Author:
 * @author Frank M. Carrano
 * @author Timothy M. Henry
 */
public class AVLTree<T extends Comparable<? super T>> implements Iterable<T> {

    private AVLNode<T> virtualRoot = new AVLNode<T>(null);

    private int modCount = 0;

    public AVLTree() {
    }

    private AVLTree(AVLNode<T> value) {
       makeRoot(value);
    }

    public AVLNode<T> insert(T value) {
        if(value.compareTo(getMax().getValue()) > 0) {
            return addMax(value);
        }
        if(value.compareTo(getMin().getValue()) < 0 ){
            return addMin(value);
        }
        AVLNode<T> newNode = new AVLNode<T>(value);
        insertNode(newNode);
        return newNode;
    }

    public void insertNode(AVLNode<T> newNode) {
        registerModification();
        if (isEmpty()) {
            virtualRoot.left = newNode;
            newNode.parent = virtualRoot;
        }
        else {
            //traverse inorder and find the node that is greater than newNode
            Stack<AVLTree.AVLNode<T>> stack = new Stack<>();
            AVLTree.AVLNode<T> curr = getRoot();

            while (!stack.empty() || curr != null) {
                if (curr != null) {
                    stack.push(curr);
                    curr = curr.left;
                } else {
                    curr = stack.pop();
                    if (curr.getValue().compareTo(newNode.getValue()) > 0) {
                        AVLTree<T> right = splitBefore(curr);
                        addMax(newNode.getValue());
                        mergeAfter(right);
                        break;
                    }
                    else {
                        curr = curr.right;
                    }
                }
            }
        }
    }
    public AVLNode<T> addMax(T value) {
        AVLNode<T> newMax = new AVLNode<T>(value);
        addMaxNode(newMax);
        return newMax;
    }

    public void addMaxNode(AVLNode<T> newMax) {
        registerModification();

        if (isEmpty()) {
            virtualRoot.left = newMax;
            newMax.parent = virtualRoot;
        } else {
            AVLNode<T> max = getMax();
            max.setRightChild(newMax);
            balance(max);
        }
    }

    public AVLNode<T>addMin(T value) {
        AVLNode<T> newMin = new AVLNode<T>(value);
        addMinNode(newMin);
        return newMin;
    }

    public void addMinNode(AVLNode<T> newMin) {
        registerModification();
        if (isEmpty()) {
            virtualRoot.left = newMin;
            newMin.parent = virtualRoot;
        }
        else {
            AVLNode<T> min = getMin();
            min.setLeftChild(newMin);
            balance(min);
        }
    }

    public AVLTree<T> splitBefore(AVLNode<T> node) {
        registerModification();
        AVLNode<T> predecessor = predecessor(node);

        // node is min, swap
        if (predecessor == null) {
            AVLTree<T> tree = new AVLTree<T>();
            swap(tree);
            System.out.println("node: " + node.getValue() + " predeccsor is null. SWAP with tree. tree: " + tree.getRoot().getValue());
            return tree;
        }
        System.out.println("SENDING PREDECESSOR TO SPLIT AFTER: " + predecessor.getValue());
        return splitAfter(predecessor);
    }

    public AVLTree<T> splitAfter(AVLNode<T> node) {
        registerModification();
        AVLNode<T> parent = node.getParent();
        boolean nextMove = node.isLeftChild();
        AVLNode<T> left = node.getLeft();
        AVLNode<T> right = node.getRight();

        node.parent.substituteChild(node, null);

        node.reset();
        System.out.println("\nSPLITTING AFTER PARAMETER NODE: " + node.getValue());
        if (left != null) {
            System.out.print("Left value: " +left.getValue() + " Before making null, left parent: " + left.parent.getValue());
            left.parent = null;
        }
        if (right != null) {
            System.out.print(" Right value: " +right.getValue() + " Before making null, right parent: " + right.parent.getValue());
            right.parent = null;
        }

        if (left == null) {
            left = node;
            System.out.print("\nPredecessor's left is null. left is now: " + left.getValue() + " ");
        }
        else {

            // insert node as a left subtree max
            AVLNode<T> t = left;
            System.out.print("\nTraversing for Successor-> ");
            while (t.right != null) {
                System.out.print("t: " + t.getValue() + ", ");
                t = t.right;
            }
            System.out.println("Found Successor. Referencing path: " + t.getValue() + ".setRightChild( " + node.getValue() + ")");
            t.setRightChild(node);

            while (t != left) {
                System.out.print("\nBalancing Successor, substituting child of successor's parent ");
                AVLNode<T> p = t.parent;
                System.out.print(" p.substituteChild( " + t.getValue() + ", balanceNode(" +  t.getValue()+ ")");
                p.substituteChild(t, balanceNode(t));
                t = p;
                System.out.println("\nreferencing t after substituting child. t = p = " + p.getValue());
            }
            left = balanceNode(left);
            System.out.println("Balance Node: " + left.getValue());
        }

        T v = null;
        if(right != null) {
            v = right.getValue();
        }
        System.out.println(" Send left, right, parent, nextMove to split( " + left.getValue() +  ", " + v + ", " + parent.getValue() +", "+ nextMove);

        return split(left, right, parent, nextMove);
    }

    private AVLTree<T>split(AVLNode<T> left, AVLNode<T>  right, AVLNode<T>  p, boolean leftMove) {
        System.out.println();
        while (p != virtualRoot) {
            boolean nextMove = p.isLeftChild();
            AVLNode<T> nextP = p.getParent();

            p.getParent().substituteChild(p, null);
            p.parent = null;

            if (leftMove) {
                String rightVal = right == null ? null : right.getValue().toString();
                System.out.print("right before merge: " + rightVal);
//                System.out.println("right = merge(p, right, p.right) p: " + p.getValue() + " right: " + right.getValue() + " p.right: " + rightVal);
                right = merge(p, right, p.right);
                System.out.println(" right after merge: " + right.getValue());
            } else {
                String leftVal = p.left == null ? null : p.left.getValue().toString();
                System.out.print("left before merge: " + leftVal);
//                System.out.println("left = merge(p, p.left, left) p: " + p.getValue() + " p.left:  " + p.left.getValue() + " left: " + leftVal);
                left = merge(p, p.left, left);
                System.out.println(" left after merge: " + left.getValue());
            }
            p = nextP;
            leftMove = nextMove;
        }

        System.out.println("make root " + left.getValue());
        makeRoot(left);

        return new AVLTree<>(right);
    }


    private void makeRoot(AVLNode<T> node) {
        virtualRoot.left = node;
        if (node != null) {
            node.subtreeMax.successor = null;
            node.subtreeMin.predecessor = null;
            node.parent = virtualRoot;
        }
    }


    public void mergeAfter(AVLTree<T> tree) {
        registerModification();
        if (tree.isEmpty()) {
            return;
        }
        else if (tree.getSize() == 1) {
            addMaxNode(tree.removeMin());
            return;
        }

        AVLTree.AVLNode<T> junctionNode = tree.removeMin();
        AVLTree.AVLNode<T> treeRoot = tree.getRoot();
        tree.clear();

        makeRoot(merge(junctionNode, getRoot(), treeRoot));
    }

    public void mergeBefore(AVLTree<T> tree) {
        registerModification();
        tree.mergeAfter(this);
        swap(tree);
    }

    private void swap(AVLTree<T> tree) {
        AVLNode<T> t = virtualRoot.left;
        makeRoot(tree.virtualRoot.left);
        tree.makeRoot(t);
    }

    /**
     * Merges the left and right subtrees using the junctionNode.
     * @param junctionNode a node between left and right subtrees
     * @param left a left subtree
     * @param right a right subtree
     * @return the root of the resulting tree
     */
    private AVLNode<T> merge(AVLNode<T> junctionNode, AVLNode<T> left, AVLNode<T> right) {
        if (left == null && right == null) {
            junctionNode.reset();
            return junctionNode;
        } else if (left == null) {
            right.setLeftChild(merge(junctionNode, left, right.left));
            return balanceNode(right);
        } else if (right == null) {
            left.setRightChild(merge(junctionNode, left.right, right));
            return balanceNode(left);
        } else if (left.getHeight() > right.getHeight() + 1) {
            left.setRightChild(merge(junctionNode, left.right, right));
            return balanceNode(left);
        } else if (right.getHeight() > left.getHeight() + 1) {
            right.setLeftChild(merge(junctionNode, left, right.left));
            return balanceNode(right);
        } else {
            junctionNode.setLeftChild(left);
            junctionNode.setRightChild(right);
            return balanceNode(junctionNode);
        }
    }
    public AVLNode<T> removeMin() {
        registerModification();

        if (isEmpty()) {
            return null;
        }
        AVLTree.AVLNode<T> min = getMin();
        // min.parent != null
        if (min.parent == virtualRoot) {
            makeRoot(min.right);
        } else {
            min.parent.setLeftChild(min.right);
        }

        balance(min.parent);

        return min;
    }


    public AVLNode<T> removeMax() {
        registerModification();
        if (isEmpty()) {
            return null;
        }
        AVLNode<T>  max = getMax();
        if (max.parent == virtualRoot) {
            makeRoot(max.left);
        } else {
            max.parent.setRightChild(max.left);
        }
        balance(max.parent);
        return max;
    }


    private AVLNode<T> rotateRight(AVLNode<T> node) {
        AVLNode<T> left = node.left;
        left.parent = null;

        node.setLeftChild(left.right);
        left.setRightChild(node);

        node.updateHeightAndSubtreeSize();
        left.updateHeightAndSubtreeSize();

        return left;
    }


    private AVLNode<T> rotateLeft(AVLNode<T> node) {
        AVLNode<T> right = node.right;
        right.parent = null;

        node.setRightChild(right.left);

        right.setLeftChild(node);

        node.updateHeightAndSubtreeSize();
        right.updateHeightAndSubtreeSize();

        return right;
    }


    public AVLNode<T> getRoot() {
        return virtualRoot.left;
    }

    public void clear() {
        registerModification();

        virtualRoot.left = null;
    }

    public int getSize() {
        return virtualRoot.left == null ? 0 : virtualRoot.left.subtreeSize;
    }


// Performs a node balancing on the path from {@code node} up until the root
    private void balance(AVLNode<T> node) {
        balance(node, virtualRoot);
    }

    private void balance(AVLNode<T> node, AVLNode<T> stop) {
        if (node == stop) {
            return;
        }
        AVLNode<T> p = node.getParent();
        if (p == virtualRoot) {
            makeRoot(balanceNode(node));
        }
        else {
            p.substituteChild(node, balanceNode(node));
        }

        balance(p, stop);
    }


    private AVLNode<T> balanceNode(AVLNode<T> node) {
        node.updateHeightAndSubtreeSize();
        if (node.isLeftDoubleHeavy()) {
            System.out.print(node.getValue() + " is Left Double Heavy ");
            if (node.left.isRightHeavy()) {
                System.out.print(" left child: " + node.left.getValue() + " is right heavy");
                node.setLeftChild(rotateLeft(node.left));
                System.out.print(" set node: " + node.getValue() + "'s left child to post left rotation node: " + node.left.getValue() + " ");

            }
            System.out.println("Rotate node: " + node.getValue() + " Right ");
            rotateRight(node);
            return node.parent;
        }
        else if (node.isRightDoubleHeavy()) {
            System.out.print(node.getValue() + " is Right Double Heavy ");
            if (node.right.isLeftHeavy()) {
                System.out.print(" right child: " + node.right.getValue() + " is left heavy");
                node.setRightChild(rotateRight(node.right));
                System.out.print(" set node: " + node.getValue() + "'s right child to post right rotation node: " + node.right.getValue() + " ");
            }
            System.out.println("Rotate node: " + node.getValue() + " Left ");
            rotateLeft(node);
            return node.parent;
        }
        return node;
    }

    public AVLNode<T> predecessor(AVLNode<T>node) {
        return node.predecessor;
    }

    public AVLNode<T> getMin() {return getRoot() == null ? null : getRoot().getSubtreeMin();
    }
    public AVLNode<T> successor(AVLNode<T> node) {
        return node.successor;
    }

    public AVLNode<T> getMax() {return getRoot() == null ? null : getRoot().getSubtreeMax();}
    public boolean isEmpty() {

        return getRoot() == null;
    }

    private void registerModification() {
        ++modCount;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Iterator<AVLNode<T>> i = nodeIterator(); i.hasNext(); ) {
            AVLNode<T> node = i.next();
            builder.append(node.toString()).append("\n");
        }
        return builder.toString();
    }


    @Override
    public Iterator<T> iterator() {
        return new AVLValueIterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    public Iterator<AVLNode<T>> nodeIterator() {
        return new AVLNodeIterator();
    }


    private class AVLValueIterator implements Iterator<T> {

        private AVLNodeIterator iterator;

        public AVLValueIterator() {
            iterator  = new AVLNodeIterator();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next().getValue();
        }

    }

    private class AVLNodeIterator implements Iterator<AVLNode<T>> {

        private AVLNode<T> nextNode;
        private final int expectedModCount;

        public AVLNodeIterator() {
            nextNode = getMin();
            expectedModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }


        public AVLNode<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            AVLNode<T> result = nextNode;
            nextNode = successor(nextNode);
            return result;
        }


        private void checkForComodification() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public static class AVLNode<T extends Comparable<? super T>>{
        T value;

        AVLNode<T> left, parent, right;

        AVLNode<T> predecessor;

        AVLNode<T> successor;

        AVLNode<T> subtreeMin, subtreeMax;

        int subtreeSize, height;

        public AVLNode (T value) {
            this.value = value;
            reset();
        }

        public T getValue() {
    return value;
        }

        public AVLNode<T> getRoot() {
            AVLNode<T> current = this;
            while (current.parent != null) {
                current = current.parent;
            }
            return current.left;
        }
        public AVLNode<T> getSubtreeMin() {
            return subtreeMin;
        }
        AVLNode<T> getSubtreeMax() {
            return subtreeMax;
        }
        public AVLNode<T> getTreeMin() {
            return getRoot().getSubtreeMin();
        }
        public AVLNode<T> getTreeMax() {
            return getRoot().getSubtreeMax();
        }
        public AVLNode<T> getParent() {
            return parent;
        }
        public AVLNode<T> getLeft() {
            return left;
        }
        public AVLNode<T> getRight() {
            return right;
        }
        int getHeight() {return height;}
        int getSubtreeSize() {return subtreeSize;}

        void reset() {
            this.height = 1;
            this.subtreeSize = 1;
            this.subtreeMin = this;
            this.subtreeMax = this;
            this.left = this.right = this.parent = this.predecessor = this.successor = null;
        }
        /**
         * Returns a height of the right subtree
         *
         * @return a height of the right subtree
         */
        int getRightHeight()
        {
            return right == null ? 0 : right.height;
        }


        int getLeftHeight()
        {
            return left == null ? 0 : left.height;
        }

        int getLeftSubtreeSize()
        {
            return left == null ? 0 : left.subtreeSize;
        }

        int getRightSubtreeSize()
        {
            return right == null ? 0 : right.subtreeSize;
        }

        void updateHeightAndSubtreeSize() {
//            System.out.print("previous height: " + this.height + " previous subtree size: " + this.subtreeSize);
            height = Math.max(getLeftHeight(), getRightHeight()) + 1;
            subtreeSize = getLeftSubtreeSize() + getRightSubtreeSize() + 1;
            System.out.println(" updated height: " + this.height + " updated subtree size: " + this.subtreeSize);

        }


        boolean isLeftDoubleHeavy() {
            return getLeftHeight() > getRightHeight() + 1;
        }

        boolean isRightDoubleHeavy()
        {
            return getRightHeight() > getLeftHeight() + 1;
        }

        boolean isLeftHeavy()
        {
            return getLeftHeight() > getRightHeight();
        }

        boolean isRightHeavy()
        {
            return getRightHeight() > getLeftHeight();
        }

        boolean isLeftChild()
        {
            return this == parent.left;
        }

        boolean isRightChild()
        {
            return this == parent.right;
        }

        //Resets this node to the default state
        public void setRight(AVLNode<T> right) {
            this.right = right;
        }


        public void setValue(T value) {
            this.value = value;
        }
        public void setLeft(AVLNode<T> left) {
            this.left = left;
        }
        public void setParent(AVLNode<T> parent) {
            this.parent = parent;
        }
        public void setSubtreeMin(AVLNode<T> subtreeMin) {
            this.subtreeMin = subtreeMin;
        }
        public void setSubtreeMax(AVLNode<T> subtreeMax) {
            this.subtreeMax = subtreeMax;
        }
        public void setSubtreeSize(int subtreeSize) {
            this.subtreeSize = subtreeSize;
        }
        public void setHeight(int height) {
            this.height = height;
        }

        public AVLNode<T> getSuccessor() {
            return successor;
        }

        public AVLNode<T> getPredecessor() {
            return predecessor;
        }


        void setSuccessor(AVLNode<T> node) {
            successor = node;
            if (node != null) {
                node.predecessor = this;
            }
        }

        void setPredecessor(AVLNode<T> node) {
            predecessor = node;
            if (node != null) {
                node.successor = this;
            }
        }

        void setLeftChild(AVLNode<T> node) {
            left = node;
            if (node != null) {
                node.parent = this;
                setPredecessor(node.subtreeMax);
                subtreeMin = node.subtreeMin;
            } else {
                subtreeMin = this;
                predecessor = null;
            }
        }

        public  void setRightChild(AVLNode<T> node) {
            right = node;
            if (node != null) {
                node.parent = this;
                setSuccessor(node.getSubtreeMin());
                subtreeMax = node.getSubtreeMax();
            } else {
                successor = null;
                subtreeMax = this;
            }
        }

        // substitute either left or right child with parent
        public void substituteChild(AVLNode<T> prevChild, AVLNode<T> newChild) {
            assert left == prevChild || right == prevChild;
            assert !(left == prevChild && right == prevChild);
            if (left == prevChild) {
                setLeftChild(newChild);
            } else {
                setRightChild(newChild);
            }
        }



//    }

//    protected static class AVLTreePrinter {
//
//        public static <T extends Comparable<T>> String getString(AVLTree<T> tree) {
//            if (tree.getRoot() == null)
//                return "Tree has no nodes.";
//
//            return getString((AVLNode<T>) tree.getRoot(), "", true);
//        }
//
//        public static <T extends Comparable<T>> String getString(AVLNode<T> node) {
//            if (node == null)
//                return "Sub-tree has no nodes.";
//
//            return getString(node, "", true);
//        }

        @Override
        public String toString()
        {
            return String
                    .format(
                            "{%s}: [parent = %s, left = %s, right = %s], [subtreeMin = %s, subtreeMax = %s], [predecessor = %s, successor = %s], [height = %d, subtreeSize = %d]",
                            value, parent == null ? "null" : parent.value,
                            left == null ? "null" : left.value, right == null ? "null" : right.value,
                            subtreeMin == null ? "null" : subtreeMin.value,
                            subtreeMax == null ? "null" : subtreeMax.value,
                            predecessor == null ? "null" : predecessor.value,
                            successor == null ? "null" : successor.value, height, subtreeSize);
        }
    }
}



