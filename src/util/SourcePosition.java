package util;

/**
 * Represents a position in the source code.
 * Used for error reporting to show line and column numbers.
 */
public class SourcePosition {
    private final int line;
    private final int column;
    private final String filename;

    public SourcePosition(int line, int column, String filename) {
        this.line = line;
        this.column = column;
        this.filename = filename;
    }

    public SourcePosition(int line, int column) {
        this(line, column, "");
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        if (filename.isEmpty()) {
            return "line " + line + ", column " + column;
        }
        return filename + ":" + line + ":" + column;
    }
}
