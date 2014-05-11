grammar Qux ;

// Code for assisting with INDENT / DEDENT generation

tokens { INDENT, DEDENT }

@lexer::header {

    import com.hjwylde.common.error.CompilerErrors;

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
            if (next.getType() == QuxParser.NEWLINE) {
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
                    String source = getSourceName();
                    if (source == null) {
                        source = "<empty>";
                    }
                    int length = (next.getStopIndex() + 1) - next.getStartIndex();

                    throw CompilerErrors.invalidDedent(source, next.getLine(),
                            next.getCharPositionInLine(), length);
                }
            } else {
                // Either we have reached the end of input, or next token isn't a DENT and last
                // token was a NEWLINE, hence, we must dedent

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

start : NEWLINE? file EOF
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

stmt : stmtAccessAssign
     | stmtAssign
     | stmtFor
     | stmtIf
     | stmtPrint
     | stmtReturn
     | stmtWhile
     | stmtExpr
     ;

stmtAccessAssign : Identifier ('[' expr ']')+ '=' expr NEWLINE
                 ;

stmtAssign : Identifier (AOP | AOP_ADD | AOP_SUB | AOP_MUL | AOP_DIV | AOP_REM) expr NEWLINE
           ;

stmtFor : 'for' Identifier BOP_IN expr block
        ;

stmtIf : 'if' expr block ('elif' expr block)* ('else' block)?
       ;

stmtPrint : 'print' expr NEWLINE
          ;

stmtReturn : 'return' expr? NEWLINE
           ;

stmtWhile : 'while' expr block
          ;

stmtExpr : exprFunction NEWLINE
         | exprIncrement NEWLINE
         ;

block : ':' NEWLINE INDENT stmt* DEDENT
      ;

// Expressions

// TODO: Could push a mode that skips NEWLINE, INDENT and DEDENT tokens, then pop it at the end
expr : exprBinary ;

exprBinary : exprBinary_1
           ;

exprBinary_1 : exprBinary_2 ((BOP_IMPLIES) exprBinary_2)*
             ;

exprBinary_2 : exprBinary_3 ((BOP_XOR | BOP_IFF) exprBinary_3)*
             ;

exprBinary_3 : exprBinary_4 ((BOP_AND | BOP_OR) exprBinary_4)*
             ;

exprBinary_4 : exprBinary_5 ((BOP_EQ | BOP_NEQ) exprBinary_5)*
             ;

exprBinary_5 : exprBinary_6 ((BOP_LT | BOP_LTE | BOP_GT | BOP_GTE) exprBinary_6)*
             ;

exprBinary_6 : exprBinary_7 ((BOP_ADD | BOP_SUB) exprBinary_7)*
             ;

exprBinary_7 : exprRange ((BOP_MUL | BOP_DIV | BOP_REM) exprRange)*
             ;

exprRange : exprUnary (BOP_RANGE exprUnary)?
          ;

exprUnary : UOP_NEG? exprAccess
          | UOP_NOT? exprAccess
          | UOP_LEN exprAccess UOP_LEN
          ;

exprAccess : exprTerm exprAccess_1*
           ;

exprAccess_1 : exprAccess_1_1
             | exprAccess_1_2
             | exprAccess_1_3
             | exprAccess_1_4
             | exprAccess_1_5
             ;

exprAccess_1_1 : '[' expr ']'
               ;

exprAccess_1_2 : '[' expr ':' expr ']'
               ;

exprAccess_1_3 : '[' expr ':' ']'
               ;

exprAccess_1_4 : '[' ':' expr ']'
               ;

exprAccess_1_5 : '[' ':' ']'
               ;

exprTerm : exprBrace
         | exprBracket
         | exprFunction
         | exprIncrement
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

exprIncrement : Identifier UOP_INC
              ;

exprParen : '(' expr ')'
          ;

exprVariable : Identifier
             ;

// Values

value : valueKeyword
      | ValueInt
      | ValueReal
      | ValueString
      ;

valueKeyword : FALSE
             | NULL
             | TRUE
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
ELIF    : 'elif' ;
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

// Binary operators

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

BOP_RANGE : '..' ;

BOP_ADD : '+' ;
BOP_SUB : '-' ;
BOP_MUL : '*' ;
BOP_DIV : '/' ;
BOP_REM : '%' ;

// Unary operators

UOP_NOT : 'not' ;

UOP_LEN: '|' ;

UOP_NEG: '-' ;
UOP_INC: '++' ;

// Assignment operators

AOP : '=' ;
AOP_ADD : '+=' ;
AOP_SUB : '-=' ;
AOP_MUL : '*=' ;
AOP_DIV : '/=' ;
AOP_REM : '%=' ;

// Identifier

Identifier : [a-zA-Z_$][a-zA-Z0-9_$]* ;

// Miscellaneous

COMMENT_LINE : '#' ~[\r\n]*? -> skip ;

COMMENT_DOC : '/**' .*? '*/' -> skip ;

COMMENT_BLOCK : '/*' .*? '*/' -> skip ;

NEWLINE : (' '* '\r'? '\n')+ ;

DENT : { getCharPositionInLine() == 0 }? [ ]+ ;

WHITESPACE : [ ]+ -> skip;

UNKNOWN : .+? ;

