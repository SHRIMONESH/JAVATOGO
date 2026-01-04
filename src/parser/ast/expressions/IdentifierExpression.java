package parser.ast.expressions;

import parser.ast.ASTVisitor;
import util.SourcePosition;

/**
 * Represents a variable or identifier reference.
 */
public class IdentifierExpression implements Expression {
    private final String name;
    private final SourcePosition position;

    public IdentifierExpression(String name, SourcePosition position) {
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
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
