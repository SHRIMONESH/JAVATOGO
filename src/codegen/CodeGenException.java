package codegen;

import util.SourcePosition;

/**
 * Exception thrown during code generation.
 */
public class CodeGenException extends RuntimeException {
    private final SourcePosition position;

    public CodeGenException(String message, SourcePosition position) {
        super("Code generation error at " + position + ": " + message);
        this.position = position;
    }

    public CodeGenException(String message) {
        super("Code generation error: " + message);
        this.position = null;
    }

    public SourcePosition getPosition() {
        return position;
    }
}
