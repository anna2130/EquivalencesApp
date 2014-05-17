grammar Expr;	

prog: expr;

expr: '(' expr ')' 			#EXPR
	| '¬' expr				#NOT
	| expr BINOP expr		#BINOP_
	| expr '→' expr			#IMPLIES
	| expr '↔' expr			#IFF
	| ATOM					#ATOM_
	| BINOP					#ERROR
	;

BINOP: ('^' | 'v');
ATOM:  ('a'..'i' | '┬' | '⊥');