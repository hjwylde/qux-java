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
                do {
                    next = super.nextToken();
                } while (next.getType() == QuxParser.NEWLINE);
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

// Parser section

start : NEWLINE? file EOF
      ;

// File

file : pkg imp* decl*
     ;

// Package statement

pkg : 'package' Identifier ('.' Identifier)* NEWLINE
    ;

// Import statement

imp : 'import' Identifier ('.' Identifier)+ ('$' Identifier)? NEWLINE
    ;

// Declarations

decl : declConstant
     | declFunction
     | declMethod
     | declType
     ;

declConstant : type Identifier 'is' expr NEWLINE
             ;

declFunction : typeReturn Identifier '(' (type Identifier (',' type Identifier)*)? ')' block
             ;

declMethod : typeReturn type '::' Identifier '(' (type Identifier (',' type Identifier)*)? ')' block
           ;

declType : 'type' Identifier 'is' type NEWLINE
         ;

// Statements

stmt : stmtAssign
     | stmtExpr
     | stmtFor
     | stmtIf
     | stmtPrint
     | stmtReturn
     | stmtWhile
     ;

stmtAssign : Identifier exprAccess_1* (AOP | AOP_EXP | AOP_ADD | AOP_SUB | AOP_MUL | AOP_DIV | AOP_IDIV | AOP_REM) expr NEWLINE
           ;

stmtExpr : expr NEWLINE
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

block : ':' NEWLINE INDENT stmt* DEDENT
      ;

// Expressions

// TODO: Could push a mode that skips NEWLINE, INDENT and DEDENT tokens, then pop it at the end
expr : exprBinary
     ;

exprBinary : exprBinary_1
           ;

exprBinary_1 : exprBinary_2 (BOP_IMP exprBinary_2)*
             ;

exprBinary_2 : exprBinary_3 ((BOP_XOR | BOP_IFF) exprBinary_3)*
             ;

exprBinary_3 : exprBinary_4 ((BOP_AND | BOP_OR) exprBinary_4)*
             ;

exprBinary_4 : exprBinary_5 ((BOP_EQ | BOP_NEQ) exprBinary_5)*
             ;

exprBinary_5 : exprBinary_6 ((BOP_LT | BOP_LTE | BOP_GT | BOP_GTE) exprBinary_6)*
             ;

exprBinary_6 : exprBinary_7 ((BOP_IN | BOP_NIN) exprBinary_7)*
             ;

exprBinary_7 : exprBinary_8 ((BOP_ADD | BOP_SUB) exprBinary_8)*
             ;

exprBinary_8 : exprBinary_9 ((BOP_MUL | BOP_DIV | BOP_IDIV | BOP_REM) exprBinary_9)*
             ;

exprBinary_9 : exprBinary_10 (BOP_EXP exprBinary_10)*
             ;

exprBinary_10 : exprUnary (BOP_RNG exprUnary)?
             ;

exprUnary : exprMethod UOP_DEC?
          | exprMethod UOP_INC?
          | UOP_NEG? exprMethod
          | UOP_NOT? exprMethod
          | UOP_LEN exprMethod UOP_LEN
          ;

exprMethod : exprAccess '::' exprMeta? Identifier '(' ')'
           | exprAccess '::' exprMeta? Identifier '(' expr (',' expr)* ')'
           | exprAccess
           ;

exprAccess : exprTerm exprAccess_1*
           ;

exprAccess_1 : exprAccess_1_1
             | exprAccess_1_2
             | exprAccess_1_3
             | exprAccess_1_4
             | exprAccess_1_5
             | exprAccess_1_6
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

exprAccess_1_6 : '.' Identifier
               ;

exprTerm : exprBrace
         | exprBracket
         | exprFunction
         | exprParen
         | exprVariable
         | value
         ;

exprBrace : '{' (expr (',' expr)*)? '}'
          | '{' Identifier ':' expr (',' Identifier ':' expr)* '}'
          ;

