package treeBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.SortedSet;

public class Atom extends Node {
	
	public Atom(int key, int depth, String value, LinkedList<String> vars) {
		super(key, depth, value, vars);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getValue());
		
		LinkedList<String> vars = getVars();
		if (vars != null)
			for (String var : vars)
				sb.append(var);
		
		return sb.toString();
	}
	
	public String toTreeString() {
		return getKey() + "-" + getDepth() + ": " + getValue();
	}

	@Override
	public Node clone() {
		Atom clone = new Atom(getKey(), getDepth(), getValue(), getVars());
		clone.setParent(getParent());
		return clone;
	}
	
	public boolean getTruthValue(HashMap<String, Integer> values) {
		if (isTop())
			return true;
		else if (isBottom())
			return false;
		else
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
	
	@Override
	public boolean isTop() {
		return getValue().equals("┬");
	}
	
	@Override
	public boolean isBottom() {
		return getValue().equals("⊥");
	}

	// Atomic formulas. If φ is an atomic formula then x is free in φ if and only 
	// if x occurs in φ. Moreover, there are no bound variables in any atomic formula.
	@Override
	public boolean hasFree(String variable) {
		return getVars().contains(variable);
	}

	@Override
	public boolean isBound(String variable) {
		return false;
	}

	@Override
	public void replaceVariable(String oldVar, String newVar) {
		LinkedList<String> vars = getVars();
		if (isAll() || isExists() && vars.contains(oldVar) && !vars.contains(newVar)) {
			vars.remove(oldVar);
			vars.add(newVar);
		}
	}
}
