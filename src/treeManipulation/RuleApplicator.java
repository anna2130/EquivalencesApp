package treeManipulation;

import treeBuilder.Atom;
import treeBuilder.BinaryOperator;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.UnaryOperator;

public class RuleApplicator {
	
	private void relabelNode(Node node) {
		if (node.hasChildren()) {
			Node[] children = node.getChildren();
			int key = node.getKey();
			int depth = node.getDepth();
			
			if (children.length > 0) {
				children[0].setKey(key << 1);
				children[0].setDepth(depth + 1);
				
				relabelNode(children[0]);
			}
			if (children.length > 1) {
				children[1].setKey((key << 1) + 1);
				children[1].setDepth(depth + 1);
				
				relabelNode(children[1]);
			}
		}
	}
	
	public void replaceNode(FormationTree tree, Node node, Node result) {
		Node parent = result;
		
		if (node.isRoot())
			tree.setRoot(result);
		else {
			parent = node.getParent();
			if (parent instanceof BinaryOperator) {
				if (node.getKey() % 2 == 0)
					((BinaryOperator) parent).setLeftChild(result);
				else
					((BinaryOperator) parent).setRightChild(result);
			} else
				((UnaryOperator) parent).setChild(result);
		}
		relabelNode(parent);
	}
	
	public Atom createNewAtom(String type) {
		return new Atom(0, 0, type);
	}
	
	public Atom createNewTop() {
		return createNewAtom("┬");
	}
	
	public Atom createNewBottom() {
		return createNewAtom("⊥");
	}

	public UnaryOperator createNewUnary(String type, Node child) {
		UnaryOperator result = new UnaryOperator(0, 0, type);
		result.setChild(child);
		return result;
	}
	
	public UnaryOperator createNewNot(Node child) {
		return createNewUnary("¬", child);
	}
	
	public BinaryOperator createNewBinary(String type, Node leftChild, Node rightChild) {
		BinaryOperator result = new BinaryOperator(0, 0, type);
		result.setLeftChild(leftChild);
		result.setRightChild(rightChild);
		return result;
	}
	
	public BinaryOperator createNewAnd(Node leftChild, Node rightChild) {
		return createNewBinary("^", leftChild, rightChild);
	}
	
	public BinaryOperator createNewOr(Node leftChild, Node rightChild) {
		return createNewBinary("v", leftChild, rightChild);
	}
	
	public BinaryOperator createNewImplies(Node leftChild, Node rightChild) {
		return createNewBinary("→", leftChild, rightChild);
	}
	
	public BinaryOperator createNewIff(Node leftChild, Node rightChild) {
		return createNewBinary("↔", leftChild, rightChild);
	}

