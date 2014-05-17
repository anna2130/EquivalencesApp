package tests;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeManipulation.RuleEngine;
import treeManipulation.RuleSelector;

public class RuleSelection {

	private Compiler compiler;
	private RuleEngine re;
	private RuleSelector rs;
	int noRules;
	
	@Before 
	public void method() {
		compiler = new Compiler();
		re = new RuleEngine();
		rs = re.getRuleSelector();
		noRules = rs.getNoRules();
	}

	
	// TODO: RuleSelector tests
	
	/* And tests
	 * Equivalences involving ^
	 * 0.  a^b 			|- 	b^a				-- Commutativity
	 * 1.  a^a			|-  a 				-- Idempotence
	 * 2.  a^┬			|- 	a
	 * 3.  ┬^a			|-  a
	 * 4.  ⊥^a			|-  ⊥
	 * 5.  a^⊥			|-  ⊥
	 * 6.  a^¬a			|-  ⊥
	 * 7.  ¬a^a			|-  ⊥				
	 * 8.  a^(b^c)		|-  (a^b)^c			-- Associativity
	 * 9.  (a^b)^c  	|- 	a^(b^c)			-- Associativity
	 * 10. a^¬b 		|-	¬(a→b)
	 * 11. (a→b)^(b→a)	|-  a↔b
	 * 12. ¬a^¬b		|-	¬(avb)			-- De Morgan laws
	 * 13. a^(bvc) 		|- 	(a^b)v(a^c)		-- Distributitivity
	 * 14. (avb)^c		|-  (a^c)v(b^c)		-- Distributitivity
	 * 15. (avb)^(avc)	|- 	av(b^c)			-- Distributitivity
	 * 16. (avc)^(bvc)	|-	(a^b)vc			-- Distributitivity
	 * 17. a^(avb)  	|-	a				-- Absoption
	 * 18. (avb)^a  	|-	a				-- Absoption
	 */
	
	@Test
	public void testAndSelector() {
		FormationTree tree = compiler.compile("a^(r^s)");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(8);
		assertEquals("a^(r^s)", bs, expected);
	}
	
	@Test
	public void testAndSelector1() {
		FormationTree tree = compiler.compile("(rvs)^(rvs)");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(1);
		expected.set(13);
		expected.set(14);
		expected.set(15);
		expected.set(16);
		assertEquals("(rvs)^(rvs)", bs, expected);
	}
	
