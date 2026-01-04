package parser.ast.statements;

import parser.ast.ASTVisitor;
import util.SourcePosition;
import java.util.List;

/**
 * Represents a block of statements enclosed in braces.
 */
public class BlockStatement implements Statement {
    private final List<Statement> statements;
    private final SourcePosition position;

    public BlockStatement(List<Statement> statements, SourcePosition position) {
        this.statements = statements;
        this.position = position;
    }

    public List<Statement> getStatements() {
        return statements;
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
