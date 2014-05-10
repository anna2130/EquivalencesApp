package treeBuilder;

import java.util.HashMap;
import java.util.SortedSet;

public class UnaryOperator extends Node {
	
	private Node child;

	public UnaryOperator(int key, int depth, String value) {
		super(key, depth, value);
	}
	
	public void setChild(Node n) {
		child = n;
		child.setParent(this);
	}
	
	public Node getChild() {
		return child;
	}

	@Override
	public boolean hasChildren() {
		return child != null;
	}

	@Override
	public Node[] getChildren() {
		Node[] children = new Node[1];
		
		if (child != null)
			children[0] = child;
		
		return children;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getValue());
		sb.append(child);
		
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
		
		if (child != null)
			sb.append(child.toTreeString());
		sb.append(")");
		
		return sb.toString();
	}

	@Override
	public Node clone() {
		UnaryOperator clone = new UnaryOperator(getKey(), getDepth(), getValue());
		
		clone.setChild(getChild().clone());
		clone.setParent(getParent());
		
		return clone;
	}

	@Override
	public boolean getTruthValue(HashMap<String, Integer> values) {
		return !child.getTruthValue(values);
	}

	@Override
	public SortedSet<String> getVariables() {
		SortedSet<String> variables = new java.util.TreeSet<String>();
		variables.addAll(child.getVariables());
		return variables;
	}

}
