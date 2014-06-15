package treeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
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
	
	public Compiler() {
		binaryOps = new ArrayList<String>(Arrays.asList("∧", "∨", "→", "↔"));
		unaryOps = new ArrayList<String>(Arrays.asList("¬"));
	}

	 /* 102: ∀x[e]	|-  ∀y[e{x->y}]
	 * 103: ∃x[e]	|-  ∃y[e{x->y}]
	 * 104: t		|- 	∀x[t]
	 * 105: t		|- 	∃x[t]
	 */
	public static void main(String args[]) {
		Compiler compiler = new Compiler();

		//∀ | ∃ | ∧ | ∨ | ┬ | ⊥ | ¬ | → | ↔
		String s = "∀x∀y[Px→¬(Px→∃z[Qxyz])]";
//		String s = "∀x∀y[Px]";
//		String s = "(x∧¬y)→((x∨y)∧u)";
		FormationTree tree = compiler.compile(s);
		System.out.println(tree.toTreeString());
		
//		RuleEngine re = new RuleEngine();
//		re.applyRuleFromBitSet(103, tree, tree.findNode(0, 0), "y");
		
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
        walker.walk(new ExprWalker(tree), parseTree);

        return tree;
	}

	public String generateRandomEquivalence(int numVars, int depth, int probability) {
		ArrayList<String> vars = new ArrayList<String>();
		
		for (int i = 0; i < numVars; ++i) {
			String var = Variable.randomVariable(probability).getValue();
			
			while (vars.contains(var))
				var = Variable.randomVariable(probability).getValue();
			
			vars.add(var);
		}
		
		String equiv = generateSubEquivalence(vars, depth);
		return equiv.substring(1, equiv.length() - 1);
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