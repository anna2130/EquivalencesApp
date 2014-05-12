package tests;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.Node;
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
		FormationTree tree = compiler.compile("a^(r^s)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(2);
		assertEquals("a^(r^s)", bs, expected);
	}
	
	@Test
	public void testAndSelector1() {
		FormationTree tree = compiler.compile("(rvs)^(rvs)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(1);
		assertEquals("(rvs)^(rvs)", bs.get(0, 2), expected);
	}
	
	@Test
	public void testAndSelector2() {
		FormationTree tree = compiler.compile("q→(r^(p^q))");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(1, 1));
		BitSet expected = new BitSet(8);
		expected.set(0);
		expected.set(2);
		assertEquals("q→(r^(p^q))", bs, expected);
	}
	
	@Test
	public void testOrSelector() {
		FormationTree tree = compiler.compile("av(rvs)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(4);
		expected.set(6);
		assertEquals("av(rvs)", bs, expected);
	}
	
	@Test
	public void testOrSelector1() {
		FormationTree tree = compiler.compile("(r^s)v(r^s)");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(4);
		expected.set(5);
		assertEquals("(r^s)v(r^s)", bs.get(0, 6), expected);
	}
	
	@Test
	public void testOrSelector2() {
		FormationTree tree = compiler.compile("q→(rv(pvq))");
		BitSet bs = rs.getApplicableRules(tree, tree.findNode(1, 1));
		BitSet expected = new BitSet(8);
		expected.set(4);
		expected.set(6);
		assertEquals("q→(rv(pvq))", bs, expected);
	}
	// RuleApplicator AND Tests
	
	@Test
	public void testAndCommutativity() {
		FormationTree tree = compiler.compile("q^p");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("q^p: ", tree.toTreeString(), "0-0: ^ (0-1: p, 1-1: q)");
	}

	@Test
	public void testAndCommutativityComplex() {
		FormationTree tree = compiler.compile("(¬q→r)^(pvs)");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("(¬q→r)^(pvs): ", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: p, 1-2: s), 1-1: → (2-2: ¬ (4-3: q), 3-2: r))");
	}
	
	@Test
	public void testAndIdempotence() {
		FormationTree tree = compiler.compile("p^p");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("p^p: ", tree.toTreeString(), "0-0: p");
	}
	
	@Test
	public void testAndIdempotenceComplex() {
		FormationTree tree = compiler.compile("(¬q→r)^(¬q→r)");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(¬q→r)^(¬q→r): ", tree.toTreeString(), "0-0: → (0-1: ¬ (0-2: q), 1-1: r)");
	}
	
	@Test
	public void testAndRightAssociativity() {
		FormationTree tree = compiler.compile("(p^q)^r");
		ra.applyRightAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(p^q)^r: ", tree.toTreeString(), "0-0: ^ (0-1: p, 1-1: ^ (2-2: q, 3-2: r))");
	}

	@Test
	public void testAndRightAssociativityComplex() {
		FormationTree tree = compiler.compile("tv((¬p^q)^(r→s))");
		ra.applyRightAssociativity(tree,(BinaryOperator) tree.findNode(1, 1));
		assertEquals("tv((¬p^q)^(r→s)): ", tree.toTreeString(), "0-0: v (0-1: t, 1-1: ^ (2-2: ¬ (4-3: p), 3-2: ^ (6-3: q, 7-3: → (14-4: r, 15-4: s))))");
	}
	
	@Test
	public void testAndLeftAssociativity() {
		FormationTree tree = compiler.compile("p^(q^r)");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("p^(q^r): ", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: p, 1-2: q), 1-1: r)");
	}

	@Test
	public void testAndLeftAssociativityComplex() {
		FormationTree tree = compiler.compile("((r→s)^(¬p^q))vt");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("((r→s)^(¬p^q))vt: ", tree.toTreeString(), "0-0: v (0-1: ^ (0-2: ^ (0-3: → (0-4: r, 1-4: s), 1-3: ¬ (2-4: p)), 1-2: q), 1-1: t)");
	}
	
	// OR Tests
	
	@Test
	public void testOrCommutativity() {
		FormationTree tree = compiler.compile("qvp");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("qvp: ", tree.toTreeString(), "0-0: v (0-1: p, 1-1: q)");
	}

	@Test
	public void testOrCommutativityComplex() {
		FormationTree tree = compiler.compile("(¬q→r)v(pvs)");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("(¬q→r)v(pvs): ", tree.toTreeString(), "0-0: v (0-1: v (0-2: p, 1-2: s), 1-1: → (2-2: ¬ (4-3: q), 3-2: r))");
	}
	
	@Test
	public void testOrIdempotence() {
		FormationTree tree = compiler.compile("pvp");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("pvp: ", tree.toTreeString(), "0-0: p");
	}
	
	@Test
	public void testOrIdempotenceComplex() {
		FormationTree tree = compiler.compile("(¬q→r)v(¬q→r)");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(¬q→r)v(¬q→r): ", tree.toTreeString(), "0-0: → (0-1: ¬ (0-2: q), 1-1: r)");
	}
	
	@Test
	public void testOrRightAssociativity() {
		FormationTree tree = compiler.compile("(pvq)vr");
		ra.applyRightAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(pvq)vr: ", tree.toTreeString(), "0-0: v (0-1: p, 1-1: v (2-2: q, 3-2: r))");
	}

	@Test
	public void testOrRightAssociativityComplex() {
		FormationTree tree = compiler.compile("t^((¬pvq)v(r→s))");
		ra.applyRightAssociativity(tree,(BinaryOperator) tree.findNode(1, 1));
		assertEquals("t^((¬pvq)v(r→s)): ", tree.toTreeString(), "0-0: ^ (0-1: t, 1-1: v (2-2: ¬ (4-3: p), 3-2: v (6-3: q, 7-3: → (14-4: r, 15-4: s))))");
	}
	
	@Test
	public void testOrLeftAssociativity() {
		FormationTree tree = compiler.compile("pv(qvr)");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("pv(qvr): ", tree.toTreeString(), "0-0: v (0-1: v (0-2: p, 1-2: q), 1-1: r)");
	}

	@Test
	public void testOrLeftAssociativityComplex() {
		FormationTree tree = compiler.compile("((r→s)v(¬pvq))^t");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("((r→s)v(¬pvq))^t: ", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: v (0-3: → (0-4: r, 1-4: s), 1-3: ¬ (2-4: p)), 1-2: q), 1-1: t)");
	}
	
	@Test
	public void testNotNotRoot() {
		FormationTree tree = compiler.compile("¬¬a");
		ra.applyNotNot(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("¬¬a", tree.toTreeString(), "0-0: a");
	}

	@Test
	public void testNotNotUnary() {
		FormationTree tree = compiler.compile("¬¬¬a");
		ra.applyNotNot(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬¬¬a", tree.toTreeString(), "0-0: ¬ (0-1: a)");
	}
	
	@Test
	public void testNotNotBinaryLeft() {
		FormationTree tree = compiler.compile("¬¬a^b");
		ra.applyNotNot(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬¬a^b", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: b)");
	}
	
	@Test
	public void testNotNotBinaryRight() {
		FormationTree tree = compiler.compile("b^¬¬a");
		ra.applyNotNot(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("b^¬¬a", tree.toTreeString(), "0-0: ^ (0-1: b, 1-1: a)");
	}

	// 9. A→B v- ¬AvB
	@Test
	public void testImpilesToOrRoot() {
		FormationTree tree = compiler.compile("a→b");
		ra.applyImpliesToOr(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a→b", tree.toTreeString(), "0-0: v (0-1: ¬ (0-2: a), 1-1: b)");
	}

	@Test
	public void testImpilesToOrUnary() {
		FormationTree tree = compiler.compile("¬(a→b)");
		ra.applyImpliesToOr(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬(a→b)", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: ¬ (0-3: a), 1-2: b))");
	}
	
	@Test
	public void testImpilesToOrBinaryLeft() {
		FormationTree tree = compiler.compile("(a→b)^a");
		ra.applyImpliesToOr(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a→b)^a", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: ¬ (0-3: a), 1-2: b), 1-1: a)");
	}
	
	@Test
	public void testImpilesToOrBinaryRight() {
		FormationTree tree = compiler.compile("a^(a→b)");
		ra.applyImpliesToOr(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a→b)", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: v (2-2: ¬ (4-3: a), 3-2: b))");
	}

	// 10. ¬(A→B) 	v-	A^¬B
	@Test
	public void testNotImpilesRoot() {
		FormationTree tree = compiler.compile("¬(a→b)");
		ra.applyNotImplies(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("a→b", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ¬ (2-2: b))");
	}

	@Test
	public void testNotImpilesUnary() {
		FormationTree tree = compiler.compile("¬¬(a→b)");
		ra.applyNotImplies(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬(a→b)", tree.toTreeString(), "0-0: ¬ (0-1: ^ (0-2: a, 1-2: ¬ (2-3: b)))");
	}
	
	@Test
	public void testNotImpilesBinaryLeft() {
		FormationTree tree = compiler.compile("¬(a→b)^a");
		ra.applyNotImplies(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("(a→b)^a", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: a, 1-2: ¬ (2-3: b)), 1-1: a)");
	}
	
	@Test
	public void testNotImpilesBinaryRight() {
		FormationTree tree = compiler.compile("a^¬(a→b)");
		ra.applyNotImplies(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a→b)", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ^ (2-2: a, 3-2: ¬ (6-3: b)))");
	}
	
	// 11. A↔B	v-  (A→B)^(B→A)
	@Test
	public void testIffToAndRoot() {
		FormationTree tree = compiler.compile("a↔b");
		ra.applyIffToAndImplies(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a↔b", tree.toTreeString(), "0-0: ^ (0-1: → (0-2: a, 1-2: b), 1-1: → (2-2: b, 3-2: a))");
	}

	@Test
	public void testIffToAndUnary() {
		FormationTree tree = compiler.compile("¬(a↔b)");
		ra.applyIffToAndImplies(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬(a↔b)", tree.toTreeString(), "0-0: ¬ (0-1: ^ (0-2: → (0-3: a, 1-3: b), 1-2: → (2-3: b, 3-3: a)))");
	}
	
	@Test
	public void testIffToAndBinaryLeft() {
		FormationTree tree = compiler.compile("(a↔b)^a");
		ra.applyIffToAndImplies(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a↔b)^a", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: → (0-3: a, 1-3: b), 1-2: → (2-3: b, 3-3: a)), 1-1: a)");
	}
	
	@Test
	public void testIffToAndBinaryRight() {
		FormationTree tree = compiler.compile("a^(a↔b)");
		ra.applyIffToAndImplies(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a↔b)", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ^ (2-2: → (4-3: a, 5-3: b), 3-2: → (6-3: b, 7-3: a)))");
	}

	// 12. A↔B	v-	(A^B)v(¬A^¬B)
	@Test
	public void testIffToOrAndRoot() {
		FormationTree tree = compiler.compile("a↔b");
		ra.applyIffToOrAnd(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a↔b", tree.toTreeString(), "0-0: v (0-1: ^ (0-2: a, 1-2: b), 1-1: ^ (2-2: ¬ (4-3: a), 3-2: ¬ (6-3: b)))");
	}

	@Test
	public void testIffToOrAndUnary() {
		FormationTree tree = compiler.compile("¬(a↔b)");
		ra.applyIffToOrAnd(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬(a↔b)", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: ^ (0-3: a, 1-3: b), 1-2: ^ (2-3: ¬ (4-4: a), 3-3: ¬ (6-4: b))))");
	}
	
	@Test
	public void testIffToOrAndBinaryLeft() {
		FormationTree tree = compiler.compile("(a↔b)^a");
		ra.applyIffToOrAnd(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a↔b)^a", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: ^ (0-3: a, 1-3: b), 1-2: ^ (2-3: ¬ (4-4: a), 3-3: ¬ (6-4: b))), 1-1: a)");
	}
	
	@Test
	public void testIffToOrAndBinaryRight() {
		FormationTree tree = compiler.compile("a^(a↔b)");
		ra.applyIffToOrAnd(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a↔b)", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: v (2-2: ^ (4-3: a, 5-3: b), 3-2: ^ (6-3: ¬ (12-4: a), 7-3: ¬ (14-4: b))))");
	}
	
	// 13. ¬(A↔B) v- 	A↔¬B
	@Test
	public void testNotIffToNotBRoot() {
		FormationTree tree = compiler.compile("¬(a↔b)");
		ra.applyNotIffToNotB(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("¬(a↔b)", tree.toTreeString(), "0-0: ↔ (0-1: a, 1-1: ¬ (2-2: b))");
	}

	@Test
	public void testNotIffToNotBUnary() {
		FormationTree tree = compiler.compile("¬¬(a↔b)");
		ra.applyNotIffToNotB(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬¬(a↔b)", tree.toTreeString(), "0-0: ¬ (0-1: ↔ (0-2: a, 1-2: ¬ (2-3: b)))");
	}
	
	@Test
	public void testNotIffToNotBBinaryLeft() {
		FormationTree tree = compiler.compile("(¬(a↔b))^a");
		ra.applyNotIffToNotB(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("(¬(a↔b))^a", tree.toTreeString(), "0-0: ^ (0-1: ↔ (0-2: a, 1-2: ¬ (2-3: b)), 1-1: a)");
	}
	
	@Test
	public void testNotIffToNotBBinaryRight() {
		FormationTree tree = compiler.compile("a^(¬(a↔b))");
		ra.applyNotIffToNotB(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("a^(¬(a↔b))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ↔ (2-2: a, 3-2: ¬ (6-3: b)))");
	}
	
	// 14. ¬(A↔B) v-  ¬A↔B
	@Test
	public void testNotIffToNotARoot() {
		FormationTree tree = compiler.compile("¬(a↔b)");
		ra.applyNotIffToNotA(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("¬(a↔b)", tree.toTreeString(), "0-0: ↔ (0-1: ¬ (0-2: a), 1-1: b)");
	}

	@Test
	public void testNotIffToNotAUnary() {
		FormationTree tree = compiler.compile("¬¬(a↔b)");
		ra.applyNotIffToNotA(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬¬(a↔b)", tree.toTreeString(), "0-0: ¬ (0-1: ↔ (0-2: ¬ (0-3: a), 1-2: b))");
	}
	
	@Test
	public void testNotIffToNotABinaryLeft() {
		FormationTree tree = compiler.compile("(¬(a↔b))^a");
		ra.applyNotIffToNotA(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("(¬(a↔b))^a", tree.toTreeString(), "0-0: ^ (0-1: ↔ (0-2: ¬ (0-3: a), 1-2: b), 1-1: a)");
	}
	
	@Test
	public void testNotIffToNotABinaryRight() {
		FormationTree tree = compiler.compile("a^(¬(a↔b))");
		ra.applyNotIffToNotA(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("a^(¬(a↔b))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ↔ (2-2: ¬ (4-3: a), 3-2: b))");
	}
	
	// 15. ¬(A↔B) v-  (A^¬B)v(¬A^B)
	@Test
	public void testNotIffToOrAndRoot() {
		FormationTree tree = compiler.compile("¬(a↔b)");
		ra.applyNotIffToOrAnd(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("¬(a↔b)", tree.toTreeString(), "0-0: v (0-1: ^ (0-2: a, 1-2: ¬ (2-3: b)), 1-1: ^ (2-2: ¬ (4-3: a), 3-2: b))");
	}

	@Test
	public void testNotIffToOrAndUnary() {
		FormationTree tree = compiler.compile("¬¬(a↔b)");
		ra.applyNotIffToOrAnd(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬¬(a↔b)", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: ^ (0-3: a, 1-3: ¬ (2-4: b)), 1-2: ^ (2-3: ¬ (4-4: a), 3-3: b)))");
	}
	
	@Test
	public void testNotIffToOrAndBinaryLeft() {
		FormationTree tree = compiler.compile("(¬(a↔b))^a");
		ra.applyNotIffToOrAnd(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("(¬(a↔b))^a", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: ^ (0-3: a, 1-3: ¬ (2-4: b)), 1-2: ^ (2-3: ¬ (4-4: a), 3-3: b)), 1-1: a)");
	}
	
	@Test
	public void testNotIffToOrAndBinaryRight() {
		FormationTree tree = compiler.compile("a^(¬(a↔b))");
		ra.applyNotIffToOrAnd(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("a^(¬(a↔b))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: v (2-2: ^ (4-3: a, 5-3: ¬ (10-4: b)), 3-2: ^ (6-3: ¬ (12-4: a), 7-3: b)))");
	}
	
	// 16. ¬(A^B)	v-  ¬Av¬B
	@Test
	public void testDeMorgansAndRoot() {
		FormationTree tree = compiler.compile("¬(a^b)");
		ra.applyDeMorgansAnd(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("¬(a^b)", tree.toTreeString(), "0-0: v (0-1: ¬ (0-2: a), 1-1: ¬ (2-2: b))");
	}

	@Test
	public void testDeMorgansAndUnary() {
		FormationTree tree = compiler.compile("¬¬(a^b)");
		ra.applyDeMorgansAnd(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬¬(a^b)", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: ¬ (0-3: a), 1-2: ¬ (2-3: b)))");
	}
	
	@Test
	public void testDeMorgansAndBinaryLeft() {
		FormationTree tree = compiler.compile("¬(a^b)^a");
		ra.applyDeMorgansAnd(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬(a^b)^a", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: ¬ (0-3: a), 1-2: ¬ (2-3: b)), 1-1: a)");
	}
	
	@Test
	public void testDeMorgansAndBinaryRight() {
		FormationTree tree = compiler.compile("a^¬(a^b)");
		ra.applyDeMorgansAnd(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("a^¬(a^b)", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: v (2-2: ¬ (4-3: a), 3-2: ¬ (6-3: b)))");
	}
	
	// 17. ¬(AvB)	v-	¬A^¬B
	@Test
	public void testDeMorgansOrRoot() {
		FormationTree tree = compiler.compile("¬(avb)");
		ra.applyDeMorgansOr(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("¬(a^b)", tree.toTreeString(), "0-0: ^ (0-1: ¬ (0-2: a), 1-1: ¬ (2-2: b))");
	}

	@Test
	public void testDeMorgansOrUnary() {
		FormationTree tree = compiler.compile("¬¬(avb)");
		ra.applyDeMorgansOr(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬¬(a^b)", tree.toTreeString(), "0-0: ¬ (0-1: ^ (0-2: ¬ (0-3: a), 1-2: ¬ (2-3: b)))");
	}
	
	@Test
	public void testDeMorgansOrBinaryLeft() {
		FormationTree tree = compiler.compile("¬(avb)^a");
		ra.applyDeMorgansOr(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬(a^b)^a", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: ¬ (0-3: a), 1-2: ¬ (2-3: b)), 1-1: a)");
	}
	
	@Test
	public void testDeMorgansOrBinaryRight() {
		FormationTree tree = compiler.compile("a^¬(avb)");
		ra.applyDeMorgansOr(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("a^¬(a^b)", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ^ (2-2: ¬ (4-3: a), 3-2: ¬ (6-3: b)))");
	}
	
	// 18. A^(BvC) 	v- 	(A^B)v(A^C)
	@Test
	public void testDistributivityAndLeftRoot() {
		FormationTree tree = compiler.compile("a^(bvc)");
		ra.applyDistributivityAndLeft(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: v (0-1: ^ (0-2: a, 1-2: b), 1-1: ^ (2-2: a, 3-2: c))");
	}

	@Test
	public void testDistributivityAndLeftUnary() {
		FormationTree tree = compiler.compile("¬(a^(bvc))");
		ra.applyDistributivityAndLeft(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: ^ (0-3: a, 1-3: b), 1-2: ^ (2-3: a, 3-3: c)))");
	}
	
	@Test
	public void testDistributivityAndLeftBinaryLeft() {
		FormationTree tree = compiler.compile("(a^(bvc))^a");
		ra.applyDistributivityAndLeft(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: ^ (0-3: a, 1-3: b), 1-2: ^ (2-3: a, 3-3: c)), 1-1: a)");
	}
	
	@Test
	public void testDistributivityAndLeftBinaryRight() {
		FormationTree tree = compiler.compile("a^(a^(bvc))");
		ra.applyDistributivityAndLeft(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: v (2-2: ^ (4-3: a, 5-3: b), 3-2: ^ (6-3: a, 7-3: c)))");
	}

	// 19. (AvB)^C	v-  (A^C)v(B^C)
	@Test
	public void testDistributivityAndRightRoot() {
		FormationTree tree = compiler.compile("(avb)^c");
		ra.applyDistributivityAndRight(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: v (0-1: ^ (0-2: a, 1-2: c), 1-1: ^ (2-2: b, 3-2: c))");
	}

	@Test
	public void testDistributivityAndRightUnary() {
		FormationTree tree = compiler.compile("¬((avb)^c)");
		ra.applyDistributivityAndRight(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: ^ (0-3: a, 1-3: c), 1-2: ^ (2-3: b, 3-3: c)))");
	}
	
	@Test
	public void testDistributivityAndRightBinaryLeft() {
		FormationTree tree = compiler.compile("((avb)^c)^a");
		ra.applyDistributivityAndRight(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: ^ (0-3: a, 1-3: c), 1-2: ^ (2-3: b, 3-3: c)), 1-1: a)");
	}
	
	@Test
	public void testDistributivityAndRightBinaryRight() {
		FormationTree tree = compiler.compile("a^((avb)^c)");
		ra.applyDistributivityAndRight(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: v (2-2: ^ (4-3: a, 5-3: c), 3-2: ^ (6-3: b, 7-3: c)))");
	}

	// 20. Av(B^C)	v- 	(AvB)^(AvC)
	@Test
	public void testDistributivityOrLeftRoot() {
		FormationTree tree = compiler.compile("av(b^c)");
		ra.applyDistributivityOrLeft(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: a, 1-2: b), 1-1: v (2-2: a, 3-2: c))");
	}

	@Test
	public void testDistributivityOrLeftUnary() {
		FormationTree tree = compiler.compile("¬(av(b^c))");
		ra.applyDistributivityOrLeft(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: ^ (0-2: v (0-3: a, 1-3: b), 1-2: v (2-3: a, 3-3: c)))");
	}
	
	@Test
	public void testDistributivityOrLeftBinaryLeft() {
		FormationTree tree = compiler.compile("(av(b^c))^a");
		ra.applyDistributivityOrLeft(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: v (0-3: a, 1-3: b), 1-2: v (2-3: a, 3-3: c)), 1-1: a)");
	}
	
	@Test
	public void testDistributivityOrLeftBinaryRight() {
		FormationTree tree = compiler.compile("a^(av(b^c))");
		ra.applyDistributivityOrLeft(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ^ (2-2: v (4-3: a, 5-3: b), 3-2: v (6-3: a, 7-3: c)))");
	}

	// 21. (A^B)vC	v-	(AvC)^(BvC)
	@Test
	public void testDistributivityOrRightRoot() {
		FormationTree tree = compiler.compile("(a^b)vc");
		ra.applyDistributivityOrRight(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: a, 1-2: c), 1-1: v (2-2: b, 3-2: c))");
	}

	@Test
	public void testDistributivityOrRightUnary() {
		FormationTree tree = compiler.compile("¬((a^b)vc)");
		ra.applyDistributivityOrRight(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: ^ (0-2: v (0-3: a, 1-3: c), 1-2: v (2-3: b, 3-3: c)))");
	}
	
	@Test
	public void testDistributivityOrRightBinaryLeft() {
		FormationTree tree = compiler.compile("((a^b)vc)^a");
		ra.applyDistributivityOrRight(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: v (0-3: a, 1-3: c), 1-2: v (2-3: b, 3-3: c)), 1-1: a)");
	}
	
	@Test
	public void testDistributivityOrRightBinaryRight() {
		FormationTree tree = compiler.compile("a^((a^b)vc)");
		ra.applyDistributivityOrRight(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ^ (2-2: v (4-3: a, 5-3: c), 3-2: v (6-3: b, 7-3: c)))");
	}

	// 22. A^(AvB)  v-	A
	// 23. Av(A^B)	v-  A
	@Test
	public void testLeftAbsorptionAndRoot() {
		FormationTree tree = compiler.compile("a^(bva)");
		ra.applyLeftAbsorption(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: a");
	}

	@Test
	public void testLeftAbsorptionAndUnary() {
		FormationTree tree = compiler.compile("¬(a^(bva))");
		ra.applyLeftAbsorption(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: a)");
	}
	
	@Test
	public void testLeftAbsorptionAndBinaryLeft() {
		FormationTree tree = compiler.compile("(a^(bva))^a");
		ra.applyLeftAbsorption(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: a)");
	}
	
	@Test
	public void testLeftAbsorptionAndBinaryRight() {
		FormationTree tree = compiler.compile("a^(a^(bva))");
		ra.applyLeftAbsorption(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: a)");
	}
	

	// 24. (AvB)^A  v-	A
	// 25. (A^B)vA	v-  A
	@Test
	public void testRightAbsorptionAndRoot() {
		FormationTree tree = compiler.compile("(avb)^a");
		ra.applyRightAbsorption(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: a");
	}

	@Test
	public void testRightAbsorptionAndUnary() {
		FormationTree tree = compiler.compile("¬((avb)^a)");
		ra.applyRightAbsorption(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: a)");
	}
	
	@Test
	public void testRightAbsorptionAndBinaryLeft() {
		FormationTree tree = compiler.compile("((avb)^a)^a");
		ra.applyRightAbsorption(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: a)");
	}
	
	@Test
	public void testRightAbsorptionAndBinaryRight() {
		FormationTree tree = compiler.compile("a^((avb)^a)");
		ra.applyRightAbsorption(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: a)");
	}
	
}
