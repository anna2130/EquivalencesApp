package treeManipulation;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import treeBuilder.Atom;
import treeBuilder.BinaryOperator;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.UnaryOperator;
import treeBuilder.Variable;
import android.util.SparseArray;

public class RuleEngine {

	RuleApplicator ra;
	RuleSelector rs;
	int noRules;
	int probability;
	HashSet<Integer> rulesWithTruths;
	boolean firstOrder;

	private static int min_user_input_required = 69;
	private static int first_order_rules = 78;
	private static int fo_user_input_required = 102;

	public RuleEngine(boolean firstOrder) {
		ra = new RuleApplicator();
		rs = new RuleSelector();
		noRules = rs.getNoRules();
		probability = 50;
		this.firstOrder = firstOrder;

		Integer[] rules = new Integer[] {4,5,6,7,21,22,23,24,39,40,42,50,52,53,62,64,66,67,68,69,71,74,75};
		rulesWithTruths = new HashSet<Integer>(Arrays.asList(rules));
	}

	public int getMinUserInputRequired() {
		return min_user_input_required;
	}

	public RuleApplicator getRuleApplicator() {
		return ra;
	}

	public RuleSelector getRuleSelector() {
		return rs;
	}

	/* The BitSet returns the rules applicable to a node in the order below:
	 * 
	 * Equivalences involving ∧
	 * 0.  a∧b 			|- 	b∧a				-- Commutativity
	 * 1.  a∧a			|-  a 				-- Idempotence
	 * 2.  a∧┬			|- 	a
	 * 3.  ┬∧a			|-  a
	 * 4.  ⊥∧a			|-  ⊥
	 * 5.  a∧⊥			|-  ⊥
	 * 6.  a∧¬a			|-  ⊥
	 * 7.  ¬a∧a			|-  ⊥				
	 * 8.  a∧(b∧c)		|-  (a∧b)∧c			-- Associativity
	 * 9.  (a∧b)∧c  	|- 	a∧(b∧c)			-- Associativity
	 * 10. a∧¬b 		|-	¬(a→b)
	 * 11. (a→b)∧(b→a)	|-  a↔b
	 * 12. ¬a∧¬b		|-	¬(avb)			-- De Morgan laws
	 * 13. a∧(bvc) 		|- 	(a∧b)v(a∧c)		-- Distributitivity
	 * 14. (avb)∧c		|-  (a∧c)v(b∧c)		-- Distributitivity
	 * 15. (avb)∧(avc)	|- 	av(b∧c)			-- Distributitivity
	 * 16. (avc)∧(bvc)	|-	(a∧b)vc			-- Distributitivity
	 * 17. a∧(avb)  	|-	a				-- Absoption
	 * 18. (avb)∧a  	|-	a				-- Absoption
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
	 * 29. (a∧b)v(¬a∧¬b)	|-	a↔b
	 * 30. (a∧¬b)v(¬a∧b) 	|-  ¬(a↔b)
	 * 31. ¬av¬b			|-  ¬(a∧b)			-- De Morgan laws
	 * 32. av(b∧c)			|- 	(avb)∧(avc)		-- Distributitivity
	 * 33. (a∧b)vc			|-	(avc)∧(bvc)		-- Distributitivity
	 * 34. (a∧b)v(a∧c) 		|- 	a∧(bvc)			-- Distributitivity
	 * 35. (a∧c)v(b∧c)		|-  (avb)∧c			-- Distributitivity
	 * 36. av(a∧b)			|-  a				-- Absoption
	 * 37. (a∧b)va			|-  a				-- Absoption
	 * 38. ¬avb				|- 	a→b
	 * 
	 * Equivalences involving ¬
	 * 39. ¬┬		|-  ⊥
	 * 40. ¬⊥		|-  ┬
	 * 41. ¬¬a 		|- 	a
	 * 42. ¬a		|-  a→⊥
	 * 43. ¬(a→b) 	|-	a∧¬b
	 * 44. ¬(av¬b)	|- 	a→b
	 * 45. ¬(a↔b) 	|- 	a↔¬b
	 * 46. ¬(a↔b) 	|-  ¬a↔b
	 * 47. ¬(a↔b) 	|-  (a∧¬b)v(¬a∧b)	-- Exclusive or of a and b
	 * 48. ¬(a∧b)	|-  ¬av¬b			-- De Morgan laws
	 * 49. ¬(avb)	|-	¬a∧¬b			-- De Morgan laws
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
	 * 57. a↔b		|-  (a→b)∧(b→a)
	 * 58. a↔b		|-	(a∧b)v(¬a∧¬b)
	 * 59. a↔¬b 	|- 	¬(a↔b)
	 * 60. ¬a↔b 	|-  ¬(a↔b)
	 * 
	 * Equivalences involving atoms
	 * 61. a		|-  a∧a
	 * 62. a		|-  a∧┬
	 * 63. a		|-  ava
	 * 64. a		|-  av⊥
	 * 65. a		|-  ¬¬a
	 * 66. a		|-  ┬→a
	 * 67. ⊥		|-  ¬┬
	 * 68. ┬		|-  ¬⊥
	 * 
	 * Equivalences involving user input
	 * 69. ⊥		|-  a∧⊥
	 * 70. ⊥		|-  a∧¬a
	 * 71. ┬		|-  av┬
	 * 72. ┬		|-  av¬a
	 * 73. ┬		|- 	a→a	
	 * 74. ┬		|- 	a→┬
	 * 75. ┬		|- 	⊥→a
	 * 76. a		|-  av(a∧b)
	 * 77. a  		|-	a∧(avb)
	 * 
	 * ∀ | ∃ | ∧ | ∨ | ┬ | ⊥ | ¬ | → | ↔
	 *  
	 * First order - e and f represent any wff
	 * 			   - t represents a wff with no free occurrences of x 
	 * 
	 * Equivalences involving ∀
	 * 78. ∀x[¬e]   |- 	¬∃x[e]
	 * 79. ∀x∀y[e]	|-	∀y∀x[e]
	 * 80. ∀x[e∧f] 	|- 	∀x[e]∧∀x[f]
	 * 81. ∀x[e∨t] 	|- 	∀x[e]∨t
	 * 82. ∀x[e→t]  |-  ∃x[e]→t
	 * 83. ∀x[t→e]	|-  t→∀x[e]
	 * 84: ∀x[t]	|-  t
	 * 
	 * Equivalences involving ∃
	 * 86: ∃x[¬e]  	|-	¬∀x[e]
	 * 87: ∃x∃y[e] 	|-  ∃y∃x[e]
	 * 88: ∃x[e∨f]	|-  ∃x[e]∨∃x[f]
	 * 89: ∃x[e∧t]	|-  ∃x[e]∧t
	 * 90: ∃x[e→t]  |-  ∃x[e]→t
	 * 91: ∃x[t→e]  |-  t→∃x[e]
	 * 92: ∃x[t]	|-  t
	 * 
	 * Equivalences involving ¬
	 * 93: ¬∀x[e]	|- 	∃x[¬e]
	 * 94: ¬∃x[e]	|-	∀x[¬e]
	 * 
	 * Equivalences involving ∧
	 * 95: ∀x[e]∧∀x[f] 	|-	∀x[e∧f]
	 * 96: ∃x[e]∧t		|-  ∃x[e∧t]
	 * 
	 * Equivalences involving ∨
	 * 97: ∀x[e]∨t		|- 	∀x[e∨t]
	 * 98: ∃x[e]∨∃x[f]	|- 	∃x[e∨f]
	 * 
	 * Equivalences involving →
	 * 99: ∃x[e]→t 	|- 	∀x[e→t]
	 * 100: t→∀x[e]	|- 	∀x[t→e]
	 * 101: ∃x[e]→t |- 	∃x[e→t]
	 * 
	 * Equivalences involving user input
	 * 102: ∀x[e]	|-  ∀y[e{x->y}]
	 * 103: ∃x[e]	|-  ∃y[e{x->y}]
	 * 104: t		|- 	∀x[t]
	 * 105: t		|- 	∃x[t]
	 * 
	 * Combined commutativity rules
	 * 106. ¬a∧b 		|-	¬(a→b)
	 * 107. (¬a∧¬b)v(a∧b)	|-	a↔b
	 * 108. (¬a∧b)v(a∧¬b) 	|-  ¬(a↔b)
	 * 109. ∀x[t∨e] 	|- 	t∨∀x[e]
	 * 110: ∃x[t∧e]		|-  t∧∃x[e]
	 * 111: t∧∃x[e]		|-  ∃x[t∧e]
	 * 112: t∨∀x[e]		|- 	∀x[t∨e]
	 */

