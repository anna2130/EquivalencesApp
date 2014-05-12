package treeBuilder;

import java.util.HashMap;
import java.util.SortedSet;

public class Atom extends Node {
	
	public Atom(int key, int depth, String value) {
		super(key, depth, value);
	}

	@Override
	public String toString() {
		return super.getValue();
	}
	
	public String toTreeString() {
		return getKey() + "-" + getDepth() + ": " + getValue();
	}

	@Override
	public Node clone() {
		Atom clone = new Atom(getKey(), getDepth(), getValue());
		clone.setParent(getParent());
		return clone;
	}
	
	public boolean getTruthValue(HashMap<String, Integer> values) {
		return values.get(getValue()) == 1;
	}

	@Override
	public SortedSet<String> getVariables() {
		SortedSet<String> variables = new java.util.TreeSet<String>(); 
		variables.add(getValue());
		return variables;
	}

	@Override
	public boolean isAtom() {
		return true;
	}

}
