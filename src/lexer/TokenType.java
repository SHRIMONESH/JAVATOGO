package lexer;

/**
 * Enumeration of all token types in the Java subset.
 * Demonstrates finite automata theory - each token type is recognized by a DFA.
 */
public enum TokenType {
    // Keywords - recognized via hash table lookup after identifier DFA
    CLASS, INTERFACE, EXTENDS, IMPLEMENTS,
    PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL,
    VOID, INT, BOOLEAN, STRING, DOUBLE, LONG, CHAR,
    IF, ELSE, WHILE, FOR, DO,
    RETURN, NEW, THIS, SUPER,
    TRUE, FALSE, NULL,
    BREAK, CONTINUE,

    // Identifiers and Literals
    IDENTIFIER,        // [a-zA-Z_][a-zA-Z0-9_]*
    INTEGER_LITERAL,   // [0-9]+
    DOUBLE_LITERAL,    // [0-9]+\.[0-9]+
    STRING_LITERAL,    // "..."
    CHAR_LITERAL,      // '.'

    // Arithmetic Operators
    PLUS,              // +
    MINUS,             // -
    STAR,              // *
    SLASH,             // /
    PERCENT,           // %
    INCREMENT,         // ++
    DECREMENT,         // --

    // Comparison Operators
    EQUALS,            // ==
    NOT_EQUALS,        // !=
    LESS_THAN,         // <
    GREATER_THAN,      // >
    LESS_EQUAL,        // <=
    GREATER_EQUAL,     // >=

    // Logical Operators
    LOGICAL_AND,       // &&
    LOGICAL_OR,        // ||
    LOGICAL_NOT,       // !

    // Assignment Operators
    ASSIGN,            // =
    PLUS_ASSIGN,       // +=
    MINUS_ASSIGN,      // -=
    STAR_ASSIGN,       // *=
    SLASH_ASSIGN,      // /=

    // Delimiters
    LPAREN,            // (
    RPAREN,            // )
    LBRACE,            // {
    RBRACE,            // }
    LBRACKET,          // [
    RBRACKET,          // ]
    SEMICOLON,         // ;
    COMMA,             // ,
    DOT,               // .
    COLON,             // :

    // Special
    EOF,               // End of file
    NEWLINE;           // \n (may be used for error reporting)

    /**
     * Checks if this token type is a keyword.
     */
    public boolean isKeyword() {
        return this.ordinal() >= CLASS.ordinal() && this.ordinal() <= CONTINUE.ordinal();
    }

    /**
     * Checks if this token type is an operator.
     */
    public boolean isOperator() {
        return (this.ordinal() >= PLUS.ordinal() && this.ordinal() <= DECREMENT.ordinal()) ||
               (this.ordinal() >= EQUALS.ordinal() && this.ordinal() <= LOGICAL_NOT.ordinal()) ||
               (this.ordinal() >= ASSIGN.ordinal() && this.ordinal() <= SLASH_ASSIGN.ordinal());
    }

    /**
     * Checks if this token type is a literal.
     */
    public boolean isLiteral() {
        return this == INTEGER_LITERAL || this == DOUBLE_LITERAL ||
               this == STRING_LITERAL || this == CHAR_LITERAL ||
               this == TRUE || this == FALSE || this == NULL;
    }
}
