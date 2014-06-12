grammar Expr;	

prog: expr;

expr: '(' expr ')' 						#EXPR
	| '¬' expr							#NOT
	| expr BINOP expr					#BINOP_
	| (QUANTIFIER ATOM)* '[' expr ']'	#QUANTIFIER_
	| expr '→' expr						#IMPLIES
	| expr '↔' expr						#IFF
	| ATOM								#ATOM_
	| 									#ERROR
	;

QUANTIFIER: ('∀' | '∃');
PREDICATE: ('A'..'Z');
BINOP: ('∧' | '∨');
ATOM:  ('a'..'z' | '┬' | '⊥' | PREDICATE ATOM* );