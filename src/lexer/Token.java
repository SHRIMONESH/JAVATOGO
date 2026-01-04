package lexer;

import util.SourcePosition;

/**
 * Represents a single token from the lexical analysis phase.
 * A token is the smallest meaningful unit recognized by the lexer's DFA.
 */
public class Token {
    private final TokenType type;
    private final String lexeme;
    private final SourcePosition position;
    private final Object literal;  // For storing actual values of literals

    public Token(TokenType type, String lexeme, SourcePosition position, Object literal) {
        this.type = type;
        this.lexeme = lexeme;
        this.position = position;
        this.literal = literal;
    }

    public Token(TokenType type, String lexeme, SourcePosition position) {
        this(type, lexeme, position, null);
    }

    public Token(TokenType type, String lexeme, int line, int column) {
        this(type, lexeme, new SourcePosition(line, column), null);
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public SourcePosition getPosition() {
        return position;
    }

    public Object getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        if (literal != null) {
            return String.format("Token(%s, '%s', %s, literal=%s)",
                               type, lexeme, position, literal);
        }
        return String.format("Token(%s, '%s', %s)", type, lexeme, position);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Token)) return false;
        Token other = (Token) obj;
        return type == other.type && lexeme.equals(other.lexeme);
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 31 + lexeme.hashCode();
    }
}
