package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.Atom;
import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.UnaryOperator;
import treeManipulation.RuleApplicator;
import treeManipulation.RuleEngine;

public class RuleApplication {

	private Compiler compiler;
	private RuleEngine re;
	private RuleApplicator ra;
	
	@Before 
	public void method() {
		compiler = new Compiler();
		re = new RuleEngine();
		ra = re.getRuleApplicator();
	}

	// TODO: And tests
	
	// 0.  a^b 		|- 	b^a				-- Commutativity
	@Test
	public void testAndCommutativity() {
		FormationTree tree = compiler.compile("q^p");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("q^p: ", tree.toTreeString(), "0-0: ^ (0-1: p, 1-1: q)");
	}

	@Test
	public void testAndCommutativityComplex() {
		FormationTree tree = compiler.compile("(¬q→r)^(pva)");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("(¬q→r)^(pva): ", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: p, 1-2: a), 1-1: → (2-2: ¬ (4-3: q), 3-2: r))");
	}
	
	// 1.  a^a		|-  a 				-- Idempotence
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

	// 2.  a^┬		|- 	a
	// 25. av⊥		|-  a
	@Test
	public void testAndTop() {
		FormationTree tree = compiler.compile("a^┬");
		ra.applyToLeftChild(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toTreeString(), "0-0: a");
	}

	// 3.  ┬^a		|-  a
	// 26. ⊥va		|-  a
	// 51. ┬→a		|- 	a
	@Test
	public void testTopAnd() {
		FormationTree tree = compiler.compile("┬^a");
		ra.applyToRightChild(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toTreeString(), "0-0: a");
	}

	@Test
	public void testTopAndComplex() {
		FormationTree tree = compiler.compile("┬^(av¬b)");
		ra.applyToRightChild(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toTreeString(), "0-0: v (0-1: a, 1-1: ¬ (2-2: b))");
	}
	
	// 4.  ⊥^a		|-  ⊥
	// 5.  a^⊥		|-  ⊥
	// 6.  a^¬a		|-  ⊥
	// 7.  ¬a^a		|-  ⊥	
	@Test
	public void testBottomAnd() {
		FormationTree tree = compiler.compile("⊥^a");
		ra.applyToBottom(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toTreeString(), "0-0: ⊥");
	}
	
	// 8.  a^(b^c)		|-  (a^b)^c			-- Associativity
	@Test
	public void testAndRightAssociativity() {
		FormationTree tree = compiler.compile("(p^q)^r");
		ra.applyRightAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(p^q)^r: ", tree.toTreeString(), "0-0: ^ (0-1: p, 1-1: ^ (2-2: q, 3-2: r))");
	}

	@Test
	public void testAndRightAssociativityComplex() {
		FormationTree tree = compiler.compile("bv((¬p^q)^(r→a))");
		ra.applyRightAssociativity(tree,(BinaryOperator) tree.findNode(1, 1));
		assertEquals("bv((¬p^q)^(r→a)): ", tree.toTreeString(), "0-0: v (0-1: b, 1-1: ^ (2-2: ¬ (4-3: p), 3-2: ^ (6-3: q, 7-3: → (14-4: r, 15-4: a))))");
	}
	
	// 9.  (a^b)^c  	|- 	a^(b^c)			-- Associativity
	@Test
	public void testAndLeftAssociativity() {
		FormationTree tree = compiler.compile("p^(q^r)");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("p^(q^r): ", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: p, 1-2: q), 1-1: r)");
	}

	@Test
	public void testAndLeftAssociativityComplex() {
		FormationTree tree = compiler.compile("((r→a)^(¬p^q))vb");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("((r→a)^(¬p^q))vb: ", tree.toTreeString(), "0-0: v (0-1: ^ (0-2: ^ (0-3: → (0-4: r, 1-4: a), 1-3: ¬ (2-4: p)), 1-2: q), 1-1: b)");
	}
	
	// 10. a^¬b 		|-	¬(a→b)
	@Test
	public void testAndNotToNotImplies() {
		FormationTree tree = compiler.compile("a^¬b");
		ra.applyAndNotToNotImplies(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toTreeString(), "0-0: ¬ (0-1: → (0-2: a, 1-2: b))");
	}
	
	// 11. (a→b)^(b→a)	|-  a↔b
	@Test
	public void testAndImpliesToIff() {
		FormationTree tree = compiler.compile("(a→b)^(b→a)");
		ra.applyAndImpliesToIff(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toTreeString(), "0-0: ↔ (0-1: a, 1-1: b)");
	}
	
	// 12. ¬a^¬b		|-	¬(avb)			-- De Morgan laws
	@Test
	public void testDeMorganOrBackwards() {
		FormationTree tree = compiler.compile("¬a^¬b");
		ra.applyDeMorganOrBackwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: a, 1-2: b))");
	}
	
	// 13. a^(bvc) 		|- 	(a^b)v(a^c)		-- Distributitivity
	@Test
	public void testDistributivityAndLeftForwards() {
		FormationTree tree = compiler.compile("a^(bvc)");
		ra.applyDistributivityAndLeftForwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "(a^b)v(a^c)");
	}
	
	// 14. (avb)^c		|-  (a^c)v(b^c)		-- Distributitivity
	@Test
	public void testDistributivityAndRightForwards() {
		FormationTree tree = compiler.compile("(avb)^c");
		ra.applyDistributivityAndRightForwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "(a^c)v(b^c)");
	}
	
	// 15. (avb)^(avc)	|- 	av(b^c)			-- Distributitivity
	@Test
	public void testDistributivityOrLeftBackwards() {
		FormationTree tree = compiler.compile("(avb)^(avc)");
		ra.applyDistributivityOrLeftBackwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "av(b^c)");
	}
	
	// 16. (avc)^(bvc)	|-	(a^b)vc			-- Distributitivity
	@Test
	public void testDistributivityOrRightBackwards() {
		FormationTree tree = compiler.compile("(avc)^(bvc)");
		ra.applyDistributivityOrRightBackwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "(a^b)vc");
	}
	
	// 17. a^(avb)  	|-	a				-- Absoption
	@Test
	public void applyLeftAbsorption() {
		FormationTree tree = compiler.compile("a^(avb)");
		ra.applyLeftAbsorption(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "a");
	}
	
	// 18. (avb)^a  	|-	a				-- Absoption
	@Test
	public void applyRightAbsorption() {
		FormationTree tree = compiler.compile("(avb)^a");
		ra.applyRightAbsorption(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "a");
	}
	
	// A^(BvC) 	|- 	(A^B)v(A^C)
	@Test
	public void testDistributivityAndLeftRoot() {
		FormationTree tree = compiler.compile("a^(bvc)");
		ra.applyDistributivityAndLeftForwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: v (0-1: ^ (0-2: a, 1-2: b), 1-1: ^ (2-2: a, 3-2: c))");
	}

	@Test
	public void testDistributivityAndLeftUnary() {
		FormationTree tree = compiler.compile("¬(a^(bvc))");
		ra.applyDistributivityAndLeftForwards(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: ^ (0-3: a, 1-3: b), 1-2: ^ (2-3: a, 3-3: c)))");
	}
	
	@Test
	public void testDistributivityAndLeftBinaryLeft() {
		FormationTree tree = compiler.compile("(a^(bvc))^a");
		ra.applyDistributivityAndLeftForwards(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: ^ (0-3: a, 1-3: b), 1-2: ^ (2-3: a, 3-3: c)), 1-1: a)");
	}
	
	@Test
	public void testDistributivityAndLeftBinaryRight() {
		FormationTree tree = compiler.compile("a^(a^(bvc))");
		ra.applyDistributivityAndLeftForwards(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: v (2-2: ^ (4-3: a, 5-3: b), 3-2: ^ (6-3: a, 7-3: c)))");
	}

	// (AvB)^C	|-  (A^C)v(B^C)
	@Test
	public void testDistributivityAndRightRoot() {
		FormationTree tree = compiler.compile("(avb)^c");
		ra.applyDistributivityAndRightForwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: v (0-1: ^ (0-2: a, 1-2: c), 1-1: ^ (2-2: b, 3-2: c))");
	}

	@Test
	public void testDistributivityAndRightUnary() {
		FormationTree tree = compiler.compile("¬((avb)^c)");
		ra.applyDistributivityAndRightForwards(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: ^ (0-3: a, 1-3: c), 1-2: ^ (2-3: b, 3-3: c)))");
	}
	
	@Test
	public void testDistributivityAndRightBinaryLeft() {
		FormationTree tree = compiler.compile("((avb)^c)^a");
		ra.applyDistributivityAndRightForwards(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: ^ (0-3: a, 1-3: c), 1-2: ^ (2-3: b, 3-3: c)), 1-1: a)");
	}
	
	@Test
	public void testDistributivityAndRightBinaryRight() {
		FormationTree tree = compiler.compile("a^((avb)^c)");
		ra.applyDistributivityAndRightForwards(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: v (2-2: ^ (4-3: a, 5-3: c), 3-2: ^ (6-3: b, 7-3: c)))");
	}

	// TODO: Or tests
	
	// 19. avb		|- 	bva				-- Commutativity
	@Test
	public void testOrCommutativity() {
		FormationTree tree = compiler.compile("qvp");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("qvp: ", tree.toTreeString(), "0-0: v (0-1: p, 1-1: q)");
	}

	@Test
	public void testOrCommutativityComplex() {
		FormationTree tree = compiler.compile("(¬q→r)v(pva)");
		ra.applyCommutativity((BinaryOperator) tree.findNode(0, 0));
		assertEquals("(¬q→r)v(pva): ", tree.toTreeString(), "0-0: v (0-1: v (0-2: p, 1-2: a), 1-1: → (2-2: ¬ (4-3: q), 3-2: r))");
	}
	
	// 20. ava		|- 	a 				-- Idempotence
	@Test
	public void testOrIdempotence() {
		FormationTree tree = compiler.compile("pvp");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("pvp: ", tree.toTreeString(), "0-0: p");
	}
	
	// 21. ┬va				|-  ┬
	// 22. av┬				|-  ┬
	// 23. av¬a				|-  ┬
	// 24. ¬ava				|-  ┬
	@Test
	public void applyToTop() {
		FormationTree tree = compiler.compile("┬va");
		ra.applyToTop(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "┬");
	}
	
	@Test
	public void testOrIdempotenceComplex() {
		FormationTree tree = compiler.compile("(¬q→r)v(¬q→r)");
		ra.applyIdempotence(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(¬q→r)v(¬q→r): ", tree.toTreeString(), "0-0: → (0-1: ¬ (0-2: q), 1-1: r)");
	}
	
	// 27. av(bvc)	|-  (avb)vc			-- Associativity
	@Test
	public void testOrLeftAssociativity() {
		FormationTree tree = compiler.compile("pv(qvr)");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("pv(qvr): ", tree.toTreeString(), "0-0: v (0-1: v (0-2: p, 1-2: q), 1-1: r)");
	}

	@Test
	public void testOrLeftAssociativityComplex() {
		FormationTree tree = compiler.compile("((r→a)v(¬pvq))^b");
		ra.applyLeftAssociativity(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("((r→a)v(¬pvq))^b: ", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: v (0-3: → (0-4: r, 1-4: a), 1-3: ¬ (2-4: p)), 1-2: q), 1-1: b)");
	}

	// 28. (avb)vc  |- 	av(bvc)			-- Associativity
	@Test
	public void testOrRightAssociativity() {
		FormationTree tree = compiler.compile("(pvq)vr");
		ra.applyRightAssociativity(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("(pvq)vr: ", tree.toTreeString(), "0-0: v (0-1: p, 1-1: v (2-2: q, 3-2: r))");
	}

	@Test
	public void testOrRightAssociativityComplex() {
		FormationTree tree = compiler.compile("b^((¬pvq)v(r→a))");
		ra.applyRightAssociativity(tree,(BinaryOperator) tree.findNode(1, 1));
		assertEquals("b^((¬pvq)v(r→a)): ", tree.toTreeString(), "0-0: ^ (0-1: b, 1-1: v (2-2: ¬ (4-3: p), 3-2: v (6-3: q, 7-3: → (14-4: r, 15-4: a))))");
	}
	
	// 29. (a^b)v(¬a^¬b)	|-	a↔b
	@Test
	public void applyOrAndToIff() {
		FormationTree tree = compiler.compile("(a^b)v(¬a^¬b)");
		ra.applyOrAndToIff(tree,(BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "a↔b");
	}
	
	// 30. (a^¬b)v(¬a^b) 	|-  ¬(a↔b)
	@Test
	public void applyOrAndToNotIff() {
		FormationTree tree = compiler.compile("(a^¬b)v(¬a^b)");
		ra.applyOrAndToNotIff(tree,(BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬(a↔b)");
	}
	
	// 31. ¬av¬b			|-  ¬(a^b)
	@Test
	public void applyDeMorganAndBackwards() {
		FormationTree tree = compiler.compile("¬av¬b");
		ra.applyDeMorganAndBackwards(tree,(BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬(a^b)");
	}
	
	// Av(B^C)	|- 	(AvB)^(AvC)
	@Test
	public void testDistributivityOrLeftRoot() {
		FormationTree tree = compiler.compile("av(b^c)");
		ra.applyDistributivityOrLeftForwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: a, 1-2: b), 1-1: v (2-2: a, 3-2: c))");
	}

	@Test
	public void testDistributivityOrLeftUnary() {
		FormationTree tree = compiler.compile("¬(av(b^c))");
		ra.applyDistributivityOrLeftForwards(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: ^ (0-2: v (0-3: a, 1-3: b), 1-2: v (2-3: a, 3-3: c)))");
	}
	
	@Test
	public void testDistributivityOrLeftBinaryLeft() {
		FormationTree tree = compiler.compile("(av(b^c))^a");
		ra.applyDistributivityOrLeftForwards(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: v (0-3: a, 1-3: b), 1-2: v (2-3: a, 3-3: c)), 1-1: a)");
	}
	
	@Test
	public void testDistributivityOrLeftBinaryRight() {
		FormationTree tree = compiler.compile("a^(av(b^c))");
		ra.applyDistributivityOrLeftForwards(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ^ (2-2: v (4-3: a, 5-3: b), 3-2: v (6-3: a, 7-3: c)))");
	}

	// (A^B)vC	|-	(AvC)^(BvC)
	@Test
	public void testDistributivityOrRightRoot() {
		FormationTree tree = compiler.compile("(a^b)vc");
		ra.applyDistributivityOrRightForwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("a^(bvc)", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: a, 1-2: c), 1-1: v (2-2: b, 3-2: c))");
	}

	@Test
	public void testDistributivityOrRightUnary() {
		FormationTree tree = compiler.compile("¬((a^b)vc)");
		ra.applyDistributivityOrRightForwards(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("¬a^(bvc)", tree.toTreeString(), "0-0: ¬ (0-1: ^ (0-2: v (0-3: a, 1-3: c), 1-2: v (2-3: b, 3-3: c)))");
	}
	
	@Test
	public void testDistributivityOrRightBinaryLeft() {
		FormationTree tree = compiler.compile("((a^b)vc)^a");
		ra.applyDistributivityOrRightForwards(tree, (BinaryOperator) tree.findNode(0, 1));
		assertEquals("(a^(bvc))^a", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: v (0-3: a, 1-3: c), 1-2: v (2-3: b, 3-3: c)), 1-1: a)");
	}
	
	@Test
	public void testDistributivityOrRightBinaryRight() {
		FormationTree tree = compiler.compile("a^((a^b)vc)");
		ra.applyDistributivityOrRightForwards(tree, (BinaryOperator) tree.findNode(1, 1));
		assertEquals("a^(a^(bvc))", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ^ (2-2: v (4-3: a, 5-3: c), 3-2: v (6-3: b, 7-3: c)))");
	}
	
	// 34. (a^b)v(a^c) 		|- 	a^(bvc)		-- Distributitivity
	@Test
	public void applyDistributivityAndLeftBackwards() {
		FormationTree tree = compiler.compile("(a^b)v(a^c)");
		ra.applyDistributivityAndLeftBackwards(tree,(BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "a^(bvc)");
	}
	
	// 35. (a^c)v(b^c)		|-  (avb)^c		-- Distributitivity
	@Test
	public void applyDistributivityAndRightBackwards() {
		FormationTree tree = compiler.compile("(a^c)v(b^c)");
		ra.applyDistributivityAndRightBackwards(tree,(BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "(avb)^c");
	}
	
	// 38. ¬avb				|- 	a→b
	@Test
	public void applyOrToImplies() {
		FormationTree tree = compiler.compile("¬avb");
		ra.applyOrToImplies(tree,(BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "a→b");
	}
	
	// TODO: Not tests
	
	// 39. ¬┬		|-  ⊥
	@Test
	public void applyNotTop() {
		FormationTree tree = compiler.compile("¬┬");
		ra.applyNotTop(tree,(UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "⊥");
	}
	
	// 40. ¬⊥		|-  ┬
	@Test
	public void applyNotBottom() {
		FormationTree tree = compiler.compile("¬⊥");
		ra.applyNotBottom(tree,(UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "┬");
	}
	
	// 41. ¬¬a 		|- 	a
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
	
	// 42. ¬a		|-  a→⊥
	@Test
	public void applyNotAtom() {
		FormationTree tree = compiler.compile("¬a");
		ra.applyNotAtom(tree,(UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "a→⊥");
	}
	
	// 43. ¬(a→b) 	|-	a^¬b
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

	// 44. ¬(a^¬b)	|- 	a→b
	@Test
	public void applyNotOrToImplies() {
		FormationTree tree = compiler.compile("¬(a^¬b)");
		ra.applyNotAndToImplies(tree,(UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "a→b");
	}
	

	// 45. ¬(a↔b) 	|- 	a↔¬b
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
	
	// 46. ¬(a↔b) 	|-  ¬a↔b
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
	
	// 47. ¬(a↔b) 	|-  (a^¬b)v(¬a^b)
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
	
	// 48. ¬(a^b)	|-  ¬av¬b
	@Test
	public void testDeMorgansAndRoot() {
		FormationTree tree = compiler.compile("¬(a^b)");
		ra.applyDeMorganAndForwards(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("¬(a^b)", tree.toTreeString(), "0-0: v (0-1: ¬ (0-2: a), 1-1: ¬ (2-2: b))");
	}

	@Test
	public void testDeMorgansAndUnary() {
		FormationTree tree = compiler.compile("¬¬(a^b)");
		ra.applyDeMorganAndForwards(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬¬(a^b)", tree.toTreeString(), "0-0: ¬ (0-1: v (0-2: ¬ (0-3: a), 1-2: ¬ (2-3: b)))");
	}
	
	@Test
	public void testDeMorgansAndBinaryLeft() {
		FormationTree tree = compiler.compile("¬(a^b)^a");
		ra.applyDeMorganAndForwards(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬(a^b)^a", tree.toTreeString(), "0-0: ^ (0-1: v (0-2: ¬ (0-3: a), 1-2: ¬ (2-3: b)), 1-1: a)");
	}
	
	@Test
	public void testDeMorgansAndBinaryRight() {
		FormationTree tree = compiler.compile("a^¬(a^b)");
		ra.applyDeMorganAndForwards(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("a^¬(a^b)", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: v (2-2: ¬ (4-3: a), 3-2: ¬ (6-3: b)))");
	}
	
	// 49. ¬(avb)	|-	¬a^¬b
	@Test
	public void testDeMorgansOrRoot() {
		FormationTree tree = compiler.compile("¬(avb)");
		ra.applyDeMorganOrForwards(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("¬(a^b)", tree.toTreeString(), "0-0: ^ (0-1: ¬ (0-2: a), 1-1: ¬ (2-2: b))");
	}

	@Test
	public void testDeMorgansOrUnary() {
		FormationTree tree = compiler.compile("¬¬(avb)");
		ra.applyDeMorganOrForwards(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬¬(a^b)", tree.toTreeString(), "0-0: ¬ (0-1: ^ (0-2: ¬ (0-3: a), 1-2: ¬ (2-3: b)))");
	}
	
	@Test
	public void testDeMorgansOrBinaryLeft() {
		FormationTree tree = compiler.compile("¬(avb)^a");
		ra.applyDeMorganOrForwards(tree, (UnaryOperator) tree.findNode(0, 1));
		assertEquals("¬(a^b)^a", tree.toTreeString(), "0-0: ^ (0-1: ^ (0-2: ¬ (0-3: a), 1-2: ¬ (2-3: b)), 1-1: a)");
	}
	
	@Test
	public void testDeMorgansOrBinaryRight() {
		FormationTree tree = compiler.compile("a^¬(avb)");
		ra.applyDeMorganOrForwards(tree, (UnaryOperator) tree.findNode(1, 1));
		assertEquals("a^¬(a^b)", tree.toTreeString(), "0-0: ^ (0-1: a, 1-1: ^ (2-2: ¬ (4-3: a), 3-2: ¬ (6-3: b)))");
	}
	
	// TODO: Implies tests
	
	// 54. a→⊥		|- 	¬a
	@Test
	public void applyImpliesToNot() {
		FormationTree tree = compiler.compile("a→⊥");
		ra.applyImpliesToNot(tree,(BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬a");
	}
	
	// 55. a→b 		|- 	¬avb
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

	// 56. a→b 		|- 	¬(av¬b)
	@Test
	public void applyImpliesToNotAnd() {
		FormationTree tree = compiler.compile("a→b");
		ra.applyImpliesToNotAnd(tree,(BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬(a^¬b)");
	}
	
	// TODO: Iff tests
	
	// 57. a↔b				|-  (a→b)^(b→a)
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

	// 58. a↔b				|-	(a^b)v(¬a^¬b)
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
	
	// 59. a↔¬b 			|- 	¬(a↔b)
	@Test
	public void applyIffNotBToNotIff() {
		FormationTree tree = compiler.compile("a↔¬b");
		ra.applyIffNotBToNotIff(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬(a↔b)");
	}
	
	// 60. ¬a↔b 			|-  ¬(a↔b)
	@Test
	public void applyIffNotAToNotIff() {
		FormationTree tree = compiler.compile("¬a↔b");
		ra.applyIffNotAToNotIff(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬(a↔b)");
	}
	
	// TODO: Atom tests
	
	// 61. a		|-  a^a
	@Test
	public void applyAndIdempotenceBackwards() {
		FormationTree tree = compiler.compile("a");
		ra.applyAndIdempotenceBackwards(tree, (Atom) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "a^a");
	}
	
	// 62. a		|-  a^┬
	@Test
	public void applyAndTop() {
		FormationTree tree = compiler.compile("a");
		ra.applyAndTop(tree, (Atom) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "a^┬");
	}
	
	// 63. a		|-  ava
	@Test
	public void applyOrIdempotenceBackwards() {
		FormationTree tree = compiler.compile("a");
		ra.applyOrIdempotenceBackwards(tree, (Atom) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "ava");
	}
	
	// 64. a		|-  av⊥
	@Test
	public void applyOrBottom() {
		FormationTree tree = compiler.compile("a");
		ra.applyOrBottom(tree, (Atom) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "av⊥");
	}
	
	// 65. a		|-  ¬¬a
	@Test
	public void applyNotNotBackwards() {
		FormationTree tree = compiler.compile("a");
		ra.applyNotNotBackwards(tree, (Atom) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬¬a");
	}
	
	// 66. a		|-  ┬→a
	@Test
	public void applyTopImplies() {
		FormationTree tree = compiler.compile("a");
		ra.applyTopImplies(tree, (Atom) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "┬→a");
	}
	
	// 67. ⊥		|-  ¬┬
	@Test
	public void applyNotTop2() {
		FormationTree tree = compiler.compile("⊥");
		ra.applyNotTop(tree, (Atom) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬┬");
	}
	
	// 68. ┬		|-  ¬⊥
	@Test
	public void applyNotBottom2() {
		FormationTree tree = compiler.compile("┬");
		ra.applyNotBottom(tree, (Atom) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬⊥");
	}
	
	// TODO: User input tests
	
	// 69. ⊥		|-  a^⊥
	@Test
	public void applyAtomAndBottom() {
		FormationTree tree = compiler.compile("⊥");
		ra.applyAtomAndBottom(tree, (Atom) tree.findNode(0, 0), "a");
		assertEquals("", tree.toString(), "a^⊥");
	}

	// 70. ⊥		|-  a^¬a
	@Test
	public void applyBottomToAndAtom() {
		FormationTree tree = compiler.compile("⊥");
		ra.applyBottomToAndAtom(tree, (Atom) tree.findNode(0, 0), "a");
		assertEquals("", tree.toString(), "a^¬a");
	}

	// 71. ┬		|-  av┬
	@Test
	public void applyAtomOrTop() {
		FormationTree tree = compiler.compile("┬");
		ra.applyAtomOrTop(tree, (Atom) tree.findNode(0, 0), "a");
		assertEquals("", tree.toString(), "av┬");
	}

	// 72. ┬		|-  av¬a
	@Test
	public void applyTopToOrAtom() {
		FormationTree tree = compiler.compile("┬");
		ra.applyTopToOrAtom(tree, (Atom) tree.findNode(0, 0), "a");
		assertEquals("", tree.toString(), "av¬a");
	}

	// 73. ┬		|- 	a→a	
	@Test
	public void applyTopToImpliesAtom() {
		FormationTree tree = compiler.compile("┬");
		ra.applyTopToImpliesAtom(tree, (Atom) tree.findNode(0, 0), "a");
		assertEquals("", tree.toString(), "a→a");
	}

	// 74. ┬		|- 	a→┬
	@Test
	public void applyTopToImpliesAtomTop() {
		FormationTree tree = compiler.compile("┬");
		ra.applyTopToImpliesAtomTop(tree, (Atom) tree.findNode(0, 0), "a");
		assertEquals("", tree.toString(), "a→┬");
	}

	// 75. ┬		|- 	⊥→a
	@Test
	public void applyTopToImpliesBottomAtom() {
		FormationTree tree = compiler.compile("┬");
		ra.applyTopToImpliesBottomAtom(tree, (Atom) tree.findNode(0, 0), "a");
		assertEquals("", tree.toString(), "⊥→a");
	}

	// 76. a		|-  av(a^b)
	@Test
	public void applyAbsorptionOrBackwards() {
		FormationTree tree = compiler.compile("a");
		ra.applyAbsorptionOrBackwards(tree, (Atom) tree.findNode(0, 0), "b");
		assertEquals("", tree.toString(), "av(a^b)");
	}

	// 77. a  		|-	a^(avb)
	@Test
	public void applyAbsorptionAndBackwards() {
		FormationTree tree = compiler.compile("a");
		ra.applyAbsorptionAndBackwards(tree, (Atom) tree.findNode(0, 0), "b");
		assertEquals("", tree.toString(), "a^(avb)");
	}

	// TODO: Multi method tests
	
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
