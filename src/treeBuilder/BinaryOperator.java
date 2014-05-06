package treeBuilder;

public class BinaryOperator extends Node {

	private Node leftChild;
	private Node rightChild;
	
	public BinaryOperator(int currentIndex, int depth, String value, int index) {
		super(currentIndex, depth, value, index);
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}

	public Node getRightChild() {
		return rightChild;
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}

	@Override
	public boolean hasChildren() {
		return leftChild != null || rightChild != null;
	}

	@Override
	public Node[] getChildren() {
		Node[] children = new Node[2];
		
		int i = 0;
		if (leftChild != null) 
			children[i++] = leftChild;
		if (rightChild != null)
			children[i++] = rightChild;
				
		return children;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		sb.append(leftChild);
		sb.append(super.getValue());
		sb.append(rightChild);
		sb.append(")");
		
		return sb.toString();
	}
	
	public String toTreeString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getKey());
		sb.append("-");
		sb.append(getDepth());
		sb.append(": ");
		sb.append(getValue());
		sb.append(" (");
		
		if (leftChild != null)
			sb.append(leftChild.toTreeString());
		if (rightChild != null) {
			sb.append(", ");
			sb.append(rightChild.toTreeString());
		}
		sb.append(")");
		
		return sb.toString();
	}

}
