package treeManipulation;

import java.util.LinkedList;

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
		String value = type.charAt(0) + "";
		LinkedList<String> vars = new LinkedList<String>();

		System.out.println("New atom " + type);

		if (type.length() > 1) {
			for (int i = 1; i < type.length(); ++i) {
				char c = type.charAt(i);
				if (c <= 'z' && c >= 'a')
					vars.add(c + "");
			}
		}

		System.out.println("Val " + value);
		System.out.println("Vars " + vars);

		return new Atom(0, 0, value, vars);
	}

	public Atom createNewTop() {
		return createNewAtom("┬");
	}

	public Atom createNewBottom() {
		return createNewAtom("⊥");
	}

	public UnaryOperator createNewUnary(String type, Node child, LinkedList<String> vars) {
		UnaryOperator result = new UnaryOperator(0, 0, type, vars);
		result.setChild(child.clone());
		return result;
	}

	public UnaryOperator createNewAll(Node child, LinkedList<String> vars) {
		return createNewUnary("∀", child, vars);
	}

	public UnaryOperator createNewExists(Node child, LinkedList<String> vars) {
		return createNewUnary("∃", child, vars);
	}

	public UnaryOperator createNewNot(Node child) {
		return createNewUnary("¬", child, new LinkedList<String>());
	}

	public BinaryOperator createNewBinary(String type, Node leftChild, Node rightChild) {
		BinaryOperator result = new BinaryOperator(0, 0, type, new LinkedList<String>());
		result.setLeftChild(leftChild.clone());
		result.setRightChild(rightChild.clone());
		return result;
	}

	public BinaryOperator createNewAnd(Node leftChild, Node rightChild) {
		return createNewBinary("∧", leftChild, rightChild);
	}

	public BinaryOperator createNewOr(Node leftChild, Node rightChild) {
		return createNewBinary("∨", leftChild, rightChild);
	}

	public BinaryOperator createNewImplies(Node leftChild, Node rightChild) {
		return createNewBinary("→", leftChild, rightChild);
	}

	public BinaryOperator createNewIff(Node leftChild, Node rightChild) {
		return createNewBinary("↔", leftChild, rightChild);
	}

	// 2.  a∧┬		|- 	a
	// 25. av⊥		|-  a
	public void applyToLeftChild(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, node.getLeftChild());
	}

	// 3.  ┬∧a		|-  a
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

	// 4.  ⊥∧a		|-  ⊥
	// 5.  a∧⊥		|-  ⊥
	// 6.  a∧¬a		|-  ⊥				-- Could do inverse operation here requiring user input (to define variable)
	// 7.  ¬a∧a		|-  ⊥	
	public void applyToBottom(FormationTree tree, Node node) {
		replaceNode(tree, node, createNewBottom());
	}

	// TODO: Basic Rules

	// 0.  a∧b 		|- 	b∧a				-- Commutativity
	// 19. avb		|- 	bva				-- Commutativity
	public void applyCommutativity(BinaryOperator node) {
		Node leftChild = node.getLeftChild();
		node.setLeftChild(node.getRightChild());
		node.setRightChild(leftChild);

		relabelNode(node);
	}

	// 1.  a∧a		|-  a 				-- Idempotence
	// 20. ava		|- 	a 				-- Idempotence
	public void applyIdempotence(FormationTree tree, BinaryOperator node) {
		Node child = node.getLeftChild();
		replaceNode(tree, node, child);
	}

	// 8.  a∧(b∧c)	|-  (a∧b)∧c			-- Associativity
	// 27. av(bvc)	|-  (avb)vc			-- Associativity
	public void applyLeftAssociativity(FormationTree tree, BinaryOperator node) {
		applyLeftRotation(tree, node);
	}

	// 9.  (a∧b)∧c  |- 	a∧(b∧c)			-- Associativity
	// 28. (avb)vc  |- 	av(bvc)			-- Associativity
	public void applyRightAssociativity(FormationTree tree, BinaryOperator node) {
		applyRightRotation(tree, node);
	}

	// 17. a∧(avb)  |-	a
	// 36. av(a∧b)	|-  a
	public void applyLeftAbsorption(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, node.getLeftChild());
	}

	// 18. (avb)∧a  |-	a
	// 37. (a∧b)va	|-  a
	public void applyRightAbsorption(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, node.getRightChild());
	}

	// TODO: And Rules

	// 10. a∧¬b 		|-	¬(a→b)
	public void applyAndNotToNotImplies(FormationTree tree, BinaryOperator node) {
		UnaryOperator rightChild = (UnaryOperator) node.getRightChild();
		BinaryOperator implies = createNewImplies(node.getLeftChild(), rightChild.getChild());
		replaceNode(tree, node, createNewNot(implies));
	}

	// 11. (a→b)∧(b→a)	|-  a↔b
	public void applyAndImpliesToIff(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		replaceNode(tree, node, createNewIff(leftChild.getLeftChild(), leftChild.getRightChild()));
	}

	// 12. ¬a∧¬b		|-	¬(avb)			-- De Morgan laws
	public void applyDeMorganOrBackwards(FormationTree tree, BinaryOperator node) {
		UnaryOperator leftChild = (UnaryOperator) node.getLeftChild();
		UnaryOperator rightChild = (UnaryOperator) node.getRightChild();
		BinaryOperator or = createNewOr(leftChild.getChild(), rightChild.getChild());
		replaceNode(tree, node, createNewNot(or));
	}

	// 13. a∧(bvc) 		|- 	(a∧b)v(a∧c)		-- Distributitivity
	public void applyDistributivityAndLeftForwards(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator leftAnd = createNewAnd(leftChild, rightChild.getLeftChild());
		BinaryOperator rightAnd = createNewAnd(leftChild, rightChild.getRightChild());
		replaceNode(tree, node, createNewOr(leftAnd, rightAnd));
	}

	// 14. (avb)∧c		|-  (a∧c)v(b∧c)		-- Distributitivity
	public void applyDistributivityAndRightForwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		Node rightChild = node.getRightChild();
		BinaryOperator leftAnd = createNewAnd(leftChild.getLeftChild(), rightChild);
		BinaryOperator rightAnd = createNewAnd(leftChild.getRightChild(), rightChild);
		replaceNode(tree, node, createNewOr(leftAnd, rightAnd));
	}

	// 15. (avb)∧(avc)	|- 	av(b∧c)		-- Distributitivity
	public void applyDistributivityOrLeftBackwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator and = createNewAnd(leftChild.getRightChild(), rightChild.getRightChild());
		replaceNode(tree, node, createNewOr(leftChild.getLeftChild(), and));
	}

	// 16. (avc)∧(bvc)	|-	(a∧b)vc		-- Distributitivity
	public void applyDistributivityOrRightBackwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator and = createNewAnd(leftChild.getLeftChild(), rightChild.getLeftChild());
		replaceNode(tree, node, createNewOr(and, leftChild.getRightChild()));
	}

	// TODO: Or Rules

	// 29. (a∧b)v(¬a∧¬b)	|-	a↔b
	public void applyOrAndToIff(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		replaceNode(tree, node, createNewIff(leftChild.getLeftChild(), leftChild.getRightChild()));
	}

	// 30. (a∧¬b)v(¬a∧b) 	|-  ¬(a↔b)
	public void applyOrAndToNotIff(FormationTree tree, BinaryOperator node) {
		Node leftChild = ((BinaryOperator) node.getLeftChild()).getLeftChild();
		Node rightChild = ((BinaryOperator) node.getRightChild()).getRightChild();
		BinaryOperator iff = createNewIff(leftChild, rightChild);
		replaceNode(tree, node, createNewNot(iff));
	}

	// 31. ¬av¬b	|-  ¬(a∧b)			-- De Morgan laws
	public void applyDeMorganAndBackwards(FormationTree tree, BinaryOperator node) {
		UnaryOperator leftChild = (UnaryOperator) node.getLeftChild();
		UnaryOperator rightChild = (UnaryOperator) node.getRightChild();
		BinaryOperator and = createNewAnd(leftChild.getChild(), rightChild.getChild());
		replaceNode(tree, node, createNewNot(and));
	}

	// 32. av(b∧c)	|- 	(avb)∧(avc)		-- Distributitivity
	public void applyDistributivityOrLeftForwards(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator leftOr = createNewOr(leftChild, rightChild.getLeftChild());
		BinaryOperator rightOr = createNewOr(leftChild, rightChild.getRightChild());
		replaceNode(tree, node, createNewAnd(leftOr, rightOr));
	}

	// 33. (a∧b)vc	|-	(avc)∧(bvc)		-- Distributitivity
	public void applyDistributivityOrRightForwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		Node rightChild = node.getRightChild();
		BinaryOperator leftOr = createNewOr(leftChild.getLeftChild(), rightChild);
		BinaryOperator rightOr = createNewOr(leftChild.getRightChild(), rightChild);
		replaceNode(tree, node, createNewAnd(leftOr, rightOr));
	}

	// 34. (a∧b)v(a∧c) 	|- 	a∧(bvc)		-- Distributitivity
	public void applyDistributivityAndLeftBackwards(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		BinaryOperator or = createNewOr(leftChild.getRightChild(), rightChild.getRightChild());
		replaceNode(tree, node, createNewAnd(leftChild.getLeftChild(), or));
	}

	// 35. (a∧c)v(b∧c)	|-  (avb)∧c		-- Distributitivity
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

	// 43. ¬(a→b) 	|-	a∧¬b
	public void applyNotImplies(FormationTree tree, UnaryOperator node) {
		BinaryOperator implies = (BinaryOperator) node.getChild();
		UnaryOperator not = createNewNot(implies.getRightChild());
		replaceNode(tree, node, createNewAnd(implies.getLeftChild(), not));
	}

	// 44. ¬(a∧¬b)	|- 	a→b
	public void applyNotAndToImplies(FormationTree tree, UnaryOperator node) {
		BinaryOperator and = (BinaryOperator) node.getChild();
		UnaryOperator not = (UnaryOperator) and.getRightChild();
		replaceNode(tree, node, createNewImplies(and.getLeftChild(), not.getChild()));
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

	// 47. ¬(a↔b) 	|-  (a∧¬b)v(¬a∧b)	-- Exclusive or of A and b
	public void applyNotIffToOrAnd(FormationTree tree, UnaryOperator node) {
		BinaryOperator iff = (BinaryOperator) node.getChild();
		Node leftChild = createNewAnd(iff.getLeftChild(), createNewNot(iff.getRightChild()));
		Node rightChild = createNewAnd(createNewNot(iff.getLeftChild()), iff.getRightChild());
		replaceNode(tree, node, createNewOr(leftChild, rightChild));
	}

	// 48. ¬(a∧b)	|-  ¬av¬b			-- De Morgan laws
	public void applyDeMorganAndForwards(FormationTree tree, UnaryOperator node) {
		BinaryOperator and = (BinaryOperator) node.getChild();
		Node leftChild = createNewNot(and.getLeftChild());
		Node rightChild = createNewNot(and.getRightChild());
		replaceNode(tree, node, createNewOr(leftChild, rightChild));
	}

	// 49. ¬(avb)	|-	¬a∧¬b			-- De Morgan laws
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

	// 56. a→b 		|- 	¬(a∧¬b)
	public void applyImpliesToNotAnd(FormationTree tree, BinaryOperator node) {
		UnaryOperator not = createNewNot(node.getRightChild());
		BinaryOperator and = createNewAnd(node.getLeftChild(), not);
		replaceNode(tree, node, createNewNot(and));
	}

	// TODO: Iff Rules

	// 57. a↔b				|-  (a→b)∧(b→a)
	public void applyIffToAndImplies(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = createNewImplies(node.getLeftChild(), node.getRightChild());
		BinaryOperator rightChild = createNewImplies(node.getRightChild(), node.getLeftChild());
		replaceNode(tree, node, createNewAnd(leftChild, rightChild));
	}

	// 58. a↔b				|-	(a∧b)v(¬a∧¬b)
	public void applyIffToOrAnd(FormationTree tree, BinaryOperator node) {
		Node leftChild = node.getLeftChild();
		Node rightChild = node.getRightChild();
		UnaryOperator leftNot = createNewNot(leftChild);
		UnaryOperator rightNot = createNewNot(rightChild);
		BinaryOperator leftAnd = createNewAnd(leftChild, rightChild);
		BinaryOperator rightAnd = createNewAnd(leftNot, rightNot);
		replaceNode(tree, node, createNewOr(leftAnd, rightAnd));
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

	// 61. a		|-  a∧a
	public void applyAndIdempotenceBackwards(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewAnd(node, node));
	}

	// 62. a		|-  a∧┬
	public void applyAndTop(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewAnd(node, createNewTop()));
	}

	// 63. a		|-  ava
	public void applyOrIdempotenceBackwards(FormationTree tree, Atom node) {
		replaceNode(tree, node, createNewOr(node, node));
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

	// 69. ⊥		|-  a∧⊥
	public void applyAtomAndBottom(FormationTree tree, Node node, String atom) {

		replaceNode(tree, node, createNewAnd(createNewAtom(atom), node));
	}

	// 70. ⊥		|-  a∧¬a
	public void applyBottomToAndAtom(FormationTree tree, Node node, String atom) {
		UnaryOperator not = createNewNot(createNewAtom(atom));
		replaceNode(tree, node, createNewAnd(createNewAtom(atom), not));
	}

	// 71. ┬		|-  av┬
	public void applyAtomOrTop(FormationTree tree, Node node, String atom) {
		replaceNode(tree, node, createNewOr(createNewAtom(atom), node));
	}

	// 72. ┬		|-  av¬a
	public void applyTopToOrAtom(FormationTree tree, Node node, String atom) {
		UnaryOperator not = createNewNot(createNewAtom(atom));
		replaceNode(tree, node, createNewOr(createNewAtom(atom), not));
	}

	// 73. ┬		|- 	a→a	
	public void applyTopToImpliesAtom(FormationTree tree, Node node, String atom) {
		replaceNode(tree, node, createNewImplies(createNewAtom(atom), createNewAtom(atom)));
	}

	// 74. ┬		|- 	a→┬
	public void applyTopToImpliesAtomTop(FormationTree tree, Node node, String atom) {
		replaceNode(tree, node, createNewImplies(createNewAtom(atom), node));
	}

	// 75. ┬		|- 	⊥→a
	public void applyTopToImpliesBottomAtom(FormationTree tree, Node node, String atom) {
		replaceNode(tree, node, createNewImplies(createNewBottom(), createNewAtom(atom)));
	}

	// 76. a		|-  av(a∧b)
	public void applyAbsorptionOrBackwards(FormationTree tree, Node node, String atom) {
		BinaryOperator and = createNewAnd(node, createNewAtom(atom));
		replaceNode(tree, node, createNewOr(node, and));
	}

	// 77. a  		|-	a∧(avb)
	public void applyAbsorptionAndBackwards(FormationTree tree, Node node, String atom) {
		BinaryOperator or = createNewOr(node, createNewAtom(atom));
		replaceNode(tree, node, createNewAnd(node, or));
	}

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

	// 78. ∀x[¬e]   |- 	¬∃x[e]
	public void applyAllComplementation(FormationTree tree,
			UnaryOperator node) {
		System.out.println("Applying");
		UnaryOperator not = (UnaryOperator) node.getChild();
		Node child = not.getChild();
		replaceNode(tree, node, createNewNot(createNewExists(child, node.getVars())));
	}

	// 79. ∀x∀y[e]	|-	∀y∀x[e]
	public void applyAllSwapQuantifiers(FormationTree tree, UnaryOperator node) {
		UnaryOperator inner = (UnaryOperator) node.getChild();
		Node child = inner.getChild();
		replaceNode(tree, node, createNewAll(createNewAll(child, node.getVars()), inner.getVars()));
	}

	// 80. ∀x[e∧f] 	|- 	∀x[e]∧∀x[f]
	public void applyAllDistrubutionOfQuantifiers(FormationTree tree,
			UnaryOperator node) {
		BinaryOperator and = (BinaryOperator) node.getChild();
		replaceNode(tree, node, createNewAnd(createNewAll(and.getLeftChild(), 
				node.getVars()), createNewAll(and.getRightChild(), node.getVars())));
	}

	// 81. ∀x[e∨t] 	|- 	∀x[e]∨t
	public void applyAllDistrubutionOfQuantifiersWithNoFreeVariables(
			FormationTree tree, UnaryOperator node) {
		BinaryOperator or = (BinaryOperator) node.getChild();
		replaceNode(tree, node, createNewOr(createNewAll(or.getLeftChild(), node.getVars()), or.getRightChild()));
	}

	// 82. ∀x[e→t]  |-  ∃x[e]→t
	// 90: ∃x[e→t]  |-  ∃x[e]→t
	public void applyImpliesDistributionOfQuantifiersLeft(FormationTree tree,
			UnaryOperator node) {
		BinaryOperator implies = (BinaryOperator) node.getChild();
		replaceNode(tree, node, createNewImplies(createNewExists(implies.getLeftChild(), node.getVars()), implies.getRightChild()));
	}

	// 83. ∀x[t→e]	|-  t→∀x[e]
	public void applyAllImpliesDistributionOfQuantifiersRight(FormationTree tree,
			UnaryOperator node) {
		BinaryOperator implies = (BinaryOperator) node.getChild();
		replaceNode(tree, node, createNewImplies(implies.getLeftChild(), createNewAll(implies.getRightChild(), node.getVars())));
	}

	// 84: ∀x[t]	|-  t
	// 92: ∃x[t]	|-  t
	public void applySimplificationOfQuantifiers(FormationTree tree,
			UnaryOperator node) {
		replaceNode(tree, node, node.getChild());
	}

	// 86: ∃x[¬e]  	|-	¬∀x[e]
	public void applyExistsComplementation(FormationTree tree,
			UnaryOperator node) {
		UnaryOperator not = (UnaryOperator) node.getChild();
		Node child = not.getChild();
		replaceNode(tree, node, createNewNot(createNewAll(child, node.getVars())));
	}

	// 87: ∃x∃y[e] 	|-  ∃y∃x[e]
	public void applyExistsSwapQuantifiers(FormationTree tree,
			UnaryOperator node) {
		UnaryOperator inner = (UnaryOperator) node.getChild();
		Node child = inner.getChild();
		replaceNode(tree, node, createNewExists(createNewExists(child, node.getVars()), inner.getVars()));
	}

	// 88: ∃x[e∨f]	|-  ∃x[e]∨∃x[f]
	public void applyExistsDistributionOfQuantifiers(FormationTree tree,
			UnaryOperator node) {
		BinaryOperator or = (BinaryOperator) node.getChild();
		replaceNode(tree, node, createNewOr(createNewExists(or.getLeftChild(), 
				node.getVars()), createNewExists(or.getRightChild(), node.getVars())));
	}

	// 89: ∃x[e∧t]	|-  ∃x[e]∧t
	public void applyExistsDistributionOfQuantifiersWithNoFreeVariables(
			FormationTree tree, UnaryOperator node) {
		BinaryOperator and = (BinaryOperator) node.getChild();
		replaceNode(tree, node, createNewAnd(createNewExists(and.getLeftChild(), node.getVars()), and.getRightChild()));
	}

	// 91: ∃x[t→e]  |-  t→∃x[e]
	public void applyExistsImpliesDistributionOfQuantifiersRight(
			FormationTree tree, UnaryOperator node) {
		BinaryOperator implies = (BinaryOperator) node.getChild();
		replaceNode(tree, node, createNewImplies(implies.getLeftChild(), createNewExists(implies.getRightChild(), node.getVars())));
	}

	// 93: ¬∀x[e]	|- 	∃x[¬e]
	public void applyAllComplementationBackwards(FormationTree tree,
			UnaryOperator node) {
		Node child = ((UnaryOperator) node.getChild()).getChild();
		replaceNode(tree, node, createNewExists(createNewNot(child), node.getChild().getVars()));
	}

	// 94: ¬∃x[e]	|-	∀x[¬e]
	public void applyExistsComplementationBackwards(FormationTree tree,
			UnaryOperator node) {
		Node child = ((UnaryOperator) node.getChild()).getChild();
		replaceNode(tree, node, createNewAll(createNewNot(child), node.getChild().getVars()));
	}

	// 95: ∀x[e]∧∀x[f] 	|-	∀x[e∧f]
	public void applyAllDistributionOfQuantifiersBackwards(FormationTree tree,
			BinaryOperator node) {
		Node leftChild = ((UnaryOperator) node.getLeftChild()).getChild();
		Node rightChild = ((UnaryOperator) node.getRightChild()).getChild();
		replaceNode(tree, node, createNewAll(createNewAnd(leftChild, rightChild), node.getLeftChild().getVars()));
	}

	// 96: ∃x[e]∧t		|-  ∃x[e∧t]
	public void applyExistsDistributionOfQuantifiersWithNoFreeVariablesBackwards(
			FormationTree tree, BinaryOperator node) {
		Node leftChild = ((UnaryOperator) node.getLeftChild()).getChild();
		replaceNode(tree, node, createNewExists(createNewAnd(leftChild, node.getRightChild()), node.getLeftChild().getVars()));
	}

	// 97: ∀x[e]∨t		|- 	∀x[e∨t]
	public void applyAllDistributionOfQuantifiersWithNoFreeVariablesBackwards(
			FormationTree tree, BinaryOperator node) {
		Node leftChild = ((UnaryOperator) node.getLeftChild()).getChild();
		replaceNode(tree, node, createNewAll(createNewOr(leftChild, node.getRightChild()), node.getLeftChild().getVars()));
	}

	// 98: ∃x[e]∨∃x[f]	|- 	∃x[e∨f]
	public void applyExistsDistributionOfQuantifiersBackwards(
			FormationTree tree, BinaryOperator node) {
		Node leftChild = ((UnaryOperator) node.getLeftChild()).getChild();
		Node rightChild = ((UnaryOperator) node.getRightChild()).getChild();
		replaceNode(tree, node, createNewExists(createNewOr(leftChild, rightChild), node.getLeftChild().getVars()));
	}

	// 99: ∃x[e]→t 	|- 	∀x[e→t]
	public void applyAllImpliesDistributionBackwardsLeft(FormationTree tree,
			BinaryOperator node) {
		Node leftChild = ((UnaryOperator) node.getLeftChild()).getChild();
		replaceNode(tree, node, createNewAll(createNewImplies(leftChild, node.getRightChild()), node.getLeftChild().getVars()));
	}

	// 100: t→∀x[e]	|- 	∀x[t→e]
	public void applyAllImpliesDistributionBackwardsRight(FormationTree tree,
			BinaryOperator node) {
		Node rightChild = ((UnaryOperator) node.getRightChild()).getChild();
		replaceNode(tree, node, createNewAll(createNewImplies(node.getLeftChild(), rightChild), node.getRightChild().getVars()));
	}

	// 101: ∃x[e]→t |- 	∃x[e→t]
	public void applyExistsImpliesDistributionBackwards(FormationTree tree,
			BinaryOperator node) {
		Node leftChild = ((UnaryOperator) node.getLeftChild()).getChild();
		replaceNode(tree, node, createNewExists(createNewImplies(leftChild, node.getRightChild()), node.getLeftChild().getVars()));
	}

	// 102: ∀x[e]	|-  ∀y[e{x->y}]
	// 103: ∃x[e]	|-  ∃y[e{x->y}]
	public void applyRenameVariable(FormationTree tree, UnaryOperator node,
			String input) {
		node.replaceVariable(node.getVars().peek(), input);
	}

	// 104: t		|- 	∀x[t]
	public void applyAddAll(FormationTree tree, Node node, String input) {
		LinkedList<String> vars = new LinkedList<String>();
		vars.add(input);
		replaceNode(tree, node, createNewAll(node, vars));
	}

	// 105: t		|- 	∃x[t]
	public void applyAddExists(FormationTree tree, Node node, String input) {
		LinkedList<String> vars = new LinkedList<String>();
		vars.add(input);
		replaceNode(tree, node, createNewExists(node, vars));
	}

}