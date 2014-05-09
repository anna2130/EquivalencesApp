package treeManipulation;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import treeBuilder.BinaryOperator;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.UnaryOperator;

public class RuleSelector {

	/* The BitSet returns the rules applicable to a node in the order below:
	 * 0.  Commutativity of &
	 * 1.  Idempotence of & 
	 * 2.  Left Associativity of &
	 * 3.  Right Associativity of &
	 * 4.  Commutativity of |
	 * 5.  Idempotence of |
	 * 6.  Left Associativity of |
	 * 7.  Right Associativity of |
	 * 8.  !!A 		|- 	A
	 * 9.  A->B 	|- 	!A|B 			-- Also equivalent to !(A&!B). Separate rule?
	 * 10. !(A->B) 	|-	A|!B
	 * 11. A<->B	|-  (A->B)&(B->A)
	 * 12. A<->B	|-	(A&B)|(!A&!B)
	 * 13. !(A<->B) |- 	A<->!B
	 * 14. !(A<->B) |-  !A<->B
	 * 15. !(A<->B) |-  (A&!B)|(!A&B)	-- Exclusive or of A and B
	 * 16. !(A&B)	|-  !A|!B			-- De Morgan laws
	 * 17. !(A|B)	|-	!A&!B
	 * 18. A&(B|C) 	|- 	(A&B)|(A&C)		-- Distributitivity
	 * 19. (A|B)&C	|-  (A&C)|(B&C)
	 * 20. A|(B&C)	|- 	(A|B)&(A|C)
	 * 21. (A&B)|C	|-	(A|C)&(B|C)
	 * 22. A&(A|B)  |-	A				-- Absoption
	 * 23. A|(A&B)	|-  A
	 * 24. (A|B)&A  |-	A
	 * 25. (A&B)|A	|-  A
	 */
	
	private int noRules = 26;
	
	public int getNoRules() {
		return noRules;
	}
	
	public BitSet getApplicableRules(FormationTree tree, Node node) {
		BitSet bs = new BitSet(noRules);
		bs.set(0, isAnd(tree, node));
		bs.set(1, isAnd(tree, node) && isIdempotent(tree, (BinaryOperator) node));
		bs.set(2, isAnd(tree, node) && isLeftAssociative(tree, (BinaryOperator) node, "&"));
		bs.set(3, isAnd(tree, node) && isRightAssociative(tree, (BinaryOperator) node, "&"));
		bs.set(4, isOr(tree, node));
		bs.set(5, isOr(tree, node) && isIdempotent(tree, (BinaryOperator) node));
		bs.set(6, isOr(tree, node) && isLeftAssociative(tree, (BinaryOperator) node, "|"));
		bs.set(7, isOr(tree, node) && isRightAssociative(tree, (BinaryOperator) node, "|"));
		bs.set(8, isNot(tree, node) && isNot(tree, ((UnaryOperator) node).getChild()));
		bs.set(9, isImplies(tree, node));
		bs.set(10, isNot(tree, node) && isImplies(tree, ((UnaryOperator) node).getChild()));
		bs.set(11, isIff(tree, node));
		bs.set(12, isIff(tree, node));
		bs.set(13, isNot(tree, node) && isIff(tree, ((UnaryOperator) node).getChild()));
		bs.set(14, isNot(tree, node) && isIff(tree, ((UnaryOperator) node).getChild()));
		bs.set(15, isNot(tree, node) && isIff(tree, ((UnaryOperator) node).getChild()));
		bs.set(16, isNot(tree, node) && isAnd(tree, ((UnaryOperator) node).getChild()));
		bs.set(17, isNot(tree, node) && isOr(tree, ((UnaryOperator) node).getChild()));
		bs.set(18, isAnd(tree, node) && isOr(tree, ((BinaryOperator) node).getRightChild()));
		bs.set(19, isAnd(tree, node) && isOr(tree, ((BinaryOperator) node).getLeftChild()));
		bs.set(20, isOr(tree, node) && isAnd(tree, ((BinaryOperator) node).getRightChild()));
		bs.set(21, isOr(tree, node) && isAnd(tree, ((BinaryOperator) node).getLeftChild()));
		bs.set(22, isAnd(tree, node) && isOr(tree, ((BinaryOperator) node).getRightChild()) 
				&& canBeAbsorbedLeft(tree, (BinaryOperator) node));
		bs.set(23, isOr(tree, node) && isAnd(tree, ((BinaryOperator) node).getRightChild()) 
				&& canBeAbsorbedLeft(tree, (BinaryOperator) node));
		bs.set(24, isAnd(tree, node) && isOr(tree, ((BinaryOperator) node).getLeftChild()) 
				&& canBeAbsorbedRight(tree, (BinaryOperator) node));
		bs.set(25, isOr(tree, node) && isAnd(tree, ((BinaryOperator) node).getLeftChild()) 
				&& canBeAbsorbedRight(tree, (BinaryOperator) node));
		
		return bs;
	}
	
