package parser.ast.declarations;

import parser.ast.ASTNode;
import parser.ast.ASTVisitor;
import util.SourcePosition;
import java.util.List;

/**
 * Represents a class declaration.
 */
public class ClassDeclaration implements ASTNode {
    private final String name;
    private final List<FieldDeclaration> fields;
    private final List<MethodDeclaration> methods;
    private final SourcePosition position;

    public ClassDeclaration(String name, List<FieldDeclaration> fields,
                           List<MethodDeclaration> methods, SourcePosition position) {
        this.name = name;
        this.fields = fields;
        this.methods = methods;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public List<FieldDeclaration> getFields() {
        return fields;
    }

    public List<MethodDeclaration> getMethods() {
        return methods;
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
