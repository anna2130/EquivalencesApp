package treeManipulation;

import java.util.BitSet;

import treeBuilder.BinaryOperator;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.UnaryOperator;
import android.util.SparseArray;

public class RuleSelector {

	private int noRules = 78;
	
	public int getNoRules() {
		return noRules;
	}
	
	public SparseArray<String> rulesToStringMap(BitSet bs, FormationTree tree, Node node) {
		SparseArray<String> rules = new SparseArray<String>();
//		List<String> rules = new ArrayList<String>();
		
		for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {
		     switch (i) {
				case 0:		rules.put(0, "p∧q					|- 	q∧p				-- Commutativity");
							break;
				case 1:		rules.put(1, "p∧p					|-  p 				-- Idempotence");
							break;
				case 2:		rules.put(2, "p∧┬					|- 	p");
							break;
				case 3:		rules.put(3, "┬∧p					|-  p");
							break;
				case 4:		rules.put(4, "p∧a					|-  ⊥");
							break;
				case 5:		rules.put(5, "p∧⊥					|-  ⊥");
							break;
				case 6:		rules.put(6, "p∧¬p				|-  ⊥");
							break;
				case 7:		rules.put(7, "¬p∧p				|-  ⊥");
							break;
				case 8:		rules.put(8, "p∧(q∧r)			|-  (p∧q)∧r			-- Associativity");
							break;
				case 9:		rules.put(9, "(p∧q)∧r			|- 	a∧(q∧r)			-- Associativity");
							break;
				case 10:	rules.put(10, "p∧¬q				|-	¬(p→q)");
							break;
				case 11:	rules.put(11, "(p→q)∧(q→p)		|-  p↔q");
							break;
				case 12:	rules.put(12, "¬p∧¬q			|-	¬(p∨q)			-- De Morgan laws");
							break;
				case 13:	rules.put(13, "p∧(q∨r)			|- 	(p∧q)∨(p∧r)		-- Distributitivity");
							break;
				case 14:	rules.put(14, "(p∨q)∧r			|-  (p∧r)∨(q∧r)		-- Distributitivity");
							break;
				case 15:	rules.put(15, "(p∨q)∧(p∨r)		|- 	p∨(q∧r)			-- Distributitivity");
							break;
				case 16:	rules.put(16, "(p∨r)∧(p∨r)		|-	(p∧q)∨r			-- Distributitivity");
							break;
				case 17:	rules.put(17, "p∧(p∨q)			|-	p				-- Absorbtion");
							break;
				case 18:	rules.put(18, "(p∨q)∧p			|-	p				-- Absorbtion");
							break;
				case 19:	rules.put(19, "p∨q					|- 	q∨p				-- Commutativity");
							break;
				case 20:	rules.put(20, "p∨p					|- 	p 				-- Idempotence");
							break;
				case 21:	rules.put(21, "┬∨a					|-  ┬");
							break;
				case 22:	rules.put(22, "p∨┬					|-  ┬");
							break;
				case 23:	rules.put(23, "p∨¬p				|-  ┬");
							break;
				case 24:	rules.put(24, "¬p∨p				|-  ┬");
							break;
				case 25:	rules.put(25, "p∨⊥				|-  p");
							break;
				case 26:	rules.put(26, "⊥∨p				|-  p");
							break;
				case 27:	rules.put(27, "p∨(q∨r)			|-  (p∨q)∨r			-- Associativity");
							break;
				case 28:	rules.put(28, "(p∨q)∨r			|- 	p∨(q∨r)			-- Associativity");
							break;
				case 29:	rules.put(29, "(p∧q)∨(¬p∧¬q)	|-	p↔q");
							break;
				case 30:	rules.put(30, "(p∧¬q)∨(¬p∧q) 	|-  ¬(p↔q)");
							break;
				case 31:	rules.put(31, "¬p∨¬q			|-  ¬(p∧q)			-- De Morgan laws");
							break;
				case 32:	rules.put(32, "p∨(q∧r)			|- 	(p∨q)∧(p∨r)		-- Distributitivity");
							break;
				case 33:	rules.put(33, "(p∧q)∨r			|-	(p∨r)∧(q∨r)		-- Distributitivity");
							break;
				case 34:	rules.put(34, "(p∧q)∨(p∧r)		|- 	p∧(q∨r)			-- Distributitivity");
							break;
				case 35:	rules.put(35, "(p∧r)∨(q∧r)		|-  (p∨q)∧r			-- Distributiti∨ity");
							break;
				case 36:	rules.put(36, "p∨(p∧q)			|-  p				-- Absorption");
							break;
				case 37:	rules.put(37, "(p∧q)∨p			|-  p				-- Absorption");
							break;
				case 38:	rules.put(38, "¬p∨q				|- 	p→q");
							break;
				case 39:	rules.put(39, "¬┬				|-  ⊥");
							break;
				case 40:	rules.put(40, "¬⊥				|-  ┬");
							break;
				case 41:	rules.put(41, "¬¬p				|- 	p");
							break;
				case 42:	rules.put(42, "¬p					|-  p→⊥");
							break;
				case 43:	rules.put(43, "¬(p→q)			|-	p∧¬p");
							break;
				case 44:	rules.put(44, "¬(p∧¬q)			|- 	p→q");
							break;
				case 45:	rules.put(45, "¬(p↔q)			|- 	p↔¬q");
							break;
				case 46:	rules.put(46, "¬(p↔q)			|-  ¬p↔q");
							break;
				case 47:	rules.put(47, "¬(p↔q)			|-  (p∧¬q)∨(¬p∧q)	-- Exclusive or of p and q");
							break;
				case 48:	rules.put(48, "¬(p∧q)			|-  ¬p∨¬q			-- De Morgan laws");
							break;
				case 49:	rules.put(49, "¬(p∨q)			|-	¬p∧¬q			-- De Morgan laws");
							break;
				case 50:	rules.put(50, "p→p				|- 	┬");
							break;
				case 51:	rules.put(51, "┬→p				|- 	p");
							break;
				case 52:	rules.put(52, "p→┬				|- 	┬");
							break;
				case 53:	rules.put(53, "⊥→p				|- 	┬");
							break;
				case 54:	rules.put(54, "p→⊥				|- 	¬p");
							break;
				case 55:	rules.put(55, "p→q				|- 	¬p∨q");
							break;
				case 56:	rules.put(56, "p→q				|- 	¬(p∧¬q)");
							break;
				case 57:	rules.put(57, "p↔q				|-  (p→q)∧(q→p)");
							break;
				case 58:	rules.put(58, "p↔q				|-	(p∧q)∨(¬p∧¬q)");
							break;
				case 59:	rules.put(59, "p↔¬q				|- 	¬(p↔q)");
							break;
				case 60:	rules.put(60, "¬p↔q				|-  ¬(p↔q)");
							break;
				case 61:	rules.put(61, "p				|-  p∧p");
							break;
				case 62:	rules.put(62, "p				|-  p∧┬");
							break;
				case 63:	rules.put(63, "p				|-  p∨p");
							break;
				case 64:	rules.put(64, "p				|-  p∨⊥");
							break;
				case 65:	rules.put(65, "p				|-  ¬¬p");
							break;
				case 66:	rules.put(66, "p				|-  ┬→p");
							break;
				case 67:	rules.put(67, "⊥			|-  ¬┬");
							break;
				case 68:	rules.put(68, "┬				|-  ¬⊥");
							break;
				case 69:	rules.put(69, "⊥			|-  p∧⊥");
							break;
				case 70:	rules.put(70, "⊥			|-  p∧¬p");
							break;
				case 71:	rules.put(71, "┬				|-  p∨┬");
							break;
				case 72:	rules.put(72, "┬				|-  p∨¬p");
							break;
				case 73:	rules.put(73, "┬				|- 	p→p");
							break;
				case 74:	rules.put(74, "┬				|- 	p→┬");
							break;
				case 75:	rules.put(75, "┬				|- 	⊥→p");
							break;
				case 76:	rules.put(76, "p				|-  p∨(p∧q)");
							break;
				case 77:	rules.put(77, "p  			|-	p∧(p∨q)");
							break;
				case 78:	rules.put(78, "∀p[¬φ]   |- 	¬∃p[φ]");
							break;
				case 79:	rules.put(79, "∀p∀q[φ]	|-	∀q∀p[φ]");
							break;
				case 80:	rules.put(80, "∀p[φ∧ψ] 	|- 	∀p[φ]∧∀p[ψ]");
							break;
				case 81:	rules.put(81, "∀p[φ∨θ] 	|- 	∀p[φ]∨θ");
							break;
				case 82:	rules.put(82, "∀p[φ→θ]  |-  ∃p[φ]→θ");
							break;
				case 83:	rules.put(83, "∀p[θ→φ]	|-  θ→∀p[φ]");
							break;
				case 84:	rules.put(84, "∀p[θ]	|-  θ");
							break;
				case 86:	rules.put(86, "∃p[¬φ]  	|-	¬∀p[φ]");
							break;
				case 87:	rules.put(87, "∃p∃q[φ] 	|-  ∃q∃p[φ]");
							break;
				case 88:	rules.put(88, "∃p[φ∨ψ]	|-  ∃p[φ]∨∃p[ψ]");
							break;
				case 89:	rules.put(89, "∃p[φ∧θ]	|-  ∃p[φ]∧θ");
							break;
				case 90:	rules.put(90, "∃p[φ→θ]  |-  ∃p[φ]→θ");
							break;
				case 91:	rules.put(91, "∃p[θ→φ]  |-  θ→∃p[φ]");
							break;
				case 92:	rules.put(92, "∃p[θ]	|-  θ");
							break;
				case 93:	rules.put(93, "¬∀p[φ]	|- 	∃p[¬φ]");
							break;
				case 94:	rules.put(94, "¬∃p[φ]	|-	∀p[¬φ]");
							break;
				case 95:	rules.put(95, "∀p[φ]∧∀p[ψ] 	|-	∀p[φ∧ψ]");
							break;
				case 96:	rules.put(96, "∃p[φ]∧θ		|-  ∃p[φ∧θ]");
							break;
				case 97:	rules.put(97, "∀p[φ]∨θ		|- 	∀p[φ∨θ]");
							break;
				case 98:	rules.put(98, "∃p[φ]∨∃p[ψ]	|- 	∃p[φ∨ψ]");
							break;
				case 99:	rules.put(99, "∃p[φ]→θ 	|- 	∀p[φ→θ]");
							break;
				case 100:	rules.put(100, "θ→∀p[φ]	|- 	∀p[θ→φ]");
							break;
				case 101:	rules.put(101, "∃p[φ]→θ |- 	∃p[φ→θ]");
							break;
				case 102:	rules.put(102, "∀p[φ]	|-  ∀q[φ{p->q}]");
							break;
				case 103:	rules.put(103, "∃p[φ]	|-  ∃q[φ{p->q}]");
							break;
				case 104:	rules.put(104, "θ				|- 	∀p[θ]");
							break;
				case 105:	rules.put(105, "θ				|- 	∃p[θ]");
							break;
		     }
		 }
		return rules;
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
