package parser.ast.statements;

import parser.ast.ASTVisitor;
import parser.ast.expressions.Expression;
import util.SourcePosition;

/**
 * Represents a return statement.
 */
public class ReturnStatement implements Statement {
    private final Expression value;  // may be null for void returns
    private final SourcePosition position;

    public ReturnStatement(Expression value, SourcePosition position) {
        this.value = value;
        this.position = position;
    }

    public Expression getValue() {
        return value;
    }

    public boolean hasValue() {
        return value != null;
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
