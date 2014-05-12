package tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeManipulation.TruthTable;

public class TruthTables {

	private Compiler compiler;
	private TruthTable tt;
	
	@Before 
	public void method() {
		compiler = new Compiler();
		tt = new TruthTable();
	}

	// Test commonly used logical operators
	
	@Test
	public void testAtom() {
		String s = "p";
		FormationTree tree = compiler.compile(s);
		ArrayList<Integer> list = tt.getTruthValues(tree);
		assertEquals("", "[1, 0]", list.toString());
	}

	@Test
	public void testNegation() {
		String s = "¬p";
		FormationTree tree = compiler.compile(s);
		ArrayList<Integer> list = tt.getTruthValues(tree);
		assertEquals("", "[0, 1]", list.toString());
	}

	@Test
	public void testAnd() {
		String s = "p^q";
		FormationTree tree = compiler.compile(s);
		ArrayList<Integer> list = tt.getTruthValues(tree);
		assertEquals("", "[1, 0, 0, 0]", list.toString());
	}

	@Test
	public void testOr() {
		String s = "pvq";
		FormationTree tree = compiler.compile(s);
		ArrayList<Integer> list = tt.getTruthValues(tree);
		assertEquals("", "[1, 1, 1, 0]", list.toString());
	}

	@Test
	public void testImplies() {
		String s = "p→q";
		FormationTree tree = compiler.compile(s);
		ArrayList<Integer> list = tt.getTruthValues(tree);
		assertEquals("", "[1, 1, 0, 1]", list.toString());
	}

	@Test
	public void testIff() {
		String s = "p↔q";
		FormationTree tree = compiler.compile(s);
		ArrayList<Integer> list = tt.getTruthValues(tree);
		assertEquals("", "[1, 0, 0, 1]", list.toString());
	}

	@Test
	public void testAndOr() {
		String s = "p^(pvq)";
		FormationTree tree = compiler.compile(s);
		System.out.println(tree.getVariables());
		ArrayList<Integer> list = tt.getTruthValues(tree);
		System.out.println(list.toString());
		assertEquals("", "[1, 0, 1, 0]", list.toString());
	}

	// Test comparisons for equality

	/* 0.  Commutativity of ^
	 * 1.  Idempotence of ^ 
	 * 2.  Left Associativity of ^
	 * 3.  Right Associativity of ^
	 * 4.  Commutativity of v
	 * 5.  Idempotence of v
	 * 6.  Left Associativity of v
	 * 7.  Right Associativity of v
	 */
	