	public BitSet getApplicableRules(FormationTree tree, Node node) {
		BitSet bs = new BitSet(noRules);

		// Equivalences involving ∧
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
			bs.set(8, rs.isLeftAssociative(tree, binary, "∧"));
			bs.set(9, rs.isRightAssociative(tree, binary, "∧"));
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
			if (firstOrder) {
				bs.set(95, leftChild.isAll() && rightChild.isAll());
				if (leftChild.isExists()) {
					String variable = leftChild.getVars().peek();
					bs.set(96, !rightChild.hasFree(variable));
				} else if (rightChild.isExists()) {
					String variable = rightChild.getVars().peek();
					bs.set(111, !leftChild.hasFree(variable));
				}
			}
			bs.set(106, leftChild.isNot());
		}

		// Equivalences involving V
		else if (node.isOr()) {
			BinaryOperator binary = (BinaryOperator) node;
			Node leftChild = binary.getLeftChild();
			Node rightChild = binary.getRightChild();

			bs.set(19);
			bs.set(20, rs.isIdempotent(tree, binary));
			bs.set(21, leftChild.isTop());
			bs.set(22, rightChild.isTop());
			bs.set(23, rs.rightIsNotOfLeft(tree, binary));
			bs.set(24, rs.leftIsNotOfRight(tree, binary));
			bs.set(25, rightChild.isBottom());
			bs.set(26, leftChild.isBottom());
			bs.set(27, rs.isLeftAssociative(tree, binary, "∨"));
			bs.set(28, rs.isRightAssociative(tree, binary, "∨"));
			if (leftChild.isAnd() && rightChild.isAnd()) {
				Node[] leftGChildren = leftChild.getChildren();
				Node[] rightGChildren = rightChild.getChildren();
				bs.set(29, rightGChildren[0].isNot() && rightGChildren[1].isNot()
						&& tree.equalSubTrees(leftGChildren[0], rightGChildren[0].getChildren()[0])
						&& tree.equalSubTrees(leftGChildren[1], rightGChildren[1].getChildren()[0]));
				bs.set(30, rightGChildren[0].isNot() && leftGChildren[1].isNot()
						&& tree.equalSubTrees(leftGChildren[0], rightGChildren[0].getChildren()[0])
						&& tree.equalSubTrees(leftGChildren[1].getChildren()[0], rightGChildren[1]));
				bs.set(34, tree.equalSubTrees(leftGChildren[0], rightGChildren[0]));
				bs.set(35, tree.equalSubTrees(leftGChildren[1], rightGChildren[1]));
				bs.set(107, leftGChildren[0].isNot() && leftGChildren[1].isNot()
						&& tree.equalSubTrees(rightGChildren[0], leftGChildren[0].getChildren()[0])
						&& tree.equalSubTrees(rightGChildren[1], leftGChildren[1].getChildren()[0]));
				bs.set(108, leftGChildren[0].isNot() && rightGChildren[1].isNot()
						&& tree.equalSubTrees(rightGChildren[0], leftGChildren[0].getChildren()[0])
						&& tree.equalSubTrees(rightGChildren[1].getChildren()[0], leftGChildren[1]));
			}
			bs.set(31, leftChild.isNot() && rightChild.isNot());
			bs.set(32, rightChild.isAnd());
			bs.set(33, leftChild.isAnd());
			if (rightChild.isAnd()) {
				Node[] rightGChildren = rightChild.getChildren();
				bs.set(36, tree.equalSubTrees(leftChild, rightGChildren[0]));
			}
			if (leftChild.isAnd()) {
				Node[] leftGChildren = leftChild.getChildren();
				bs.set(37, tree.equalSubTrees(leftGChildren[0], rightChild));
			}
			bs.set(38, leftChild.isNot());
			if (firstOrder) {
				if (leftChild.isAll()) {
					String variable = leftChild.getVars().peek();
					bs.set(97, !rightChild.hasFree(variable));
				} else if (rightChild.isAll()) {
					String variable = rightChild.getVars().peek();
					bs.set(112, !leftChild.hasFree(variable));
				}
			}
			bs.set(98, leftChild.isExists() && rightChild.isExists());
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
			if (firstOrder) {
				bs.set(93, child.isAll());
				bs.set(94, child.isExists());
			}
		}

