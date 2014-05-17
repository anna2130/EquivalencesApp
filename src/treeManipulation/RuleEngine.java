package treeManipulation;

import java.util.BitSet;
import java.util.Random;

import treeBuilder.Atom;
import treeBuilder.BinaryOperator;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.UnaryOperator;

public class RuleEngine {
	
	RuleApplicator ra;
	RuleSelector rs;
	int noRules;
	
	public RuleEngine() {
		ra = new RuleApplicator();
		rs = new RuleSelector();
		noRules = rs.getNoRules();
	}
	
	public RuleApplicator getRuleApplicator() {
		return ra;
	}
	
	public RuleSelector getRuleSelector() {
		return rs;
	}

	/* The BitSet returns the rules applicable to a node in the order below:
	 * 
	 * Equivalences involving ^
	 * 0.  a^b 			|- 	b^a				-- Commutativity
	 * 1.  a^a			|-  a 				-- Idempotence
	 * 2.  a^┬			|- 	a
	 * 3.  ┬^a			|-  a
	 * 4.  ⊥^a			|-  ⊥
	 * 5.  a^⊥			|-  ⊥
	 * 6.  a^¬a			|-  ⊥
	 * 7.  ¬a^a			|-  ⊥				
	 * 8.  a^(b^c)		|-  (a^b)^c			-- Associativity
	 * 9.  (a^b)^c  	|- 	a^(b^c)			-- Associativity
	 * 10. a^¬b 		|-	¬(a→b)
	 * 11. (a→b)^(b→a)	|-  a↔b
	 * 12. ¬a^¬b		|-	¬(avb)			-- De Morgan laws
	 * 13. a^(bvc) 		|- 	(a^b)v(a^c)		-- Distributitivity
	 * 14. (avb)^c		|-  (a^c)v(b^c)		-- Distributitivity
	 * 15. (avb)^(avc)	|- 	av(b^c)			-- Distributitivity
	 * 16. (avc)^(bvc)	|-	(a^b)vc			-- Distributitivity
	 * 17. a^(avb)  	|-	a				-- Absoption
	 * 18. (avb)^a  	|-	a				-- Absoption
	 * 
	 * Equivalences involving v
	 * 19. avb				|- 	bva				-- Commutativity
	 * 20. ava				|- 	a 				-- Idempotence
	 * 21. ┬va				|-  ┬
	 * 22. av┬				|-  ┬
	 * 23. av¬a				|-  ┬
	 * 24. ¬ava				|-  ┬
	 * 25. av⊥				|-  a
	 * 26. ⊥va				|-  a
	 * 27. av(bvc)			|-  (avb)vc			-- Associativity
	 * 28. (avb)vc  		|- 	av(bvc)			-- Associativity
	 * 29. (a^b)v(¬a^¬b)	|-	a↔b
	 * 30. (a^¬b)v(¬a^b) 	|-  ¬(a↔b)
	 * 31. ¬av¬b			|-  ¬(a^b)			-- De Morgan laws
	 * 32. av(b^c)			|- 	(avb)^(avc)		-- Distributitivity
	 * 33. (a^b)vc			|-	(avc)^(bvc)		-- Distributitivity
	 * 34. (a^b)v(a^c) 		|- 	a^(bvc)		-- Distributitivity
	 * 35. (a^c)v(b^c)		|-  (avb)^c		-- Distributitivity
	 * 36. av(a^b)			|-  a				-- Absoption
	 * 37. (a^b)va			|-  a				-- Absoption
	 * 38. ¬avb				|- 	a→b
	 * 
	 * Equivalences involving ¬
	 * 39. ¬┬		|-  ⊥
	 * 40. ¬⊥		|-  ┬
	 * 41. ¬¬a 		|- 	a
	 * 42. ¬a		|-  a→⊥
	 * 43. ¬(a→b) 	|-	a^¬b
	 * 44. ¬(av¬b)	|- 	a→b
	 * 45. ¬(a↔b) 	|- 	a↔¬b
	 * 46. ¬(a↔b) 	|-  ¬a↔b
	 * 47. ¬(a↔b) 	|-  (a^¬b)v(¬a^b)	-- Exclusive or of A and b
	 * 48. ¬(a^b)	|-  ¬av¬b			-- De Morgan laws
	 * 49. ¬(avb)	|-	¬a^¬b			-- De Morgan laws
	 * 
	 * Equivalences involving →
	 * 50. a→a		|- 	┬
	 * 51. ┬→a		|- 	a
	 * 52. a→┬		|- 	┬
	 * 53. ⊥→a		|- 	┬
	 * 54. a→⊥		|- 	¬a
	 * 55. a→b 		|- 	¬avb
	 * 56. a→b 		|- 	¬(av¬b)
	 * 
	 * Equivalences involving ↔
	 * 57. a↔b		|-  (a→b)^(b→a)
	 * 58. a↔b		|-	(a^b)v(¬a^¬b)
	 * 59. a↔¬b 	|- 	¬(a↔b)
	 * 60. ¬a↔b 	|-  ¬(a↔b)
	 * 
	 * Equivalences involving atoms
	 * 61. a		|-  a^a
	 * 62. a		|-  a^┬
	 * 63. a		|-  ava
	 * 64. a		|-  av⊥
	 * 65. a		|-  ¬¬a
	 * 66. a		|-  ┬→a
	 * 67. ⊥		|-  ¬┬
	 * 68. ┬		|-  ¬⊥
	 * 
	 * Equivalences involving user input
	 * 69. ⊥		|-  a^⊥
	 * 70. ⊥		|-  a^¬a
	 * 71. ┬		|-  av┬
	 * 72. ┬		|-  av¬a
	 * 73. ┬		|- 	a→a	
	 * 74. ┬		|- 	a→┬
	 * 75. ┬		|- 	⊥→a
	 * 76. a		|-  av(a^b)
	 * 77. a  		|-	a^(avb)
	 */
	
