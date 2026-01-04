package parser.ast.expressions;

import parser.ast.ASTVisitor;
import util.SourcePosition;
import java.util.List;

/**
 * Represents a method call (e.g., obj.method(args) or method(args)).
 */
public class MethodCallExpression implements Expression {
    private final Expression object;  // null for static calls or calls on 'this'
    private final String methodName;
    private final List<Expression> arguments;
    private final SourcePosition position;

    public MethodCallExpression(Expression object, String methodName,
                               List<Expression> arguments, SourcePosition position) {
        this.object = object;
        this.methodName = methodName;
        this.arguments = arguments;
        this.position = position;
    }

    public Expression getObject() {
        return object;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Expression> getArguments() {
        return arguments;
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
