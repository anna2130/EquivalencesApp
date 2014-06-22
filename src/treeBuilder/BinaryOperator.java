package treeBuilder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

public class BinaryOperator extends Node {

	private Node leftChild;
	private Node rightChild;
	
	public BinaryOperator(int key, int depth, String value, LinkedList<String> vars) {
		super(key, depth, value, vars);
	}

	public Node getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
		leftChild.setParent(this);
	}

	public Node getRightChild() {
		return rightChild;
	}

	public void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
		rightChild.setParent(this);
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

	@Override
	public Node clone() {
		BinaryOperator clone = new BinaryOperator(getKey(), getDepth(), getValue(), getVars());
		
		clone.setLeftChild(getLeftChild().clone());
		clone.setRightChild(getRightChild().clone());
		clone.setParent(getParent());
		
		return clone;
	}

	@Override
	public boolean getTruthValue(HashMap<String, Integer> values) {
		if (isAnd())
			return leftChild.getTruthValue(values) & rightChild.getTruthValue(values);
		else if (isOr())
			return leftChild.getTruthValue(values) | rightChild.getTruthValue(values);
		else if (isImplies())
			return !(leftChild.getTruthValue(values) & !rightChild.getTruthValue(values));
		else if (isIff())
			return leftChild.getTruthValue(values) == rightChild.getTruthValue(values);
		return false;
	}

	@Override
	public SortedSet<String> getVariables() {
		SortedSet<String> variables = new java.util.TreeSet<String>();
		variables.addAll(leftChild.getVariables());
		variables.addAll(rightChild.getVariables());
		return variables;
	}
	
	@Override
	public SortedSet<String> getAtoms() {
		SortedSet<String> atoms = new TreeSet<String>();
		atoms.addAll(leftChild.getAtoms());
		atoms.addAll(rightChild.getAtoms());
		return atoms;
	}

	@Override
	public boolean isAnd() {
		return getValue().equals("∧");
	}

	@Override
	public boolean isOr() {
		return getValue().equals("∨");
	}

	@Override
	public boolean isImplies() {
		return getValue().equals("→");
	}

	@Override
	public boolean isIff() {
		return getValue().equals("↔");
	}

	// Binary connectives. x is free in (φ \rightarrow ψ) if and only if x is 
	// free in either φ or ψ. 
	// x is bound in (φ \rightarrow ψ) if and only if x is bound in either φ or ψ. 
	// The same rule applies to any other binary connective in place of \rightarrow.
	@Override
	public boolean hasFree(String variable) {
		return leftChild.hasFree(variable) || rightChild.hasFree(variable);
	}

	@Override
	public boolean isBound(String variable) {
		return leftChild.isBound(variable) || rightChild.isBound(variable);
	}
	
	@Override
	public LinkedList<String> getUsedQuantifiedVars() {
		LinkedList<String> usedVars = new LinkedList<String>();
		usedVars.addAll(leftChild.getUsedQuantifiedVars());
		usedVars.addAll(rightChild.getUsedQuantifiedVars());
		return usedVars;
	}

	@Override
	public void replaceVariable(String oldVar, String newVar) {
		leftChild.replaceVariable(oldVar, newVar);
		rightChild.replaceVariable(oldVar, newVar);
	}

}
