package tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;

public class EquivalenceGeneration {

	private ArrayList<String> variables;
	private ArrayList<String> binaryOps;
	private ArrayList<String> unaryOps;

	private Compiler compiler;
	private int n;

	@Before 
	public void method() {
		compiler = new Compiler();
		n = 100;

		variables = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "┬", "⊥"));
		binaryOps = new ArrayList<String>(Arrays.asList("∧", "∨", "→", "↔"));
		unaryOps = new ArrayList<String>(Arrays.asList("¬"));
	}

	@Test
	public void testGenerateBinary() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			String s = compiler.getRandomBinaryOperator();
			b = b && binaryOps.contains(s);
		}
		assertTrue(b);
	}

	@Test
	public void testGenerateUnary() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			String s = compiler.getRandomUnaryOperator();
			b = b && unaryOps.contains(s);
		}
		assertTrue(b);
	}

	@Test
	public void testGenerateVariable() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			String s = compiler.getRandomVariable(variables);
			b = b && variables.contains(s);
		}
		assertTrue(b);
	}

	@Test
	public void testZeroIteration() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			String s = compiler.generateRandomEquivalence(1, 0, 50);
			FormationTree tree = compiler.compile(s);
			b = b && (tree.maxDepth() == 1);
		}
		assertTrue(b);
	}

	@Test
	public void testOneIteration() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			String s = compiler.generateRandomEquivalence(1, 1, 50);
			FormationTree tree = compiler.compile(s);
			b = b && (tree.maxDepth() <= 2);
		}
		assertTrue(b);
	}

	@Test
	public void testTwoIteration() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			String s = compiler.generateRandomEquivalence(1, 2, 50);
			FormationTree tree = compiler.compile(s);
			b = b && (tree.maxDepth() <= 3);
		}
		assertTrue(b);
	}

	@Test
	public void testTwoVariables() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			String s = compiler.generateRandomEquivalence(2, 0, 50);
			FormationTree tree = compiler.compile(s);
			b = b && (tree.maxDepth() == 1);
		}
		assertTrue(b);
	}

	@Test
	public void testProbability() {
		boolean b = true;
		for (int i = 0; i < n; ++i) {
			String s = compiler.generateRandomEquivalence(2, 0, 0);
			System.out.println(s);
			b = b && (!(s.contains("┬") || s.contains("⊥")));
		}
		assertTrue(b);
	}
}
