// Generated from Expr.g4 by ANTLR 4.1
package parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExprParser}.
 */
public interface ExprListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ExprParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(@NotNull ExprParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(@NotNull ExprParser.ProgContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExprParser#EXPR}.
	 * @param ctx the parse tree
	 */
	void enterEXPR(@NotNull ExprParser.EXPRContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#EXPR}.
	 * @param ctx the parse tree
	 */
	void exitEXPR(@NotNull ExprParser.EXPRContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExprParser#IMPLIES}.
	 * @param ctx the parse tree
	 */
	void enterIMPLIES(@NotNull ExprParser.IMPLIESContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#IMPLIES}.
	 * @param ctx the parse tree
	 */
	void exitIMPLIES(@NotNull ExprParser.IMPLIESContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExprParser#NOT}.
	 * @param ctx the parse tree
	 */
	void enterNOT(@NotNull ExprParser.NOTContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#NOT}.
	 * @param ctx the parse tree
	 */
	void exitNOT(@NotNull ExprParser.NOTContext ctx);

	/**
	 * Enter a parse tree produced by {@link ExprParser#BINOP_}.
	 * @param ctx the parse tree
	 */
	void enterBINOP_(@NotNull ExprParser.BINOP_Context ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#BINOP_}.
	 * @param ctx the parse tree
	 */
	void exitBINOP_(@NotNull ExprParser.BINOP_Context ctx);

	/**
	 * Enter a parse tree produced by {@link ExprParser#ATOM_}.
	 * @param ctx the parse tree
	 */
	void enterATOM_(@NotNull ExprParser.ATOM_Context ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#ATOM_}.
	 * @param ctx the parse tree
	 */
	void exitATOM_(@NotNull ExprParser.ATOM_Context ctx);
}