		// Equivalences involving →
		else if (node.isImplies()) {
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
			if (firstOrder) {
				if (leftChild.isExists()) {
					String variable = leftChild.getVars().peek();
					bs.set(99, !rightChild.hasFree(variable));
					bs.set(101, !rightChild.hasFree(variable));
				} else if (rightChild.isAll()) {
					String variable = rightChild.getVars().peek();
					bs.set(100, !leftChild.hasFree(variable));
				}
			}
		}

		// Equivalences involving ↔
		else if (node.isIff()) {
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
		else if (node.isAtom()) {
			bs.set(61, 67);
			bs.set(76, 78);
			if (node.isBottom()) {
				bs.set(67);
				bs.set(69, 71);
			} else if (node.isTop()) {
				bs.set(68);
				bs.set(71, 76);
			}
			if (firstOrder && !node.isTop() && !node.isBottom())
				bs.set(104, 106);
		}

		// Equivalences involving ∀
		else if (firstOrder && node.isAll()) {
			UnaryOperator unary = (UnaryOperator) node;
			Node child = unary.getChild();
			String variable = unary.getVars().peek();

			bs.set(78, child.isNot());
			bs.set(79, child.isAll());
			bs.set(80, child.isAnd());
			if (child.isOr()) {
				Node rightGrandChild = ((BinaryOperator) child).getRightChild();
				Node leftGrandChild = ((BinaryOperator) child).getLeftChild();
				bs.set(81, !rightGrandChild.hasFree(variable));
				bs.set(109, !leftGrandChild.hasFree(variable));
			} else if (child.isImplies()) {
				Node leftGrandChild = ((BinaryOperator) child).getLeftChild();
				Node rightGrandChild = ((BinaryOperator) child).getRightChild();

				bs.set(82, !rightGrandChild.hasFree(variable));
				bs.set(83, !leftGrandChild.hasFree(variable));
			}
			bs.set(84, !child.hasFree(variable));
			bs.set(102);
		}

		// Equivalences involving ∃
		else if (firstOrder && node.isExists()) {
			UnaryOperator unary = (UnaryOperator) node;
			Node child = unary.getChild();
			String variable = unary.getVars().peek();

			bs.set(86, child.isNot());
			bs.set(87, child.isExists());
			bs.set(88, child.isOr());
			if (child.isAnd()) {
				Node rightGrandChild = ((BinaryOperator) child).getRightChild();
				Node leftGrandChild = ((BinaryOperator) child).getLeftChild();
				bs.set(89, !rightGrandChild.hasFree(variable));
				bs.set(110, !leftGrandChild.hasFree(variable));
			} else if (child.isImplies()) {
				Node leftGrandChild = ((BinaryOperator) child).getLeftChild();
				Node rightGrandChild = ((BinaryOperator) child).getRightChild();

				bs.set(90, !rightGrandChild.hasFree(variable));
				bs.set(91, !leftGrandChild.hasFree(variable));
			}
			bs.set(92, !child.hasFree(variable));
			bs.set(103);
		}

		return bs;
	}

