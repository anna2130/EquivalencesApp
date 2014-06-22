package treeBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.SortedSet;

public abstract class Node {

	private int key;
	private int depth;
	private String value;
	private Node parent;
	private LinkedList<String> vars;
	
	public Node(int key, int depth, String value, LinkedList<String> variables) {
		this.setKey(key);
		this.setDepth(depth);
		this.setValue(value);
		this.setVars(variables);
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
	
	public boolean isNot() {
		return false;
	}

	public boolean isAnd() {
		return false;
	}
	
	public boolean isOr() {
		return false;
	}
	
	public boolean isImplies() {
		return false;
	}

	public boolean isIff() {
		return false;
	}
	
	public boolean isAtom() {
		return false;
	}
	
	public boolean isTop() {
		return false;
	}
	
	public boolean isBottom() {
		return false;
	}

	public boolean isAll() {
		return false;
	}

	public boolean isExists() {
		return false;
	}
	
	public abstract Node clone();
	
	public abstract boolean getTruthValue(HashMap<String, Integer> values);
	
	public abstract SortedSet<String> getVariables();

	public abstract SortedSet<String> getAtoms();
	
	@Override
	public abstract String toString();
	
	public abstract String toTreeString();

	public int maxDepth() {
		int depth = getDepth();
		
		if (hasChildren()) {
			Node[] children = getChildren();
			for (int i = 0; i < children.length; ++i) {
				int newDepth = children[i].maxDepth();
				if (newDepth > depth)
					depth = newDepth;
			}
		}
		return depth;
	}

	public LinkedList<String> getVars() {
		return vars;
	}

	public void setVars(LinkedList<String> vars) {
		this.vars = vars;
	}
	
	public abstract void replaceVariable(String oldVar, String newVar);

	public abstract boolean hasFree(String variable);
	
	public abstract boolean isBound(String variable);
	
	public double getWidth() {
		double width = 20;
		
		if (vars != null)
			for (int i = 0; i < vars.size(); ++i)
				width += 18;
		
		return width;
	}
	
	public double getHeight() {
		return 20;
	}

	public LinkedList<String> getUsedQuantifiedVars() {
		return new LinkedList<String>();
	}
}
