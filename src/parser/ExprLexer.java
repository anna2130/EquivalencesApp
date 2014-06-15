// Generated from Expr.g4 by ANTLR 4.1
package parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExprLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__6=1, T__5=2, T__4=3, T__3=4, T__2=5, T__1=6, T__0=7, QUANTIFIER=8, 
		PREDICATE=9, BINOP=10, TERM=11, ATOM=12;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"']'", "')'", "'['", "'('", "'→'", "'↔'", "'¬'", "QUANTIFIER", "PREDICATE", 
		"BINOP", "TERM", "ATOM"
	};
	public static final String[] ruleNames = {
		"T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", "QUANTIFIER", 
		"PREDICATE", "BINOP", "TERM", "ATOM"
	};


	public ExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Expr.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\16:\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3"+
		"\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\7\f\62\n\f\f\f\16\f\65\13\f\3"+
		"\r\3\r\5\r9\n\r\2\16\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n"+
		"\1\23\13\1\25\f\1\27\r\1\31\16\1\3\2\4\4\2\u2202\u2202\u2205\u2205\5\2"+
		"c|\u22a7\u22a7\u252e\u252e:\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\3\33\3\2\2\2\5\35\3\2\2\2\7\37"+
		"\3\2\2\2\t!\3\2\2\2\13#\3\2\2\2\r%\3\2\2\2\17\'\3\2\2\2\21)\3\2\2\2\23"+
		"+\3\2\2\2\25-\3\2\2\2\27/\3\2\2\2\318\3\2\2\2\33\34\7_\2\2\34\4\3\2\2"+
		"\2\35\36\7+\2\2\36\6\3\2\2\2\37 \7]\2\2 \b\3\2\2\2!\"\7*\2\2\"\n\3\2\2"+
		"\2#$\7\u2194\2\2$\f\3\2\2\2%&\7\u2196\2\2&\16\3\2\2\2\'(\7\u00ae\2\2("+
		"\20\3\2\2\2)*\t\2\2\2*\22\3\2\2\2+,\4C\\\2,\24\3\2\2\2-.\4\u2229\u222a"+
		"\2.\26\3\2\2\2/\63\5\23\n\2\60\62\5\31\r\2\61\60\3\2\2\2\62\65\3\2\2\2"+
		"\63\61\3\2\2\2\63\64\3\2\2\2\64\30\3\2\2\2\65\63\3\2\2\2\669\t\3\2\28"+
		"\66\3\2\2\28\67\3\2\2\29\32\3\2\2\2\5\2\638";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}