exprBracket : '[' (expr (',' expr)*)? ']'
            ;

exprFunction : exprMeta? Identifier '(' ')'
             | exprMeta? Identifier '(' expr (',' expr)* ')'
             ;

exprParen : '(' expr ')'
          ;

exprVariable : exprMeta? Identifier
             ;

exprMeta : Identifier ('.' Identifier)+ '$'
         ;

// Values

value : valueKeyword
      | ValueInt
      | ValueRat
      | ValueString
      ;

valueKeyword : FALSE
             | NULL
             | OBJ
             | TRUE
             ;

// Types

type : typeKeyword
     | typeList
     | typeNamed
     | typeRecord
     | typeSet
     ;

typeKeyword : ANY
            | BOOL
            | INT
            | NULL
            | OBJ
            | RAT
            | STR
            ;

typeList : '[' type ']'
         ;

typeRecord : '{' type Identifier (',' type Identifier)* '}'
           ;

typeSet : '{' type '}'
         ;

typeNamed : exprMeta? Identifier
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
EscapeSequence : '\\' [fnrt'\\]
               | '\\' 'u' HexDigit HexDigit HexDigit HexDigit
               ;

ValueInt : '-'? Numeral
         | '-'? BinNumeral
         | '-'? OctNumeral
         | '-'? HexNumeral
         ;

ValueRat : '-'? Numeral '.' Numeral Exponent ?
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
FOR     : 'for' ;
IF      : 'if' ;
IMPORT  : 'import' ;
INT     : 'int' ;
IS      : 'is' ;
LIST    : 'list' ;
NULL    : 'null' ;
OBJ     : 'obj' ;
PACKAGE : 'package' ;
PRINT   : 'print' ;
RAT     : 'rat' ;
RECORD  : 'record' ;
RETURN  : 'return' ;
SET     : 'set' ;
STR     : 'str' ;
TRUE    : 'true' ;
TYPE    : 'type' ;
VOID    : 'void' ;
WHILE   : 'while' ;

// Separators

LPAREN      : '(' ;
LBRACE      : '{' ;
LBRACKET    : '[' ;
RPAREN      : ')' ;
RBRACE      : '}' ;
RBRACKET    : ']' ;
DOT         : '.' ;
COMMA       : ',' ;
SEMI_COLON  : ';' ;
COLON_COLON : '::' ;
COLON       : ':' ;
DOLLAR      : '$' ;

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
BOP_IMP : 'implies' ;

BOP_IN : 'in' ;
BOP_NIN : 'nin' ;

BOP_RNG : '..' ;
BOP_EXP : '**' ;

BOP_ADD : '+' ;
BOP_SUB : '-' ;
BOP_MUL : '*' ;
BOP_DIV : '/' ;
BOP_IDIV : '//' ;
BOP_REM : '%' ;

// Unary operators

UOP_NOT : 'not' ;

UOP_LEN: '|' ;

UOP_NEG: '-' ;
UOP_DEC: '--' ;
UOP_INC: '++' ;

// Assignment operators

AOP : '=' ;
AOP_EXP : '**=' ;
AOP_ADD : '+=' ;
AOP_SUB : '-=' ;
AOP_MUL : '*=' ;
AOP_DIV : '/=' ;
AOP_IDIV : '//=' ;
AOP_REM : '%=' ;

// Identifier

Identifier : [a-zA-Z_][a-zA-Z0-9_]* ;

// Miscellaneous

COMMENT_LINE : ' '* '#' ~[\r\n]* -> skip ;

COMMENT_DOC : ' '* '/**' .*? '*/' -> skip ;

COMMENT_BLOCK : ' '* '/*' .*? '*/' -> skip ;

NEWLINE : (' '* '\r'? '\n')+ ;

DENT : { getCharPositionInLine() == 0 }? [ ]+ ;

WHITESPACE : [ ]+ -> skip;

UNKNOWN : .+? ;

