package tests;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeManipulation.RuleApplicator;
import treeManipulation.RuleSelector;

public class TreeManipulation {

	private Compiler compiler;
	private RuleSelector rs;
	private RuleApplicator ra;
	private int noRules;
	
	@Before 
	public void method() {
		compiler = new Compiler();
		rs = new RuleSelector();
		ra = new RuleApplicator();
		noRules = rs.getNoRules();
	}
	
	// RuleSelector tests
	
	@Test
	public void testSelector() {
		FormationTree tree = compiler.compile("a&(r&s)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(2);
		assertEquals("a|(r|s)", bs, expected);
	}
	
	@Test
	public void testSelector1() {
		FormationTree tree = compiler.compile("(r|s)&(r|s)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(1);
		assertEquals("(r|s)&(r|s)", bs, expected);
	}
	
	@Test
	public void testSelector2() {
		FormationTree tree = compiler.compile("q->(r&(p&q))");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(1, 1));
		BitSet expected = new BitSet(8);
		expected.set(0);
		expected.set(2);
		assertEquals("q->(r&(p&q))", bs, expected);
	}
	
	// RuleApplicator tests
	
	@Test
	public void testAndCommutativity() {
		FormationTree tree = compiler.compile("q&p");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("q&p: ", tree.toTreeString(), "0-0: & (0-1: p, 1-1: q)");
	}

	@Test
	public void testAndCommutativityComplex() {
		FormationTree tree = compiler.compile("(!q->r)&(p|s)");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("(!q->r)&(p|s): ", tree.toTreeString(), "0-0: & (0-1: | (0-2: p, 1-2: s), 1-1: -> (2-2: ! (4-3: q), 3-2: r))");
	}
	
	@Test
	public void testAndSimplification() {
		FormationTree tree = compiler.compile("p&p");
		ra.applyAndIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("p&p: ", tree.toTreeString(), "0-0: p");
	}
	
	@Test
	public void testAndSimplificationComplex() {
		FormationTree tree = compiler.compile("(!q->r)&(!q->r)");
		ra.applyAndIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(!q->r)&(!q->r): ", tree.toTreeString(), "0-0: -> (0-1: ! (0-2: q), 1-1: r)");
	}
	
	@Test
	public void testAndSimplificationComplex2() {
		FormationTree tree = compiler.compile("((p->q)&(p->q))&q");
		ra.applyAndIdempotence(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(!q->r)&(!q->r): ", tree.toTreeString(), "0-0: & (0-1: -> (0-2: p, 1-2: q), 1-1: q)");
	}
	
	@Test
	public void testAndRightAssociativity() {
		FormationTree tree = compiler.compile("(p&q)&r");
		ra.applyAndRightAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(p&q)&r: ", tree.toTreeString(), "0-0: & (0-1: p, 1-1: & (2-2: q, 3-2: r))");
	}

	@Test
	public void testAndRightAssociativityComplex() {
		FormationTree tree = compiler.compile("t|((!p&q)&(r->s))");
		ra.applyAndRightAssociativity(tree,(BinaryOperator) tree.findNode(1, 1));
		assertEquals("t|((!p&q)&(r->s)): ", tree.toTreeString(), "0-0: | (0-1: t, 1-1: & (2-2: ! (4-3: p), 3-2: & (6-3: q, 7-3: -> (14-4: r, 15-4: s))))");
	}
	
	@Test
	public void testAndLeftAssociativity() {
		FormationTree tree = compiler.compile("p&(q&r)");
		ra.applyAndLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("p&(q&r): ", tree.toTreeString(), "0-0: & (0-1: & (0-2: p, 1-2: q), 1-1: r)");
	}

	@Test
	public void testAndLeftAssociativityComplex() {
		FormationTree tree = compiler.compile("((r->s)&(!p&q))|t");
		ra.applyAndLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("((r->s)&(!p&q))|t: ", tree.toTreeString(), "0-0: | (0-1: & (0-2: & (0-3: -> (0-4: r, 1-4: s), 1-3: ! (2-4: p)), 1-2: q), 1-1: t)");
	}
	
}
