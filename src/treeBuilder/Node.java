package treeBuilder;

public abstract class Node {

	private int key;
	private int depth;
	private String value;
	private int index;
	private Node parent;
	
	public Node(int key, int depth, String value, int index) {
		this.setKey(key);
		this.setDepth(depth);
		this.setValue(value);
		this.setIndex(index);
		parent = null;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public int getKey() {
		return key;
	}

	public void setKey(int currentIndex) {
		this.key = currentIndex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public boolean hasChildren() {
		return false;
	}

	public Node[] getChildren() {
		return null;
	}
	
	public int numNodes() {
		int numNodes = 1;
		
		if (hasChildren()) {
			Node[] children = getChildren();
			for (int i = 0; i < children.length; ++i) {
				numNodes += children[i].numNodes();
			}
		}
		
		return numNodes;
	}
	
	public void reduceDepth() {
		setDepth(getDepth() - 1);
		
		if (hasChildren()) {
			Node[] children = getChildren();
			for (int i = 0; i < children.length; ++i) {
				children[i].reduceDepth();
			}
		}
	}
	
	public boolean isRoot() {
		return key == 0 && depth == 0;
	}
	
	@Override
	public abstract String toString();
	
	public abstract String toTreeString();

}
