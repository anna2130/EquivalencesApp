package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import treeBuilder.Compiler;
import treeBuilder.FormationTree;
import treeManipulation.TruthTable;

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
		binaryOps = new ArrayList<String>(Arrays.asList("^", "v", "→", "↔"));
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
	public void testZeroGeneration() {
		String s = compiler.generateRandomEquivalence(1, 0);
		System.out.println(s);
		//		assertEquals(expected, actual);
	}
}
