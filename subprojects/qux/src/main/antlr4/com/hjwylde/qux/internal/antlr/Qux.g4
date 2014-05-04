grammar Qux ;

// Code for assisting with INDENT / DEDENT generation

tokens { INDENT, DEDENT }

@lexer::header {

    import com.hjwylde.common.error.CompilerErrors;

    import com.google.common.io.Files;

    import java.util.ArrayDeque;
    import java.util.Deque;
    import java.util.Stack;

}

@lexer::members {

    private final Deque<Token> pending = new ArrayDeque<>();

    private final Stack<Integer> dents = new Stack<Integer>() {{ push(0); }};

    @Override
    public Token nextToken() {
        while (pending.isEmpty()) {
            Token next = super.nextToken();

            pending.offer(next);
            if (next.getType() == QuxParser.NL) {
                next = super.nextToken();
            } else {
                continue;
            }

            if (next.getType() == QuxParser.DENT) {
                int level = next.getText().length();

                // Add an indent token if needed
                if (level > dents.peek()) {
                    dents.push(level);

                    CommonToken indent = new CommonToken(next);
                    indent.setType(QuxParser.INDENT);
                    pending.offer(indent);
                }

                // Add as many dedent tokens as needed
                while (level < dents.peek()) {
                    dents.pop();

                    CommonToken dedent = new CommonToken(next);
                    dedent.setType(QuxParser.DEDENT);
                    pending.offer(dedent);
                }
                if (level != dents.peek()) {
                    String source = Files.getNameWithoutExtension(getSourceName());
                    int length = (next.getStopIndex() + 1) - next.getStartIndex();

                    throw CompilerErrors.invalidDedent(source, next.getLine(),
                            next.getCharPositionInLine(), length);
                }
            } else {
                // Either we have reached the end of input, or next token isn't a DENT and last
                // token was a NL, hence, we must dedent

                while (dents.size() > 1) {
                    dents.pop();

                    CommonToken dedent = new CommonToken(next);
                    dedent.setType(QuxParser.DEDENT);
                    pending.offer(dedent);
                }

                pending.offer(next);
            }
        }

        return pending.poll();
    }

}

// Grammar section

start : NL? file EOF
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
     | stmtFor
     | stmtIf
     | stmtPrint
     | stmtReturn
     | exprFunction NL
     ;

stmtAssign : Identifier '=' expr NL
           ;

stmtFor : 'for' Identifier BOP_IN expr block
        ;

stmtIf : 'if' expr block ('else' block)?
       ;

stmtPrint : 'print' expr NL
          ;

stmtReturn : 'return' expr? NL
           ;

block : ':' NL INDENT stmt* DEDENT ;

// Expressions

// TODO: Could push a mode that skips NL, INDENT and DEDENT tokens, then pop it at the end
expr : exprBinary ;

exprBinary : exprUnary ((BOP_MUL | BOP_DIV | BOP_REM) expr)*
           | exprUnary ((BOP_ADD | BOP_SUB) expr)*
           | exprUnary ((BOP_LT | BOP_LTE | BOP_GT | BOP_GTE) expr)*
           | exprUnary ((BOP_EQ | BOP_NEQ) expr)*
           | exprUnary ((BOP_AND | BOP_OR) expr)*
           | exprUnary ((BOP_XOR | BOP_IFF) expr)*
           | exprUnary ((BOP_IMPLIES) expr)*
           ;

exprUnary : UOP_NEG? exprAccess
          | UOP_NOT? exprAccess
          | exprLength
          ;

exprAccess : exprTerm ('[' expr ']')*
           ;

exprLength : UOP_LEN exprTerm UOP_LEN
           ;

exprTerm : exprBrace
         | exprBracket
         | exprFunction
         | exprParen
         | exprVariable
         | value
         ;

exprBrace : '{' (expr (',' expr)*)? '}'
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

type : typeList
     | typeSet
     | typeTerm
     ;

typeList : '[' type ']'
         ;

typeSet : '{' type '}'
         ;

typeTerm : typeKeyword
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
LIST    : 'list' ;
NULL    : 'null' ;
REAL    : 'real' ;
RETURN  : 'return' ;
SET     : 'set' ;
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

BOP_IN : 'in' ;

BOP_ADD : '+' ;
BOP_SUB : '-' ;
BOP_MUL : '*' ;
BOP_DIV : '/' ;
BOP_REM : '%' ;

UOP_NOT : 'not' ;

UOP_NEG: '-' ;

UOP_LEN: '|' ;

// Identifier

Identifier : [a-zA-Z_$][a-zA-Z0-9_$]* ;

// Miscellaneous

COMMENT_LINE : '#' ~[\r\n]*? -> skip ;

COMMENT_DOC : '/**' .*? '*/' -> skip ;

COMMENT_BLOCK : '/*' .*? '*/' -> skip ;

NL : (' '* '\r'? '\n')+ ;

DENT : { getCharPositionInLine() == 0 }? [ ]+ ;

WS : [ ]+ -> skip;

UNKNOWN : .+? ;

