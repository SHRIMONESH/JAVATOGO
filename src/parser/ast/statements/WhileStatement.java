package parser.ast.statements;

import parser.ast.ASTVisitor;
import parser.ast.expressions.Expression;
import util.SourcePosition;

/**
 * Represents a while loop.
 */
public class WhileStatement implements Statement {
    private final Expression condition;
    private final Statement body;
    private final SourcePosition position;

    public WhileStatement(Expression condition, Statement body, SourcePosition position) {
        this.condition = condition;
        this.body = body;
        this.position = position;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
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
