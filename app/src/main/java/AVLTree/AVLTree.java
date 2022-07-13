package AVLTree;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
JGraphT's AVLTree with added operations to insert and remove values that are not min/max of the current tree
 * @Author Timofey Chudakov
 */
public class AVLTree <T extends Comparable<? super T>> implements Iterable<T> {
    /** An auxiliary node that is always present in a tree and doesn't contain any data */
    private TreeNode<T> virtualRoot = new TreeNode<T>(null);
    /** Keeps track of any modifications to this tree */
    private int modCount = 0;
    /** Constructs and  instance, with a null virtualRoot left and right */
    public AVLTree () {}

    /** @param: root  – the root of the newly create tree */
    private AVLTree(TreeNode<T> root) {
        makeRoot(root);
    }

//    /** @param: value  – the value being added to this tree */
//    public TreeNode<T> add(T value) {
//        if(value.compareTo(getMax().getValue()) >= 0) {
//            return addMax(value);
//        }
//        if(value.compareTo(getMin().getValue()) <= 0 ){
//            return addMin(value);
//        }
//        else {
//            return insert(value);
//        }
//    }
    /** @param: value  – the value that will be set
     *  to a node before finding a location to insert.*/
    public TreeNode<T> insert(T value) {
        if(value.compareTo(getMax().getValue()) >= 0) {
            return addMax(value);
        }
        if(value.compareTo(getMin().getValue()) <= 0 ){
            return addMin(value);
        }
        System.out.println(" AVLNode<T> insert(T value) {} @param value to insert: "+ value);
        TreeNode<T> newNode = new TreeNode<T>(value);
        insertNode(newNode);
        return newNode;
    }
    /** @param: newNode  – the node that will be added as
     * the max node of this tree or
     * the min node of a tree that will contain
     * all nodes with greater value than this node. */
    public void insertNode(TreeNode<T> newNode) {
        System.out.println("\nvoid insertNode(AVLNode<T> newNode){} @param node to insert: " + newNode.getValue());
        registerModification();
        if (isEmpty()) {
            virtualRoot.left = newNode;
            newNode.parent = virtualRoot;
        }
        else {
            //traverse inorder and find the node that is greater than newNode
            Stack<TreeNode<T>> stack = new Stack<>();
            TreeNode<T> curr = getRoot();

            while (!stack.empty() || curr != null) {
                if (curr != null) {
                    stack.push(curr);
                    curr = curr.left;
                } else {
                    curr = stack.pop();
                    if (curr.getValue().compareTo(newNode.getValue()) > 0) {

                        AVLTree <T> right = splitBefore(curr);
                        System.out.println("right bfs " + right.getBFS(right.getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));

                        System.out.print("\n\nPost splitBefore right tree root: " + right.getRoot().getValue());
                        System.out.print(" this.root before newMax: " + getRoot().getValue());
//                        right.addMin(newNode.getValue());
                        addMax(newNode.getValue());
                        System.out.println("\n\nthis.root after newMax: " + getRoot().getValue());
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
    public TreeNode<T> addMax(T value) {
        TreeNode<T> newMax = new TreeNode<T>(value);
        addMaxNode(newMax);
        return newMax;
    }

    /**
     * Adds the {@code newMax} as a maximum node to this tree.
     *
     * @param newMax a node to add as a tree max
     */
    public void addMaxNode(TreeNode<T> newMax) {
        System.out.print("void addMaxNode(AVLNode<T> newMax) {} @param new max value of this tree. newMax: " + newMax.getValue());
        registerModification();
        if (isEmpty()) {
            virtualRoot.left = newMax;
            System.out.print(" virtual root.left = " + newMax.getValue());
            newMax.parent = virtualRoot;
            System.out.print(", newMax.parent = virtualRoot");

        } else {
            TreeNode<T> max = getMax();
            System.out.println("     max = " + max.getValue() + " setRightChild( newMax = " + newMax.getValue());
            max.setRightChild(newMax);
            System.out.println("   balance(max = " + max.getValue());
            balance(max);
            System.out.println("\n) addMaxNode balance complete");

        }
    }
    /**
     * Adds the {@code value} as a minimum element to this tree
     * @param value a value to add as a tree min
     * @return a tree node holding the {@code value}
     */
    public TreeNode<T> addMin(T value) {
        TreeNode<T> newMin = new TreeNode<T>(value);
        addMinNode(newMin);
        return newMin;
    }

    public void addMinNode(TreeNode<T> newMin) {
        System.out.print("void addMinNode(AVLNode<T> newMin) {} @param new min value: " + newMin.getValue());
        registerModification();
        if (isEmpty()) {
            System.out.print(" virtual root.left = " + newMin.getValue());
            virtualRoot.left = newMin;
            newMin.parent = virtualRoot;
            System.out.println(" newMin.parent = virtualRoot");

        }
        else {
            TreeNode<T> min = getMin();
            System.out.println("   min = " + min.getValue() + " setLeftChild( newMin = " + newMin.getValue());
            min.setLeftChild(newMin);
            balance(min);
        }
    }
    /**
     * Nodes with value less than or equal to the {@code node} remain in this tree.
     * Nodes with value greater than the {@code node} are returned as a new tree.
     * than the {@code node}. The second part is returned as a tree.
     *
     * @param node a separating node
     * @return a tree containing the nodes which are strictly greater than the {@code node}
     */
    public AVLTree <T> splitAfter(TreeNode<T> node) {
        System.out.println("\nAVLTree<T> splitAfter(AVLNode<T> node) @param: " + node.getValue());
        registerModification();
        TreeNode<T> parent = node.getParent();
        boolean nextMove = node.isLeftChild();
        TreeNode<T> left = node.getLeft();
        TreeNode<T> right = node.getRight();
        System.out.println("   node.parent = " + node.parent.getValue() + " substituting child: " + node.getValue() + " with: "+ null);
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
        }
        else {

            AVLTree.TreeNode<T> t = left;
            while (t.right != null) {
                t = t.right;
            }
            System.out.println("     t = " + t.getValue() + " setRightChild( node =  " + node.getValue() + " )");
            t.setRightChild(node);
            while (t != left) {
                StringBuilder sb = new StringBuilder();
                TreeNode<T> p = t.parent;
                TreeNode<T> balancedP = balanceNode(t);

                sb.append("     p = ").append(p.getValue()).append("substiuting child: ").append(t.getValue()).append(" with: balanceNode(").append(t.getValue()).append(")").append(" = ").append(balancedP.getValue());

                p.substituteChild(t, balancedP);
                t = p;
                sb.append(t.getValue());
                System.out.println(sb.toString());
            }
            left = balanceNode(left);
            System.out.println("            left = balanceNode(left) = " + left.getValue());
        }
        return split(left, right, parent, nextMove);
    }
    /**
     * Nodes with value less than the {@code node} remain in this tree.
     * Nodes with value greater than or equal to the {@code node} are returned as a new tree.
     * {@code node}.
     * @param node a separating node
     * @return a tree containing the nodes which are greater than or equal to the {@code node}
     */
    public AVLTree <T> splitBefore(TreeNode<T> node) {
        System.out.print("\nAVLTree<T> splitBefore(" + node.getValue() + ") @param junction node + all nodes greater will not remain in this tree. ");

        registerModification();
        TreeNode<T> predecessor = predecessor(node);

        // node is min, swap
        if (predecessor == null) {
            AVLTree <T> tree = new AVLTree <T>();
            swap(tree);
            System.out.println("pred: null");
            return tree;
        }
//        System.out.println("predecessor(" +node.getValue()+") = "+ predecessor.getValue());
        return splitAfter(predecessor);
    }

    /**
     * Append the nodes in the {@code tree} after the nodes in this tree.
     * The result of this operation is stored in this tree.
     * @param tree a tree to append
     */
    public void mergeAfter(AVLTree <T> tree) {
        System.out.println("\nvoid mergeAfter(AVLTree<T> tree){} @param tree that is being merged after current. param tree: " + tree.getDFS(tree.getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
        registerModification();
        if (tree.isEmpty()) {
            return;
        }
        else if (tree.getSize() == 1) {
            addMaxNode(tree.removeMin());
            return;
        }

        TreeNode<T> junctionNode = tree.removeMin();
        TreeNode<T> treeRoot = tree.getRoot();

        tree.clear();
        System.out.println("merge after: makeRoot(merge(junctionNode, getRoot(), treeRoot));");
        makeRoot(merge(junctionNode, getRoot(), treeRoot));
        System.out.println("bfs " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("dfs inorder " + getDFS(getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));

    }

    private AVLTree <T>split(TreeNode<T> left, TreeNode<T> right, TreeNode<T> p, boolean leftMove) {
        T v = null;
        if(right != null) {v = right.getValue();}

        System.out.println("\n  AVLTree<T>split(AVLNode<T> left, AVLNode<T>  right, AVLNode<T>  p, boolean leftMove)"+
                ". left: "+ left.getValue() + ", right: " + v + ", " + ", p: " + p.getValue() + ", nextMove: " + leftMove);
        while (p != virtualRoot) {
            boolean nextMove = p.isLeftChild();
            TreeNode<T> nextP = p.getParent();
            System.out.println("        p.getParent = " + p.getParent().getValue() + " substituting child: "+p.getValue()+" with: "+ null);
            p.getParent().substituteChild(p, null);
            p.parent = null;

            if (leftMove) {
                String rightVal = right == null ? null : right.getValue().toString();
                String pRightVal = p.right == null ? null : p.right.getValue().toString();
                System.out.println("        right = merge(p = "+ p.getValue() + ", right = " + rightVal + " p.right =  " + pRightVal);
                right = merge(p, right, p.right);
            } else {
                String leftVal = p.left == null ? null : p.left.getValue().toString();
                System.out.println("        left = merge(p = "+ p.getValue() + ", p.left = " + leftVal + " left =  " + left.getValue());
                left = merge(p, p.left, left);
            }
            p = nextP;
            leftMove = nextMove;
        }
        System.out.println("    AVLTree<T>split: makeRoot(left); left: " + left.getValue());
        makeRoot(left);
        System.out.println("    bfs " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("    dfs inorder " + getDFS(getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));

        return new AVLTree <>(right);
    }


    private void makeRoot(TreeNode<T> node) {
        virtualRoot.left = node;
        if(node == null) {
            System.out.println("node null");
            System.out.println("node null");
            System.out.println("node null");
            System.out.println("node null");

        }
        if (node != null) {
            System.out.println("root: " + node.getValue());
            node.subtreeMax.successor = null;
            node.subtreeMin.predecessor = null;
            node.parent = virtualRoot;
        }
    }


    public void mergeBefore(AVLTree <T> tree) {
        registerModification();
        tree.mergeAfter(this);
        swap(tree);
    }

    private void swap(AVLTree <T> tree) {
        System.out.println("\nswap(AVLTree<T> tree){} @param tree that is swapping virtualRoot.left with current tree");
        TreeNode<T> t = virtualRoot.left;
        System.out.println("swap: makeRoot(tree.virtualRoot.left)");

        makeRoot(tree.virtualRoot.left);
        System.out.println("bfs " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("dfs inorder " + getDFS(getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("swap: tree.makeRoot(t)");
        tree.makeRoot(t);
        System.out.println("tree bfs " + tree.getBFS(tree.getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
        System.out.println("tree dfs inorder " + tree.getDFS(tree.getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    /**
     * Merges the left and right subtrees using the junctionNode.
     * @param junctionNode a node between left and right subtrees
     * @param left a left subtree
     * @param right a right subtree
     * @return the root of the resulting tree
     */
    private TreeNode<T> merge(TreeNode<T> junctionNode, TreeNode<T> left, TreeNode<T> right) {
//        System.out.println("        AVLNode<T> merge(AVLNode<T> junctionNode, AVLNode<T> left, AVLNode<T> right) {} @param junctionNode, left, right");

        if (left == null && right == null) {
            System.out.println("            if (left == null && right == null) { junctionNode.reset();");
            junctionNode.reset();
            return junctionNode;
        }

        else if (left == null) {
            var rightLeftValue = right.left == null ? null: right.left.getValue();
            System.out.println("            else if (left == null){ "+right.getValue()+".setLeftChild(merge("+junctionNode.getValue()+", null, "+rightLeftValue);
            right.setLeftChild(merge(junctionNode, left, right.left));
            System.out.println("                balanceNode(" + right.getValue()+")");
            return balanceNode(right);
        } else if (right == null) {
            System.out.println("            else if (right == null){ "+left.getValue()+".setRightChild(merge("+junctionNode.getValue()+", "+left.right.getValue()+", null");
            left.setRightChild(merge(junctionNode, left.right, right));
            System.out.println("            balanceNode( left = " + left.getValue()+")");
            return balanceNode(left);
        } else if (left.getHeight() > right.getHeight() + 1) {
            System.out.println("            else if (left.getHeight() > right.getHeight() + 1) { left.setRightChild(merge(junctionNode, left.right, right));");
            System.out.println("            left =  " + left.getValue() + " setRightChild(merge (junctionNode = " + junctionNode.getValue() + ", left.right = "+left.right.getValue() +" right = "+ right.getValue());
            left.setRightChild(merge(junctionNode, left.right, right));
            System.out.println("                balanceNode(left = " + left.getValue()+")");
            return balanceNode(left);
        } else if (right.getHeight() > left.getHeight() + 1) {
            var rightLeftValue = right.left == null ? null: right.left.getValue();
            System.out.println("            else if (right.getHeight() > left.getHeight() + 1) { right.setLeftChild(merge(junctionNode, left, right.left));");
            System.out.println("            right = " + right.getValue() + " setLeftChild( merge( junctionNode = " + junctionNode.getValue()
                    + ", left = " + left.getValue() + ", right.left = " + rightLeftValue);
            right.setLeftChild(merge(junctionNode, left, right.left));
            System.out.println("                balanceNode(right = " + right.getValue()+")");
            return balanceNode(right);
        } else {
            System.out.println("            else junctionNode = " + junctionNode.getValue() + ".setLeftChild( left = " + left.getValue() +  ") junctionNode = " + junctionNode.getValue() + ".setRightChild( right = " + right.getValue() +")");
            junctionNode.setLeftChild(left);
            junctionNode.setRightChild(right);
            System.out.println("                balanceNode(junctionNode = " + junctionNode.getValue()+")");
            return balanceNode(junctionNode);
        }
    }
    public TreeNode<T> removeMin() {
        registerModification();
        System.out.println("AVLNode<T> removeMin() {} @param: " + getMin().getValue());

        if (isEmpty()) {
            return null;
        }
        TreeNode<T> min = getMin();
        // min.parent != null
        if (min.parent == virtualRoot) {
            var minRightValue = min.right == null?null:min.right.getValue();
            System.out.println("    remove min: makeRoot(min.right); min.right: " + minRightValue);
            makeRoot(min.right);
            System.out.println("    bfs " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            System.out.println("    dfs inorder " + getDFS(getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
        } else {
            var minRightValue = min.right == null?null:min.right.getValue();
            System.out.println("    min.parent = " + min.parent.getValue() + " setLeftChild( min.right = " + minRightValue);
            min.parent.setLeftChild(min.right);
        }
        System.out.println("            balance( min.parent = " + min.parent.getValue());
        balance(min.parent);
        System.out.println("\n");

        return min;
    }

    public TreeNode<T> removeMax() {
        registerModification();
        if (isEmpty()) {
            return null;
        }
        TreeNode<T> max = getMax();
        if (max.parent == virtualRoot) {
            System.out.println("    remove max: makeRoot(max.left);");
            makeRoot(max.left);
            System.out.println("    bfs " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            System.out.println("    dfs inorder " + getDFS(getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
        } else {
            String maxLeft = max.left == null ? null : max.left.getValue().toString();
            System.out.println("    max.parent = " + max.parent.getValue() + " setRightChild( max.left = " + maxLeft);
            max.parent.setRightChild(max.left);
        }
        System.out.println("            balance( max.parent = " + max.parent.getValue());
        balance(max.parent);
        return max;
    }


    private TreeNode<T> rotateRight(TreeNode<T> node) {
        System.out.println("\n  AVLNode<T> rotateRight(AVLNode<T> node) {} @param node being rotated right: " + node.getValue());
        TreeNode<T> left = node.left;
        left.parent = null;
        var leftRightValue = left.right == null ? null : left.right.getValue();
        System.out.println("        node = " + node.getValue() + " setLeftChild( left.right = " + leftRightValue);
        node.setLeftChild(left.right);
        System.out.println("        left = " + left.getValue() + " setRightChild( node = " + node.getValue());
        left.setRightChild(node);

        node.updateHeightAndSubtreeSize();
//        System.out.println("            node update: " + node.getValue()+ ".updateHeightAndSubtreeSize() height: "+ node.getHeight() + " subtreeSize: " + node.getSubtreeSize());
        left.updateHeightAndSubtreeSize();
//        System.out.println("            left update: " + left.getValue()+ ".updateHeightAndSubtreeSize() height: " + left.getHeight() + " subtreeSize: " + left.getSubtreeSize());
        return left;
    }


    private TreeNode<T> rotateLeft(TreeNode<T> node) {
        System.out.println("\n  AVLNode<T> rotateLeft(AVLNode<T> node) {} @param node being rotated left:  " + node.getValue());
        TreeNode<T> right = node.right;
        right.parent = null;
        var rightLeftValue = right.left == null?null:right.left.getValue();
        System.out.println("        node = " + node.getValue() + " setRightChild( right.left = " + rightLeftValue);
        node.setRightChild(right.left);
        System.out.println("        right = " + right.getValue() + " setLeftChild( node = " + node.getValue());
        right.setLeftChild(node);
        node.updateHeightAndSubtreeSize();
//        System.out.println("        node update: "+ node.getValue()+ ".updateHeightAndSubtreeSize() height: "+ node.getHeight() + " subtreeSize: " + node.getSubtreeSize());

        right.updateHeightAndSubtreeSize();
//        System.out.println("        right update: " + right.getValue()+ ".updateHeightAndSubtreeSize() height: "+ right.getHeight() + " subtreeSize: " + right.getSubtreeSize());
        return right;
    }


    public TreeNode<T> getRoot() {
        return virtualRoot.left;
    }

    public void clear() {
        registerModification();

        virtualRoot.left = null;
    }

    public int getSize() {
        return virtualRoot.left == null ? 0 : virtualRoot.left.subtreeSize;
    }


    // Performs a node balancing on the path from the @param node up until the root
    private void balance(TreeNode<T> node) {
        balance(node, virtualRoot);
    }

    private void balance(TreeNode<T> node, TreeNode<T> stop) {
        System.out.println("\n      void balance(AVLNode<T> node, AVLNode<T> stop) @param current node that is being balanced, virtual root (base case). node:" + node.getValue());
        if (node == stop) {
            return;
        }
        TreeNode<T> p = node.getParent();
        if (p == virtualRoot) {
            System.out.println("        void balance: makeRoot(balanceNode(node)); ");
            makeRoot(balanceNode(node));
            System.out.println("bfs " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            System.out.println("dfs inorder " + getDFS(getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
        }
        else {
            System.out.println("    p = " + p.getValue() + " substituting child: " + node.getValue() + " with: ");
            p.substituteChild(node, balanceNode(node));

        }
        balance(p, stop);
    }

    private TreeNode<T> balanceNode(TreeNode<T> node) {
        node.updateHeightAndSubtreeSize();
//        System.out.println("             " + node.getValue()+ " updated height: "+ node.getHeight() + " updated subtreeSize: " + node.getSubtreeSize());
        if (node.isLeftDoubleHeavy()) {
            System.out.println("    Left Double Heavy ");
            if (node.left.isRightHeavy()) {
                System.out.println("    node =  " + node.getValue() + " setLeftChild( rotateLeft node.left = " + node.left.getValue());
                System.out.println("    bfs before node.setLeftChild(rotateLeft(node.left));: " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
                System.out.println("    inorder dfs before node.setLeftChild(rotateLeft(node.left));: " + getDFS(getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
                node.setLeftChild(rotateLeft(node.left));
                System.out.println("    inorder dfs after node.setLeftChild(rotateLeft(node.left));: " + getDFS(getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
                System.out.println("    bfs after node.setLeftChild(rotateLeft(node.left));: " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));

            }
            System.out.println("    bfs before rotateRight(node); " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            rotateRight(node);
            System.out.println("    bfs after rotateRight(node): " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            return node.parent;
        }
        else if (node.isRightDoubleHeavy()) {
            System.out.println("    Right Double Heavy ");
            if (node.right.isLeftHeavy()) {
                System.out.println("    node =  " + node.getValue() + " setRightChild( rotateRight node.right = " + node.right.getValue());
                System.out.println("    bfs before node.setRightChild(rotateRight(node.right)); " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
                node.setRightChild(rotateRight(node.right));
                System.out.println("    inorder dfs after node.setRightChild(rotateRight(node.right)) " + getDFS(getRoot(),"inorder").stream().map(String::valueOf).collect(Collectors.joining(", ")));
                System.out.println("    bfs after node.setRightChild(rotateRight(node.right)) " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            }
            System.out.println("        bfs rotateLeft(node) " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            rotateLeft(node);
            System.out.println("        bfs after rotateLeft(node) " + getBFS(getRoot()).stream().map(String::valueOf).collect(Collectors.joining(", ")));
            return node.parent;
        }
        return node;
    }

    public TreeNode<T> predecessor(TreeNode<T> node) {
        return node.predecessor;
    }

    public TreeNode<T> getMin() {return getRoot() == null ? null : getRoot().getSubtreeMin();
    }
    public TreeNode<T> successor(TreeNode<T> node) {
        return node.successor;
    }

    public TreeNode<T> getMax() {return getRoot() == null ? null : getRoot().getSubtreeMax();}
    public boolean isEmpty() {

        return getRoot() == null;
    }

    private void registerModification() {
        ++modCount;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Iterator<TreeNode<T>> i = nodeIterator(); i.hasNext(); ) {
            TreeNode<T> node = i.next();
            builder.append(node.toString()).append("\n");
        }
        return builder.toString();
    }

    public List<T> getDFS(TreeNode<T> start, String order) {
        List<T> values = new ArrayList<>();
        if (order.equalsIgnoreCase("inorder")) {
            Stack<TreeNode<T>> stack = new Stack<>();
            TreeNode<T> curr = getRoot();
            while (!stack.empty() || curr != null) {
                while(curr != null) {
                    stack.push(curr);
                    curr = curr.left;
                }
                curr = stack.pop();
                values.add(curr.getValue());
                curr = curr.right;
            }
        }return values;
    }


    public List<T> getBFS(TreeNode<T> start) {
        final Queue<TreeNode<T>> queue = new ArrayDeque<TreeNode<T>>();
        int count = 0;
        List<T> values = new ArrayList<>();
        TreeNode<T> node = start;
        while (node != null) {
            values.add(node.value);
            if (node.left != null)
                queue.add(node.left);
            if (node.right != null)
                queue.add(node.right);
            if (!queue.isEmpty())
                node = queue.remove();
            else
                node = null;
        }
        return values;
    }


    @Override
    public Iterator<T> iterator() {
        return new AVLValueIterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    public Iterator<TreeNode<T>> nodeIterator() {
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

    private class AVLNodeIterator implements Iterator<TreeNode<T>> {

        private TreeNode<T> nextNode;
        private final int expectedModCount;

        public AVLNodeIterator() {
            nextNode = getMin();
            expectedModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            return nextNode != null;
        }


        public TreeNode<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            TreeNode<T> result = nextNode;
            nextNode = successor(nextNode);
            return result;
        }


        private void checkForComodification() {
            if (expectedModCount != modCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public static class TreeNode<T extends Comparable<? super T>>{
        T value;

        TreeNode<T> left, parent, right;

        TreeNode<T> predecessor;

        TreeNode<T> successor;

        TreeNode<T> subtreeMin, subtreeMax;

        int subtreeSize, height;

//        List<TreeNode<T>> children;
//        public Stream<TreeNode<T>> stream() {
//            return Stream.concat(Stream.of(this), children.stream().flatMap(TreeNode<T>::stream));
//        }
//


        public TreeNode(T value) {
            this.value = value;
            reset();
        }

        public T getValue() {
            return value;
        }
        public TreeNode<T> getRoot() {
            TreeNode<T> current = this;
            while (current.parent != null) {
                current = current.parent;
            }
            return current.left;
        }
        public TreeNode<T> getSubtreeMin() {
            return subtreeMin;
        }
        TreeNode<T> getSubtreeMax() {
            return subtreeMax;
        }
        public TreeNode<T> getTreeMin() {
            return getRoot().getSubtreeMin();
        }
        public TreeNode<T> getTreeMax() {
            return getRoot().getSubtreeMax();
        }
        public TreeNode<T> getParent() {
            return parent;
        }
        public TreeNode<T> getLeft() {
            return left;
        }
        public TreeNode<T> getRight() {
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
        /* @return a height of the right subtree */
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
            height = Math.max(getLeftHeight(), getRightHeight()) + 1;
            subtreeSize = getLeftSubtreeSize() + getRightSubtreeSize() + 1;

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

        boolean isLeftChild() {
            return this == parent.left;
        }

        boolean isRightChild()
        {
            return this == parent.right;
        }

        //Resets this node to the default state
        public void setRight(TreeNode<T> right) {
            this.right = right;
        }


        public void setValue(T value) {
            this.value = value;
        }
        public void setLeft(TreeNode<T> left) {
            this.left = left;
        }
        public void setParent(TreeNode<T> parent) {
            this.parent = parent;
        }
        public void setSubtreeMin(TreeNode<T> subtreeMin) {
            this.subtreeMin = subtreeMin;
        }
        public void setSubtreeMax(TreeNode<T> subtreeMax) {
            this.subtreeMax = subtreeMax;
        }
        public void setSubtreeSize(int subtreeSize) {
            this.subtreeSize = subtreeSize;
        }
        public void setHeight(int height) {
            this.height = height;
        }

        public TreeNode<T> getSuccessor() {
            return successor;
        }

        public TreeNode<T> getPredecessor() {
            return predecessor;
        }


        void setSuccessor(TreeNode<T> node) {
            successor = node;
            if (node != null) {
                node.predecessor = this;
            }
        }

        void setPredecessor(TreeNode<T> node) {
            predecessor = node;
            if (node != null) {
                node.successor = this;
            }
        }

        void setLeftChild(TreeNode<T> node) {
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

        public  void setRightChild(TreeNode<T> node) {
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
        public void substituteChild(TreeNode<T> prevChild, TreeNode<T> newChild) {
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



