package parser.ast.declarations;

import parser.ast.ASTNode;
import parser.ast.ASTVisitor;
import parser.ast.Type;
import parser.ast.statements.BlockStatement;
import util.SourcePosition;
import java.util.List;

/**
 * Represents a method declaration.
 */
public class MethodDeclaration implements ASTNode {
    private final Type returnType;
    private final String name;
    private final List<Parameter> parameters;
    private final BlockStatement body;
    private final boolean isStatic;
    private final boolean isPublic;
    private final SourcePosition position;

    public MethodDeclaration(Type returnType, String name, List<Parameter> parameters,
                            BlockStatement body, boolean isStatic, boolean isPublic,
                            SourcePosition position) {
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.isStatic = isStatic;
        this.isPublic = isPublic;
        this.position = position;
    }

    public Type getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isPublic() {
        return isPublic;
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
