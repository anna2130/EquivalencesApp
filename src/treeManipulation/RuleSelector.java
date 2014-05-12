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
	 * 0.  Commutativity of ^
	 * 1.  Idempotence of ^ 
	 * 2.  Left Associativity of ^
	 * 3.  Right Associativity of ^
	 * 4.  Commutativity of v
	 * 5.  Idempotence of v
	 * 6.  Left Associativity of v
	 * 7.  Right Associativity of v
	 * 8.  ¬¬A 		|- 	A
	 * 9.  A→B 		|- 	¬AvB 			-- Also equivalent to ¬(A^¬B). Separate rule?
	 * 10. ¬(A→B) 	|-	A^¬B
	 * 11. A↔B		|-  (A→B)^(B→A)
	 * 12. A↔B		|-	(A^B)v(¬A^¬B)
	 * 13. ¬(A↔B) 	|- 	A↔¬B
	 * 14. ¬(A↔B) 	|-  ¬A↔B
	 * 15. ¬(A↔B) 	|-  (A^¬B)v(¬A^B)	-- Exclusive or of A and B
	 * 16. ¬(A^B)	|-  ¬Av¬B			-- De Morgan laws
	 * 17. ¬(AvB)	|-	¬A^¬B
	 * 18. A^(BvC) 	|- 	(A^B)v(A^C)		-- Distributitivity
	 * 19. (AvB)^C	|-  (A^C)v(B^C)
	 * 20. Av(B^C)	|- 	(AvB)^(AvC)
	 * 21. (A^B)vC	|-	(AvC)^(BvC)
	 * 22. A^(AvB)  |-	A				-- Absoption
	 * 23. Av(A^B)	|-  A
	 * 24. (AvB)^A  |-	A
	 * 25. (A^B)vA	|-  A
	 */
	
	private int noRules = 26;
	
	public int getNoRules() {
		return noRules;
	}
	
	public BitSet getApplicableRules(FormationTree tree, Node node) {
		BitSet bs = new BitSet(noRules);
		bs.set(0, node.isAnd());
		bs.set(1, node.isAnd() && isIdempotent(tree, (BinaryOperator) node));
		bs.set(2, node.isAnd() && isLeftAssociative(tree, (BinaryOperator) node, "^"));
		bs.set(3, node.isAnd() && isRightAssociative(tree, (BinaryOperator) node, "^"));
		bs.set(4, node.isOr());
		bs.set(5, node.isOr() && isIdempotent(tree, (BinaryOperator) node));
		bs.set(6, node.isOr() && isLeftAssociative(tree, (BinaryOperator) node, "v"));
		bs.set(7, node.isOr() && isRightAssociative(tree, (BinaryOperator) node, "v"));
		bs.set(8, node.isNot() && ((UnaryOperator) node).getChild().isNot());
		bs.set(9, node.isImplies());
		bs.set(10, node.isNot() && ((UnaryOperator) node).getChild().isImplies());
		bs.set(11, node.isIff());
		bs.set(12, node.isIff());
		bs.set(13, node.isNot() && ((UnaryOperator) node).getChild().isIff());
		bs.set(14, node.isNot() && ((UnaryOperator) node).getChild().isIff());
		bs.set(16, node.isNot() && ((UnaryOperator) node).getChild().isAnd());
		bs.set(17, node.isNot() && ((UnaryOperator) node).getChild().isOr());
		bs.set(18, node.isAnd() && ((BinaryOperator) node).getRightChild().isOr());
		bs.set(19, node.isAnd() && ((BinaryOperator) node).getLeftChild().isOr());
		bs.set(20, node.isOr() && ((BinaryOperator) node).getRightChild().isAnd());
		bs.set(21, node.isOr() && ((BinaryOperator) node).getLeftChild().isAnd());
		bs.set(22, node.isAnd() &&  ((BinaryOperator) node).getRightChild().isOr() 
				&& canBeAbsorbedLeft(tree, (BinaryOperator) node));
		bs.set(23, node.isOr() && ((BinaryOperator) node).getRightChild().isAnd() 
				&& canBeAbsorbedLeft(tree, (BinaryOperator) node));
		bs.set(24, node.isAnd() && ((BinaryOperator) node).getLeftChild().isOr() 
				&& canBeAbsorbedRight(tree, (BinaryOperator) node));
		bs.set(25, node.isOr() && ((BinaryOperator) node).getLeftChild().isAnd() 
				&& canBeAbsorbedRight(tree, (BinaryOperator) node));
		
		return bs;
	}
	
	public String[] rulesToString(BitSet bs, FormationTree tree, Node node) {
		List<String> rules = new ArrayList<String>();
		RuleApplicator ra = new RuleApplicator();
		
		String s = node.toString();
		String nodeBefore = s.substring(1, s.length() - 1);
		
		if (bs.get(0))
			rules.add(nodeBefore + " |- " + 
					ra.viewCommutativity((BinaryOperator) node) + "\n");
		if (bs.get(1))
			rules.add(nodeBefore + " |- " + 
					ra.viewIdempotence((BinaryOperator) node) + "\n");
		if (bs.get(2))
			rules.add(nodeBefore + " |- " + 
					ra.viewLeftAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(3))
			rules.add(nodeBefore + " |- " + 
					ra.viewRightAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(4))
			rules.add(nodeBefore + " |- " + 
					ra.viewCommutativity((BinaryOperator) node) + "\n");
		if (bs.get(5))
			rules.add(nodeBefore + " |- " + 
					ra.viewIdempotence((BinaryOperator) node) + "\n");
		if (bs.get(6))
			rules.add(nodeBefore + " |- " + 
					ra.viewLeftAssociativity((BinaryOperator) node) + "\n");
		if (bs.get(7))
			rules.add(nodeBefore + " |- " + 
					ra.viewRightAssociativity((BinaryOperator) node) + "\n");
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
