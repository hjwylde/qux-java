grammar Qux ;

// Grammar section

start : file EOF
      ;

// File

file : decl*
     ;

// Declarations

decl : declFunction
     ;

declFunction : typeReturn Identifier '(' (type Identifier (',' type Identifier)*)? ')' block
             ;

// Statements

stmt : stmtAssign
     | stmtIf
     | stmtPrint
     | stmtReturn
     | exprFunction ';'
     ;

stmtAssign : Identifier '=' expr ';'
           ;

stmtIf : 'if' expr block ('else' block)?
       ;

// Temporary
stmtPrint : 'print' expr ';'
          ;

stmtReturn : 'return' expr? ';'
           ;

block : '{' stmt* '}' ;

// Expressions

expr : exprBinary ;

exprBinary : exprUnary ((BOP_MUL | BOP_DIV | BOP_REM) expr)*
           | exprUnary ((BOP_ADD | BOP_SUB) expr)*
           | exprUnary ((BOP_EQ | BOP_NEQ | BOP_LT | BOP_LTE | BOP_GT | BOP_GTE) expr)*
           | exprUnary ((BOP_AND | BOP_OR) expr)*
           | exprUnary ((BOP_XOR | BOP_IFF) expr)*
           | exprUnary ((BOP_IMPLIES) expr)*
           ;

exprUnary : UOP_NEGATE? exprTerm
          | UOP_NOT? exprTerm
          ;

exprTerm : exprBracket
         | exprFunction
         | exprParen
         | exprVariable
         | value
         ;

exprBracket : '[' (expr (',' expr)*)? ']'
            ;

exprFunction : Identifier '(' (expr (',' expr)*) ')'
             ;

exprParen : '(' expr ')'
          ;

exprVariable : Identifier
             ;

// Values

value : ValueKeyword
      | ValueInt
      | ValueReal
      | ValueString
      ;

// Types

type : typeTerm
     | typeList
     ;

typeTerm : typeKeyword
         ;

typeList : '[' type ']'
         ;

typeKeyword : ANY
            | BOOL
            | INT
            | NULL
            | REAL
            | STR
            ;

typeReturn : type
           | VOID
           ;

// Lexer section

// Values

ValueKeyword : FALSE
             | NULL
             | TRUE
             ;

ValueString : '\'' StringCharacter* '\'' ;

fragment
StringCharacter : ~['\\]
                | EscapeSequence
                ;

fragment
EscapeSequence : '\\' [fnrt'"\\]
               | '\\' 'u' HexDigit HexDigit HexDigit HexDigit
               ;

ValueInt : '-'? Numeral
         | BinNumeral
         | OctNumeral
         | HexNumeral
         ;

ValueReal : '-'? Numeral '.' Numeral Exponent ?
          | '-'? Numeral Exponent
          ;

fragment
Numeral : Digit+;

fragment
Digit : [0-9] ;

fragment
BinNumeral : '0b' BinDigit+ ;

fragment
BinDigit : [01] ;

fragment
OctNumeral : '0o' OctDigit+ ;

fragment
OctDigit : [0-7] ;

fragment
HexNumeral : '0x' HexDigit+ ;

fragment
HexDigit : [0-9a-fA-F] ;

fragment
Exponent : 'e' [+-] Numeral ;

// Keywords

ANY     : 'any' ;
BOOL    : 'bool' ;
ELSE    : 'else' ;
FALSE   : 'false' ;
IF      : 'if' ;
INT     : 'int' ;
NULL    : 'null' ;
REAL    : 'real' ;
RETURN  : 'return' ;
STR     : 'str' ;
TRUE    : 'true' ;
VOID    : 'void' ;

// Separators

LPAREN      : '(' ;
LBRACE      : '{' ;
LBRACKET    : '[' ;
RPAREN      : ')' ;
RBRACE      : '}' ;
RBRACKET    : ']' ;
COMMA       : ',' ;
SEMI_COLON  : ';' ;
COLON       : ':' ;

// Operators

BOP_EQ : '==' ;
BOP_NEQ : '!=' ;
BOP_LT : '<' ;
BOP_LTE : '<=' ;
BOP_GT : '>' ;
BOP_GTE : '>=' ;

BOP_AND : 'and' ;
BOP_OR : 'or' ;
BOP_XOR : 'xor' ;
BOP_IFF : 'iff' ;
BOP_IMPLIES : 'implies' ;

BOP_ADD : '+' ;
BOP_SUB : '-' ;
BOP_MUL : '*' ;
BOP_DIV : '/' ;
BOP_REM : '%' ;

UOP_NOT : 'not' ;

UOP_NEGATE : '-' ;

// Identifier

Identifier : [a-zA-Z_$][a-zA-Z0-9_$]* ;

// Miscellaneous

WS : [ \n\r]+ -> skip;

COMMENT_LINE : '#' ~[\r\n]*? -> skip ;

COMMENT_DOC : '/**' .*? '*/' -> skip ;

COMMENT_BLOCK : '/*' .*? '*/' -> skip ;

