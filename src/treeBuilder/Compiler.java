package treeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import parser.ExprLexer;
import parser.ExprParser;
import parser.ExprWalker;
import treeManipulation.RuleEngine;

public class Compiler {

	private ArrayList<String> binaryOps;
	private ArrayList<String> unaryOps;
	private ArrayList<String> firstOrderOps;
	private ArrayList<String> predicateSymbols;
	private ArrayList<String> truthValues;
	private boolean firstOrder;

	public Compiler(boolean firstOrder) {
		predicateSymbols = new ArrayList<String>(Arrays.asList("P", "Q", "R"));
		binaryOps = new ArrayList<String>(Arrays.asList("∧", "∨", "→", "↔"));
		unaryOps = new ArrayList<String>(Arrays.asList("¬"));
		firstOrderOps = new ArrayList<String>(Arrays.asList("∀", "∃"));
		truthValues = new ArrayList<String>(Arrays.asList("┬", "⊥"));
		this.firstOrder = firstOrder;
	}

	public static void main(String args[]) {
		boolean firstOrder = false;
		Compiler compiler = new Compiler(firstOrder);

		//∀ | ∃ | ∧ | ∨ | ┬ | ⊥ | ¬ | → | ↔
		//		String s = "∀x∀y[Px→¬(Qx→∃z[Qxyz])]";
				String s = "x→y";
		//		String s = "(x∧¬y)→((x∨y)∧u)";

//		String s = compiler.generateRandomEquivalence(3, 0, 0);
		System.out.println(s);
		FormationTree tree = compiler.compile(s);
		System.out.println(tree.toTreeString());
		System.out.println();

		RuleEngine re = new RuleEngine(firstOrder);
//		re.applyRandomRules(tree, 3);
		re.getRuleApplicator().applyAbsorptionAndBackwards(tree, tree.findNode(0, 1), "z");
//		System.out.println(re.getApplicableRules(tree, tree.findNode(0, 0)));

		System.out.println("Done: " + tree.toTreeString());
		System.out.println(tree);
	}

	public FormationTree compile(String expr) {
		CharStream input = new ANTLRInputStream(expr);
		ExprLexer lexer = new ExprLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ExprParser parser = new ExprParser(tokens);

		FormationTree tree = new FormationTree(null);
		ParseTree parseTree = parser.prog();

		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(new ExprWalker(tree, firstOrder), parseTree);

		return tree;
	}

	public String generateRandomEquivalence(int numVars, int depth, int probability) {
		ArrayList<String> vars = new ArrayList<String>();

		for (int i = 0; i < numVars; ++i) {
			String var = Variable.randomVariable(probability, firstOrder).getValue();

			while (vars.contains(var))
				var = Variable.randomVariable(probability, firstOrder).getValue();

			vars.add(var);
		}

		String equiv;
		if (firstOrder)
			equiv = generateFOSubEquivalence(vars, depth, probability, true, new HashSet<String>(), new HashMap<String, Integer>());
		else
			equiv = generateSubEquivalence(vars, depth);

		return equiv.substring(1, equiv.length() - 1);
	}

	public String generateQuantifiers(ArrayList<String> vars, HashSet<String> usedVars) {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();

		int foOp = rand.nextInt(firstOrderOps.size());
		sb.append(firstOrderOps.get(foOp));

		rand = new Random();
		int var = rand.nextInt(vars.size());
		String s = vars.get(var);

		while (usedVars.contains(s)) {
			rand = new Random();
			var = rand.nextInt(vars.size());
			s = vars.get(var);
		}

		sb.append(s);
		usedVars.add(s);

		return sb.toString();
	}

