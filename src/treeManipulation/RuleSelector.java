package treeManipulation;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import treeBuilder.BinaryOperator;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.UnaryOperator;

public class RuleSelector {

	private int noRules = 78;
	
	public int getNoRules() {
		return noRules;
	}
	
	public String[] rulesToString(BitSet bs, FormationTree tree, Node node) {
		List<String> rules = new ArrayList<String>();
		RuleViewer rv = new RuleViewer();
		
		String s = node.toString();
		String nodeBefore = s.substring(1, s.length() - 1);
		
		if (bs.get(0))
			rules.add(nodeBefore + " |- " + 
					rv.viewCommutativity((BinaryOperator) node) + "\n");
		if (bs.get(1))
			rules.add(nodeBefore + " |- " + 
					rv.viewIdempotence((BinaryOperator) node) + "\n");
		if (bs.get(2))
			rules.add(nodeBefore + " |- " + 
					rv.viewLeftAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(3))
			rules.add(nodeBefore + " |- " + 
					rv.viewRightAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(4))
			rules.add(nodeBefore + " |- " + 
					rv.viewCommutativity((BinaryOperator) node) + "\n");
		if (bs.get(5))
			rules.add(nodeBefore + " |- " + 
					rv.viewIdempotence((BinaryOperator) node) + "\n");
		if (bs.get(6))
			rules.add(nodeBefore + " |- " + 
					rv.viewLeftAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(7))
			rules.add(nodeBefore + " |- " + 
					rv.viewRightAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(8))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(9))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(10))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(11))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(12))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(13))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(14))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(15))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(16))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(17))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(18))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(19))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(20))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(21))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(22))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(23))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(24))
			rules.add(nodeBefore + " |- " + "" + "\n");
		if (bs.get(25))
			rules.add(nodeBefore + " |- " + "" + "\n");
		
		String[] arr = new String[rules.size()];
		return rules.toArray(arr);
	}
	
	boolean isIdempotent(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getChildren()[0];
		Node rightChild = node.getChildren()[1];
			
		return tree.equalSubTrees(leftChild, rightChild);
	}
	
	boolean rightIsNotOfLeft(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getChildren()[0];
		Node rightChild = node.getChildren()[1];
		
		if (rightChild.isNot())
			rightChild = ((UnaryOperator) rightChild).getChild();
			
		return tree.equalSubTrees(leftChild, rightChild);
	}
	
	boolean leftIsNotOfRight(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getChildren()[0];
		Node rightChild = node.getChildren()[1];
		
		if (leftChild.isNot())
			leftChild = ((UnaryOperator) rightChild).getChild();
			
		return tree.equalSubTrees(leftChild, rightChild);
	}
	
	boolean canBeAbsorbedLeft(FormationTree tree, BinaryOperator node) {
		Node n1 = node.getLeftChild();
		Node n2 = ((BinaryOperator) node.getRightChild()).getLeftChild();
		Node n3 = ((BinaryOperator) node.getRightChild()).getRightChild();
		
		return tree.equalSubTrees(n1, n2) || tree.equalSubTrees(n1, n3);
	}
	
	boolean canBeAbsorbedRight(FormationTree tree, BinaryOperator node) {
		Node n1 = node.getRightChild();
		Node n2 = ((BinaryOperator) node.getLeftChild()).getLeftChild();
		Node n3 = ((BinaryOperator) node.getLeftChild()).getRightChild();
		
		return tree.equalSubTrees(n1, n2) || tree.equalSubTrees(n1, n3);
	}
	
	boolean isLeftAssociative(FormationTree tree, BinaryOperator node, String op) {
		return node.getValue().equals(op) && node.getChildren()[1].getValue().equals(op);
	}
	
	boolean isRightAssociative(FormationTree tree, BinaryOperator node, String op) {
		return node.getValue().equals(op) && node.getChildren()[0].getValue().equals(op);
	}
}
