package treeManipulation;

import treeBuilder.BinaryOperator;

public class RuleViewer {

	public String viewCommutativity(BinaryOperator node) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(node.getRightChild());
		sb.append(node.getValue());
		sb.append(node.getLeftChild());
		
		return sb.toString();
	}
	
	public String viewIdempotence(BinaryOperator node) {
		return node.getLeftChild().toString();
	}
	
	public String viewLeftAssociativity(BinaryOperator node) {
		StringBuilder sb = new StringBuilder();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		
		sb.append('(');
		sb.append(node.getLeftChild());
		sb.append(node.getValue());
		sb.append(rightChild.getLeftChild());
		sb.append(')');
		sb.append(rightChild.getValue());
		sb.append(rightChild.getRightChild());
		
		return sb.toString();
	}
	
	public String viewRightAssociativity(BinaryOperator node) {
		StringBuilder sb = new StringBuilder();
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		
		sb.append(leftChild.getLeftChild());
		sb.append(node.getValue());
		sb.append('(');
		sb.append(leftChild.getRightChild());
		sb.append(node.getValue());
		sb.append(node.getRightChild());
		sb.append(')');
		
		return sb.toString();
	}
}