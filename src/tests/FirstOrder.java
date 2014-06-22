package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.Atom;
import treeBuilder.BinaryOperator;
import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeBuilder.UnaryOperator;
import treeManipulation.RuleApplicator;
import treeManipulation.RuleEngine;
import treeManipulation.RuleSelector;

public class FirstOrder {

	private Compiler compiler;
	private RuleEngine re;
	private RuleSelector rs;
	private RuleApplicator ra;
	int noRules;

	private ArrayList<String> variables;
	private ArrayList<String> predicates;
	private ArrayList<String> unaryOps;

	private int n;

	@Before 
	public void method() {
		compiler = new Compiler(true);
		re = new RuleEngine(true);
		rs = re.getRuleSelector();
		ra = re.getRuleApplicator();
		noRules = rs.getNoRules();

		n = 100;

		variables = new ArrayList<String>(Arrays.asList("u", "v", "w", "x", "y", "z"));
		predicates = new ArrayList<String>(Arrays.asList("P", "Q", "R"));
		unaryOps = new ArrayList<String>(Arrays.asList("¬", "∀", "∃"));
	}

	// Parser tests

	@Test
	public void testParserBrackets() {
		FormationTree tree = compiler.compile("(∀x[Px])");
		assertEquals(tree.toTreeString(), "0-0: ∀x (0-1: Px)");
	}

	@Test
	public void testParserAll() {
		FormationTree tree = compiler.compile("∀x[Px]");
		assertEquals(tree.toTreeString(), "0-0: ∀x (0-1: Px)");
	}

	@Test
	public void testParserAllExistsNoBrackets() {
		FormationTree tree = compiler.compile("∀x∃y[Pxy]");
		assertEquals(tree.toTreeString(), "0-0: ∀x (0-1: ∃y (0-2: Pxy))");
	}

	@Test
	public void testParserAllExistsBrackets() {
		FormationTree tree = compiler.compile("∀x[∃y[Pxy]]");
		assertEquals(tree.toTreeString(), "0-0: ∀x (0-1: ∃y (0-2: Pxy))");
	}

	@Test
	public void testParserAllAnd() {
		FormationTree tree = compiler.compile("∀x[Pxy∧Qx]");
		assertEquals(tree.toTreeString(), "0-0: ∀x (0-1: ∧ (0-2: Pxy, 1-2: Qx))");
	}

	@Test
	public void testParserAllOr() {
		FormationTree tree = compiler.compile("∀x[Pxy∨Qx]");
		assertEquals(tree.toTreeString(), "0-0: ∀x (0-1: ∨ (0-2: Pxy, 1-2: Qx))");
	}

	@Test
	public void testParserAllImplies() {
		FormationTree tree = compiler.compile("∀x[Pxy→Qx]");
		assertEquals(tree.toTreeString(), "0-0: ∀x (0-1: → (0-2: Pxy, 1-2: Qx))");
	}

	@Test
	public void testParserAllIff() {
		FormationTree tree = compiler.compile("∀x[Pxy↔Qx]");
		assertEquals(tree.toTreeString(), "0-0: ∀x (0-1: ↔ (0-2: Pxy, 1-2: Qx))");
	}

	@Test
	public void testParserAllNot() {
		FormationTree tree = compiler.compile("∀x[¬Pxy]");
		assertEquals(tree.toTreeString(), "0-0: ∀x (0-1: ¬ (0-2: Pxy))");
	}

	@Test
	public void testParserExists() {
		FormationTree tree = compiler.compile("∃x[Pxy]");
		assertEquals(tree.toTreeString(), "0-0: ∃x (0-1: Pxy)");
	}

	@Test
	public void testParserNotAll() {
		FormationTree tree = compiler.compile("¬∀x[Pxy]");
		assertEquals(tree.toTreeString(), "0-0: ¬ (0-1: ∀x (0-2: Pxy))");
	}

	@Test
	public void testParserNotExists() {
		FormationTree tree = compiler.compile("¬∃x[Pxy]");
		assertEquals(tree.toTreeString(), "0-0: ¬ (0-1: ∃x (0-2: Pxy))");
	}

	// Rule Selector tests
	// ∀ | ∃ | ∧ | ∨ | ┬ | ⊥ | ¬ | → | ↔