	@Test
	public void testAndCommutativityEquality() {
		String s1 = "p^q";
		String s2 = "q^p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testAndIdempotenceEquality() {
		String s1 = "p^p";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testAndLeftAssociativityEquality() {
		String s1 = "p^(q^r)";
		String s2 = "(p^q)^r";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testAndRightAssociativityEquality() {
		String s1 = "(p^q)^r";
		String s2 = "p^(q^r)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testOrCommutativityEquality() {
		String s1 = "pvq";
		String s2 = "qvp";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testOrIdempotenceEquality() {
		String s1 = "pvp";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testOrLeftAssociativityEquality() {
		String s1 = "pv(qvr)";
		String s2 = "(pvq)vr";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testOrRightAssociativityEquality() {
		String s1 = "(pvq)vr";
		String s2 = "pv(qvr)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}

	/* 8.  ¬¬A 		v- 	A
	 * 9.  A→B 	v- 	¬AvB 			-- Also equivalent to ¬(A^¬B). Separate rule?
	 * 10. ¬(A→B) 	v-	A^¬B
	 */
	
	@Test
	public void testNotNotEquality() {
		String s1 = "¬¬p";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testImpliesToOrEquality() {
		String s1 = "p→q";
		String s2 = "¬pvq";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testNotImpliesToOrEquality() {
		String s1 = "¬(p→q)";
		String s2 = "p^¬q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	/* 11. A↔B	v-  (A→B)^(B→A)
	 * 12. A↔B	v-	(A^B)v(¬A^¬B)
	 */
	
	@Test
	public void testIffToAndEquality() {
		String s1 = "p↔q";
		String s2 = "(p→q)^(q→p)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testIffToOrEquality() {
		String s1 = "p↔q";
		String s2 = "(p^q)v(¬p^¬q)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	

	/* 13. ¬(A↔B) v- 	A↔¬B
	 * 14. ¬(A↔B) v-  ¬A↔B
	 * 15. ¬(A↔B) v-  (A^¬B)v(¬A^B)	-- Exclusive or of A and B
	 */
	
	@Test
	public void testNotIffToNotBEquality() {
		String s1 = "¬(p↔q)";
		String s2 = "p↔¬q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testNotIffToOrEquality() {
		String s1 = "¬(p↔q)";
		String s2 = "¬p↔q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testNotIffToNotAEquality() {
		String s1 = "¬(p↔q)";
		String s2 = "(p^¬q)v(¬p^q)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	/* 16. ¬(A^B)	v-  ¬Av¬B			-- De Morgan laws
	 * 17. ¬(AvB)	v-	¬A^¬B
	 */
	
	@Test
	public void testDeMorganAndEquality() {
		String s1 = "¬(p^q)";
		String s2 = "¬pv¬q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testDeMorganOrEquality() {
		String s1 = "¬(pvq)";
		String s2 = "¬p^¬q";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	/* 18. A^(BvC) 	v- 	(A^B)v(A^C)		-- Distributitivity
	 * 19. (AvB)^C	v-  (A^C)v(B^C)
	 * 20. Av(B^C)	v- 	(AvB)^(AvC)
	 * 21. (A^B)vC	v-	(AvC)^(BvC)
	 */
	
	@Test
	public void testDistributivityAndLeftEquality() {
		String s1 = "p^(qvr)";
		String s2 = "(p^q)v(p^r)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testDistributivityAndRightEquality() {
		String s1 = "(pvq)^r";
		String s2 = "(p^r)v(q^r)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}

	
	@Test
	public void testDistributivityOrLeftEquality() {
		String s1 = "pv(q^r)";
		String s2 = "(pvq)^(pvr)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	@Test
	public void testDistributivityOrRightEquality() {
		String s1 = "(p^q)vr";
		String s2 = "(pvr)^(qvr)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		ArrayList<Integer> list1 = tt.getTruthValues(tree1);
		ArrayList<Integer> list2 = tt.getTruthValues(tree2);
		assertEquals("", list1, list2);
	}
	
	/* 22. A^(AvB)  v-	A				-- Absorption
	 * 23. Av(A^B)	v-  A
	 * 24. (AvB)^A  v-	A
	 * 25. (A^B)vA	v-  A
	 */
	
	@Test
	public void testAbsorptionAndLeftEquality() {
		String s1 = "p^(pvq)";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		assertEquals(tt.testEquivalence(tree1, tree2), true);
	}
	
	@Test
	public void testAbsorptionOrLeftEquality() {
		String s1 = "pv(p^q)";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		assertEquals(tt.testEquivalence(tree1, tree2), true);
	}
	
	@Test
	public void testAbsorptionAndRightEquality() {
		String s1 = "(pvq)^p";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		assertEquals(tt.testEquivalence(tree1, tree2), true);
	}
	
	@Test
	public void testAbsorptionOrRightEquality() {
		String s1 = "(p^q)vp";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		assertEquals(tt.testEquivalence(tree1, tree2), true);
	}
	
	// Test failures
	
	@Test
	public void testAbsorptionOrFailure() {
		String s1 = "(r^q)vp";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		assertEquals(tt.testEquivalence(tree1, tree2), false);
	}
	
	@Test
	public void testAbsorptionAndFailure() {
		String s1 = "(pvq)^q";
		String s2 = "p";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		assertEquals(tt.testEquivalence(tree1, tree2), false);
	}

}
