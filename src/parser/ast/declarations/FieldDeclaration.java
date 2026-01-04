package parser.ast.declarations;

import parser.ast.ASTNode;
import parser.ast.ASTVisitor;
import parser.ast.Type;
import parser.ast.expressions.Expression;
import util.SourcePosition;

/**
 * Represents a field (instance variable) declaration.
 */
public class FieldDeclaration implements ASTNode {
    private final Type type;
    private final String name;
    private final Expression initializer;  // may be null
    private final boolean isPublic;
    private final boolean isStatic;
    private final SourcePosition position;

    public FieldDeclaration(Type type, String name, Expression initializer,
                           boolean isPublic, boolean isStatic, SourcePosition position) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
        this.isPublic = isPublic;
        this.isStatic = isStatic;
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

    public boolean isPublic() {
        return isPublic;
    }

    public boolean isStatic() {
        return isStatic;
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
