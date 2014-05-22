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
		
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
		     switch (i) {
				case 0:		rules.add("p^q 				|- 	q^p				-- Commutativity");
							break;
				case 1:		rules.add("p^p				|-  p 				-- Idempotence");
							break;
				case 2:		rules.add("p^┬				|- 	p");
							break;
				case 3:		rules.add("┬^p				|-  p");
							break;
				case 4:		rules.add("p^a				|-  ⊥");
							break;
				case 5:		rules.add("p^⊥				|-  ⊥");
							break;
				case 6:		rules.add("p^¬p				|-  ⊥");
							break;
				case 7:		rules.add("¬p^p				|-  ⊥");
							break;
				case 8:		rules.add("p^(q^r)			|-  (p^q)^r			-- Associativity");
							break;
				case 9:		rules.add("(p^q)^r  		|- 	a^(q^r)			-- Associativity");
							break;
				case 10:	rules.add("p^¬q 			|-	¬(p→q)");
							break;
				case 11:	rules.add("(p→q)^(q→p)		|-  p↔q");
							break;
				case 12:	rules.add("¬p^¬q			|-	¬(pvq)			-- De Morgan laws");
							break;
				case 13:	rules.add("p^(qvr) 			|- 	(p^q)v(p^r)		-- Distributitivity");
							break;
				case 14:	rules.add("(pvq)^r			|-  (p^r)v(q^r)		-- Distributitivity");
							break;
				case 15:	rules.add("(pvq)^(pvr)		|- 	pv(q^r)			-- Distributitivity");
							break;
				case 16:	rules.add("(pvr)^(pvr)		|-	(p^q)vr			-- Distributitivity");
							break;
				case 17:	rules.add("p^(pvq)  		|-	p				-- Absorbtion");
							break;
				case 18:	rules.add("(pvq)^p  		|-	p				-- Absorbtion");
							break;
				case 19:	rules.add("pvq				|- 	qvp				-- Commutativity");
							break;
				case 20:	rules.add("pvp				|- 	p 				-- Idempotence");
							break;
				case 21:	rules.add("┬va				|-  ┬");
							break;
				case 22:	rules.add("pv┬				|-  ┬");
							break;
				case 23:	rules.add("pv¬p				|-  ┬");
							break;
				case 24:	rules.add("¬pvp				|-  ┬");
							break;
				case 25:	rules.add("pv⊥				|-  p");
							break;
				case 26:	rules.add("⊥vp				|-  p");
							break;
				case 27:	rules.add("pv(qvr)			|-  (pvq)vr			-- Associativity");
							break;
				case 28:	rules.add("(pvq)vr  		|- 	pv(qvr)			-- Associativity");
							break;
				case 29:	rules.add("(p^q)v(¬p^¬q)	|-	p↔q");
							break;
				case 30:	rules.add("(p^¬q)v(¬p^q) 	|-  ¬(p↔q)");
							break;
				case 31:	rules.add("¬pv¬q			|-  ¬(p^q)			-- De Morgan laws");
							break;
				case 32:	rules.add("pv(q^r)			|- 	(pvq)^(pvr)		-- Distributitivity");
							break;
				case 33:	rules.add("(p^q)vr			|-	(pvr)^(qvr)		-- Distributitivity");
							break;
				case 34:	rules.add("(p^q)v(p^r) 		|- 	p^(qvr)			-- Distributitivity");
							break;
				case 35:	rules.add("(p^r)v(q^r)		|-  (pvq)^r			-- Distributitivity");
							break;
				case 36:	rules.add("pv(p^q)			|-  p				-- Absorption");
							break;
				case 37:	rules.add("(p^q)vp			|-  p				-- Absorption");
							break;
				case 38:	rules.add("¬pvq				|- 	p→q");
							break;
				case 39:	rules.add("¬┬				|-  ⊥");
							break;
				case 40:	rules.add("¬⊥				|-  ┬");
							break;
				case 41:	rules.add("¬¬p 				|- 	p");
							break;
				case 42:	rules.add("¬p				|-  p→⊥");
							break;
				case 43:	rules.add("¬(p→q) 			|-	p^¬p");
							break;
				case 44:	rules.add("¬(p^¬q)			|- 	p→q");
							break;
				case 45:	rules.add("¬(p↔q) 			|- 	p↔¬q");
							break;
				case 46:	rules.add("¬(p↔q) 			|-  ¬p↔q");
							break;
				case 47:	rules.add("¬(p↔q) 			|-  (p^¬q)v(¬p^q)	-- Exclusive or of p and q");
							break;
				case 48:	rules.add("¬(p^q)			|-  ¬pv¬q			-- De Morgan laws");
							break;
				case 49:	rules.add("¬(pvq)			|-	¬p^¬q			-- De Morgan laws");
							break;
				case 50:	rules.add("p→p				|- 	┬");
							break;
				case 51:	rules.add("┬→p				|- 	p");
							break;
				case 52:	rules.add("p→┬				|- 	┬");
							break;
				case 53:	rules.add("⊥→p				|- 	┬");
							break;
				case 54:	rules.add("p→⊥				|- 	¬p");
							break;
				case 55:	rules.add("p→q 				|- 	¬pvq");
							break;
				case 56:	rules.add("p→q 				|- 	¬(p^¬q)");
							break;
				case 57:	rules.add("p↔q				|-  (p→q)^(q→p)");
							break;
				case 58:	rules.add("p↔q				|-	(p^q)v(¬p^¬q)");
							break;
				case 59:	rules.add("p↔¬q 			|- 	¬(p↔q)");
							break;
				case 60:	rules.add("¬p↔q 			|-  ¬(p↔q)");
							break;
				case 61:	rules.add("p				|-  p^p");
							break;
				case 62:	rules.add("p				|-  p^┬");
							break;
				case 63:	rules.add("p				|-  pvp");
							break;
				case 64:	rules.add("p				|-  pv⊥");
							break;
				case 65:	rules.add("p				|-  ¬¬p");
							break;
				case 66:	rules.add("p				|-  ┬→p");
							break;
				case 67:	rules.add("⊥				|-  ¬┬");
							break;
				case 68:	rules.add("┬				|-  ¬⊥");
							break;
				case 69:	rules.add("⊥				|-  p^⊥");
							break;
				case 70:	rules.add("⊥				|-  p^¬p");
							break;
				case 71:	rules.add("┬				|-  pv┬");
							break;
				case 72:	rules.add("┬				|-  pv¬p");
							break;
				case 73:	rules.add("┬				|- 	p→p");
							break;
				case 74:	rules.add("┬				|- 	p→┬");
							break;
				case 75:	rules.add("┬				|- 	⊥→p");
							break;
				case 76:	rules.add("p				|-  pv(p^q)");
							break;
				case 77:	rules.add("p  				|-	p^(pvq)");
							break;
		     }
		 }
		
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
		
		if (rightChild.isNot()) {
			rightChild = ((UnaryOperator) rightChild).getChild();
			return tree.equalSubTrees(leftChild, rightChild);
		} else
			return false;
	}
	
	boolean leftIsNotOfRight(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getChildren()[0];
		Node rightChild = node.getChildren()[1];
		
		if (leftChild.isNot()) {
			leftChild = ((UnaryOperator) leftChild).getChild();
			return tree.equalSubTrees(leftChild, rightChild);
		} else
			return false;
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
