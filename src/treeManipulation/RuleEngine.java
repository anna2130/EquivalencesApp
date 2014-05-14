package treeManipulation;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.SortedSet;

import treeBuilder.Compiler;
import treeBuilder.BinaryOperator;
import treeBuilder.FormationTree;
import treeBuilder.Node;
import treeBuilder.UnaryOperator;

public class RuleEngine {
	
//	private FormationTree tree;
//	private BitSet bs;
	private HashSet<Rule> rules;
	private Compiler compiler;
	
	public RuleEngine(FormationTree tree, Compiler compiler) {
//		this.tree = tree;
//		bs = new BitSet();
		this.compiler = compiler;
		setRules();
	}
	
	public TruthTable getTruthTable(Node node) {
		FormationTree subTree = new FormationTree(node);
		TruthTable tt = new TruthTable(subTree);
		return tt;
	}

	/* 0	A^A 	|- A	8
	 * 1	A^┬ 	|- A	9
	 * 2 	┬^A 	|- A	10
	 * 3	AvA 	|- A	11
	 * 4	Av⊥ 		|- A	12
	 * 5	⊥vA		|- A	13
	 * 6 	¬¬A		|- A	14
	 * 7 	┬→A		|- A	15
	 * 16 	A^(AvB) |- A	23
	 * 17	A^(BvA) |- A	
	 * 18	(AvB)^A |- A	24
	 * 19	(BvA)^A	|- A	
	 * 20	Av(A^B) |- A	25
	 * 21	Av(B^A)	|- A	
	 * 22	(A^B)vA	|- A	26
	 * 22	(B^A)vA |- A	
	 */
	private void setRules() {
		rules = new HashSet<Rule>();

		addRule("a");
		addRule("a^a");
		addRule("a^┬");
		addRule("┬^a");
		addRule("ava");
		addRule("av⊥");
		addRule("⊥va");
		addRule("¬¬a");
		addRule("┬→a");
		addRule("a^(avb)");
		addRule("a^(bva)");
		addRule("(avb)^a");
		addRule("(bva)^a");
		addRule("av(a^b)");
		addRule("av(b^a)");
		addRule("(a^b)va");
		addRule("(b^a)va");
	}
	
	public BitSet getRulesBitSet(Node node) {
		TruthTable tt = getTruthTable(node);
		BitSet bs = new BitSet();
		
		Iterator<Rule> it = rules.iterator();
		for (int i = 0; it.hasNext(); ++i) {
			Rule r = it.next();
			if (tt.testRuleEquivalence(r.getTruthTable())) {
				bs.set(i);
			}
		}
		return bs;
	}
	
	private void addRule(String rule) {
		FormationTree tree = compiler.compile(rule);
		TruthTable tt = new TruthTable(tree);
		rules.add(new Rule(rule, tt));
	}

	public void applyRandomRule() {
		
	}
	
	public void applyRuleFromBitSet(Node node, int index) {
		
	}
	
//	private void getSingleVarRules(Node node) {
//		if (node.isAnd()) {
//			BinaryOperator binary = (BinaryOperator) node;
//			if (binary.getLeftChild().getValue().equals(binary.getRightChild().getValue()))
//				bs.set(0);
//			else if (binary.getRightChild().isTop())
//				bs.set(1);
//			else if (binary.getLeftChild().isTop())
//				bs.set(2);
//			else if (binary.getRightChild().isOr()) {
//				BinaryOperator rightChild = (BinaryOperator) binary.getRightChild();
//				if (tree.equalSubTrees(binary, rightChild.getLeftChild()))
//					bs.set(16);
//				else if (tree.equalSubTrees(binary, rightChild.getRightChild()))
//					bs.set(17);
//			}
//		} else if (node.isOr()){
//			BinaryOperator binary = (BinaryOperator) node;
//			if (binary.getLeftChild().getValue().equals(binary.getRightChild().getValue()))
//				bs.set(3);
//			else if (binary.getRightChild().isBottom())
//				bs.set(4);
//			else if (binary.getLeftChild().isBottom())
//				bs.set(5);
//		} else if (node.isNot() && ((UnaryOperator) node).getChild().isNot()) {
//			bs.set(6);
//		} else if (node.isImplies() && ((BinaryOperator) node).getLeftChild().isTop()) {
//			bs.set(7);
//		} else if (node.isAtom()) {
//			bs.set(8, 16);
//			bs.set(23, 27);
//		}
//	}
}
