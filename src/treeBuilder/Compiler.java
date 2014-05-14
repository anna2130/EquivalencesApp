package treeBuilder;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import parser.ExprLexer;
import parser.ExprParser;
import parser.ExprWalker;
import treeManipulation.RuleEngine;
import treeManipulation.TruthTable;

public class Compiler {
	public static void main(String args[]) {
		Compiler compiler = new Compiler();
//		String s = "(r&q)&p";
		String s1 = "a^(avb)";
//		String s2 = "a";
		FormationTree tree1 = compiler.compile(s1);
//		FormationTree tree2 = compiler.compile(s2);
		System.out.println(tree1.toString());
//		System.out.println(tree2.toString() + "\n");

		Node node = tree1.findNode(0, 0);
		RuleEngine re = new RuleEngine(tree1);
//		BitSet bs = re.getBitSet(node);
		TruthTable tt = re.getTruthTable(node);
		System.out.println(tt);
		
//		TruthTable tt1 = new TruthTable(tree1);
//		TruthTable tt2 = new TruthTable(tree2);
//		ArrayList<int[]> vals = tt1.getTable();
//		System.out.println(tt1.testEquivalence(tt2));
		
//		for (int i = 0; i < vals.size(); ++i) {
//			System.out.print(vals.get(i));
//		}
//		System.out.println("\n");
		
//		int n = 3;
//		String[] variables = new String[] {"p", "q", "r"};
//        int rows = (int) Math.pow(2,n);
        
//        for (int i = n - 1; i >= 0; --i) {
//        	String variable = variables[i];
//        	int[] column = new int[rows];
//        	
//        	for (int j = 0; j < rows; ++j) {
//        		System.out.print((j / (int) Math.pow(2, i)) % 2);
//        	}
//        	System.out.println("");
//        }
//    	System.out.println("");
		
//		TreeIterator it = new TreeIterator(node);
//		while (it.hasNext()) {
//			System.out.println(it.next().getValue());
//		}
//		
//		RuleSelector rs = new RuleSelector();
//		BitSet bs = rs.getApplicableRules(tree, node);
//		
//		RuleApplicator ra = new RuleApplicator();
//		ra.applyDistributivityAndRight(tree, (BinaryOperator) node);
//		ra.applyRandomRule(bs, tree, (BinaryOperator) node);
//		ra.applyRuleFromBitSet(bs, 1, tree, (BinaryOperator) node); 
		System.out.println(tree1.toTreeString());
	}
	
	public FormationTree compile(String expr) {
		CharStream input = new ANTLRInputStream(expr);
		ExprLexer lexer = new ExprLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ExprParser parser = new ExprParser(tokens);
		
		FormationTree tree = new FormationTree();
		
		ParseTree parseTree = parser.prog();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new ExprWalker(tree), parseTree);

        return tree;
	}
}