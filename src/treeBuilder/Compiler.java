package treeBuilder;

import java.util.BitSet;
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
import treeManipulation.TruthTable;

public class Compiler {
	public static void main(String args[]) {
		Compiler compiler = new Compiler();
		
//		String s1 = "av(c^¬b)";
		String s1 = "┬";
		FormationTree tree1 = compiler.compile(s1);
		FormationTree copy = compiler.compile(s1);
//		FormationTree tree2 = compiler.compile(s2);
		System.out.println(tree1.toString() + "\n");
//		System.out.println(tree2.toString() + "\n");

//		Node node = tree1.findNode(0, 0);
//		System.out.println(node.isAtom());
		RuleEngine re = new RuleEngine();
//		BitSet bs = re.getApplicableRules(tree1, node);
//		System.out.println(bs);
		
//		re.applyRuleToRandomNode(tree1);
		re.applyRandomRules(tree1, 4);
		
		System.out.println(tree1.toTreeString());
		System.out.println(tree1);
		System.out.println("Trees are equal: " + tree1.equals(copy));
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