	/* First order - e and f represent any wff
	 * 			   - t represents a wff with no free occurrences of x 
	 * 
	 * Equivalences involving ∀
	 * 78. ∀x[¬e]   |- 	¬∃x[e]
	 * 79. ∀x∀y[e]	|-	∀y∀x[e]
	 * 80. ∀x[e∧f] 	|- 	∀x[e]∧∀x[f]
	 * 81. ∀x[e∨t] 	|- 	∀x[e]∨t
	 * 82. ∀x[e→t]  |-  ∃x[e]→t
	 * 83. ∀x[t→e]	|-  t→∀x[e]
	 * 84: ∀x[t]	|-  t
	 */

	@Test
	public void testSelectorAllNot() {
		FormationTree tree = compiler.compile("∀x[¬Pxy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(78);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAllAll() {
		FormationTree tree = compiler.compile("∀x∀y[Pxy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(79);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAllAnd() {
		FormationTree tree = compiler.compile("∀x[Pxy∧Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(80);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAllOr() {
		FormationTree tree = compiler.compile("∀x[Pxy∨Qy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(81);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAllImpliesLeft() {
		FormationTree tree = compiler.compile("∀x[Pxy→Qy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(82);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAllImpliesRight() {
		FormationTree tree = compiler.compile("∀x[Py→Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(83);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAll() {
		FormationTree tree = compiler.compile("∀x[Py]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(84);
		expected.set(102);
		assertEquals(bs, expected);
	}

	// Test false
	@Test
	public void testSelectorAllOrFalse() {
		FormationTree tree = compiler.compile("∀x[Pxy∨Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAllImpliesLeftFalse() {
		FormationTree tree = compiler.compile("∀x[Pxy→Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAllImpliesRightFalse() {
		FormationTree tree = compiler.compile("∀x[Px→Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAllFalse() {
		FormationTree tree = compiler.compile("∀x[Px]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExistsNot() {
		FormationTree tree = compiler.compile("∃x[¬Pxy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(86);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExistsExists() {
		FormationTree tree = compiler.compile("∃x∃y[Pxy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(87);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExistsOr() {
		FormationTree tree = compiler.compile("∃x[Pxy∨Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(88);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExistsAnd() {
		FormationTree tree = compiler.compile("∃x[Pxy∧Qy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(89);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExistsImpliesLeft() {
		FormationTree tree = compiler.compile("∃x[Pxy→Qy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(90);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExistsImpliesRight() {
		FormationTree tree = compiler.compile("∃x[Py→Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(91);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExists() {
		FormationTree tree = compiler.compile("∃x[Py]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(92);
		expected.set(103);
		assertEquals(bs, expected);
	}

	/* 106. ¬a∧b 		|-	¬(a→b)
	 * 107. (¬a∧¬b)v(a∧b)	|-	a↔b
	 * 108. (¬a∧b)v(a∧¬b) 	|-  ¬(a↔b)
	 * 109. ∀x[t∨e] 	|- 	t∨∀x[e]
	 * 110: ∃x[t∧e]		|-  t∧∃x[e]
	 * 111: t∧∃x[e]		|-  ∃x[t∧e]
	 * 112: t∨∀x[e]		|- 	∀x[t∨e]
	 */

	@Test
	public void testSelectorCommuatativity1() {
		FormationTree tree = compiler.compile("∀x[Qy∨Pxy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(109);
		expected.set(102);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorCommuatativity2() {
		FormationTree tree = compiler.compile("∃x[Qy∧Pxy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(110);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorCommuatativity3() {
		FormationTree tree = compiler.compile("Qy∧∃x[Pxy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(111);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorCommuatativity4() {
		FormationTree tree = compiler.compile("Qy∨∀x[Pxy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(19);
		expected.set(112);
		assertEquals(bs, expected);
	}

	// Test false
	@Test
	public void testSelectorExistsAndFalse() {
		FormationTree tree = compiler.compile("∃x[Pxy∧Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExistsImpliesLeftFalse() {
		FormationTree tree = compiler.compile("∃x[Pxy→Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExistsImpliesRightFalse() {
		FormationTree tree = compiler.compile("∃x[Px→Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(103);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorExistsFalse() {
		FormationTree tree = compiler.compile("∃x[Px]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(103);
		assertEquals(bs, expected);
	}

	/* Equivalences involving ¬
	 * 93: ¬∀x[e]	|- 	∃x[¬e]
	 * 94: ¬∃x[e]	|-	∀x[¬e]
	 */ 

	// 42. ¬a		|-  a→⊥
	@Test
	public void testSelectorNotAll() {
		FormationTree tree = compiler.compile("¬∀x[Px]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(42);
		expected.set(93);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorNotExists() {
		FormationTree tree = compiler.compile("¬∃x[Px]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(42);
		expected.set(94);
		assertEquals(bs, expected);
	}

	/* Equivalences involving ∧
	 * 95: ∀x[e]∧∀x[f] 	|-	∀x[e∧f]
	 * 96: ∃x[e]∧t		|-  ∃x[e∧t]
	 */ 

	@Test
	public void testSelectorAndAll() {
		FormationTree tree = compiler.compile("∀x[Pxy]∧∀x[Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(95);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAndExists() {
		FormationTree tree = compiler.compile("∃x[Pxy]∧Qy");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		expected.set(96);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorAndExistsFalse() {
		FormationTree tree = compiler.compile("∃x[Pxy]∧Qx");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(0);
		assertEquals(bs, expected);
	}

	/* Equivalences involving ∨
	 * 97: ∀x[e]∨t		|- 	∀x[e∨t]
	 * 98: ∃x[e]∨∃x[f]	|- 	∃x[e∨f]
	 */ 

	@Test
	public void testSelectorOrAll() {
		FormationTree tree = compiler.compile("∀x[Pxy]∨Qy");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(19);
		expected.set(97);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorOrExists() {
		FormationTree tree = compiler.compile("∃x[Pxy]∨∃x[Qx]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(19);
		expected.set(98);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorOrAndFalse() {
		FormationTree tree = compiler.compile("∀x[Pxy]∨Qx");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(19);
		assertEquals(bs, expected);
	}

	/* Equivalences involving →
	 * 99: ∃x[e]→t 	|- 	∀x[e→t]
	 * 100: t→∀x[e]	|- 	∀x[t→e]
	 * 101: ∃x[e]→t |- 	∃x[e→t]
	 */ 

	@Test
	public void testSelectorImpliesExists() {
		FormationTree tree = compiler.compile("∃x[Pxy]→Qy");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(55);
		expected.set(56);
		expected.set(99);
		expected.set(101);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorImpliesAll() {
		FormationTree tree = compiler.compile("Qy→∀x[Pxy]");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(55);
		expected.set(56);
		expected.set(100);
		assertEquals(bs, expected);
	}

	@Test
	public void testSelectorImpliesExistsFalse() {
		FormationTree tree = compiler.compile("∃x[Pxy]→Qx");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(55);
		expected.set(56);
		assertEquals(bs, expected);
	}

	/* Equivalences involving user input
	 * 102: ∀x[e]	|-  ∀y[e{x->y}]
	 * 103: ∃x[e]	|-  ∃y[e{x->y}]
	 * 104: t		|- 	∀x[t]
	 * 105: t		|- 	∃x[t]
	 */

	@Test
	public void testSelectorAtom() {
		FormationTree tree = compiler.compile("Pxy");
		BitSet bs = re.getApplicableRules(tree, tree.findNode(0, 0));
		BitSet expected = new BitSet(noRules);
		expected.set(61, 67);
		expected.set(76, 78);
		expected.set(104);
		expected.set(105);
		assertEquals(bs, expected);
	}

	// Rule Applicator tests

	/* 
	 * Equivalences involving ∀
	 * 78. ∀x[¬e]   |- 	¬∃x[e]
	 * 79. ∀x∀y[e]	|-	∀y∀x[e]
	 * 80. ∀x[e∧f] 	|- 	∀x[e]∧∀x[f]
	 * 81. ∀x[e∨t] 	|- 	∀x[e]∨t
	 * 82. ∀x[e→t]  |-  ∃x[e]→t
	 * 83. ∀x[t→e]	|-  t→∀x[e]
	 * 84: ∀x[t]	|-  t
	 */ 
	
	@Test
	public void testAllComplementation() {
		FormationTree tree = compiler.compile("∀x[¬Px]");
		ra.applyAllComplementation(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬∃x[Px]");
	}
	
	@Test
	public void testAllSwapQuantifiers() {
		FormationTree tree = compiler.compile("∀x∀y[Pxy]");
		ra.applyAllSwapQuantifiers(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∀y∀x[Pxy]");
	}
	
	@Test
	public void testAllDistributionOfQuantifiers() {
		FormationTree tree = compiler.compile("∀x[Px∧Qxy]");
		ra.applyAllDistrubutionOfQuantifiers(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∀x[Px]∧∀x[Qxy]");
	}
	
	@Test
	public void testAllDistributionOfQuantifiersWithNoFreeVariables() {
		FormationTree tree = compiler.compile("∀x[Pxy∨Qy]");
		ra.applyAllDistrubutionOfQuantifiersWithNoFreeVariables(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∀x[Pxy]∨Qy");
	}
	
	@Test
	public void testAllImpliesDistributionOfQuantifiersLeft() {
		FormationTree tree = compiler.compile("∀x[Pxy→Qy]");
		ra.applyImpliesDistributionOfQuantifiersLeft(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∃x[Pxy]→Qy");
	}
	
	@Test
	public void testAllImpliesDistributionOfQuantifiersRight() {
		FormationTree tree = compiler.compile("∀x[Qy→Pxy]");
		ra.applyAllImpliesDistributionOfQuantifiersRight(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "Qy→∀x[Pxy]");
	}
	
	@Test
	public void testAllSimplificationOfQuantifiers() {
		FormationTree tree = compiler.compile("∀x[Pxy]");
		ra.applySimplificationOfQuantifiers(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "Pxy");
	}
	
	/* Equivalences involving ∃
	 * 86: ∃x[¬e]  	|-	¬∀x[e]
	 * 87: ∃x∃y[e] 	|-  ∃y∃x[e]
	 * 88: ∃x[e∨f]	|-  ∃x[e]∨∃x[f]
	 * 89: ∃x[e∧t]	|-  ∃x[e]∧t
	 * 90: ∃x[e→t]  |-  ∃x[e]→t
	 * 91: ∃x[t→e]  |-  t→∃x[e]
	 * 92: ∃x[t]	|-  t
	 */
	
	@Test
	public void testExistsComplementation() {
		FormationTree tree = compiler.compile("∃x[¬Px]");
		ra.applyExistsComplementation(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "¬∀x[Px]");
	}
	
	@Test
	public void testExistsSwapQuantifiers() {
		FormationTree tree = compiler.compile("∃x∃y[Pxy]");
		ra.applyExistsSwapQuantifiers(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∃y∃x[Pxy]");
	}
	
	@Test
	public void testExistsDistributionOfQuantifiers() {
		FormationTree tree = compiler.compile("∃x[Px∨Qxy]");
		ra.applyExistsDistributionOfQuantifiers(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∃x[Px]∨∃x[Qxy]");
	}
	
	@Test
	public void testExistsDistributionOfQuantifiersWithNoFreeVariables() {
		FormationTree tree = compiler.compile("∃x[Pxy∧Qy]");
		ra.applyExistsDistributionOfQuantifiersWithNoFreeVariables(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∃x[Pxy]∧Qy");
	}
	
	@Test
	public void testExistsImpliesDistributionOfQuantifiersLeft() {
		FormationTree tree = compiler.compile("∃x[Pxy→Qy]");
		ra.applyImpliesDistributionOfQuantifiersLeft(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∃x[Pxy]→Qy");
	}
	
	@Test
	public void testExistsImpliesDistributionOfQuantifiersRight() {
		FormationTree tree = compiler.compile("∃x[Qy→Pxy]");
		ra.applyExistsImpliesDistributionOfQuantifiersRight(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "Qy→∃x[Pxy]");
	}
	
	@Test
	public void testExistsSimplificationOfQuantifiers() {
		FormationTree tree = compiler.compile("∃x[Pxy]");
		ra.applySimplificationOfQuantifiers(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "Pxy");
	}
	
	/* Equivalences involving ¬
	 * 93: ¬∀x[e]	|- 	∃x[¬e]
	 * 94: ¬∃x[e]	|-	∀x[¬e]
	 */
	
	@Test
	public void testNotAllComplementation() {
		FormationTree tree = compiler.compile("¬∀x[Px]");
		ra.applyAllComplementationBackwards(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∃x[¬Px]");
	} 
	
	@Test
	public void testNotExistsComplementation() {
		FormationTree tree = compiler.compile("¬∃x[Px]");
		ra.applyExistsComplementationBackwards(tree, (UnaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∀x[¬Px]");
	} 
	
	/* Equivalences involving ∧
	 * 95: ∀x[e]∧∀x[f] 	|-	∀x[e∧f]
	 * 96: ∃x[e]∧t		|-  ∃x[e∧t]
	 */
	
	@Test
	public void testAndAllDistributionOfQuantifiers() {
		FormationTree tree = compiler.compile("∀x[Px]∧∀x[Qx]");
		ra.applyAllDistributionOfQuantifiersBackwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∀x[Px∧Qx]");
	} 
	
	@Test
	public void testAndExistsDistribution() {
		FormationTree tree = compiler.compile("∃x[Px]∧Qy");
		ra.applyExistsDistributionOfQuantifiersWithNoFreeVariablesBackwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∃x[Px∧Qy]");
	} 
	
	/* Equivalences involving ∨
	 * 97: ∀x[e]∨t		|- 	∀x[e∨t]
	 * 98: ∃x[e]∨∃x[f]	|- 	∃x[e∨f]
	 */ 
	
	@Test
	public void testOrAllDistribution() {
		FormationTree tree = compiler.compile("∀x[Px]∨Qy");
		ra.applyAllDistributionOfQuantifiersWithNoFreeVariablesBackwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∀x[Px∨Qy]");
	} 
	
	@Test
	public void testOrExistsDistribution() {
		FormationTree tree = compiler.compile("∃x[Px]∨∃x[Qx]");
		ra.applyExistsDistributionOfQuantifiersBackwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∃x[Px∨Qx]");
	} 
	
	/* Equivalences involving →
	 * 99: ∃x[e]→t 	|- 	∀x[e→t]
	 * 100: t→∀x[e]	|- 	∀x[t→e]
	 * 101: ∃x[e]→t |- 	∃x[e→t]
	 */
	
	@Test
	public void testImpliesAllDistributionLeft() {
		FormationTree tree = compiler.compile("∃x[Px]→Qy");
		ra.applyAllImpliesDistributionBackwardsLeft(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∀x[Px→Qy]");
	} 
	
	@Test
	public void testImpliesAllDistributionRight() {
		FormationTree tree = compiler.compile("Qy→∀x[Px]");
		ra.applyAllImpliesDistributionBackwardsRight(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∀x[Qy→Px]");
	} 
	
	@Test
	public void testImpliesExistsDistribution() {
		FormationTree tree = compiler.compile("∃x[Px]→Qy");
		ra.applyExistsImpliesDistributionBackwards(tree, (BinaryOperator) tree.findNode(0, 0));
		assertEquals("", tree.toString(), "∃x[Px→Qy]");
	} 
	
	/* Equivalences involving user input
	 * 102: ∀x[e]	|-  ∀y[e{x->y}]
	 * 103: ∃x[e]	|-  ∃y[e{x->y}]
	 * 104: t		|- 	∀x[t]
	 * 105: t		|- 	∃x[t]
	 */
	
	@Test
	public void testAllReplacement() {
		FormationTree tree = compiler.compile("∀x[Px]");
		ra.applyRenameVariable(tree, (UnaryOperator) tree.findNode(0, 0), "y");
		assertEquals("", tree.toString(), "∀y[Py]");
	} 
	
	@Test
	public void testAllReplacement2() {
		FormationTree tree = compiler.compile("∀x[Pxy]");
		ra.applyRenameVariable(tree, (UnaryOperator) tree.findNode(0, 0), "z");
		assertEquals("", tree.toString(), "∀z[Pzy]");
	} 
	
	@Test
	public void testAllReplacement3() {
		FormationTree tree = compiler.compile("∀x[Puxy]");
		ra.applyRenameVariable(tree, (UnaryOperator) tree.findNode(0, 0), "z");
		assertEquals("", tree.toString(), "∀z[Puzy]");
	} 
	
	@Test
	public void testExistsReplacement() {
		FormationTree tree = compiler.compile("∃x[Px]");
		ra.applyRenameVariable(tree, (UnaryOperator) tree.findNode(0, 0), "y");
		assertEquals("", tree.toString(), "∃y[Py]");
	} 
	
	@Test
	public void testExistsReplacement2() {
		FormationTree tree = compiler.compile("∃x[Px∨Qzx]");
		ra.applyRenameVariable(tree, (UnaryOperator) tree.findNode(0, 0), "y");
		assertEquals("", tree.toString(), "∃y[Py∨Qzy]");
	} 
	
	@Test
	public void testExistsReplacement3() {
		FormationTree tree = compiler.compile("∃x[Px∨(Qzx→Px)]");
		ra.applyRenameVariable(tree, (UnaryOperator) tree.findNode(0, 0), "y");
		assertEquals("", tree.toString(), "∃y[Py∨(Qzy→Py)]");
	} 
	
	@Test
	public void testAtomAll() {
		FormationTree tree = compiler.compile("Px");
		ra.applyAddAll(tree, (Atom) tree.findNode(0, 0), "x");
		assertEquals("", tree.toString(), "∀x[Px]");
	} 
	
	@Test
	public void testAtomExists() {
		FormationTree tree = compiler.compile("Px");
		ra.applyAddExists(tree, (Atom) tree.findNode(0, 0), "x");
		assertEquals("", tree.toString(), "∃x[Px]");
	} 

	/* 106. ¬a∧b 		|-	¬(a→b)
	 * 107. (¬a∧¬b)v(a∧b)	|-	a↔b
	 * 108. (¬a∧b)v(a∧¬b) 	|-  ¬(a↔b)
	 * 109. ∀x[t∨e] 	|- 	t∨∀x[e]
	 * 110: ∃x[t∧e]		|-  t∧∃x[e]
	 * 111: t∧∃x[e]		|-  ∃x[t∧e]
	 * 112: t∨∀x[e]		|- 	∀x[t∨e]
	 */
	
	@Test
	public void testCommutativity1() {
		FormationTree tree = compiler.compile("∀x[Qy∨Pxy]");
		re.applyRuleFromBitSet(109, tree, tree.findNode(0, 0), null);
		assertEquals("", tree.toString(), "Qy∨∀x[Pxy]");
	} 
	
	@Test
	public void testCommutativity2() {
		FormationTree tree = compiler.compile("∃x[Qy∧Pxy]");
		re.applyRuleFromBitSet(110, tree, tree.findNode(0, 0), null);
		assertEquals("", tree.toString(), "Qy∧∃x[Pxy]");
	} 
	
	@Test
	public void testCommutativity3() {
		FormationTree tree = compiler.compile("Qy∧∃x[Pxy]");
		re.applyRuleFromBitSet(111, tree, tree.findNode(0, 0), null);
		assertEquals("", tree.toString(), "∃x[Qy∧Pxy]");
	} 
	
	@Test
	public void testCommutativity4() {
		FormationTree tree = compiler.compile("Qy∨∀x[Pxy]");
		re.applyRuleFromBitSet(112, tree, tree.findNode(0, 0), null);
		assertEquals("", tree.toString(), "∀x[Qy∨Pxy]");
	} 
	
	// Equivalence Generation tests

	@Test
	public void testGenerateQuantifiers() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			HashSet<String> set = new HashSet<String>();
			String s = compiler.generateQuantifiers(variables, set);
			b = b && unaryOps.contains(s.charAt(0) + "") && variables.contains(s.charAt(1) + "");
		}
		assertTrue(b);
	}

	@Test
	public void testGeneratePredicate1() {
		boolean b = true;
		ArrayList<String> vars = new ArrayList<String>();
		vars.add(variables.get(0));
		for (int i = 0; i < n; ++i) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			String s = compiler.getPredicateVariable(vars, 0, map);
			b = b && predicates.contains(s.charAt(0) + "") && vars.contains(s.charAt(1) + "");
		}
		assertTrue(b);
	}

	@Test
	public void testGeneratePredicate() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			String s = compiler.getPredicateVariable(variables, 0, map);
			b = b && predicates.contains(s.charAt(0) + "") && variables.contains(s.charAt(1) + "");
		}
		assertTrue(b);
	}
}