	// 2.  a^┬		|- 	a
	// 25. av⊥		|-  a
	public void applyToLeftChild(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, node.getLeftChild());
	}

	// 3.  ┬^a		|-  a
	// 26. ⊥va		|-  a
	// 51. ┬→a		|- 	a
	public void applyToRightChild(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, node.getRightChild());
	}
	
	// 21. ┬va		|-  ┬
	// 22. av┬		|-  ┬
	// 23. av¬a		|-  ┬
	// 24. ¬ava		|-  ┬
	// 50. a→a		|- 	┬
	// 52. a→┬		|- 	┬
	// 53. ⊥→a		|- 	┬
	public void applyToTop(FormationTree tree, Node node) {
		replaceNode(tree, node, createNewTop());
	}

	// 4.  ⊥^a		|-  ⊥
	// 5.  a^⊥		|-  ⊥
	// 6.  a^¬a		|-  ⊥				-- Could do inverse operation here requiring user input (to define variable)
	// 7.  ¬a^a		|-  ⊥	
	public void applyToBottom(FormationTree tree, Node node) {
		replaceNode(tree, node, createNewBottom());
	}
	
	// TODO: Basic Rules

	// 0.  a^b 		|- 	b^a				-- Commutativity
	// 19. avb		|- 	bva				-- Commutativity
	public void applyCommutativity(BinaryOperator node) {
		Node leftChild = node.getLeftChild();
		node.setLeftChild(node.getRightChild());
		node.setRightChild(leftChild);
		
		relabelNode(node);
	}

	// 1.  a^a		|-  a 				-- Idempotence
	// 20. ava		|- 	a 				-- Idempotence
	public void applyIdempotence(FormationTree tree, BinaryOperator node) {
		Node child = node.getLeftChild();
		replaceNode(tree, node, child);
	}

	// 8.  a^(b^c)	|-  (a^b)^c			-- Associativity
	// 27. av(bvc)	|-  (avb)vc			-- Associativity
	public void applyRightAssociativity(FormationTree tree, BinaryOperator node) {
		applyRightRotation(tree, node);
	}

	// 9.  (a^b)^c  |- 	a^(b^c)			-- Associativity
	// 28. (avb)vc  |- 	av(bvc)			-- Associativity
	public void applyLeftAssociativity(FormationTree tree, BinaryOperator node) {
		applyLeftRotation(tree, node);
	}
	
	// 17. a^(avb)  |-	a
	// 36. av(a^b)	|-  a
	public void applyLeftAbsorption(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, node.getLeftChild());
	}
	
	// 18. (avb)^a  |-	a
	// 37. (a^b)va	|-  a
	public void applyRightAbsorption(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, node.getRightChild());
	}
	
	// TODO: And Rules
				
	// 10. a^¬b 		|-	¬(a→b)
	public void applyAndNotToNotImplies(FormationTree tree, BinaryOperator node) {
		UnaryOperator rightChild = (UnaryOperator) node.getRightChild();
		BinaryOperator implies = createNewImplies(node.getLeftChild(), rightChild.getChild());
		replaceNode(tree, node, createNewNot(implies));
	}
	
	// 11. (a→b)^(b→a)	|-  a↔b
	public void applyAndImpliesToIff(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		replaceNode(tree, node, createNewIff(leftChild.getLeftChild(), leftChild.getRightChild()));
	}
	
	// 12. ¬a^¬b		|-	¬(avb)			-- De Morgan laws
	public void applyDeMorganOrBackwards(FormationTree tree, BinaryOperator node) {
		UnaryOperator leftChild = (UnaryOperator) node.getLeftChild();
		UnaryOperator rightChild = (UnaryOperator) node.getRightChild();
		BinaryOperator or = createNewOr(leftChild.getChild(), rightChild.getChild());
		replaceNode(tree, node, createNewNot(or));
	}
	
	// 13. a^(bvc) 		|- 	(a^b)v(a^c)		-- Distributitivity
	public void applyDistributivityAndLeftForwards(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator leftAnd = createNewAnd(leftChild, rightChild.getLeftChild());
		BinaryOperator rightAnd = createNewAnd(leftChild.clone(), rightChild.getRightChild());
		replaceNode(tree, node, createNewOr(leftAnd, rightAnd));
	}
	
	// 14. (avb)^c		|-  (a^c)v(b^c)		-- Distributitivity
	public void applyDistributivityAndRightForwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		Node rightChild = node.getRightChild();
		BinaryOperator leftAnd = createNewAnd(leftChild.getLeftChild(), rightChild);
		BinaryOperator rightAnd = createNewAnd(leftChild.getRightChild(), rightChild.clone());
		replaceNode(tree, node, createNewOr(leftAnd, rightAnd));
	}
	
	// 15. (avb)^(avc)	|- 	av(b^c)		-- Distributitivity
	public void applyDistributivityOrLeftBackwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator and = createNewAnd(leftChild.getRightChild(), rightChild.getRightChild());
		replaceNode(tree, node, createNewOr(leftChild.getLeftChild(), and));
	}
	
	// 16. (avc)^(bvc)	|-	(a^b)vc		-- Distributitivity
	public void applyDistributivityOrRightBackwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator and = createNewAnd(leftChild.getLeftChild(), rightChild.getLeftChild());
		replaceNode(tree, node, createNewOr(and, leftChild.getLeftChild()));
	}

	// TODO: Or Rules
	
	// 29. (a^b)v(¬a^¬b)	|-	a↔b
	public void applyOrAndToIff(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		replaceNode(tree, node, createNewIff(leftChild.getLeftChild(), leftChild.getRightChild()));
	}
	
	// 30. (a^¬b)v(¬a^b) 	|-  ¬(a↔b)
	public void applyOrAndToNotIff(FormationTree tree, BinaryOperator node) {
		Node leftChild = ((BinaryOperator) node.getLeftChild()).getLeftChild();
		Node rightChild = ((BinaryOperator) node.getRightChild()).getRightChild();
		BinaryOperator iff = createNewIff(leftChild, rightChild);
		replaceNode(tree, node, createNewNot(iff));
	}
	
	// 31. ¬av¬b	|-  ¬(a^b)			-- De Morgan laws
	public void applyDeMorganAndBackwards(FormationTree tree, BinaryOperator node) {
		UnaryOperator leftChild = (UnaryOperator) node.getLeftChild();
		UnaryOperator rightChild = (UnaryOperator) node.getRightChild();
		BinaryOperator and = createNewAnd(leftChild.getChild(), rightChild.getChild());
		replaceNode(tree, node, createNewNot(and));
	}
	
	// 32. av(b^c)	|- 	(avb)^(avc)		-- Distributitivity
	public void applyDistributivityOrLeftForwards(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator leftOr = createNewOr(leftChild, rightChild.getLeftChild());
		BinaryOperator rightOr = createNewOr(leftChild.clone(), rightChild.getRightChild());
		replaceNode(tree, node, createNewAnd(leftOr, rightOr));
	}
	
	// 33. (a^b)vc	|-	(avc)^(bvc)		-- Distributitivity
	public void applyDistributivityOrRightForwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		Node rightChild = node.getRightChild();
		BinaryOperator leftOr = createNewOr(leftChild.getLeftChild(), rightChild);
		BinaryOperator rightOr = createNewOr(leftChild.getRightChild(), rightChild.clone());
		replaceNode(tree, node, createNewAnd(leftOr, rightOr));
	}
	
	// 34. (a^b)v(a^c) 	|- 	a^(bvc)		-- Distributitivity
	public void applyDistributivityAndLeftBackwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator or = createNewOr(leftChild.getRightChild(), rightChild.getRightChild());
		replaceNode(tree, node, createNewAnd(leftChild.getLeftChild(), or));
	}
	
	// 35. (a^c)v(b^c)	|-  (avb)^c		-- Distributitivity
	public void applyDistributivityAndRightBackwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator or = createNewOr(leftChild.getLeftChild(), rightChild.getLeftChild());
		replaceNode(tree, node, createNewAnd(or, leftChild.getRightChild()));
	}
	
	// 38. ¬avb		|- 	a→b
	public void applyOrToImplies(FormationTree tree, BinaryOperator node) {
		UnaryOperator leftChild = (UnaryOperator) node.getLeftChild();
		replaceNode(tree, node, createNewImplies(leftChild.getChild(), node.getRightChild()));
	}
	
	// TODO: Not Rules
	
	// 39. ¬┬		|-  ⊥
	public void applyNotTop(FormationTree tree, UnaryOperator node) {
		replaceNode(tree, node, createNewBottom());
	}
	
	// 40. ¬⊥		|-  ┬
	public void applyNotBottom(FormationTree tree, UnaryOperator node) {
		replaceNode(tree, node, createNewTop());
	}
	
	// 41. ¬¬a 		|- 	a
	public void applyNotNot(FormationTree tree, UnaryOperator node) {
		Node result = ((UnaryOperator) node.getChild()).getChild();
		replaceNode(tree, node, result);
	}
	
	// 42. ¬a		|-  a→⊥
	public void applyNotAtom(FormationTree tree, UnaryOperator node) {
		replaceNode(tree, node, createNewImplies(node.getChild(), createNewBottom()));
	}
	
	// 43. ¬(a→b) 	|-	a^¬b
	public void applyNotImplies(FormationTree tree, UnaryOperator node) {
		BinaryOperator implies = (BinaryOperator) node.getChild();
		UnaryOperator not = createNewNot(implies.getLeftChild());
		replaceNode(tree, node, createNewAnd(implies.getLeftChild(), not));
	}
	
	// 44. ¬(av¬b)	|- 	a→b
	public void applyNotOrToImplies(FormationTree tree, UnaryOperator node) {
		BinaryOperator or = (BinaryOperator) node.getChild();
		UnaryOperator not = (UnaryOperator) or.getRightChild();
		replaceNode(tree, node, createNewImplies(or.getLeftChild(), not.getChild()));
	}
	
	// 45. ¬(a↔b) 	|- 	a↔¬b
	public void applyNotIffToNotB(FormationTree tree, UnaryOperator node) {
		BinaryOperator iff = (BinaryOperator) node.getChild();
		UnaryOperator not = createNewNot(iff.getRightChild());
		replaceNode(tree, node, createNewIff(iff.getLeftChild(), not));
	}
	
	// 46. ¬(a↔b) 	|-  ¬a↔b
	public void applyNotIffToNotA(FormationTree tree, UnaryOperator node) {
		BinaryOperator iff = (BinaryOperator) node.getChild();
		UnaryOperator not = createNewNot(iff.getLeftChild());
		replaceNode(tree, node, createNewIff(not, iff.getRightChild()));
	}
	
	// 47. ¬(a↔b) 	|-  (a^¬b)v(¬a^b)	-- Exclusive or of A and b
	public void applyNotIffToOrAnd(FormationTree tree, UnaryOperator node) {
		BinaryOperator iff = (BinaryOperator) node.getChild();
		Node leftChild = createNewAnd(iff.getLeftChild(), createNewNot(iff.getRightChild()));
		Node rightChild = createNewAnd(createNewNot(iff.getLeftChild().clone()), iff.getRightChild().clone());
		replaceNode(tree, node, createNewOr(leftChild, rightChild));
	}
	
	// 48. ¬(a^b)	|-  ¬av¬b			-- De Morgan laws
	public void applyDeMorganAndForwards(FormationTree tree, UnaryOperator node) {
		BinaryOperator and = (BinaryOperator) node.getChild();
		Node leftChild = createNewNot(and.getLeftChild());
		Node rightChild = createNewNot(and.getRightChild());
		replaceNode(tree, node, createNewOr(leftChild, rightChild));
	}
	
	// 49. ¬(avb)	|-	¬a^¬b			-- De Morgan laws
	public void applyDeMorganOrForwards(FormationTree tree, UnaryOperator node) {
		BinaryOperator or = (BinaryOperator) node.getChild();
		Node leftChild = createNewNot(or.getLeftChild());
		Node rightChild = createNewNot(or.getRightChild());
		replaceNode(tree, node, createNewAnd(leftChild, rightChild));
	}
	
	// TODO: Implies Rules
	
	// 54. a→⊥		|- 	¬a
	public void applyImpliesToNot(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, createNewNot(node.getLeftChild()));
	}
	
	// 55. a→b 		|- 	¬avb
	public void applyImpliesToOr(FormationTree tree, BinaryOperator node) {
		UnaryOperator not = createNewNot(node.getLeftChild());
		replaceNode(tree, node, createNewOr(not, node.getRightChild()));
	}
	
	// 56. a→b 		|- 	¬(av¬b)
	public void applyImpliesToNotOr(FormationTree tree, BinaryOperator node) {
		UnaryOperator not = createNewNot(node.getRightChild());
		BinaryOperator or = createNewOr(node.getLeftChild(), not);
		replaceNode(tree, node, createNewNot(or));
	}

	// TODO: Iff Rules
	
	// 57. a↔b				|-  (a→b)^(b→a)
	public void applyIffToAndImplies(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = createNewImplies(node.getLeftChild(), node.getRightChild());
		BinaryOperator rightChild = createNewImplies(node.getRightChild().clone(), node.getLeftChild().clone());
		replaceNode(tree, node, createNewAnd(leftChild, rightChild));
	}
	
	// 58. a↔b				|-	(a^b)v(¬a^¬b)
	public void applyIffToOrAnd(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = createNewAnd(node.getLeftChild(), node.getRightChild());
		UnaryOperator leftNot = createNewNot(node.getRightChild().clone());
		UnaryOperator rightNot = createNewNot(node.getLeftChild().clone());
		BinaryOperator rightChild = createNewAnd(leftNot, rightNot);
		replaceNode(tree, node, createNewAnd(leftChild, rightChild));
	}
	
	// 59. a↔¬b 			|- 	¬(a↔b)
	public void applyIffNotBToNotIff(FormationTree tree, BinaryOperator node) {
		UnaryOperator not = (UnaryOperator) node.getRightChild();
		BinaryOperator iff = createNewIff(node.getLeftChild(), not.getChild());
		replaceNode(tree, node, createNewNot(iff));
	}
	
	// 60. ¬a↔b 			|-  ¬(a↔b)
	public void applyIffNotAToNotIff(FormationTree tree, BinaryOperator node) {
		UnaryOperator not = (UnaryOperator) node.getLeftChild();
		BinaryOperator iff = createNewIff(not.getChild(), node.getRightChild());
		replaceNode(tree, node, createNewNot(iff));
	}
	
	// TODO: Equivalences involving atoms
	
	// 61. a		|-  a^a
	public void applyAndIdempotenceBackwards(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewAnd(node, node.clone()));
	}
	
	// 62. a		|-  a^┬
	public void applyAndTop(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewAnd(node, createNewTop()));
	}
	
	// 63. a		|-  ava
	public void applyOrIdempotenceBackwards(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewOr(node, node.clone()));
	}
	
	// 64. a		|-  av⊥
	public void applyOrBottom(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewOr(node, createNewBottom()));
	}
	
	// 65. a		|-  ¬¬a
	public void applyNotNotBackwards(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewNot(createNewNot(node)));
	}
	
	// 66. a		|-  ┬→a
	public void applyTopImplies(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewImplies(createNewTop(), node));
	}
	
	// 67. ⊥		|-  ¬┬
	public void applyNotTop(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewNot(createNewTop()));
	}
	
	// 68. ┬		|-  ¬⊥
	public void applyNotBottom(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewNot(createNewBottom()));
	}
	
	// TODO: Equivalences involving user input
	
	// 69. ⊥		|-  a^⊥
	// 70. ⊥		|-  a^¬a
	// 71. ┬		|-  av┬
	// 72. ┬		|-  av¬a
	// 73. ┬		|- 	a→a	
	// 74. ┬		|- 	a→┬
	// 75. ┬		|- 	⊥→a
	// 76. a		|-  av(a^b)
	// 77. a  		|-	a^(avb)
	
	// TODO: Tree rotations
	
	public void applyRightRotation(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();

		Node parent = leftChild;
		Node[] grandChildren = leftChild.getChildren();
		if (leftChild.hasChildren() && grandChildren.length == 2)
			node.setLeftChild(grandChildren[1]);
		
		if (node.isRoot())
			tree.setRoot(leftChild);
		else {
			parent = node.getParent();
			
			if (parent instanceof UnaryOperator)
				((UnaryOperator) parent).setChild(leftChild);
			else {
				// Check if left or right child
				if (node.getKey() >> (node.getDepth() - 1) == 0) 
					((BinaryOperator) parent).setLeftChild(leftChild);
				else 
					((BinaryOperator) parent).setRightChild(leftChild);
			}
		}
		leftChild.setRightChild(node);
		relabelNode(parent);
	}
	
	public void applyLeftRotation(FormationTree tree, BinaryOperator node) {
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();

		Node parent = rightChild;
		Node[] grandChildren = rightChild.getChildren();
		if (rightChild.hasChildren() && grandChildren.length == 2)
			node.setRightChild(grandChildren[0]);
		
		if (node.isRoot())
			tree.setRoot(rightChild);
		else {
			parent = node.getParent();
			
			if (parent instanceof UnaryOperator)
				((UnaryOperator) parent).setChild(rightChild);
			else {
				// Check if left or right child
				if (node.getKey() >> (node.getDepth() - 1) == 0) 
					((BinaryOperator) parent).setLeftChild(rightChild);
				else 
					((BinaryOperator) parent).setRightChild(rightChild);
			}
		}
		rightChild.setLeftChild(node);
		relabelNode(parent);
	}
}