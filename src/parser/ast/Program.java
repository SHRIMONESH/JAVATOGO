package parser.ast;

import parser.ast.declarations.ClassDeclaration;
import util.SourcePosition;
import java.util.List;

/**
 * Root node of the AST representing the entire program.
 * Contains one or more class declarations.
 */
public class Program implements ASTNode {
    private final List<ClassDeclaration> classes;
    private final SourcePosition position;

    public Program(List<ClassDeclaration> classes, SourcePosition position) {
        this.classes = classes;
        this.position = position;
    }

    public List<ClassDeclaration> getClasses() {
        return classes;
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
