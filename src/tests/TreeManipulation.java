package tests;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.UnaryOperator;
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
	public void testAndSelector() {
		FormationTree tree = compiler.compile("a&(r&s)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(2);
		assertEquals("a&(r&s)", bs, expected);
	}
	
	@Test
	public void testAndSelector1() {
		FormationTree tree = compiler.compile("(r|s)&(r|s)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(1);
		assertEquals("(r|s)&(r|s)", bs, expected);
	}
	
	@Test
	public void testAndSelector2() {
		FormationTree tree = compiler.compile("q->(r&(p&q))");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(1, 1));
		BitSet expected = new BitSet(8);
		expected.set(0);
		expected.set(2);
		assertEquals("q->(r&(p&q))", bs, expected);
	}
	
	@Test
	public void testOrSelector() {
		FormationTree tree = compiler.compile("a|(r|s)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(4);
		expected.set(6);
		assertEquals("a|(r|s)", bs, expected);
	}
	
	@Test
	public void testOrSelector1() {
		FormationTree tree = compiler.compile("(r&s)|(r&s)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(4);
		expected.set(5);
		assertEquals("(r&s)|(r&s)", bs, expected);
	}
	
	@Test
	public void testOrSelector2() {
		FormationTree tree = compiler.compile("q->(r|(p|q))");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(1, 1));
		BitSet expected = new BitSet(8);
		expected.set(4);
		expected.set(6);
		assertEquals("q->(r|(p|q))", bs, expected);
	}
	// RuleApplicator AND Tests
	
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
	public void testAndIdempotence() {
		FormationTree tree = compiler.compile("p&p");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("p&p: ", tree.toTreeString(), "0-0: p");
	}
	
	@Test
	public void testAndIdempotenceComplex() {
		FormationTree tree = compiler.compile("(!q->r)&(!q->r)");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(!q->r)&(!q->r): ", tree.toTreeString(), "0-0: -> (0-1: ! (0-2: q), 1-1: r)");
	}
	
	@Test
	public void testAndRightAssociativity() {
		FormationTree tree = compiler.compile("(p&q)&r");
		ra.applyRightAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(p&q)&r: ", tree.toTreeString(), "0-0: & (0-1: p, 1-1: & (2-2: q, 3-2: r))");
	}

	@Test
	public void testAndRightAssociativityComplex() {
		FormationTree tree = compiler.compile("t|((!p&q)&(r->s))");
		ra.applyRightAssociativity(tree,(BinaryOperator) tree.findNode(1, 1));
		assertEquals("t|((!p&q)&(r->s)): ", tree.toTreeString(), "0-0: | (0-1: t, 1-1: & (2-2: ! (4-3: p), 3-2: & (6-3: q, 7-3: -> (14-4: r, 15-4: s))))");
	}
	
	@Test
	public void testAndLeftAssociativity() {
		FormationTree tree = compiler.compile("p&(q&r)");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("p&(q&r): ", tree.toTreeString(), "0-0: & (0-1: & (0-2: p, 1-2: q), 1-1: r)");
	}

	@Test
	public void testAndLeftAssociativityComplex() {
		FormationTree tree = compiler.compile("((r->s)&(!p&q))|t");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("((r->s)&(!p&q))|t: ", tree.toTreeString(), "0-0: | (0-1: & (0-2: & (0-3: -> (0-4: r, 1-4: s), 1-3: ! (2-4: p)), 1-2: q), 1-1: t)");
	}
	
	// OR Tests
	
	@Test
	public void testOrCommutativity() {
		FormationTree tree = compiler.compile("q|p");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("q|p: ", tree.toTreeString(), "0-0: | (0-1: p, 1-1: q)");
	}

	@Test
	public void testOrCommutativityComplex() {
		FormationTree tree = compiler.compile("(!q->r)|(p|s)");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("(!q->r)|(p|s): ", tree.toTreeString(), "0-0: | (0-1: | (0-2: p, 1-2: s), 1-1: -> (2-2: ! (4-3: q), 3-2: r))");
	}
	
	@Test
	public void testOrIdempotence() {
		FormationTree tree = compiler.compile("p|p");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("p|p: ", tree.toTreeString(), "0-0: p");
	}
	
	@Test
	public void testOrIdempotenceComplex() {
		FormationTree tree = compiler.compile("(!q->r)|(!q->r)");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(!q->r)|(!q->r): ", tree.toTreeString(), "0-0: -> (0-1: ! (0-2: q), 1-1: r)");
	}
	
	@Test
	public void testOrRightAssociativity() {
		FormationTree tree = compiler.compile("(p|q)|r");
		ra.applyRightAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(p|q)|r: ", tree.toTreeString(), "0-0: | (0-1: p, 1-1: | (2-2: q, 3-2: r))");
	}

	@Test
	public void testOrRightAssociativityComplex() {
		FormationTree tree = compiler.compile("t&((!p|q)|(r->s))");
		ra.applyRightAssociativity(tree,(BinaryOperator) tree.findNode(1, 1));
		assertEquals("t&((!p|q)|(r->s)): ", tree.toTreeString(), "0-0: & (0-1: t, 1-1: | (2-2: ! (4-3: p), 3-2: | (6-3: q, 7-3: -> (14-4: r, 15-4: s))))");
	}
	
	@Test
	public void testOrLeftAssociativity() {
		FormationTree tree = compiler.compile("p|(q|r)");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("p|(q|r): ", tree.toTreeString(), "0-0: | (0-1: | (0-2: p, 1-2: q), 1-1: r)");
	}

	@Test
	public void testOrLeftAssociativityComplex() {
		FormationTree tree = compiler.compile("((r->s)|(!p|q))&t");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("((r->s)|(!p|q))&t: ", tree.toTreeString(), "0-0: & (0-1: | (0-2: | (0-3: -> (0-4: r, 1-4: s), 1-3: ! (2-4: p)), 1-2: q), 1-1: t)");
	}
	
	@Test
	public void testNotNotRoot() {
		FormationTree tree = compiler.compile("!!a");
		ra.applyNotNot(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("!!a", tree.toTreeString(), "0-0: a");
	}

	@Test
	public void testNotNotUnary() {
		FormationTree tree = compiler.compile("!!!a");
		ra.applyNotNot(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("!!!a", tree.toTreeString(), "0-0: ! (0-1: a)");
	}
	
	@Test
	public void testNotNotBinaryLeft() {
		FormationTree tree = compiler.compile("!!a&b");
		ra.applyNotNot(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("!!a&b", tree.toTreeString(), "0-0: & (0-1: a, 1-1: b)");
	}
	
	@Test
	public void testNotNotBinaryRight() {
		FormationTree tree = compiler.compile("b&!!a");
		ra.applyNotNot(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("b&!!a", tree.toTreeString(), "0-0: & (0-1: b, 1-1: a)");
	}

	// 9. A->B |- !A|B
	@Test
	public void testImpilesToOrRoot() {
		FormationTree tree = compiler.compile("a->b");
		ra.applyImpliesToOr(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a->b", tree.toTreeString(), "0-0: | (0-1: ! (0-2: a), 1-1: b)");
	}

	@Test
	public void testImpilesToOrUnary() {
		FormationTree tree = compiler.compile("!(a->b)");
		ra.applyImpliesToOr(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("!(a->b)", tree.toTreeString(), "0-0: ! (0-1: | (0-2: ! (0-3: a), 1-2: b))");
	}
	
	@Test
	public void testImpilesToOrBinaryLeft() {
		FormationTree tree = compiler.compile("(a->b)&a");
		ra.applyImpliesToOr(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a->b)&a", tree.toTreeString(), "0-0: & (0-1: | (0-2: ! (0-3: a), 1-2: b), 1-1: a)");
	}
	
	@Test
	public void testImpilesToOrBinaryRight() {
		FormationTree tree = compiler.compile("a&(a->b)");
		ra.applyImpliesToOr(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a&(a->b)", tree.toTreeString(), "0-0: & (0-1: a, 1-1: | (2-2: ! (4-3: a), 3-2: b))");
	}

	// 10. !(A->B) 	|-	A|!B
	@Test
	public void testNotImpilesRoot() {
		FormationTree tree = compiler.compile("!(a->b)");
		ra.applyNotImplies(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("a->b", tree.toTreeString(), "0-0: | (0-1: a, 1-1: ! (2-2: b))");
	}

	@Test
	public void testNotImpilesUnary() {
		FormationTree tree = compiler.compile("!!(a->b)");
		ra.applyNotImplies(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("!(a->b)", tree.toTreeString(), "0-0: ! (0-1: | (0-2: a, 1-2: ! (2-3: b)))");
	}
	
	@Test
	public void testNotImpilesBinaryLeft() {
		FormationTree tree = compiler.compile("!(a->b)&a");
		ra.applyNotImplies(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("(a->b)&a", tree.toTreeString(), "0-0: & (0-1: | (0-2: a, 1-2: ! (2-3: b)), 1-1: a)");
	}
	
	@Test
	public void testNotImpilesBinaryRight() {
		FormationTree tree = compiler.compile("a&!(a->b)");
		ra.applyNotImplies(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("a&(a->b)", tree.toTreeString(), "0-0: & (0-1: a, 1-1: | (2-2: a, 3-2: ! (6-3: b)))");
	}
	
	// 11. A<->B	|-  (A->B)&(B->A)
	@Test
	public void testIffToAndRoot() {
		FormationTree tree = compiler.compile("a<->b");
		ra.applyIffToAndImplies(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a<->b", tree.toTreeString(), "0-0: & (0-1: -> (0-2: a, 1-2: b), 1-1: -> (2-2: b, 3-2: a))");
	}

	@Test
	public void testIffToAndUnary() {
		FormationTree tree = compiler.compile("!(a<->b)");
		ra.applyIffToAndImplies(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("!(a<->b)", tree.toTreeString(), "0-0: ! (0-1: & (0-2: -> (0-3: a, 1-3: b), 1-2: -> (2-3: b, 3-3: a)))");
	}
	
	@Test
	public void testIffToAndBinaryLeft() {
		FormationTree tree = compiler.compile("(a<->b)&a");
		ra.applyIffToAndImplies(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a<->b)&a", tree.toTreeString(), "0-0: & (0-1: & (0-2: -> (0-3: a, 1-3: b), 1-2: -> (2-3: b, 3-3: a)), 1-1: a)");
	}
	
	@Test
	public void testIffToAndBinaryRight() {
		FormationTree tree = compiler.compile("a&(a<->b)");
		ra.applyIffToAndImplies(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a&(a<->b)", tree.toTreeString(), "0-0: & (0-1: a, 1-1: & (2-2: -> (4-3: a, 5-3: b), 3-2: -> (6-3: b, 7-3: a)))");
	}
}
