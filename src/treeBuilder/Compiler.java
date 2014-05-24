package treeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import parser.ExprLexer;
import parser.ExprParser;
import parser.ExprWalker;
import treeManipulation.TruthTable;

public class Compiler {
	
	private ArrayList<String> variables;
	private ArrayList<String> binaryOps;
	private ArrayList<String> unaryOps;
	
	public Compiler() {
		variables = new ArrayList<String>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "┬", "⊥"));
		binaryOps = new ArrayList<String>(Arrays.asList("^", "v", "→", "↔"));
		unaryOps = new ArrayList<String>(Arrays.asList("¬"));
	}
	
	public static void main(String args[]) {
		Compiler compiler = new Compiler();

		String s2 = "((hv(a^┬))^⊥)^⊥";
		String s1 = "⊥^(┬v┬)";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree tree2 = compiler.compile(s2);
		System.out.println(tree1);
		System.out.println(tree2);

		TruthTable tt1 = new TruthTable(tree1);
		TruthTable tt2 = new TruthTable(tree2);
		System.out.println(tt1.testEquivalence(tt2));
		System.out.println(tt1);
		System.out.println(tt2);
	}
	
	public FormationTree compile(String expr) throws RecognitionException {
		CharStream input = new ANTLRInputStream(expr);
		ExprLexer lexer = new ExprLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ExprParser parser = new ExprParser(tokens);
		
		FormationTree tree = new FormationTree(null);
		ParseTree parseTree = null;
		try {
			parseTree = parser.prog();
		} catch (RecognitionException e) {
			System.out.println("Caught exception");
			return null;
		}
		
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new ExprWalker(tree), parseTree);

        return tree;
	}

	public String generateRandomEquivalence(int numVars, int depth) {
		ArrayList<String> vars = new ArrayList<String>();
		
		int size = variables.size();
		Random rand;
		for (int i = 0; i < numVars; ++i) {
			rand = new Random();
			String var = variables.get(rand.nextInt(size));
			
			while (vars.contains(var)) {
				rand = new Random();
				var = variables.get(rand.nextInt(size));
			}
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