	public BitSet getApplicableRules(FormationTree tree, Node node) {
		BitSet bs = new BitSet(noRules);
		
		// Equivalences involving ^
		if (node.isAnd()) {
			BinaryOperator binary = (BinaryOperator) node;
			Node leftChild = binary.getLeftChild();
			Node rightChild = binary.getRightChild();
			Node[] leftGChildren = leftChild.getChildren();
			Node[] rightGChildren = rightChild.getChildren();
			
			bs.set(0);
			bs.set(1, rs.isIdempotent(tree, binary));
			bs.set(2, rightChild.isTop());
			bs.set(3, leftChild.isTop());
			bs.set(4, leftChild.isBottom());
			bs.set(5, rightChild.isBottom());
			bs.set(6, rs.rightIsNotOfLeft(tree, binary));
			bs.set(7, rs.leftIsNotOfRight(tree, binary));
			bs.set(8, rs.isLeftAssociative(tree, binary, "^"));
			bs.set(9, rs.isRightAssociative(tree, binary, "^"));
			bs.set(10, rightChild.isNot());
			bs.set(11, leftChild.isImplies() && rightChild.isImplies() 
					&& tree.equalSubTrees(leftGChildren[0], rightGChildren[1])
					&& tree.equalSubTrees(leftGChildren[1], rightGChildren[0]));
			bs.set(12, leftChild.isNot() && rightChild.isNot());
			bs.set(13, rightChild.isOr());
			bs.set(14, leftChild.isOr());
			bs.set(15, leftChild.isOr() && rightChild.isOr() 
					&& tree.equalSubTrees(leftGChildren[0], rightGChildren[0]));
			bs.set(16, leftChild.isOr() && rightChild.isOr() 
					&& tree.equalSubTrees(leftGChildren[1], rightGChildren[1]));
			bs.set(17, rightChild.isOr() && tree.equalSubTrees(leftChild, rightGChildren[0]));
			bs.set(18, leftChild.isOr() && tree.equalSubTrees(leftGChildren[0], rightChild));
		}
		
		// Equivalences involving ^
		if (node.isOr()) {
			BinaryOperator binary = (BinaryOperator) node;
			Node leftChild = binary.getLeftChild();
			Node rightChild = binary.getRightChild();
			Node[] leftGChildren = leftChild.getChildren();
			Node[] rightGChildren = rightChild.getChildren();
			
			bs.set(19);
			bs.set(20, rs.isIdempotent(tree, binary));
			bs.set(21, leftChild.isTop());
			bs.set(22, rightChild.isTop());
			bs.set(23, rs.rightIsNotOfLeft(tree, binary));
			bs.set(24, rs.leftIsNotOfRight(tree, binary));
			bs.set(25, rightChild.isBottom());
			bs.set(26, leftChild.isBottom());
			bs.set(27, rs.isLeftAssociative(tree, binary, "v"));
			bs.set(28, rs.isRightAssociative(tree, binary, "v"));
			bs.set(29, leftChild.isAnd() && rightChild.isAnd()
					&& rightGChildren[0].isNot() && rightGChildren[1].isNot()
					&& tree.equalSubTrees(leftGChildren[0], rightGChildren[0].getChildren()[0])
					&& tree.equalSubTrees(leftGChildren[1], rightGChildren[1].getChildren()[0]));
			bs.set(30, leftChild.isAnd() && rightChild.isAnd()
					&& rightGChildren[0].isNot() && leftGChildren[1].isNot()
					&& tree.equalSubTrees(leftGChildren[0], rightGChildren[0].getChildren()[0])
					&& tree.equalSubTrees(leftGChildren[1].getChildren()[0], rightGChildren[1]));
			bs.set(31, leftChild.isNot() && rightChild.isNot());
			bs.set(32, rightChild.isAnd());
			bs.set(33, leftChild.isAnd());
			bs.set(34, leftChild.isAnd() && rightChild.isAnd()
					&& tree.equalSubTrees(leftGChildren[0], rightGChildren[0]));
			bs.set(35, leftChild.isAnd() && rightChild.isAnd()
					&& tree.equalSubTrees(leftGChildren[1], rightGChildren[1]));
			bs.set(36, rightChild.isAnd() && tree.equalSubTrees(leftChild, rightGChildren[0]));
			bs.set(37, leftChild.isAnd() && tree.equalSubTrees(leftGChildren[0], rightChild));
			bs.set(38, leftChild.isNot());
		}
		
		// Equivalences involving ¬
		if (node.isNot()) {
			Node child = ((UnaryOperator) node).getChild();
			Node[] grandChildren = child.getChildren();
			
			bs.set(39, child.isTop());
			bs.set(40, child.isBottom());
			bs.set(41, child.isNot());
			bs.set(42);
			bs.set(43, child.isImplies());
			bs.set(44, child.isOr() && grandChildren[1].isNot());
			bs.set(45, child.isIff());
			bs.set(46, child.isIff());
			bs.set(47, child.isIff());
			bs.set(48, child.isAnd());
			bs.set(49, child.isOr());
		}
		
		// Equivalences involving →
		if (node.isImplies()) {
			BinaryOperator binary = (BinaryOperator) node;
			Node leftChild = binary.getLeftChild();
			Node rightChild = binary.getRightChild();
			
			bs.set(50, rs.isIdempotent(tree, binary));
			bs.set(51, leftChild.isTop());
			bs.set(52, rightChild.isTop());
			bs.set(53, leftChild.isBottom());
			bs.set(54, rightChild.isBottom());
			bs.set(55);
			bs.set(56);
		}
		
		// Equivalences involving ↔
		if (node.isIff()) {
			BinaryOperator binary = (BinaryOperator) node;
			Node leftChild = binary.getLeftChild();
			Node rightChild = binary.getRightChild();

			bs.set(57);
			bs.set(58);
			bs.set(59, rightChild.isNot());
			bs.set(60, leftChild.isNot());
		}
		
		// Equivalences involving atoms
		// Equivalences involving user input
		if (node.isAtom()) {
			bs.set(61, 67);
			bs.set(76, 78);
			if (node.isBottom()) {
				bs.set(67);
				bs.set(69, 71);
			} else if (node.isTop()) {
				bs.set(68);
				bs.set(71, 76);
			}
		}
		
		return bs;
	}
	
