package parser.ast.statements;

import parser.ast.ASTVisitor;
import parser.ast.expressions.Expression;
import util.SourcePosition;

/**
 * Represents a for loop.
 */
public class ForStatement implements Statement {
    private final Statement initializer;  // may be null
    private final Expression condition;   // may be null
    private final Expression update;      // may be null
    private final Statement body;
    private final SourcePosition position;

    public ForStatement(Statement initializer, Expression condition,
                       Expression update, Statement body, SourcePosition position) {
        this.initializer = initializer;
        this.condition = condition;
        this.update = update;
        this.body = body;
        this.position = position;
    }

    public Statement getInitializer() {
        return initializer;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getUpdate() {
        return update;
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
