package parser.ast.statements;

import parser.ast.ASTVisitor;
import parser.ast.expressions.Expression;
import util.SourcePosition;

/**
 * Represents an if statement with optional else branch.
 */
public class IfStatement implements Statement {
    private final Expression condition;
    private final Statement thenBranch;
    private final Statement elseBranch;  // may be null
    private final SourcePosition position;

    public IfStatement(Expression condition, Statement thenBranch,
                      Statement elseBranch, SourcePosition position) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
        this.position = position;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getThenBranch() {
        return thenBranch;
    }

    public Statement getElseBranch() {
        return elseBranch;
    }

    public boolean hasElse() {
        return elseBranch != null;
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
