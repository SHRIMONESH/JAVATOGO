package parser.ast.expressions;

import parser.ast.ASTVisitor;
import util.SourcePosition;

/**
 * Represents the 'this' keyword.
 */
public class ThisExpression implements Expression {
    private final SourcePosition position;

    public ThisExpression(SourcePosition position) {
        this.position = position;
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
