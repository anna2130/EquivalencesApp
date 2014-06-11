grammar FirstOrder;	

prog: expr;

expr: '(' expr ')' 			#EXPR
	| '¬' expr				#NOT
	| expr BINOP expr		#BINOP_
	| QUANTIFIER ATOM expr	#QUANTIFIER_
	| expr '→' expr			#IMPLIES
	| expr '↔' expr			#IFF
	| PREDICATE '(' expr ')'#PREDICATE_
	| FUNCTION '(' ATOM* ')'#FUNCTION_
	| ATOM					#ATOM_
	| 						#ERROR
	;

QUANTIFIER: ('∀' | '∃');
PREDICATE: ('A'..'Z');
FUNCTION: ('f'..'h');
BINOP: ('^' | 'v');
ATOM:  ('a'..'e' | 'i'..'r' | '┬' | '⊥');