	public SparseArray<String> rulesToStringMap(BitSet bs, FormationTree tree, Node node) {
		return rs.rulesToStringMap(bs, tree, node);
	}

	public void applyRuleFromBitSet(int index, FormationTree tree, 
			Node node, String input) {
		int key = node.getKey();
		int depth = node.getDepth();

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
		case 44:	ra.applyNotAndToImplies(tree, (UnaryOperator) node);
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
		case 56:	ra.applyImpliesToNotAnd(tree, (BinaryOperator) node);
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
		case 78: 	ra.applyAllComplementation(tree, (UnaryOperator) node);
		break;
		case 79:	ra.applyAllSwapQuantifiers(tree, (UnaryOperator) node);
		break;
		case 80:	ra.applyAllDistrubutionOfQuantifiers(tree, (UnaryOperator) node);
		break;
		case 81:	ra.applyAllDistrubutionOfQuantifiersWithNoFreeVariables(tree, (UnaryOperator) node);
		break;
		case 82:	ra.applyImpliesDistributionOfQuantifiersLeft(tree, (UnaryOperator) node);
		break;
		case 83: 	ra.applyAllImpliesDistributionOfQuantifiersRight(tree, (UnaryOperator) node);
		break;
		case 84: 	ra.applySimplificationOfQuantifiers(tree, (UnaryOperator) node);
		break;
		case 86:	ra.applyExistsComplementation(tree, (UnaryOperator) node);
		break;
		case 87:	ra.applyExistsSwapQuantifiers(tree, (UnaryOperator) node);
		break;
		case 88:	ra.applyExistsDistributionOfQuantifiers(tree, (UnaryOperator) node);
		break;
		case 89: 	ra.applyExistsDistributionOfQuantifiersWithNoFreeVariables(tree, (UnaryOperator) node);
		break;
		case 90: 	ra.applyImpliesDistributionOfQuantifiersLeft(tree, (UnaryOperator) node);
		break;
		case 91:	ra.applyExistsImpliesDistributionOfQuantifiersRight(tree, (UnaryOperator) node);
		break;
		case 92:	ra.applySimplificationOfQuantifiers(tree, (UnaryOperator) node);
		break;
		case 93: 	ra.applyAllComplementationBackwards(tree, (UnaryOperator) node);
		break;
		case 94:	ra.applyExistsComplementationBackwards(tree, (UnaryOperator) node);
		break;
		case 95:	ra.applyAllDistributionOfQuantifiersBackwards(tree, (BinaryOperator) node);
		break;
		case 96:	ra.applyExistsDistributionOfQuantifiersWithNoFreeVariablesBackwards(tree, (BinaryOperator) node);
		break;
		case 97:	ra.applyAllDistributionOfQuantifiersWithNoFreeVariablesBackwards(tree, (BinaryOperator) node);
		break;
		case 98:	ra.applyExistsDistributionOfQuantifiersBackwards(tree, (BinaryOperator) node);
		break;
		case 99: 	ra.applyAllImpliesDistributionBackwardsLeft(tree, (BinaryOperator) node);
		break;
		case 100:	ra.applyAllImpliesDistributionBackwardsRight(tree, (BinaryOperator) node);
		break;
		case 101:	ra.applyExistsImpliesDistributionBackwards(tree, (BinaryOperator) node);
		break;
		case 102:	ra.applyRenameVariable(tree, (UnaryOperator) node, input);
		break;
		case 103:	ra.applyRenameVariable(tree, (UnaryOperator) node, input);
		break;
		case 104:	ra.applyAddAll(tree, node, input);
		break;
		case 105:	ra.applyAddExists(tree, node, input);
		break;
		case 106: 	ra.applyCommutativity((BinaryOperator) node);
		ra.applyAndNotToNotImplies(tree, (BinaryOperator) node);
		ra.applyCommutativity((BinaryOperator) node);
		break;
		case 107:	ra.applyCommutativity((BinaryOperator) node);
		ra.applyOrAndToIff(tree, (BinaryOperator) node);
		ra.applyCommutativity((BinaryOperator) node);
		break;
		case 108:	ra.applyCommutativity((BinaryOperator) node);
		ra.applyOrAndToNotIff(tree, (BinaryOperator) node);
		ra.applyCommutativity((BinaryOperator) node);
		break;
		case 109:	
		ra.applyCommutativity((BinaryOperator) ((UnaryOperator) node).getChild());
		ra.applyAllDistrubutionOfQuantifiersWithNoFreeVariables(tree, (UnaryOperator) node);
		ra.applyCommutativity((BinaryOperator) tree.findNode(key, depth));
		break;
		case 110:	ra.applyCommutativity((BinaryOperator) ((UnaryOperator) node).getChild());
		ra.applyExistsDistributionOfQuantifiersWithNoFreeVariables(tree, (UnaryOperator) node);
		ra.applyCommutativity((BinaryOperator) tree.findNode(key, depth));
		break;
		case 111:	ra.applyCommutativity((BinaryOperator) node);
		ra.applyExistsDistributionOfQuantifiersWithNoFreeVariablesBackwards(tree, (BinaryOperator) node);
		ra.applyCommutativity((BinaryOperator) ((UnaryOperator) tree.findNode(key, depth)).getChild());
		break;
		case 112:	ra.applyCommutativity((BinaryOperator) node);
		ra.applyAllDistributionOfQuantifiersWithNoFreeVariablesBackwards(tree, (BinaryOperator) node);
		ra.applyCommutativity((BinaryOperator) ((UnaryOperator) tree.findNode(key, depth)).getChild());
		break;
		}

	}

	public void applyRandomRules(FormationTree tree, int n) {
		for (int i = 0; i < n; ++i) {
			applyRuleToRandomNode(tree);
			System.out.println(tree);
		}
		System.out.println("");
	}

	public void applyRuleToRandomNode(FormationTree tree) {
		Node node = tree.randomNode();
		BitSet bs = getApplicableRules(tree, node);
		applyRandomRule(bs, tree, node, 0);
	}

	public void applyRandomRule(BitSet bs, FormationTree tree, Node node, int attempts) {
		int numSetBits = bs.cardinality();
		int nextSetBit = bs.nextSetBit(0);

		Random rand = new Random();
		int randomNum = rand.nextInt(numSetBits);

		while (randomNum-- > 0)
			nextSetBit = bs.nextSetBit(nextSetBit + 1);

		// user input is chosen randomly
		String var = null;
		boolean notFirstOrderRule = nextSetBit < first_order_rules;

		if (nextSetBit >= min_user_input_required && notFirstOrderRule || nextSetBit >= fo_user_input_required) {
			LinkedList<String> usedVars = tree.getUsedQuantifiedVars();
			SortedSet<String> vars = new TreeSet<String>();

			if (notFirstOrderRule) {
				vars = tree.getAtoms();
				vars.removeAll(usedVars);

				rand = new Random();

				if (vars.size() != 0) {
					int randomVar = rand.nextInt(vars.size());

					Iterator<String> it = vars.iterator();
					for (int i = 0; i <= randomVar && it.hasNext(); ++i)
						var = it.next();
				} else {
					var = "x";
				}
			} else {
				System.out.println("");

				Variable[] variables = Variable.values();
				for (Variable v : variables) {
					String s = v.getValue();

					// Adding quantifier rules
					if (nextSetBit == 104 || nextSetBit == 105) {
						if (s != "┬" && s != "⊥" && !node.hasFree(s)) {
							Node parent = node.getParent();
							while (!parent.isBound(s)) {
								if (parent.isRoot()) {
									vars.add(s);
									break;
								} else {
									parent = parent.getParent();
								}
							}

						}
						// Replacement rules
					} else if (s != "┬" && s != "⊥" && !(node.hasFree(s) || node.isBound(s)))
						vars.add(s);
				}
				rand = new Random();

				if (vars.size() != 0) {
					int randomVar = rand.nextInt(vars.size());

					Iterator<String> it = vars.iterator();
					for (int i = 0; i <= randomVar && it.hasNext(); ++i)
						var = it.next();
				} else {
					var = "Px";
				}
			}

		}

		// rule to be applied creates truth values
		if (rulesWithTruths.contains(nextSetBit)) {
			rand = new Random();
			int randProbability = rand.nextInt(100);

			if (randProbability >= probability) {
				// Truth rule probably only option so don't apply any rule
				if (attempts > 10)
					return;

				applyRandomRule(bs, tree, node, ++attempts);
				return;
			}

		}

		System.out.println("Rule: " + nextSetBit);
		if (var != null)
			System.out.println("Input: " + var);
		applyRuleFromBitSet(nextSetBit, tree, node, var);
	}

	public void setTruthValueProbability(int probability) {
		this.probability = probability;
	}

	public int getMinFirstOrderRules() {
		return first_order_rules;
	}

	public int getMinFOUserInputRequired() {
		return fo_user_input_required;
	}

	public void setFirstOrder(boolean firstOrder) {
		this.firstOrder = firstOrder;
	}
}
