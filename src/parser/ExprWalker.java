package parser;

import java.util.LinkedList;
import java.util.Stack;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;

import treeBuilder.Atom;
import treeBuilder.BinaryOperator;
import treeBuilder.FormationTree;
import treeBuilder.UnaryOperator;

public class ExprWalker extends ExprBaseListener {

	private FormationTree tree;
	private Stack<Boolean> binary;
	private int key;
	private boolean increment;
	private boolean firstOrder;
	private Stack<String> variables;
	
	static private String END_QUANTIFIER;

	public ExprWalker(FormationTree tree, boolean firstOrder) {
		this.tree = tree;
		key = 0;
		binary = new Stack<Boolean>();
		this.firstOrder = firstOrder;
		variables = new Stack<String>();
		
		END_QUANTIFIER = "_";
	}

	public void remove() {
		key >>= 1;
		binary.pop();

		if (!binary.empty()) {
			increment = binary.peek();
		}
	}

	public void addBinary() {
		key <<= 1;
		if (increment) {
			key++;
			increment = false;
		}
		binary.push(true);
	}

	public void addUnary() {
		key <<= 1;
		if (increment) {
			key++;
			increment = false;
		}
		binary.push(false);
	}

	public int depth() {
		return binary.size() - 1;
	}

	@Override public void enterEXPR(ExprParser.EXPRContext ctx) { }

	@Override public void exitEXPR(ExprParser.EXPRContext ctx) { }

	@Override 
	public void enterIFF(ExprParser.IFFContext ctx) {
		addBinary();
		tree.addNode(new BinaryOperator(key, depth(), "↔", new LinkedList<String>()));
	}

	@Override 
	public void exitIFF(ExprParser.IFFContext ctx) {
		remove();
	}

	@Override 
	public void enterIMPLIES(ExprParser.IMPLIESContext ctx) {
		addBinary();
		tree.addNode(new BinaryOperator(key, depth(), "→", new LinkedList<String>()));
	}

	@Override 
	public void exitIMPLIES(ExprParser.IMPLIESContext ctx) {
		remove();
	}

	@Override public void enterQUANTIFIER_(@NotNull ExprParser.QUANTIFIER_Context ctx) {
		int i = 0;
		String quantifier = ctx.getChild(i).getText();
		String variable = ctx.getChild(i+1).getText();

		variables.push(END_QUANTIFIER);
		
		while (!quantifier.equals("[")) {
			if (variables.contains(variable)) {
				System.out.println("Error! Contains " + variable);
				tree.setErrorFlag(true);
			}

			addUnary();

			LinkedList<String> vars = new LinkedList<String>();
			vars.add(variable);
			variables.push(variable);
			tree.addNode(new UnaryOperator(key, depth(), quantifier, vars));

			i = i + 2;
			quantifier = ctx.getChild(i).getText();
			variable = ctx.getChild(i+1).getText();
		}
	}

	@Override public void exitQUANTIFIER_(@NotNull ExprParser.QUANTIFIER_Context ctx) {
		remove();
		
		while (!variables.peek().equals(END_QUANTIFIER))
			variables.pop();
		
		variables.pop();
	}

	@Override 
	public void enterNOT(ExprParser.NOTContext ctx) { 
		addUnary();
		tree.addNode(new UnaryOperator(key, depth(), "¬", new LinkedList<String>()));
	}

	@Override public void exitNOT(ExprParser.NOTContext ctx) {
		remove();
	}

	@Override 
	public void enterBINOP_(ExprParser.BINOP_Context ctx) { 
		addBinary();

		// Works out if | or &
		int i = ctx.getText().indexOf(ctx.getChild(1).getText());
		String c = ctx.getText().charAt(i) + "";

		tree.addNode(new BinaryOperator(key, depth(), c, new LinkedList<String>()));
	}

	@Override 
	public void exitBINOP_(ExprParser.BINOP_Context ctx) {
		remove();
	}

	@Override public void enterTERM_(@NotNull ExprParser.TERM_Context ctx) {
		addUnary();
		TerminalNode e = ctx.TERM();
		String text = e.getText();
		String value = text.charAt(0) + "";

		LinkedList<String> vars = new LinkedList<String>();
		for (int i = 1; i < text.length(); ++i)
			vars.add(text.charAt(i) + "");

		tree.addNode(new Atom(key, depth(), value, vars));
	}

	@Override public void exitTERM_(@NotNull ExprParser.TERM_Context ctx) {
		remove();
	}

	@Override 
	public void enterATOM_(ExprParser.ATOM_Context ctx) {
		TerminalNode e = ctx.ATOM();
		String text = e.getText();
		String value = text.charAt(0) + "";

		if (firstOrder && !(value.equals("⊥") || value.equals("┬"))) {
			addUnary();
			tree.setErrorFlag(true);
			System.out.println("Error!!!");
		} else {
			addUnary();
			tree.addNode(new Atom(key, depth(), value, new LinkedList<String>()));
		}
	}

	@Override public void exitATOM_(ExprParser.ATOM_Context ctx) {
		remove();
	}

	@Override public void enterERROR(@NotNull ExprParser.ERRORContext ctx) {
		tree.setErrorFlag(true);
		System.out.println("Error!!!");
	}
}