package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;

public class Parser {

	Compiler compiler;
	
	@Before 
	public void method() {
		compiler = new Compiler(false);
	}
	
	// Test operators and indexes
	
	@Test
	public void testBrackets() {
		FormationTree tree = compiler.compile("(a)");
		assertEquals("(a): ", tree.toTreeString(), "0-0: a");
	}
	
	@Test
	public void testNot() {
		FormationTree tree = compiler.compile("¬a");
		assertEquals("¬a: ", tree.toTreeString(), "0-0: ¬ (0-1: a)");
	}
	
	@Test
	public void testNotLeft() {
		FormationTree tree = compiler.compile("(¬a)∧b");
		assertEquals("¬a: ", tree.toTreeString(), "0-0: ∧ (0-1: ¬ (0-2: a), 1-1: b)");
	}
	
	@Test
	public void testNotRight() {
		FormationTree tree = compiler.compile("b∧(¬a)");
		assertEquals("¬a: ", tree.toTreeString(), "0-0: ∧ (0-1: b, 1-1: ¬ (2-2: a))");
	}
	
	@Test
	public void testNotNot() {
		FormationTree tree = compiler.compile("¬¬a");
		assertEquals("¬¬a: ", tree.toTreeString(), "0-0: ¬ (0-1: ¬ (0-2: a))");
	}
	
	@Test
	public void testAnd() {
		FormationTree tree = compiler.compile("a∧b");
		assertEquals("a∧b: ", tree.toTreeString(), "0-0: ∧ (0-1: a, 1-1: b)");
	}
	
	@Test
	public void testAndLeft() {
		FormationTree tree = compiler.compile("(a∧b)∧c");
		assertEquals("a∧b: ", tree.toTreeString(), "0-0: ∧ (0-1: ∧ (0-2: a, 1-2: b), 1-1: c)");
	}
	
	@Test
	public void testAndRight() {
		FormationTree tree = compiler.compile("c∧(a∧b)");
		assertEquals("a∧b: ", tree.toTreeString(), "0-0: ∧ (0-1: c, 1-1: ∧ (2-2: a, 3-2: b))");
	}
	
	@Test
	public void testOr() {
		FormationTree tree = compiler.compile("a∨b");
		assertEquals("a∨b: ", tree.toTreeString(), "0-0: ∨ (0-1: a, 1-1: b)");
	}
	
	@Test
	public void testImplies() {
		FormationTree tree = compiler.compile("a→b");
		assertEquals("a→b: ", tree.toTreeString(), "0-0: → (0-1: a, 1-1: b)");
	}
	
	@Test
	public void testIff() {
		FormationTree tree = compiler.compile("a↔b");
		assertEquals("a↔b: ", tree.toTreeString(), "0-0: ↔ (0-1: a, 1-1: b)");
	}
	
	@Test
	public void testAtom() {
		FormationTree tree = compiler.compile("a");
		assertEquals("a: ", tree.toTreeString(), "0-0: a");
	}
	
	//Testing priority/order of operations
	
	@Test
	public void testAndImplies() {
		FormationTree tree = compiler.compile("a∧b→a");
		assertEquals("a∧b→a: ", tree.toTreeString(), "0-0: → (0-1: ∧ (0-2: a, 1-2: b), 1-1: a)");
	}

	@Test
	public void testImpliesAnd() {
		FormationTree tree = compiler.compile("a→a∧b");
		assertEquals("a→a∧b: ", tree.toTreeString(), "0-0: → (0-1: a, 1-1: ∧ (2-2: a, 3-2: b))");
	}

	@Test
	public void testNotImplies() {
		FormationTree tree = compiler.compile("¬a→b");
		assertEquals("¬a→b: ", tree.toTreeString(), "0-0: → (0-1: ¬ (0-2: a), 1-1: b)");
	}
	
	@Test
	public void testNotBrackets() {
		FormationTree tree = compiler.compile("¬(a∧b)");
		assertEquals("¬(a∧b): ", tree.toTreeString(), "0-0: ¬ (0-1: ∧ (0-2: a, 1-2: b))");
	}
	
	// Complex tests
	
	@Test
	public void testComplexLeft() {
		FormationTree tree = compiler.compile("(¬a∨(b∧c))∧d");
		assertEquals("(¬p∨(s∧t))∧q: ", tree.toTreeString(), "0-0: ∧ (0-1: ∨ (0-2: ¬ (0-3: a), 1-2: ∧ (2-3: b, 3-3: c)), 1-1: d)");
	}

	@Test
	public void testComplexRight() {
		FormationTree tree = compiler.compile("d∧(¬a∨(b∧c))");
		assertEquals("q∧(¬p∨(s∧t)): ", tree.toTreeString(), "0-0: ∧ (0-1: d, 1-1: ∨ (2-2: ¬ (4-3: a), 3-2: ∧ (6-3: b, 7-3: c)))");
	}

	@Test
	public void testComplexImplies() {
		FormationTree tree = compiler.compile("¬p∨q→(p→q∧r)");
		assertEquals("¬p∨q→(p→q∧r): ", tree.toTreeString(), "0-0: → (0-1: ∨ (0-2: ¬ (0-3: p), 1-2: q), 1-1: → (2-2: p, 3-2: ∧ (6-3: q, 7-3: r)))");
	}

	@Test
	public void testComplexIff() {
		FormationTree tree = compiler.compile("¬p∨q↔(p→q∧r)");
		assertEquals("¬p∨q↔(p→q∧r): ", tree.toTreeString(), "0-0: ↔ (0-1: ∨ (0-2: ¬ (0-3: p), 1-2: q), 1-1: → (2-2: p, 3-2: ∧ (6-3: q, 7-3: r)))");
	}

	
//	@Test
//	public void testComplex4() {
//		FormationTree tree = compiler.compile("¬(a∧b)");
//		assertEquals("¬(a∧b): ", tree.toString(), "0-0: ¬ (0-1: ∧ (0-2: a, 1-2: b))");
//	}

}
