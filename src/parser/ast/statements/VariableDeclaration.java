package parser.ast.statements;

import parser.ast.ASTVisitor;
import parser.ast.Type;
import parser.ast.expressions.Expression;
import util.SourcePosition;

/**
 * Represents a local variable declaration.
 */
public class VariableDeclaration implements Statement {
    private final Type type;
    private final String name;
    private final Expression initializer;  // may be null
    private final SourcePosition position;

    public VariableDeclaration(Type type, String name, Expression initializer,
                              SourcePosition position) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        this.position = position;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Expression getInitializer() {
        return initializer;
    }

    public boolean hasInitializer() {
        return initializer != null;
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
