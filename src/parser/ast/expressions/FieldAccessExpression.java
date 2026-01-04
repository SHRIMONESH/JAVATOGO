package parser.ast.expressions;

import parser.ast.ASTVisitor;
import util.SourcePosition;

/**
 * Represents field access (e.g., obj.field).
 */
public class FieldAccessExpression implements Expression {
    private final Expression object;
    private final String fieldName;
    private final SourcePosition position;

    public FieldAccessExpression(Expression object, String fieldName, SourcePosition position) {
        this.object = object;
        this.fieldName = fieldName;
        this.position = position;
    }

    public Expression getObject() {
        return object;
    }

    public String getFieldName() {
        return fieldName;
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
