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
	 * 0. Commutativity of &
	 * 1. Idempotence of & 
	 * 2. Left Associativity of &
	 * 3. Right Associativity of &
	 * 4. Commutativity of |
	 * 5. Idempotence of |
	 * 6. Left Associativity of |
	 * 7. Right Associativity of |
	 * 
	 */
	
	public void applyRuleFromBitSet(BitSet bs, int index, FormationTree tree, 
			BinaryOperator node) {
		int numSetBits = bs.cardinality();
		int nextSetBit = bs.nextSetBit(0);
		
		while (numSetBits-- > 0 && nextSetBit != index)
			nextSetBit = bs.nextSetBit(nextSetBit + 1);
		
		if (nextSetBit == 0 || nextSetBit == 4)
			applyCommutativity(node);
		if (nextSetBit == 1)
			applyAndIdempotence(tree, node);
		if (nextSetBit == 2)
			applyAndLeftAssociativity(tree, node);
		if (nextSetBit == 3)
			applyAndRightAssociativity(tree, node);
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
	
	public void applyAndIdempotence(FormationTree tree, BinaryOperator node) {
		Node child = node.getLeftChild();
		
		Node parent = child;
		if (node.isRoot())
			tree.setRoot(child);
		else {
			int parentKey = node.getKey() >> 1;
			int parentDepth = node.getDepth() - 1;
			parent = tree.findNode(parentKey, parentDepth);
			
			if (parent instanceof BinaryOperator) {
				if (node.getKey() % 2 == 0)
					((BinaryOperator) parent).setLeftChild(child);
				else
					((BinaryOperator) parent).setRightChild(child);
			} else
				((UnaryOperator) parent).setChild(child);				
		}
		
		relabelNode(parent);
	}

	public void applyAndRightAssociativity(FormationTree tree, BinaryOperator node) {
		applyRightRotation(tree, node);
	}

	public void applyAndLeftAssociativity(FormationTree tree, BinaryOperator node) {
		applyLeftRotation(tree, node);
	}
	
	public void applyRightRotation(FormationTree tree, BinaryOperator node) {
		BinaryOperator leftChild = (BinaryOperator) node.getLeftChild();

		Node parent = leftChild;
		Node[] grandChildren = leftChild.getChildren();
		if (leftChild.hasChildren() && grandChildren.length == 2)
			node.setLeftChild(grandChildren[1]);
		
		if (node.isRoot())
			tree.setRoot(leftChild);
		else {
			parent = tree.findParent(node.getKey(), node.getDepth());
			System.out.println("parent: " + parent);
			
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
			parent = tree.findParent(node.getKey(), node.getDepth());
			
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






