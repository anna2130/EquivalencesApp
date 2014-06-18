package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeManipulation.TruthTable;

public class TruthTables {

	private Compiler compiler;
	
	@Before 
	public void method() {
		compiler = new Compiler(false);
	}

	// Test commonly used logical operators
	
	@Test
	public void testAtom() {
		String s = "p";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0], [1, 1]]", list.toString());
	}

	@Test
	public void testNegation() {
		String s = "¬p";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 1], [1, 0]]", list.toString());
	}

	@Test
	public void testAnd() {
		String s = "p∧q";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 0], [0, 1, 0], [1, 0, 0], [1, 1, 1]]", list.toString());
	}

	@Test
	public void testOr() {
		String s = "p∨q";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 0], [0, 1, 1], [1, 0, 1], [1, 1, 1]]", list.toString());
	}

	@Test
	public void testImplies() {
		String s = "p→q";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 1], [0, 1, 1], [1, 0, 0], [1, 1, 1]]", list.toString());
	}

	@Test
	public void testIff() {
		String s = "p↔q";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 1], [0, 1, 0], [1, 0, 0], [1, 1, 1]]", list.toString());
	}

	@Test
	public void testAndOr() {
		String s = "p∧(p∨q)";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 0], [0, 1, 0], [1, 0, 1], [1, 1, 1]]", list.toString());
	}

	@Test
	public void testAndTop() {
		String s = "p∧┬";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 0], [0, 1, 0], [1, 0, 1], [1, 1, 1]]", list.toString());
	}
	
	@Test
	public void testAndBottom() {
		String s = "p∧⊥";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 0], [0, 1, 0], [1, 0, 0], [1, 1, 0]]", list.toString());
	}

	@Test
	public void testOrTop() {
		String s = "p∨┬";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 1], [0, 1, 1], [1, 0, 1], [1, 1, 1]]", list.toString());
	}
	
	@Test
	public void testOrBottom() {
		String s = "p∨⊥";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 0], [0, 1, 0], [1, 0, 1], [1, 1, 1]]", list.toString());
	}
	
	// Test Top and Bottom
	
	@Test
	public void testTopOrNotBottom() {
		String s = "┬∨¬⊥";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 1], [0, 1, 1], [1, 0, 1], [1, 1, 1]]", list.toString());
	}

	@Test
	public void testNotTopOrBottom() {
		String s = "¬┬∨⊥";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 0], [0, 1, 0], [1, 0, 0], [1, 1, 0]]", list.toString());
	}

	@Test
	public void testNotNotTopOrBottom() {
		String s = "¬(¬┬∨⊥)";
		FormationTree tree = compiler.compile(s);
		TruthTable tt = new TruthTable(tree);
		HashSet<ArrayList<Integer>> list = tt.createTruthTable(tree);
		assertEquals("", "[[0, 0, 1], [0, 1, 1], [1, 0, 1], [1, 1, 1]]", list.toString());
	}
	
	// Test comparisons for equality
	// TODO: And tests
	
	// 0.  a∧b 		|- 	b∧a				-- Commutativity
	@Test
	public void testAndCommutativityEquality() {
		String s1 = "p∧q";
		String s2 = "q∧p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 1.  a∧a		|-  a 				-- Idempotence	
	@Test
	public void testAndIdempotenceEquality() {
		String s1 = "p∧p";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 8.  a∧(b∧c)	|-  (a∧b)∧c			-- Associativity
	@Test
	public void testAndLeftAssociativityEquality() {
		String s1 = "p∧(q∧r)";
		String s2 = "(p∧q)∧r";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 9.  (a∧b)∧c  |- 	a∧(b∧c)			-- Associativity
	@Test
	public void testAndRightAssociativityEquality() {
		String s1 = "(p∧q)∧r";
		String s2 = "p∧(q∧r)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 10. a∧¬b 		|-	¬(a→b)
	@Test
	public void applyAndNotToNotImplies() {
		String s1 = "a∧¬b";
		String s2 = "¬(a→b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 11. (a→b)∧(b→a)	|-  a↔b
	@Test
	public void applyAndImpliesToIff() {
		String s1 = "(a→b)∧(b→a)";
		String s2 = "a↔b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 12. ¬a∧¬b		|-	¬(avb)			-- De Morgan laws
	@Test
	public void applyDeMorganOrBackwards() {
		String s1 = "¬a∧¬b";
		String s2 = "¬(a∨b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 13. a∧(bvc) 		|- 	(a∧b)v(a∧c)		-- Distributitivity
	@Test
	public void applyDistributivityAndLeftForwards() {
		String s1 = "a∧(b∨c)";
		String s2 = "(a∧b)∨(a∧c)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 14. (avb)∧c		|-  (a∧c)v(b∧c)		-- Distributitivity
	@Test
	public void applyDistributivityAndRightForwards() {
		String s1 = "(a∨b)∧c";
		String s2 = "(a∧c)∨(b∧c)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 15. (avb)∧(avc)	|- 	av(b∧c)		-- Distributitivity
	@Test
	public void applyDistributivityOrLeftBackwards() {
		String s1 = "(a∨b)∧(a∨c)";
		String s2 = "a∨(b∧c)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 16. (avc)∧(bvc)	|-	(a∧b)vc		-- Distributitivity
	@Test
	public void applyDistributivityOrRightBackwards() {
		String s1 = "(a∨c)∧(b∨c)";
		String s2 = "(a∧b)∨c";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// TODO: Or tests

	// 19. avb		|- 	bva				-- Commutativity
	@Test
	public void testOrCommutativityEquality() {
		String s1 = "p∨q";
		String s2 = "q∨p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 20. ava		|- 	a 				-- Idempotence
	@Test
	public void testOrIdempotenceEquality() {
		String s1 = "p∨p";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 27. av(bvc)	|-  (avb)vc			-- Associativity
	@Test
	public void testOrLeftAssociativityEquality() {
		String s1 = "p∨(q∨r)";
		String s2 = "(p∨q)∨r";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 28. (avb)vc  |- 	av(bvc)			-- Associativity
	@Test
	public void testOrRightAssociativityEquality() {
		String s1 = "(p∨q)∨r";
		String s2 = "p∨(q∨r)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 29. (a∧b)v(¬a∧¬b)	|-	a↔b
	@Test
	public void applyOrAndToIff() {
		String s1 = "(a∧b)∨(¬a∧¬b)";
		String s2 = "a↔b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 30. (a∧¬b)v(¬a∧b) 	|-  ¬(a↔b)
	@Test
	public void applyOrAndToNotIff() {
		String s1 = "(a∧¬b)∨(¬a∧b)";
		String s2 = "¬(a↔b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 31. ¬av¬b	|-  ¬(a∧b)			-- De Morgan laws
	@Test
	public void applyDeMorganAndBackwards() {
		String s1 = "¬a∨¬b";
		String s2 = "¬(a∧b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 32. av(b∧c)	|- 	(avb)∧(avc)		-- Distributitivity
	@Test
	public void applyDistributivityOrLeftForwards() {
		String s1 = "a∨(b∧c)";
		String s2 = "(a∨b)∧(a∨c)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 33. (a∧b)vc	|-	(avc)∧(bvc)		-- Distributitivity
	@Test
	public void applyDistributivityOrRightForwards() {
		String s1 = "(a∧b)∨c";
		String s2 = "(a∨c)∧(b∨c)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 34. (a∧b)v(a∧c) 	|- 	a∧(bvc)		-- Distributitivity
	@Test
	public void applyDistributivityAndLeftBackwards() {
		String s1 = "(a∧b)∨(a∧c)";
		String s2 = "a∧(b∨c)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 35. (a∧c)v(b∧c)	|-  (avb)∧c		-- Distributitivity
	@Test
	public void applyDistributivityAndRightBackwards() {
		String s1 = "(a∧c)∨(b∧c)";
		String s2 = "(a∨b)∧c";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 38. ¬avb		|- 	a→b
	@Test
	public void applyOrToImplies() {
		String s1 = "¬a∨b";
		String s2 = "a→b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// TODO: Not tests
	
	// 39. ¬┬		|-  ⊥
	@Test
	public void applyNotTop() {
		String s1 = "¬┬";
		String s2 = "⊥";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 40. ¬⊥		|-  ┬
	@Test
	public void applyNotBottom() {
		String s1 = "¬⊥";
		String s2 = "┬";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 41. ¬¬a 		|- 	a
	@Test
	public void testNotNotEquality() {
		String s1 = "¬¬p";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// 42. ¬a		|-  a→⊥
	@Test
	public void applyNotAtom() {
		String s1 = "¬a";
		String s2 = "a→⊥";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 43. ¬(a→b) 	|-	a∧¬b
	@Test
	public void testNotImpliesToOrEquality() {
		String s1 = "¬(p→q)";
		String s2 = "p∧¬q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 44. ¬(a∧¬b)	|- 	a→b
	@Test
	public void applyNotOrToImplies() {
		String s1 = "¬(a∧¬b)";
		String s2 = "a→b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 45. ¬(a↔b) 	|- 	a↔¬b
	@Test
	public void applyNotIffToNotB() {
		String s1 = "¬(a↔b)";
		String s2 = "a↔¬b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 46. ¬(a↔b) 	|-  ¬a↔b
	@Test
	public void applyNotIffToNotA() {
		String s1 = "¬(a↔b)";
		String s2 = "¬a↔b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 47. ¬(a↔b) 	|-  (a∧¬b)v(¬a∧b)	-- Exclusive or of a and b
	@Test
	public void applyNotIffToOrAnd() {
		String s1 = "¬(a↔b)";
		String s2 = "(a∧¬b)∨(¬a∧b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 48. ¬(a∧b)	|-  ¬av¬b			-- De Morgan laws
	@Test
	public void applyDeMorganAndForwards() {
		String s1 = "¬(a∧b)";
		String s2 = "¬a∨¬b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 49. ¬(avb)	|-	¬a∧¬b			-- De Morgan laws
	@Test
	public void applyDeMorganOrForwards() {
		String s1 = "¬(a∨b)";
		String s2 = "¬a∧¬b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	
	// TODO: Implies tests

	// 54. a→⊥		|- 	¬a
	@Test
	public void applyImpliesToNot() {
		String s1 = "a→⊥";
		String s2 = "¬a";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 55. a→b 		|- 	¬avb	
	@Test
	public void testImpliesToOrEquality() {
		String s1 = "p→q";
		String s2 = "¬p∨q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 56. a→b 		|- 	¬(a∧¬b)
	@Test
	public void applyImpliesToNotOr() {
		String s1 = "a→b";
		String s2 = "¬(a∧¬b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	// TODO: Iff tests
	
	// 57. a↔b				|-  (a→b)∧(b→a)
	@Test
	public void testIffToAndEquality() {
		String s1 = "p↔q";
		String s2 = "(p→q)∧(q→p)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 58. a↔b				|-	(a∧b)v(¬a∧¬b)
	@Test
	public void testIffToOrEquality() {
		String s1 = "p↔q";
		String s2 = "(p∧q)∨(¬p∧¬q)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 59. a↔¬b 			|- 	¬(a↔b)
	@Test
	public void applyIffNotBToNotIff() {
		String s1 = "a↔¬b";
		String s2 = "¬(a↔b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 60. ¬a↔b 			|-  ¬(a↔b)
	@Test
	public void applyIffNotAToNotIff() {
		String s1 = "¬a↔b";
		String s2 = "¬(a↔b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// TODO: Atom tests
	
	// 61. a		|-  a∧a
	@Test
	public void atom1() {
		String s1 = "a";
		String s2 = "a∧a";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}	
	
	// 62. a		|-  a∧┬
	@Test
	public void atom2() {
		String s1 = "a";
		String s2 = "a∧┬";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}	
	
	// 63. a		|-  ava
	@Test
	public void atom3() {
		String s1 = "a";
		String s2 = "a∨a";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}	
	
	// 64. a		|-  av⊥
	@Test
	public void atom4() {
		String s1 = "a";
		String s2 = "a∨⊥";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}	
	
	// 65. a		|-  ¬¬a
	@Test
	public void atom5() {
		String s1 = "a";
		String s2 = "¬¬a";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}	
	
	// 66. a		|-  ┬→a
	@Test
	public void atom6() {
		String s1 = "a";
		String s2 = "┬→a";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// TODO: User input tests
	
	// 69. ⊥		|-  a∧⊥
	@Test
	public void atom7() {
		String s1 = "⊥";
		String s2 = "a∧⊥";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 70. ⊥		|-  a∧¬a
	@Test
	public void atom8() {
		String s1 = "⊥";
		String s2 = "a∧¬a";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 71. ┬		|-  av┬
	@Test
	public void atom9() {
		String s1 = "┬";
		String s2 = "a∨┬";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 72. ┬		|-  av¬a
	@Test
	public void atom10() {
		String s1 = "┬";
		String s2 = "a∨¬a";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 73. ┬		|- 	a→a
	@Test
	public void atom11() {
		String s1 = "┬";
		String s2 = "a→a";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 74. ┬		|- 	a→┬
	@Test
	public void atom12() {
		String s1 = "┬";
		String s2 = "a→┬";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 75. ┬		|- 	⊥→a
	@Test
	public void atom13() {
		String s1 = "┬";
		String s2 = "⊥→a";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 76. a		|-  av(a∧b)
	@Test
	public void atom14() {
		String s1 = "a";
		String s2 = "a∨(a∧b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// 77. a  		|-	a∧(avb)
	@Test
	public void atom15() {
		String s1 = "a";
		String s2 = "a∧(a∨b)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// TODO: Old tests
	
	@Test
	public void testNotIffToNotBEquality() {
		String s1 = "¬(p↔q)";
		String s2 = "p↔¬q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testNotIffToOrEquality() {
		String s1 = "¬(p↔q)";
		String s2 = "¬p↔q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testNotIffToNotAEquality() {
		String s1 = "¬(p↔q)";
		String s2 = "(p∧¬q)∨(¬p∧q)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testDeMorganAndEquality() {
		String s1 = "¬(p∧q)";
		String s2 = "¬p∨¬q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testDeMorganOrEquality() {
		String s1 = "¬(p∨q)";
		String s2 = "¬p∧¬q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testDistributivityAndLeftEquality() {
		String s1 = "p∧(q∨r)";
		String s2 = "(p∧q)∨(p∧r)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testDistributivityAndRightEquality() {
		String s1 = "(p∨q)∧r";
		String s2 = "(p∧r)∨(q∧r)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	@Test
	public void testDistributivityOrLeftEquality() {
		String s1 = "p∨(q∧r)";
		String s2 = "(p∨q)∧(p∨r)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testDistributivityOrRightEquality() {
		String s1 = "(p∧q)∨r";
		String s2 = "(p∨r)∧(q∨r)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testAbsorptionAndLeftEquality() {
		String s1 = "p∧(p∨q)";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testAbsorptionOrLeftEquality() {
		String s1 = "p∨(p∧q)";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testAbsorptionAndRightEquality() {
		String s1 = "(p∨q)∧p";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testAbsorptionOrRightEquality() {
		String s1 = "(p∧q)∨p";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testAndTopEquality() {
		String s1 = "p∧┬";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	@Test
	public void testAndBottomEquality() {
		String s1 = "p∧⊥";
		String s2 = "⊥";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	@Test
	public void testOrTopEquality() {
		String s1 = "p∨┬";
		String s2 = "┬";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

	@Test
	public void testOrBottomEquality() {
		String s1 = "p∨⊥";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}
	
	// Test failures
	
	@Test
	public void testAbsorptionOrFailure() {
		String s1 = "(r∧q)∨p";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertFalse(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testAbsorptionAndFailure() {
		String s1 = "(p∨q)∧q";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertFalse(tt1.testEquivalence(tt2));
	}
	
	@Test
	public void testDifferentVariablesFailure() {
		String s1 = "p∨q";
		String s2 = "a∨b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertFalse(tt1.testEquivalence(tt2));
	}
	
	// Test new truth tables
	
	@Test
	public void testDifferentVariables() {
		String s1 = "a∨¬a";
		String s2 = "b∨¬b";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		assertTrue(tt1.testEquivalence(tt2));
	}

}
