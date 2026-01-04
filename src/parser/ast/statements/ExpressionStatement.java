package parser.ast.statements;

import parser.ast.ASTVisitor;
import parser.ast.expressions.Expression;
import util.SourcePosition;

/**
 * Represents an expression used as a statement (e.g., method call, assignment).
 */
public class ExpressionStatement implements Statement {
    private final Expression expression;
    private final SourcePosition position;

    public ExpressionStatement(Expression expression, SourcePosition position) {
        this.expression = expression;
        this.position = position;
    }

    public Expression getExpression() {
        return expression;
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
