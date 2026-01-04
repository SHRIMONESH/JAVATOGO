package parser.ast.declarations;

import parser.ast.ASTNode;
import parser.ast.ASTVisitor;
import parser.ast.Type;
import util.SourcePosition;

/**
 * Represents a method parameter.
 */
public class Parameter implements ASTNode {
    private final Type type;
    private final String name;
    private final SourcePosition position;

    public Parameter(Type type, String name, SourcePosition position) {
        this.type = type;
        this.name = name;
        this.position = position;
    }

    public Type getType() {
        return type;
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
