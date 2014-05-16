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
		
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
		     switch (i) {
				case 0:		rules.add("a^b 			|- 	b^a				-- Commutativity");
							break;
				case 1:		rules.add("a^a			|-  a 				-- Idempotence");
							break;
				case 2:		rules.add("a^┬			|- 	a");
							break;
				case 3:		rules.add("┬^a			|-  a");
							break;
				case 4:		rules.add("⊥^a			|-  ⊥");
							break;
				case 5:		rules.add("a^⊥			|-  ⊥");
							break;
				case 6:		rules.add("a^¬a			|-  ⊥");
							break;
				case 7:		rules.add("¬a^a			|-  ⊥");
							break;
				case 8:		rules.add("a^(b^c)		|-  (a^b)^c			-- Associativity");
							break;
				case 9:		rules.add("(a^b)^c  	|- 	a^(b^c)			-- Associativity");
							break;
				case 10:	rules.add("a^¬b 		|-	¬(a→b)");
							break;
				case 11:	rules.add("(a→b)^(b→a)	|-  a↔b");
							break;
				case 12:	rules.add("¬a^¬b		|-	¬(avb)			-- De Morgan laws");
							break;
				case 13:	rules.add("a^(bvc) 		|- 	(a^b)v(a^c)		-- Distributitivity");
							break;
				case 14:	rules.add("(avb)^c		|-  (a^c)v(b^c)		-- Distributitivity");
							break;
				case 15:	rules.add("(avb)^(avc)	|- 	av(b^c)			-- Distributitivity");
							break;
				case 16:	rules.add("(avc)^(bvc)	|-	(a^b)vc			-- Distributitivity");
							break;
				case 17:	rules.add("a^(avb)  	|-	a				-- Absoption");
							break;
				case 18:	rules.add("(avb)^a  	|-	a				-- Absoption");
							break;
				case 19:	rules.add("avb			|- 	bva				-- Commutativity");
							break;
				case 20:	rules.add("ava			|- 	a 				-- Idempotence");
							break;
				case 21:	rules.add("┬va			|-  ┬");
							break;
				case 22:	rules.add("av┬			|-  ┬");
							break;
				case 23:	rules.add("av¬a			|-  ┬");
							break;
				case 24:	rules.add("¬ava			|-  ┬");
							break;
				case 25:	rules.add("av⊥			|-  a");
							break;
				case 26:	rules.add("⊥va			|-  a");
							break;
				case 27:	rules.add("av(bvc)		|-  (avb)vc			-- Associativity");
							break;
				case 28:	rules.add("(avb)vc  	|- 	av(bvc)			-- Associativity");
							break;
				case 29:	rules.add("(a^b)v(¬a^¬b)	|-	a↔b");
							break;
				case 30:	rules.add("(a^¬b)v(¬a^b) 	|-  ¬(a↔b)");
							break;
				case 31:	rules.add("¬av¬b	|-  ¬(a^b)			-- De Morgan laws");
							break;
				case 32:	rules.add("av(b^c)	|- 	(avb)^(avc)		-- Distributitivity");
							break;
				case 33:	rules.add("(a^b)vc	|-	(avc)^(bvc)		-- Distributitivity");
							break;
				case 34:	rules.add("(a^b)v(a^c) 	|- 	a^(bvc)		-- Distributitivity");
							break;
				case 35:	rules.add("(a^c)v(b^c)	|-  (avb)^c		-- Distributitivity");
							break;
				case 36:	rules.add("av(a^b)	|-  a				-- Absoption");
							break;
				case 37:	rules.add("(a^b)va	|-  a				-- Absoption");
							break;
				case 38:	rules.add("¬avb		|- 	a→b");
							break;
				case 39:	rules.add("¬┬		|-  ⊥");
							break;
				case 40:	rules.add("¬⊥		|-  ┬");
							break;
				case 41:	rules.add("¬¬a 		|- 	a");
							break;
				case 42:	rules.add("¬a		|-  a→⊥");
							break;
				case 43:	rules.add("¬(a→b) 	|-	a^¬b");
							break;
				case 44:	rules.add("¬(av¬b)	|- 	a→b");
							break;
				case 45:	rules.add("¬(a↔b) 	|- 	a↔¬b");
							break;
				case 46:	rules.add("¬(a↔b) 	|-  ¬a↔b");
							break;
				case 47:	rules.add("¬(a↔b) 	|-  (a^¬b)v(¬a^b)	-- Exclusive or of A and b");
							break;
				case 48:	rules.add("¬(a^b)	|-  ¬av¬b			-- De Morgan laws");
							break;
				case 49:	rules.add("¬(avb)	|-	¬a^¬b			-- De Morgan laws");
							break;
				case 50:	rules.add("a→a		|- 	┬");
							break;
				case 51:	rules.add("┬→a		|- 	a");
							break;
				case 52:	rules.add("a→┬		|- 	┬");
							break;
				case 53:	rules.add("⊥→a		|- 	┬");
							break;
				case 54:	rules.add("a→⊥		|- 	¬a");
							break;
				case 55:	rules.add("a→b 		|- 	¬avb");
							break;
				case 56:	rules.add("a→b 		|- 	¬(av¬b)");
							break;
				case 57:	rules.add("a↔b				|-  (a→b)^(b→a)");
							break;
				case 58:	rules.add("a↔b				|-	(a^b)v(¬a^¬b)");
							break;
				case 59:	rules.add("a↔¬b 			|- 	¬(a↔b)");
							break;
				case 60:	rules.add("¬a↔b 			|-  ¬(a↔b)");
							break;
				case 61:	rules.add("a		|-  a^a");
							break;
				case 62:	rules.add("a		|-  a^┬");
							break;
				case 63:	rules.add("a		|-  ava");
							break;
				case 64:	rules.add("a		|-  av⊥");
							break;
				case 65:	rules.add("a		|-  ¬¬a");
							break;
				case 66:	rules.add("a		|-  ┬→a");
							break;
				case 67:	rules.add("⊥		|-  ¬┬");
							break;
				case 68:	rules.add("┬		|-  ¬⊥");
							break;
				case 69:	rules.add("⊥		|-  a^⊥");
							break;
				case 70:	rules.add("⊥		|-  a^¬a");
							break;
				case 71:	rules.add("┬		|-  av┬");
							break;
				case 72:	rules.add("┬		|-  av¬a");
							break;
				case 73:	rules.add("┬		|- 	a→a");
							break;
				case 74:	rules.add("┬		|- 	a→┬");
							break;
				case 75:	rules.add("┬		|- 	⊥→a");
							break;
				case 76:	rules.add("a		|-  av(a^b)");
							break;
				case 77:	rules.add("a  		|-	a^(avb)");
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
			leftChild = ((UnaryOperator) rightChild).getChild();
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
