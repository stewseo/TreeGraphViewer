package AVLTree;

import java.util.*;
import java.util.function.Consumer;

/*
Imeplementation of JUNG and JGraphT's AVL tree
 */
public class AVLTree<T extends Comparable<T>> implements Iterable<T> {

    private AVLNode<T> virtualRoot = new AVLNode<T>(null);

    private int modCount = 0;

    public AVLTree() {
    }

    private AVLTree(AVLNode<T> value) {
       makeRoot(value);
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

    public AVLTree<T> splitBefore(AVLNode<T> node) {
        registerModification();

        AVLNode<T> predecessor = predecessor(node);
        if (predecessor == null) {
            // node is a minimum node
            AVLTree<T> tree = new AVLTree<T>();
            swap(tree);
            return tree;
        }
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

        if (left != null) {
            left.parent = null;
        }
        if (right != null) {
            right.parent = null;
        }

        if (left == null) {
            left = node;
        } else {
            // insert node as a left subtree max
            AVLNode<T> t = left;
            while (t.right != null) {
                t = t.right;
            }
            t.setRightChild(node);

            while (t != left) {
                AVLNode<T> p = t.parent;
                p.substituteChild(t, balanceNode(t));
                t = p;
            }
            left = balanceNode(left);

        }
        return split(left, right, parent, nextMove);
    }

    private AVLTree<T>split(AVLNode<T> left, AVLNode<T>  right, AVLNode<T>  p, boolean leftMove) {
        while (p != virtualRoot) {
            boolean nextMove = p.isLeftChild();
            AVLNode<T> nextP = p.getParent();

            p.getParent().substituteChild(p, null);
            p.parent = null;

            if (leftMove) {
                right = merge(p, right, p.right);
            } else {
                left = merge(p, p.left, left);
            }
            p = nextP;
            leftMove = nextMove;
        }

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

    public void addMinNode(AVLNode<T> newMin) {
        registerModification();
        if (isEmpty()) {
            virtualRoot.left = newMin;
            newMin.parent = virtualRoot;
        } else {
            AVLNode<T> min = getMin();
            min.setLeftChild(newMin);
            balance(min);
        }
    }


    public void mergeAfter(AVLTree<T> tree) {
        registerModification();
        if (isEmpty()) {
            return;
        } else if (tree.getSize() == 1) {
            addMaxNode(tree.removeMin());
            return;
        }

        AVLNode<T> junctionNode = tree.removeMin();
        AVLNode<T> treeRoot = tree.getRoot();
        tree.clear();

        makeRoot(merge(junctionNode, getRoot(), treeRoot));
    }

    public void clear() {
        registerModification();

        virtualRoot.left = null;
    }

    private int getSize() {
        return virtualRoot.left == null ? 0 : virtualRoot.left.subtreeSize;
    }

    public AVLNode<T>  successor(AVLNode<T> node) {
        return node.successor;
    }

    public AVLNode<T> predecessor(AVLNode<T>node) {
        return node.predecessor;
    }

    public AVLNode<T> getMin() {
        return getRoot() == null ? null : getRoot().getSubtreeMin();
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
     * Merges the left and  right} subtrees using the junctionNode.
     *  Donald E. Knuth.'s
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
        AVLNode<T> min = getMin();
        // min.parent != null
        if (min.parent == virtualRoot) {
            makeRoot(min.right);
        } else {
            min.parent.setLeftChild(min.right);
        }

        balance(min.parent);

        return min;
    }

    AVLNode<T>  getMax() {
        return getRoot() == null ? null : getRoot().getSubtreeMax();
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

//      Performs a node balancing on the path from {@code node} up until the root
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
        } else {
            p.substituteChild(node, balanceNode(node));
        }

        balance(p, stop);
    }


    private AVLNode<T> balanceNode(AVLNode<T> node) {
        node.updateHeightAndSubtreeSize();
        if (node.isLeftDoubleHeavy()) {
            if (node.left.isRightHeavy()) {
                node.setLeftChild(rotateLeft(node.left));
            }
            rotateRight(node);
            return node.parent;
        } else if (node.isRightDoubleHeavy()) {
            if (node.right.isLeftHeavy()) {
                node.setRightChild(rotateRight(node.right));
            }
            rotateLeft(node);
            return node.parent;
        }
        return node;
    }
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

    public static class AVLNode<T extends Comparable<T>> {
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

        public AVLNode<T> getRight() {
            return right;
        }

        public void setRight(AVLNode<T> right) {
            this.right = right;
        }

        public AVLNode<T> getSubtreeMin() {
            return subtreeMin;
        }

        private AVLNode<T> getSubtreeMax() {
            return subtreeMax;
        }


        public AVLNode<T> getMin() {
            return getRoot() == null ? null : getRoot().getSubtreeMin();
        }

        boolean isLeftChild()
        {
            return this == parent.left;
        }

        public AVLNode<T> successor(AVLNode<T> node) {
            return node.successor;
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


        public void setValue(T value) {
            this.value = value;
        }

        public AVLNode<T> getLeft() {
            return left;
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

        int getSubtreeSize() {
            return subtreeSize;
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


        int getRightHeight() {
            return right == null ? 0 : right.height;
        }

        int getLeftHeight() {
            return left == null ? 0 : left.height;
        }

        int getLeftSubtreeSize() {
            return left == null ? 0 : left.subtreeSize;
        }

        int getRightSubtreeSize() {
            return right == null ? 0 : right.subtreeSize;
        }

        void updateHeightAndSubtreeSize() {
            height = Math.max(getLeftHeight(), getRightHeight()) + 1;
            subtreeSize = getLeftSubtreeSize() + getRightSubtreeSize() + 1;
        }

        boolean isLeftDoubleHeavy() {
            return getLeftHeight() > getRightHeight() + 1;
        }

        boolean isRightDoubleHeavy() {
            return getRightHeight() > getLeftHeight() + 1;
        }

        boolean isLeftHeavy() {
            return getLeftHeight() > getRightHeight();
        }

        boolean isRightHeavy() {
            return getRightHeight() > getLeftHeight();
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

        int getHeight() {
            return height;
        }
        void reset() {
            this.height = 1;
            this.subtreeSize = 1;
            this.subtreeMin = this;
            this.subtreeMax = this;
            this.left = this.right = this.parent = this.predecessor = this.successor = null;
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