	public String[] rulesToString(BitSet bs, FormationTree tree, Node node) {
		return rs.rulesToString(bs, tree, node);
	}
	
	public void applyRuleFromBitSet(BitSet bs, int index, FormationTree tree, 
			Node node, String input) {
		
		switch (index) {
			case 0:		ra.applyCommutativity((BinaryOperator) node);
						break;
			case 1:		ra.applyIdempotence(tree, (BinaryOperator) node);
						break;
			case 2:		ra.applyToLeftChild(tree, (BinaryOperator) node); 
						break;
			case 3:		ra.applyToRightChild(tree, (BinaryOperator) node);
						break;
			case 4:		ra.applyToBottom(tree, node);
						break;
			case 5:		ra.applyToBottom(tree, node);
						break;
			case 6:		ra.applyToBottom(tree, node);
						break;
			case 7:		ra.applyToBottom(tree, node);
						break;
			case 8:		ra.applyLeftAssociativity(tree, (BinaryOperator) node);
						break;
			case 9:		ra.applyRightAssociativity(tree, (BinaryOperator) node);
						break;
			case 10:	ra.applyAndNotToNotImplies(tree, (BinaryOperator) node);
						break;
			case 11:	ra.applyAndImpliesToIff(tree, (BinaryOperator) node);
						break;
			case 12:	ra.applyDeMorganOrBackwards(tree, (BinaryOperator) node);
						break;
			case 13:	ra.applyDistributivityAndLeftForwards(tree, (BinaryOperator) node);
						break;
			case 14:	ra.applyDistributivityAndRightForwards(tree, (BinaryOperator) node);
						break;
			case 15:	ra.applyDistributivityOrLeftBackwards(tree, (BinaryOperator) node);
						break;
			case 16:	ra.applyDistributivityOrRightBackwards(tree, (BinaryOperator) node);
						break;
			case 17:	ra.applyLeftAbsorption(tree, (BinaryOperator) node);
						break;
			case 18:	ra.applyRightAbsorption(tree, (BinaryOperator) node);
						break;
			case 19:	ra.applyCommutativity((BinaryOperator) node);
						break;
			case 20:	ra.applyIdempotence(tree, (BinaryOperator) node);
						break;
			case 21:	ra.applyToTop(tree, (BinaryOperator) node);
						break;
			case 22:	ra.applyToTop(tree, (BinaryOperator) node);
						break;
			case 23:	ra.applyToTop(tree, (BinaryOperator) node);
						break;
			case 24:	ra.applyToTop(tree, (BinaryOperator) node);
						break;
			case 25:	ra.applyToLeftChild(tree, (BinaryOperator) node);
						break;
			case 26:	ra.applyToRightChild(tree, (BinaryOperator) node);
						break;
			case 27:	ra.applyLeftAssociativity(tree, (BinaryOperator) node);
						break;
			case 28:	ra.applyRightAssociativity(tree, (BinaryOperator) node);
						break;
			case 29:	ra.applyOrAndToIff(tree, (BinaryOperator) node);
						break;
			case 30:	ra.applyOrAndToNotIff(tree, (BinaryOperator) node);
						break;
			case 31:	ra.applyDeMorganAndBackwards(tree, (BinaryOperator) node);
						break;
			case 32:	ra.applyDistributivityOrLeftForwards(tree, (BinaryOperator) node);
						break;
			case 33:	ra.applyDistributivityOrRightForwards(tree, (BinaryOperator) node);
						break;
			case 34:	ra.applyDistributivityAndLeftBackwards(tree, (BinaryOperator) node);
						break;
			case 35:	ra.applyDistributivityAndRightBackwards(tree, (BinaryOperator) node);
						break;
			case 36:	ra.applyLeftAbsorption(tree, (BinaryOperator) node);
						break;
			case 37:	ra.applyRightAbsorption(tree, (BinaryOperator) node);
						break;
			case 38:	ra.applyOrToImplies(tree, (BinaryOperator) node);
						break;
			case 39:	ra.applyNotTop(tree, (UnaryOperator) node);
						break;
			case 40:	ra.applyNotBottom(tree, (UnaryOperator) node);
						break;
			case 41:	ra.applyNotNot(tree, (UnaryOperator) node);
						break;
			case 42:	ra.applyNotAtom(tree, (UnaryOperator) node);
						break;
			case 43:	ra.applyNotImplies(tree, (UnaryOperator) node);
						break;
			case 44:	ra.applyNotOrToImplies(tree, (UnaryOperator) node);
						break;
			case 45:	ra.applyNotIffToNotB(tree, (UnaryOperator) node);
						break;
			case 46:	ra.applyNotIffToNotA(tree, (UnaryOperator) node);
						break;
			case 47:	ra.applyNotIffToOrAnd(tree, (UnaryOperator) node);
						break;
			case 48:	ra.applyDeMorganAndForwards(tree, (UnaryOperator) node);
						break;
			case 49:	ra.applyDeMorganOrForwards(tree, (UnaryOperator) node);
						break;
			case 50:	ra.applyToTop(tree, (BinaryOperator) node);
						break;
			case 51:	ra.applyToRightChild(tree, (BinaryOperator) node);
						break;
			case 52:	ra.applyToTop(tree, (BinaryOperator) node);
						break;
			case 53:	ra.applyToTop(tree, (BinaryOperator) node);
						break;
			case 54:	ra.applyImpliesToNot(tree, (BinaryOperator) node);
						break;
			case 55:	ra.applyImpliesToOr(tree, (BinaryOperator) node);
						break;
			case 56:	ra.applyImpliesToNotOr(tree, (BinaryOperator) node);
						break;
			case 57:	ra.applyIffToAndImplies(tree, (BinaryOperator) node);
						break;
			case 58:	ra.applyIffToOrAnd(tree, (BinaryOperator) node);
						break;
			case 59:	ra.applyIffNotBToNotIff(tree, (BinaryOperator) node);
						break;
			case 60:	ra.applyIffNotAToNotIff(tree, (BinaryOperator) node);
						break;
			case 61:	ra.applyAndIdempotenceBackwards(tree, (Atom) node);
						break;
			case 62:	ra.applyAndTop(tree, (Atom) node);
						break;
			case 63:	ra.applyOrIdempotenceBackwards(tree, (Atom) node);
						break;
			case 64:	ra.applyOrBottom(tree, (Atom) node);
						break;
			case 65:	ra.applyNotNotBackwards(tree, (Atom) node);
						break;
			case 66:	ra.applyTopImplies(tree, (Atom) node);
						break;
			case 67:	ra.applyNotTop(tree, (Atom) node);
						break;
			case 68:	ra.applyNotBottom(tree, (Atom) node);
						break;
			case 69:	ra.applyAtomAndBottom(tree, (Atom) node, input);
						break;
			case 70:	ra.applyBottomToAndAtom(tree, (Atom) node, input);
						break;
			case 71:	ra.applyAtomOrTop(tree, (Atom) node, input);
						break;
			case 72:	ra.applyTopToOrAtom(tree, (Atom) node, input);
						break;
			case 73:	ra.applyTopToImpliesAtom(tree, (Atom) node, input);
						break;
			case 74:	ra.applyTopToImpliesAtomTop(tree, (Atom) node, input);
						break;
			case 75:	ra.applyTopToImpliesBottomAtom(tree, (Atom) node, input);
						break;
			case 76:	ra.applyAbsorptionOrBackwards(tree, (Atom) node, input);
						break;
			case 77:	ra.applyAbsorptionAndBackwards(tree, (Atom) node, input);
						break;
		}
	}
	
	public void applyRandomRules(FormationTree tree, int n) {
		for (int i = 0; i < n; ++i) {
			System.out.println("Apply " + i + " rule to: " + tree);
			applyRuleToRandomNode(tree);
			System.out.println("Replaced by: " + tree + "\n");
		}
	}
	
	public void applyRuleToRandomNode(FormationTree tree) {
		Node node = tree.randomNode();
		System.out.println("Random node: " + node);
		BitSet bs = getApplicableRules(tree, node);
		System.out.println("BitSet: " + bs);
		applyRandomRule(bs, tree, node);
	}
	
	public void applyRandomRule(BitSet bs, FormationTree tree, Node node) {
		int numSetBits = bs.cardinality();
		int nextSetBit = bs.nextSetBit(0);
		
		Random rand = new Random();
	    int randomNum = rand.nextInt(numSetBits);
	    
		while (randomNum-- > 0)
			nextSetBit = bs.nextSetBit(nextSetBit + 1);
		
		if (nextSetBit > 68)
			applyRandomRule(bs, tree, node);
		else {
			// TODO: Choose appropriate variable to pass through
			System.out.println("Apply rule: " + nextSetBit);
		    applyRuleFromBitSet(bs, nextSetBit, tree, node, null);
		}
	}
}