	public String getPredicateVariable(ArrayList<String> vars, int probability, 
			HashMap<String, Integer> usedPredicates) {
		StringBuilder sb = new StringBuilder();

		Random rand = new Random();
		int numPredicates = predicateSymbols.size();
		int predicateIndex = rand.nextInt(numPredicates * 100 + truthValues.size() * probability);
		predicateIndex /= 100;

		if (predicateIndex >= numPredicates) {
			sb.append(truthValues.get(predicateIndex - numPredicates));
			return sb.toString();
		} else {
			String predicate = predicateSymbols.get(predicateIndex);
			sb.append(predicate);

			if (usedPredicates.containsKey(predicate)) {
				int numVars = usedPredicates.get(predicate);

				for (int i = 0; i < numVars; ++i) {
					rand = new Random();
					int j = rand.nextInt(vars.size());
					String var = vars.get(j);
					sb.append(var);
				}
			} else {
				rand = new Random();
				int numVars = rand.nextInt(vars.size()) + 1;
				//			HashSet<String> used = new HashSet<>();

				for (int i = 0; i < numVars; ++i) {
					rand = new Random();
					int j = rand.nextInt(vars.size());
					String var = vars.get(j);
					//				used.add(var);
					sb.append(var);
				}
				usedPredicates.put(predicate, numVars);
			}
			return sb.toString();
		}
	}

	private String generateFOSubEquivalence(ArrayList<String> vars, int depth, 
			int probability, boolean outer, HashSet<String> usedVars, 
			HashMap<String, Integer> usedPredicates) {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();

		sb.append("(");

		if (outer) {
			sb.append(generateQuantifiers(vars, usedVars));
			sb.append("[");
		}

		int op = rand.nextInt(binaryOps.size() + unaryOps.size());

		String sub1;
		String sub2;

		if (depth == 0 || op < 1) {
			sub1 = getPredicateVariable(vars, probability, usedPredicates);
			sub2 = getPredicateVariable(vars, probability, usedPredicates);
		} else {
			sub1 = generateFOSubEquivalence(vars, depth - 1, probability, false, usedVars, usedPredicates);
			sub2 = generateFOSubEquivalence(vars, depth - 1, probability, false, usedVars, usedPredicates);
		}

		// Create binary
		if (op > 0) {
			sb.append(sub1);
			sb.append(getRandomBinaryOperator());
			sb.append(sub2);
			// Create unary
		} else {
			rand = new Random();
			int unarySize = unaryOps.size();
			int i = rand.nextInt(unarySize + firstOrderOps.size());

			if (i < unarySize) {
				sb.append(getRandomUnaryOperator());
				sb.append(sub1);
			} else {
				sb.append(generateQuantifiers(vars, usedVars));
				sb.append("[");
				sb.append(sub1);
				sb.append("]");
			}
		}

		if (outer) {
			sb.append("]");
		}

		sb.append(")");
		return sb.toString();
	}

	private String generateSubEquivalence(ArrayList<String> vars, int depth) {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();

		sb.append("(");
		int op = rand.nextInt(binaryOps.size() + unaryOps.size());

		String sub1;
		String sub2;

		if (depth == 0 || op < 1) {
			sub1 = getRandomVariable(vars);
			sub2 = getRandomVariable(vars);
		} else {
			sub1 = generateSubEquivalence(vars, depth - 1);
			sub2 = generateSubEquivalence(vars, depth - 1);
		}

		// Create binary
		if (op > 0) {
			sb.append(sub1);
			sb.append(getRandomBinaryOperator());
			sb.append(sub2);
			// Create unary
		} else {
			sb.append(getRandomUnaryOperator());
			sb.append(sub1);
		}

		sb.append(")");
		return sb.toString();
	}

	public String getRandomVariable(ArrayList<String> vars) {
		Random rand = new Random();
		int j = rand.nextInt(vars.size());
		return vars.get(j);
	}

	public String getRandomBinaryOperator() {
		Random rand = new Random();
		int i = rand.nextInt(binaryOps.size());
		return binaryOps.get(i);
	}

	public String getRandomUnaryOperator() {
		Random rand = new Random();
		int i = rand.nextInt(unaryOps.size());
		return unaryOps.get(i);
	}

}