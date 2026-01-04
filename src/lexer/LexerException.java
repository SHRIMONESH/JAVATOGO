package lexer;

import util.SourcePosition;

/**
 * Exception thrown during lexical analysis when invalid tokens are encountered.
 */
public class LexerException extends RuntimeException {
    private final SourcePosition position;

    public LexerException(String message, SourcePosition position) {
        super("Lexer error at " + position + ": " + message);
        this.position = position;
    }

    public LexerException(String message, int line, int column) {
        this(message, new SourcePosition(line, column));
    }

    public SourcePosition getPosition() {
        return position;
    }
}
