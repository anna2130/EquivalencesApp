// Generated from Expr.g4 by ANTLR 4.1
package parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ExprParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__3=1, T__2=2, T__1=3, T__0=4, BINOP=5, ATOM=6;
	public static final String[] tokenNames = {
		"<INVALID>", "'->'", "')'", "'('", "'!'", "BINOP", "ATOM"
	};
	public static final int
		RULE_prog = 0, RULE_expr = 1;
	public static final String[] ruleNames = {
		"prog", "expr"
	};

	@Override
	public String getGrammarFileName() { return "Expr.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public ExprParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgContext extends ParserRuleContext {
		public ExprContext_ expr() {
			return getRuleContext(ExprContext_.class,0);
		}
		public ProgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_prog; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterProg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitProg(this);
		}
	}

	public final ProgContext prog() throws RecognitionException {
		ProgContext _localctx = new ProgContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_prog);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(4); expr(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext_ extends ParserRuleContext {
		public int _p;
		public ExprContext_(ParserRuleContext parent, int invokingState) { super(parent, invokingState); }
		public ExprContext_(ParserRuleContext parent, int invokingState, int _p) {
			super(parent, invokingState);
			this._p = _p;
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext_() { }
		public void copyFrom(ExprContext_ ctx) {
			super.copyFrom(ctx);
			this._p = ctx._p;
		}
	}
	public static class EXPRContext extends ExprContext_ {
		public ExprContext_ expr() {
			return getRuleContext(ExprContext_.class,0);
		}
		public EXPRContext(ExprContext_ ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterEXPR(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitEXPR(this);
		}
	}
	public static class IMPLIESContext extends ExprContext_ {
		public List<ExprContext_> expr() {
			return getRuleContexts(ExprContext_.class);
		}
		public ExprContext_ expr(int i) {
			return getRuleContext(ExprContext_.class,i);
		}
		public IMPLIESContext(ExprContext_ ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterIMPLIES(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitIMPLIES(this);
		}
	}
	public static class NOTContext extends ExprContext_ {
		public ExprContext_ expr() {
			return getRuleContext(ExprContext_.class,0);
		}
		public NOTContext(ExprContext_ ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterNOT(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitNOT(this);
		}
	}
	public static class ATOM_Context extends ExprContext_ {
		public TerminalNode ATOM() { return getToken(ExprParser.ATOM, 0); }
		public ATOM_Context(ExprContext_ ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterATOM_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitATOM_(this);
		}
	}
	public static class BINOP_Context extends ExprContext_ {
		public TerminalNode BINOP() { return getToken(ExprParser.BINOP, 0); }
		public List<ExprContext_> expr() {
			return getRuleContexts(ExprContext_.class);
		}
		public ExprContext_ expr(int i) {
			return getRuleContext(ExprContext_.class,i);
		}
		public BINOP_Context(ExprContext_ ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).enterBINOP_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ExprListener ) ((ExprListener)listener).exitBINOP_(this);
		}
	}

	public final ExprContext_ expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext_ _localctx = new ExprContext_(_ctx, _parentState, _p);
		ExprContext_ _prevctx = _localctx;
		int _startState = 2;
		enterRecursionRule(_localctx, RULE_expr);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(14);
			switch (_input.LA(1)) {
			case 4:
				{
				_localctx = new NOTContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(7); match(4);
				setState(8); expr(4);
				}
				break;
			case 3:
				{
				_localctx = new EXPRContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(9); match(3);
				setState(10); expr(0);
				setState(11); match(2);
				}
				break;
			case ATOM:
				{
				_localctx = new ATOM_Context(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(13); match(ATOM);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(24);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=-1 ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(22);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						_localctx = new BINOP_Context(new ExprContext_(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(16);
						if (!(3 >= _localctx._p)) throw new FailedPredicateException(this, "3 >= $_p");
						setState(17); match(BINOP);
						setState(18); expr(4);
						}
						break;

					case 2:
						{
						_localctx = new IMPLIESContext(new ExprContext_(_parentctx, _parentState, _p));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(19);
						if (!(2 >= _localctx._p)) throw new FailedPredicateException(this, "2 >= $_p");
						setState(20); match(1);
						setState(21); expr(3);
						}
						break;
					}
					} 
				}
				setState(26);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 1: return expr_sempred((ExprContext_)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext_ _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return 3 >= _localctx._p;

		case 1: return 2 >= _localctx._p;
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\b\36\4\2\t\2\4\3"+
		"\t\3\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3\21\n\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\7\3\31\n\3\f\3\16\3\34\13\3\3\3\2\4\2\4\2\2\37\2\6\3\2\2\2"+
		"\4\20\3\2\2\2\6\7\5\4\3\2\7\3\3\2\2\2\b\t\b\3\1\2\t\n\7\6\2\2\n\21\5\4"+
		"\3\2\13\f\7\5\2\2\f\r\5\4\3\2\r\16\7\4\2\2\16\21\3\2\2\2\17\21\7\b\2\2"+
		"\20\b\3\2\2\2\20\13\3\2\2\2\20\17\3\2\2\2\21\32\3\2\2\2\22\23\6\3\2\3"+
		"\23\24\7\7\2\2\24\31\5\4\3\2\25\26\6\3\3\3\26\27\7\3\2\2\27\31\5\4\3\2"+
		"\30\22\3\2\2\2\30\25\3\2\2\2\31\34\3\2\2\2\32\30\3\2\2\2\32\33\3\2\2\2"+
		"\33\5\3\2\2\2\34\32\3\2\2\2\5\20\30\32";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}