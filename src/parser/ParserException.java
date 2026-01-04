package parser;

import util.SourcePosition;

/**
 * Exception thrown during parsing when syntax errors are encountered.
 */
public class ParserException extends RuntimeException {
    private final SourcePosition position;

    public ParserException(String message, SourcePosition position) {
        super("Parser error at " + position + ": " + message);
        this.position = position;
    }

    public SourcePosition getPosition() {
        return position;
    }
}
