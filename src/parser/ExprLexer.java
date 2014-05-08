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
		T__4=1, T__3=2, T__2=3, T__1=4, T__0=5, BINOP=6, ATOM=7;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'->'", "')'", "'<->'", "'('", "'!'", "BINOP", "ATOM"
	};
	public static final String[] ruleNames = {
		"T__4", "T__3", "T__2", "T__1", "T__0", "BINOP", "ATOM"
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\t\"\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\3\2\3\3\3\3\3\4"+
		"\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\2\t\3\3\1\5\4\1\7\5\1\t\6"+
		"\1\13\7\1\r\b\1\17\t\1\3\2\3\4\2((~~!\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\3\21\3\2\2\2\5"+
		"\24\3\2\2\2\7\26\3\2\2\2\t\32\3\2\2\2\13\34\3\2\2\2\r\36\3\2\2\2\17 \3"+
		"\2\2\2\21\22\7/\2\2\22\23\7@\2\2\23\4\3\2\2\2\24\25\7+\2\2\25\6\3\2\2"+
		"\2\26\27\7>\2\2\27\30\7/\2\2\30\31\7@\2\2\31\b\3\2\2\2\32\33\7*\2\2\33"+
		"\n\3\2\2\2\34\35\7#\2\2\35\f\3\2\2\2\36\37\t\2\2\2\37\16\3\2\2\2 !\4c"+
		"|\2!\20\3\2\2\2\3\2";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}