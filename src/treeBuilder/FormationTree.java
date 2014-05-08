package treeBuilder;

public class FormationTree {

	private Node root;
	
	public FormationTree() {
		this.root = null;
	}
	
	public FormationTree(Node root) {
		this.root = root;
	}
	
	public void setRoot(Node n) {
		root = n;
		root.setKey(0);
		root.setDepth(0);
	}
	
	public Node getRoot() {
		return root;
	}
	
	public void addNode(Node n) {
		boolean rootExists = root != null;
		
		if (rootExists) {
			int parentKey = n.getKey() >> 1;
			int depth = n.getDepth() - 1;
			Node parent = findNode(parentKey, depth);
			
			if (parent instanceof UnaryOperator) {
				((UnaryOperator) parent).setChild(n);
				n.setParent(parent);
			} else {
				if (n.getKey() % 2 == 0) {
					((BinaryOperator) parent).setLeftChild(n);
					n.setParent(parent);
				} else {
					((BinaryOperator) parent).setRightChild(n);
					n.setParent(parent);
				}
			}
			
		} else {
			root = n;
		}
	}
	
	public Node findNode(int key, int depth) {
		Node n = root;
		
		for (int i = 0; i < depth; i++) {
			int leftMostBit = key >> depth - i - 1;
		
			// Remove left most bit from key
			if (leftMostBit == 1) {
				int mask = 1 << depth - i - 1;
				key = key & ~mask;
			}
			
			n = n.getChildren()[leftMostBit];
		}
		
		return n;
	}
	
//	public Node findParent(int key, int depth) {
//		int parentKey = key >> 1;
//		int parentDepth = depth - 1;
//		
//		return findNode(parentKey, parentDepth);
//	}
	
	public int numNodes() {
		int numNodes = 1;
		
		if (root.hasChildren()) {			
			Node[] children = root.getChildren();
			for (int i = 0; i < children.length; ++i) {
				numNodes += children[i].numNodes();
			}
		}
		
		return numNodes;
	}
	
	
	
	public boolean equalSubTrees(Node n1, Node n2) {		
		if (n1.getValue().equals(n2.getValue())) {
			if (n1 instanceof Atom)
				return true;
			
			Node[] children1 = n1.getChildren();
			Node[] children2 = n2.getChildren();
			if (n1 instanceof UnaryOperator)
				return equalSubTrees(children1[0], children2[0]);
			else
				return equalSubTrees(children1[0], children2[0]) 
						&& equalSubTrees(children1[1], children2[1]);
		}
		return false;
	}
	
	@Override
	public String toString() {
		String s = root.toString();
		if (s.charAt(0) == '(')
			s = s.substring(1, s.length()-1);
		return s;
	}
	
	public String toTreeString() {
		return root.toTreeString();
	}
}