	public String[] rulesToString(BitSet bs, FormationTree tree, Node node) {
		List<String> rules = new ArrayList<String>();
		RuleApplicator ra = new RuleApplicator();
		
		String s = node.toString();
		String nodeBefore = s.substring(1, s.length() - 1);
		
		if (bs.get(0))
			rules.add("Commutativity of ^ :		" + nodeBefore + " |- " + 
					ra.viewCommutativity((BinaryOperator) node) + "\n");
		if (bs.get(1))
			rules.add("Idempotence of ^ :		" + nodeBefore + " |- " + 
					ra.viewIdempotence((BinaryOperator) node) + "\n");
		if (bs.get(2))
			rules.add("Left Associativity of ^ :	" + nodeBefore + " |- " + 
					ra.viewLeftAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(3))
			rules.add("Right Associativity of ^ :	" + nodeBefore + " |- " + 
					ra.viewRightAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(4))
			rules.add("Commutativity of v:		" + nodeBefore + " |- " + 
					ra.viewCommutativity((BinaryOperator) node) + "\n");
		if (bs.get(5))
			rules.add("Idempotence of v:		" + nodeBefore + " |- " + 
					ra.viewIdempotence((BinaryOperator) node) + "\n");
		if (bs.get(6))
			rules.add("Left Associativity of v:	" + nodeBefore + " |- " + 
					ra.viewLeftAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(7))
			rules.add("Right Associativity of v:	" + nodeBefore + " |- " + 
					ra.viewRightAssociativity((BinaryOperator) node) + "\n");
		
		String[] arr = new String[rules.size()];
		return rules.toArray(arr);
	}
	
	private boolean isAnd(FormationTree tree, Node node) {
		return node.getValue().equals("&");
	}
	
	private boolean isOr(FormationTree tree, Node node) {
		return node.getValue().equals("|");
	}

	private boolean isImplies(FormationTree tree, Node node) {
		return node.getValue().equals("->");
	}

	private boolean isIff(FormationTree tree, Node node) {
		return node.getValue().equals("<->");
	}

	private boolean isNot(FormationTree tree, Node node) {
		return node.getValue().equals("!");
	}
	
	private boolean isIdempotent(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getChildren()[0];
		Node rightChild = node.getChildren()[1];
			
		return tree.equalSubTrees(leftChild, rightChild);
	}
	
	private boolean canBeAbsorbedLeft(FormationTree tree, BinaryOperator node) {
		Node n1 = node.getLeftChild();
		Node n2 = ((BinaryOperator) node.getRightChild()).getLeftChild();
		Node n3 = ((BinaryOperator) node.getRightChild()).getRightChild();
		
		return tree.equalSubTrees(n1, n2) || tree.equalSubTrees(n1, n3);
	}
	
	private boolean canBeAbsorbedRight(FormationTree tree, BinaryOperator node) {
		Node n1 = node.getRightChild();
		Node n2 = ((BinaryOperator) node.getLeftChild()).getLeftChild();
		Node n3 = ((BinaryOperator) node.getLeftChild()).getRightChild();
		
		return tree.equalSubTrees(n1, n2) || tree.equalSubTrees(n1, n3);
	}
	
	private boolean isLeftAssociative(FormationTree tree, BinaryOperator node, String op) {
		return node.getValue().equals(op) && node.getChildren()[1].getValue().equals(op);
	}
	
	private boolean isRightAssociative(FormationTree tree, BinaryOperator node, String op) {
		return node.getValue().equals(op) && node.getChildren()[0].getValue().equals(op);
	}
}
