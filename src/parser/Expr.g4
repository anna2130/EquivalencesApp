grammar Expr;	

prog: expr;

expr: '(' expr ')' 						#EXPR
	| '¬' expr							#NOT
	| expr BINOP expr					#BINOP_
	| (QUANTIFIER ATOM)* '[' expr ']'	#QUANTIFIER_
	| expr '→' expr						#IMPLIES
	| expr '↔' expr						#IFF
	| TERM								#TERM_
	| ATOM								#ATOM_
	| 									#ERROR
	;

QUANTIFIER: ('∀' | '∃');
PREDICATE: ('A'..'Z');
BINOP: ('∧' | '∨');
TERM:  (PREDICATE ATOM*);
ATOM: ('a'..'z' | '┬' | '⊥');