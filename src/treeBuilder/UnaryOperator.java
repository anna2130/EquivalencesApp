package treeBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

public class UnaryOperator extends Node {
	
	private Node child;

	public UnaryOperator(int key, int depth, String value, LinkedList<String> vars) {
		super(key, depth, value, vars);
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
		LinkedList<String> vars = super.getVars();

		sb.append(super.getValue());
		if ((isExists() || isAll()) && vars != null) {
			for (String var : super.getVars())
				sb.append(var);
			if (child.isExists() || child.isAll()) {
				sb.append(child);
			} else {
				sb.append("[");
				
				String s = child.toString();
				if (s.charAt(0) == '(')
					s = s.substring(1, s.length()-1);
				sb.append(s);
				
				sb.append("]");
			}
		} else {
			sb.append(child);
		}
		
		return sb.toString();
	}
	
	public String toTreeString() {
		StringBuilder sb = new StringBuilder();
		LinkedList<String> vars = super.getVars();
		
		sb.append(getKey());
		sb.append("-");
		sb.append(getDepth());
		sb.append(": ");
		sb.append(getValue());
		if (vars != null)
			for (String var : super.getVars())
				sb.append(var);
		sb.append(" (");
		
		if (child != null)
			sb.append(child.toTreeString());
		sb.append(")");
		
		return sb.toString();
	}

	@Override
	public Node clone() {
		UnaryOperator clone = new UnaryOperator(getKey(), getDepth(), getValue(), getVars());
		
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
	
	@Override
	public SortedSet<String> getAtoms() {
		SortedSet<String> atoms = new TreeSet<String>();
		atoms.addAll(child.getAtoms());
		return atoms;
	}

	@Override
	public boolean isNot() {
		return getValue().equals("¬");
	}

	@Override
	public boolean isAll() {
		return getValue().charAt(0) == '∀';
	}

	@Override
	public boolean isExists() {
		return getValue().charAt(0) == '∃';
	}

	// Negation. x is free in \negφ if and only if x is free in φ. x is bound in \negφ if and only if x is bound in φ.
	// Quantifiers. x is free in \forally φ if and only if x is free in φ and x is a different symbol from y. 
	// Also, x is bound in \forally φ if and only if x is y or x is bound in φ. 
	// The same rule holds with \exists in place of \forall.
	@Override
	public boolean hasFree(String variable) {
		if (isNot())
			return child.hasFree(variable);
		else
			return !getVars().contains(variable) && child.hasFree(variable);
	}

	@Override
	public boolean isBound(String variable) {
		if (isNot())
			return child.isBound(variable);
		else
			return getVars().contains(variable) || child.isBound(variable);
	}
	
	@Override
	public void replaceVariable(String oldVar, String newVar) {
		LinkedList<String> vars = getVars();
		if ((isAll() || isExists()) && vars.contains(oldVar) && !vars.contains(newVar)) {
			int location = vars.indexOf(oldVar);
			vars.set(location, newVar);
			child.replaceVariable(oldVar, newVar);
		}
	}
	
	@Override
	public LinkedList<String> getUsedQuantifiedVars() {
		LinkedList<String> usedVars = new LinkedList<String>();
		if (isAll() || isExists())
			usedVars.addAll(getVars());
		usedVars.addAll(child.getUsedQuantifiedVars());
		return usedVars;
	}
}
