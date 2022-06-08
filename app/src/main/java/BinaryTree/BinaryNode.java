package BinaryTree;public class BinaryNode<T> {	private int count;	private T data;	private BinaryNode<T> leftChild; // Reference to left child	private BinaryNode<T> rightChild; // Reference to right child	public BinaryNode() {		this(null); // Call next constructor	} // end default constructor	public BinaryNode(T dataPortion) {		this(dataPortion, null, null); // Call next constructor	} // end constructor	public BinaryNode(T dataPortion, BinaryNode<T> newLeftChild, BinaryNode<T> newRightChild) {		data = dataPortion;		leftChild = newLeftChild;		rightChild = newRightChild;		count = 1;	} // end constructor	public T getData() {		return data;	} // end getData	public int getCount(){return count;}	public void count(){this.count++;}	public void setData(T newData) {		data = newData;	} // end setData	public BinaryNode<T> getLeftChild() {		return leftChild;	} // end getLeftChild	public BinaryNode<T> setLeft(BinaryNode<T> newLeftChild) {		leftChild = newLeftChild;		return leftChild;	}	public void setLeftChild(BinaryNode<T> newLeftChild) {		leftChild = newLeftChild;	} // end setLeftChild	public boolean hasLeftChild() {		return leftChild != null;	} // end hasLeftChild	public BinaryNode<T> getRightChild() {		return rightChild;	} // end getRightChild	public void setRightChild(BinaryNode<T> newRightChild) {		rightChild = newRightChild;	} // end setRightChild	public boolean hasRightChild() {		return rightChild != null;	} // end hasRightChild	public boolean isLeaf() {		return (leftChild == null) && (rightChild == null);	} // end isLeaf	public int getNumberOfNodes() {		int leftNumber = 0;		int rightNumber = 0;		if (leftChild != null)			leftNumber = leftChild.getNumberOfNodes();		if (rightChild != null)			rightNumber = rightChild.getNumberOfNodes();		return 1 + leftNumber + rightNumber;	} // end getNumberOfNodes	public int getHeight() {		return getHeight(this); // Call private getHeight	} // end getHeight	public BinaryNode<T> copy() {		BinaryNode<T> newRoot = new BinaryNode<>(data);		if (leftChild != null)			newRoot.setLeftChild(leftChild.copy());		if (rightChild != null)			newRoot.setRightChild(rightChild.copy());		return newRoot;	} // end copy	private int getHeight(BinaryNode<T> node) {		int height = 0;		if (node != null)			height = 1 + Math.max(getHeight(node.getLeftChild()), getHeight(node.getRightChild()));		return height;	} // end getHeight} // end BinaryNode