	@Test
	public void testAndSelector2() {
		FormationTree tree = compiler.compile("q→(r^(p^q))");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(1, 1));
		BitSet expected = new BitSet(8);
		expected.set(0);
		expected.set(8);
		assertEquals("q→(r^(p^q))", bs, expected);
	}
	
	/* TODO: Equivalences involving v
	 * 19. avb				|- 	bva				-- Commutativity
	 * 20. ava				|- 	a 				-- Idempotence
	 * 21. ┬va				|-  ┬
	 * 22. av┬				|-  ┬
	 * 23. av¬a				|-  ┬
	 * 24. ¬ava				|-  ┬
	 * 25. av⊥				|-  a
	 * 26. ⊥va				|-  a
	 * 27. av(bvc)			|-  (avb)vc			-- Associativity
	 * 28. (avb)vc  		|- 	av(bvc)			-- Associativity
	 * 29. (a^b)v(¬a^¬b)	|-	a↔b
	 * 30. (a^¬b)v(¬a^b) 	|-  ¬(a↔b)
	 * 31. ¬av¬b			|-  ¬(a^b)			-- De Morgan laws
	 * 32. av(b^c)			|- 	(avb)^(avc)		-- Distributitivity
	 * 33. (a^b)vc			|-	(avc)^(bvc)		-- Distributitivity
	 * 34. (a^b)v(a^c) 		|- 	a^(bvc)		-- Distributitivity
	 * 35. (a^c)v(b^c)		|-  (avb)^c		-- Distributitivity
	 * 36. av(a^b)			|-  a				-- Absoption
	 * 37. (a^b)va			|-  a				-- Absoption
	 * 38. ¬avb				|- 	a→b
	 */
	
	@Test
	public void testOrSelector() {
		FormationTree tree = compiler.compile("av(rvs)");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(19);
		expected.set(27);
		assertEquals("av(rvs)", bs, expected);
	}
	
	@Test
	public void testOrSelector1() {
		FormationTree tree = compiler.compile("(r^s)v(r^s)");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(19);
		expected.set(20);
		expected.set(32);
		expected.set(33);
		expected.set(34);
		expected.set(35);
		assertEquals("(r^s)v(r^s)", bs, expected);
	}
	
	@Test
	public void testOrSelector2() {
		FormationTree tree = compiler.compile("q→(rv(pvq))");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(1, 1));
		BitSet expected = new BitSet(noRules);
		expected.set(19);
		expected.set(27);
		assertEquals("q→(rv(pvq))", bs, expected);
	}
	
	/* TODO: Equivalences involving ¬
	 * 39. ¬┬		|-  ⊥
	 * 40. ¬⊥		|-  ┬
	 * 41. ¬¬a 		|- 	a
	 * 42. ¬a		|-  a→⊥
	 * 43. ¬(a→b) 	|-	a^¬b
	 * 44. ¬(av¬b)	|- 	a→b
	 * 45. ¬(a↔b) 	|- 	a↔¬b
	 * 46. ¬(a↔b) 	|-  ¬a↔b
	 * 47. ¬(a↔b) 	|-  (a^¬b)v(¬a^b)	-- Exclusive or of A and b
	 * 48. ¬(a^b)	|-  ¬av¬b			-- De Morgan laws
	 * 49. ¬(avb)	|-	¬a^¬b			-- De Morgan laws
	 */
	
	@Test
	public void testNotTop() {
		FormationTree tree = compiler.compile("¬┬");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(39);
		expected.set(42);
		assertEquals("", bs, expected);
	}
	
	@Test
	public void testNotBottom() {
		FormationTree tree = compiler.compile("¬⊥");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(40);
		expected.set(42);
		assertEquals("", bs, expected);
	}
	
	@Test
	public void testNot() {
		FormationTree tree = compiler.compile("¬a");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(42);
		assertEquals("", bs, expected);
	}
	
	@Test
	public void testNotNot() {
		FormationTree tree = compiler.compile("¬¬a");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(41);
		expected.set(42);
		assertEquals("", bs, expected);
	}
	
	@Test
	public void testNotImplies() {
		FormationTree tree = compiler.compile("¬(a→b)");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(42);
		expected.set(43);
		assertEquals("", bs, expected);
	}
	
	@Test
	public void testNotOrNot() {
		FormationTree tree = compiler.compile("¬(av¬b)");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(42);
		expected.set(44);
		expected.set(49);
		assertEquals("", bs, expected);
	}
	
	@Test
	public void testNotIff() {
		FormationTree tree = compiler.compile("¬(a↔b)");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(42);
		expected.set(45);
		expected.set(46);
		expected.set(47);
		assertEquals("", bs, expected);
	}
	
	@Test
	public void testNotAnd() {
		FormationTree tree = compiler.compile("¬(a^b)");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(42);
		expected.set(48);
		assertEquals("", bs, expected);
	}

	@Test
	public void testNotOr() {
		FormationTree tree = compiler.compile("¬(avb)");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(42);
		expected.set(49);
		assertEquals("", bs, expected);
	}
	
	/* TODO: Equivalences involving →
	 * 50. a→a		|- 	┬
	 * 51. ┬→a		|- 	a
	 * 52. a→┬		|- 	┬
	 * 53. ⊥→a		|- 	┬
	 * 54. a→⊥		|- 	¬a
	 * 55. a→b 		|- 	¬avb
	 * 56. a→b 		|- 	¬(av¬b)
	 */

	@Test
	public void testImplies() {
		FormationTree tree = compiler.compile("a→a");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(50);
		expected.set(55);
		expected.set(56);
		assertEquals("", bs, expected);
	}

	@Test
	public void testTopImplies() {
		FormationTree tree = compiler.compile("┬→a");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(51);
		expected.set(55);
		expected.set(56);
		assertEquals("", bs, expected);
	}

	@Test
	public void testImpliesTop() {
		FormationTree tree = compiler.compile("a→┬");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(52);
		expected.set(55);
		expected.set(56);
		assertEquals("", bs, expected);
	}

	@Test
	public void testBottomImplies() {
		FormationTree tree = compiler.compile("⊥→a");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(53);
		expected.set(55);
		expected.set(56);
		assertEquals("", bs, expected);
	}

	@Test
	public void testImpliesBottom() {
		FormationTree tree = compiler.compile("a→⊥");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(54);
		expected.set(55);
		expected.set(56);
		assertEquals("", bs, expected);
	}
	
	/* TODO: Equivalences involving ↔
	 * 57. a↔b		|-  (a→b)^(b→a)
	 * 58. a↔b		|-	(a^b)v(¬a^¬b)
	 * 59. a↔¬b 	|- 	¬(a↔b)
	 * 60. ¬a↔b 	|-  ¬(a↔b)
	 */

	@Test
	public void testIff() {
		FormationTree tree = compiler.compile("a↔b");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(57);
		expected.set(58);
		assertEquals("", bs, expected);
	}

	@Test
	public void testIffNot() {
		FormationTree tree = compiler.compile("a↔¬b");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(57);
		expected.set(58);
		expected.set(59);
		assertEquals("", bs, expected);
	}

	@Test
	public void testNotIff2() {
		FormationTree tree = compiler.compile("¬a↔b");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(57);
		expected.set(58);
		expected.set(60);
		assertEquals("", bs, expected);
	}
	
	/* TODO: Equivalences involving atoms
	 * 61. a		|-  a^a
	 * 62. a		|-  a^┬
	 * 63. a		|-  ava
	 * 64. a		|-  av⊥
	 * 65. a		|-  ¬¬a
	 * 66. a		|-  ┬→a
	 * 67. ⊥		|-  ¬┬
	 * 68. ┬		|-  ¬⊥
	 * 
	 *  TODO: Equivalences involving user input
	 * 69. ⊥		|-  a^⊥
	 * 70. ⊥		|-  a^¬a
	 * 71. ┬		|-  av┬
	 * 72. ┬		|-  av¬a
	 * 73. ┬		|- 	a→a	
	 * 74. ┬		|- 	a→┬
	 * 75. ┬		|- 	⊥→a
	 * 76. a		|-  av(a^b)
	 * 77. a  		|-	a^(avb)
	 */

	@Test
	public void testAtom() {
		FormationTree tree = compiler.compile("a");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(61);
		expected.set(62);
		expected.set(63);
		expected.set(64);
		expected.set(65);
		expected.set(66);
		expected.set(76);
		expected.set(77);
		assertEquals("", bs, expected);
	}

	@Test
	public void testBottom() {
		FormationTree tree = compiler.compile("⊥");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(61);
		expected.set(62);
		expected.set(63);
		expected.set(64);
		expected.set(65);
		expected.set(66);
		expected.set(67);
		expected.set(69);
		expected.set(70);
		expected.set(76);
		expected.set(77);
		assertEquals("", bs, expected);
	}

	@Test
	public void testTop() {
		FormationTree tree = compiler.compile("┬");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(61);
		expected.set(62);
		expected.set(63);
		expected.set(64);
		expected.set(65);
		expected.set(66);
		expected.set(68);
		expected.set(71);
		expected.set(72);
		expected.set(73);
		expected.set(74);
		expected.set(75);
		expected.set(76);
		expected.set(77);
		assertEquals("", bs, expected);
	}
	

	
	
}
