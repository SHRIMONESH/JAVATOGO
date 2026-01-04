package parser.ast.expressions;

import parser.ast.ASTVisitor;
import util.SourcePosition;

/**
 * Represents a literal value (integer, string, boolean, etc.).
 */
public class LiteralExpression implements Expression {
    public enum LiteralType {
        INTEGER, DOUBLE, STRING, CHAR, BOOLEAN, NULL
    }

    private final Object value;
    private final LiteralType literalType;
    private final SourcePosition position;

    public LiteralExpression(Object value, LiteralType literalType, SourcePosition position) {
        this.value = value;
        this.literalType = literalType;
        this.position = position;
    }

    public Object getValue() {
        return value;
    }

    public LiteralType getLiteralType() {
        return literalType;
    }

    @Override
    public SourcePosition getPosition() {
        return position;
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}
