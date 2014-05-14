package treeManipulation;

import java.util.BitSet;
import java.util.Random;

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
	
	public void applyRuleFromBitSet(BitSet bs, int index, FormationTree tree, 
			Node node) {
		int numSetBits = bs.cardinality();
		int nextSetBit = bs.nextSetBit(0);
		
		while (numSetBits-- > 0 && nextSetBit != index)
			nextSetBit = bs.nextSetBit(nextSetBit + 1);
		
		switch (nextSetBit) {
			case 0:		applyCommutativity((BinaryOperator) node);
						break;
			case 1:		applyIdempotence(tree, (BinaryOperator) node);
						break;
			case 2:		applyLeftAssociativity(tree, (BinaryOperator) node);
						break;
			case 3:		applyRightAssociativity(tree, (BinaryOperator) node);
						break;
			case 4:		applyCommutativity((BinaryOperator) node);
						break;
			case 5:		applyIdempotence(tree, (BinaryOperator) node);
						break;
			case 6:		applyLeftAssociativity(tree, (BinaryOperator) node);
						break;
			case 7:		applyRightAssociativity(tree, (BinaryOperator) node);
						break;
			case 8:		applyNotNot(tree,(UnaryOperator) node);
						break;
			case 9:		applyImpliesToOr(tree, (BinaryOperator) node);
						break;
			case 10: 	applyNotImplies(tree, (UnaryOperator) node);
						break;
			case 11: 	applyIffToAndImplies(tree, (BinaryOperator) node);
						break;
			case 12:	applyIffToOrAnd(tree, (BinaryOperator) node);
						break;
			case 13:	applyNotIffToNotB(tree, (UnaryOperator) node);
						break;
			case 14: 	applyNotIffToNotA(tree, (UnaryOperator) node);
						break;
			case 15: 	applyNotIffToOrAnd(tree, (UnaryOperator) node);
						break;
			case 16:	applyDeMorgansAnd(tree, (UnaryOperator) node);
						break;
			case 17:	applyDeMorgansOr(tree, (UnaryOperator) node);
						break;
			case 18: 	applyDistributivityAndLeft(tree, (BinaryOperator) node);
						break;
			case 19: 	applyDistributivityAndRight(tree, (BinaryOperator) node);
						break;
			case 20:	applyDistributivityOrLeft(tree, (BinaryOperator) node);
						break;
			case 21:	applyDistributivityOrRight(tree, (BinaryOperator) node);
						break;
			case 22: 	applyLeftAbsorption(tree, (BinaryOperator) node);
						break;
			case 23: 	applyLeftAbsorption(tree, (BinaryOperator) node);
						break;
			case 24: 	applyRightAbsorption(tree, (BinaryOperator) node);
						break;
			case 25: 	applyRightAbsorption(tree, (BinaryOperator) node);
						break;
		}
	}
	
	public void applyRandomRule(BitSet bs, FormationTree tree, BinaryOperator node) {
		int numSetBits = bs.cardinality();
		int nextSetBit = bs.nextSetBit(0);
		
		Random rand = new Random();
	    int randomNum = rand.nextInt(numSetBits);
	    
		while (randomNum-- > 0)
			nextSetBit = bs.nextSetBit(nextSetBit + 1);
	    
	    applyRuleFromBitSet(bs, nextSetBit, tree, node);
	}
	
	public void applyCommutativity(BinaryOperator node) {
		Node leftChild = node.getLeftChild();
		node.setLeftChild(node.getRightChild());
		node.setRightChild(leftChild);
		
		relabelNode(node);
	}
	
	public void applyIdempotence(FormationTree tree, BinaryOperator node) {
		Node child = node.getLeftChild();
		replaceNode(tree, node, child);
	}

	public void applyRightAssociativity(FormationTree tree, BinaryOperator node) {
		applyRightRotation(tree, node);
	}

	public void applyLeftAssociativity(FormationTree tree, BinaryOperator node) {
		applyLeftRotation(tree, node);
	}
	
	public void applyNotNot(FormationTree tree, UnaryOperator node) {
		Node result = ((UnaryOperator) node.getChild()).getChild();
		replaceNode(tree, node, result);
	}
	
	// 9. A→B |- ¬AvB
	public void applyImpliesToOr(FormationTree tree, BinaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		
		BinaryOperator result = new BinaryOperator(key, depth, "v");
		UnaryOperator not = new UnaryOperator(key << 1, depth + 1, "¬");
		not.setChild(node.getLeftChild());
		
		result.setLeftChild(not);
		result.setRightChild(node.getRightChild());
		
		replaceNode(tree, node, result);
	}
	
	// 10. ¬(A→B) 	|-	A^¬B
	public void applyNotImplies(FormationTree tree, UnaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator implies = (BinaryOperator) node.getChild();
		
		BinaryOperator result = new BinaryOperator(key, depth, "^");
		UnaryOperator not = new UnaryOperator(key << 1, depth + 1, "¬");
		
		not.setChild(implies.getRightChild());
		result.setLeftChild(implies.getLeftChild());
		result.setRightChild(not);
		
		replaceNode(tree, node, result);
	}
	
	// 11. A↔B	|-  (A→B)^(B→A)
	public void applyIffToAndImplies(FormationTree tree, BinaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		Node leftChild = node.getLeftChild();
		Node rightChild = node.getRightChild();
		Node leftChildClone = leftChild.clone();
		Node rightChildClone = rightChild.clone();
		
		BinaryOperator result = new BinaryOperator(key, depth, "^");
		BinaryOperator left = new BinaryOperator(key << 1, depth + 1, "→");
		BinaryOperator right = new BinaryOperator(key << 1 + 1, depth + 1, "→");
		
		result.setLeftChild(left);
		result.setRightChild(right);
		left.setLeftChild(leftChild);
		left.setRightChild(rightChild);
		right.setLeftChild(rightChildClone);
		right.setRightChild(leftChildClone);
		
		replaceNode(tree, node, result);
	}
	
	// 12. A↔B	|-	(A^B)v(¬A^¬B)
	public void applyIffToOrAnd(FormationTree tree, BinaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		Node leftChild = node.getLeftChild();
		Node rightChild = node.getRightChild();
		Node leftChildClone = leftChild.clone();
		Node rightChildClone = rightChild.clone();
		
		BinaryOperator result = new BinaryOperator(key, depth, "v");
		BinaryOperator left = new BinaryOperator(key, depth, "^");
		BinaryOperator right = new BinaryOperator(key, depth, "^");
		UnaryOperator leftNot = new UnaryOperator(key, depth, "¬");
		UnaryOperator rightNot = new UnaryOperator(key, depth, "¬");
		
		result.setLeftChild(left);
		result.setRightChild(right);
		right.setLeftChild(leftNot);
		right.setRightChild(rightNot);
		
		left.setLeftChild(leftChild);
		left.setRightChild(rightChild);
		leftNot.setChild(leftChildClone);
		rightNot.setChild(rightChildClone);
		
		replaceNode(tree, node, result);
	}
	
	// 13. ¬(A↔B) |- 	A↔¬B
	public void applyNotIffToNotB(FormationTree tree, UnaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator iff = (BinaryOperator) node.getChild();
		Node rightChild = iff.getRightChild();
		
		UnaryOperator not = new UnaryOperator(key, depth, "¬");
		
		not.setChild(rightChild);
		iff.setRightChild(not);
		
		replaceNode(tree, node, iff);
	}
	
	// 14. ¬(A↔B) |-  ¬A↔B
	public void applyNotIffToNotA(FormationTree tree, UnaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator iff = (BinaryOperator) node.getChild();
		Node leftChild = iff.getLeftChild();
		
		UnaryOperator not = new UnaryOperator(key, depth, "¬");
		
		not.setChild(leftChild);
		iff.setLeftChild(not);
		
		replaceNode(tree, node, iff);
	}
	
	// 15. ¬(A↔B) |-  (A^¬B)v(¬A^B)
	public void applyNotIffToOrAnd(FormationTree tree, UnaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator iff = (BinaryOperator) node.getChild();
		Node leftChild = iff.getLeftChild();
		Node rightChild = iff.getRightChild();
		Node leftChildClone = leftChild.clone();
		Node rightChildClone = rightChild.clone();
		
		BinaryOperator result = new BinaryOperator(key, depth, "v");
		BinaryOperator left = new BinaryOperator(key, depth, "^");
		BinaryOperator right = new BinaryOperator(key, depth, "^");
		UnaryOperator leftNot = new UnaryOperator(key, depth, "¬");
		UnaryOperator rightNot = new UnaryOperator(key, depth, "¬");
		
		result.setLeftChild(left);
		result.setRightChild(right);
		left.setRightChild(rightNot);
		right.setLeftChild(leftNot);
		left.setLeftChild(leftChild);
		right.setRightChild(rightChild);
		leftNot.setChild(leftChildClone);
		rightNot.setChild(rightChildClone);
		
		replaceNode(tree, node, result);
	}

	// 16. ¬(A^B)	|-  ¬Av¬B
	public void applyDeMorgansAnd(FormationTree tree, UnaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator and = (BinaryOperator) node.getChild();
		Node leftChild = and.getLeftChild();
		Node rightChild = and.getRightChild();
		
		BinaryOperator result = new BinaryOperator(key, depth, "v");
		UnaryOperator leftNot = new UnaryOperator(key, depth, "¬");
		UnaryOperator rightNot = new UnaryOperator(key, depth, "¬");
		
		result.setLeftChild(leftNot);
		result.setRightChild(rightNot);
		leftNot.setChild(leftChild);
		rightNot.setChild(rightChild);
		
		replaceNode(tree, node, result);
	}
	
	// 17. ¬(AvB)	|-	¬A^¬B
	public void applyDeMorgansOr(FormationTree tree, UnaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator and = (BinaryOperator) node.getChild();
		Node leftChild = and.getLeftChild();
		Node rightChild = and.getRightChild();
		
		BinaryOperator result = new BinaryOperator(key, depth, "^");
		UnaryOperator leftNot = new UnaryOperator(key, depth, "¬");
		UnaryOperator rightNot = new UnaryOperator(key, depth, "¬");
		
		result.setLeftChild(leftNot);
		result.setRightChild(rightNot);
		leftNot.setChild(leftChild);
		rightNot.setChild(rightChild);
		
		replaceNode(tree, node, result);
	}
	
	// 18. A^(BvC) 	|- 	(A^B)v(A^C)
	public void applyDistributivityAndLeft(FormationTree tree, BinaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator or = (BinaryOperator) node.getRightChild();
		Node leftChild = node.getLeftChild();
		Node leftChildClone = leftChild.clone();
		Node rightLeftChild = or.getLeftChild();
		Node rightRightChild = or.getRightChild();
		
		BinaryOperator result = new BinaryOperator(key, depth, "v");
		BinaryOperator leftAnd = new BinaryOperator(key, depth, "^");
		BinaryOperator rightAnd = new BinaryOperator(key, depth, "^");
		
		result.setLeftChild(leftAnd);
		result.setRightChild(rightAnd);
		leftAnd.setLeftChild(leftChild);
		leftAnd.setRightChild(rightLeftChild);
		rightAnd.setLeftChild(leftChildClone);
		rightAnd.setRightChild(rightRightChild);
		
		replaceNode(tree, node, result);
	}
	
	// 19. (AvB)^C	|-  (A^C)v(B^C)
	public void applyDistributivityAndRight(FormationTree tree, BinaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator or = (BinaryOperator) node.getLeftChild();
		Node rightChild = node.getRightChild();
		Node rightChildClone = rightChild.clone();
		Node leftLeftChild = or.getLeftChild();
		Node leftRightChild = or.getRightChild();
		
		BinaryOperator result = new BinaryOperator(key, depth, "v");
		BinaryOperator leftAnd = new BinaryOperator(key, depth, "^");
		BinaryOperator rightAnd = new BinaryOperator(key, depth, "^");
		
		result.setLeftChild(leftAnd);
		result.setRightChild(rightAnd);
		leftAnd.setLeftChild(leftLeftChild);
		leftAnd.setRightChild(rightChild);
		rightAnd.setLeftChild(leftRightChild);
		rightAnd.setRightChild(rightChildClone);
		
		replaceNode(tree, node, result);
	}
	
	// 20. Av(B^C)	|- 	(AvB)^(AvC)
	public void applyDistributivityOrLeft(FormationTree tree, BinaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator or = (BinaryOperator) node.getRightChild();
		Node leftChild = node.getLeftChild();
		Node leftChildClone = leftChild.clone();
		Node rightLeftChild = or.getLeftChild();
		Node rightRightChild = or.getRightChild();
		
		BinaryOperator result = new BinaryOperator(key, depth, "^");
		BinaryOperator leftOr = new BinaryOperator(key, depth, "v");
		BinaryOperator rightOr = new BinaryOperator(key, depth, "v");
		
		result.setLeftChild(leftOr);
		result.setRightChild(rightOr);
		leftOr.setLeftChild(leftChild);
		leftOr.setRightChild(rightLeftChild);
		rightOr.setLeftChild(leftChildClone);
		rightOr.setRightChild(rightRightChild);
		
		replaceNode(tree, node, result);
	}
	
	// 21. (A^B)vC	|-	(AvC)^(BvC)
	public void applyDistributivityOrRight(FormationTree tree, BinaryOperator node) {
		int key = node.getKey();
		int depth = node.getDepth();
		BinaryOperator or = (BinaryOperator) node.getLeftChild();
		Node rightChild = node.getRightChild();
		Node rightChildClone = rightChild.clone();
		Node leftLeftChild = or.getLeftChild();
		Node leftRightChild = or.getRightChild();
		
		BinaryOperator result = new BinaryOperator(key, depth, "^");
		BinaryOperator leftAnd = new BinaryOperator(key, depth, "v");
		BinaryOperator rightAnd = new BinaryOperator(key, depth, "v");
		
		result.setLeftChild(leftAnd);
		result.setRightChild(rightAnd);
		leftAnd.setLeftChild(leftLeftChild);
		leftAnd.setRightChild(rightChild);
		rightAnd.setLeftChild(leftRightChild);
		rightAnd.setRightChild(rightChildClone);
		
		replaceNode(tree, node, result);
	}
	
	// 22. A^(AvB)  |-	A
	// 23. Av(A^B)	|-  A
	public void applyLeftAbsorption(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, node.getLeftChild());
	}
	
	// 24. (AvB)^A  |-	A
	// 25. (A^B)vA	|-  A
	public void applyRightAbsorption(FormationTree tree, BinaryOperator node) {
		replaceNode(tree, node, node.getRightChild());
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
	
	// Tree rotations
	
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
	
	// View what would happen on application of rules
	
	public String viewCommutativity(BinaryOperator node) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(node.getRightChild());
		sb.append(node.getValue());
		sb.append(node.getLeftChild());
		
		return sb.toString();
	}
	
	public String viewIdempotence(BinaryOperator node) {
		return node.getLeftChild().toString();
	}
	
	public String viewLeftAssociativity(BinaryOperator node) {
		StringBuilder sb = new StringBuilder();
		BinaryOperator rightChild = (BinaryOperator) node.getRightChild();
		
		sb.append('(');
		sb.append(node.getLeftChild());
		sb.append(node.getValue());
		sb.append(rightChild.getLeftChild());
		sb.append(')');
		sb.append(rightChild.getValue());
		sb.append(rightChild.getRightChild());
		
		return sb.toString();
	}
	
	public String viewRightAssociativity(BinaryOperator node) {
		StringBuilder sb = new StringBuilder();
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();
		
		sb.append(leftChild.getLeftChild());
		sb.append(node.getValue());
		sb.append('(');
		sb.append(leftChild.getRightChild());
		sb.append(node.getValue());
		sb.append(node.getRightChild());
		sb.append(')');
		
		return sb.toString();
	}
}





