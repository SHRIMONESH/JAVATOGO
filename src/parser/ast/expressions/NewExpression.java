package parser.ast.expressions;

import parser.ast.ASTVisitor;
import util.SourcePosition;
import java.util.List;

/**
 * Represents object instantiation (e.g., new MyClass(args)).
 */
public class NewExpression implements Expression {
    private final String className;
    private final List<Expression> arguments;
    private final SourcePosition position;

    public NewExpression(String className, List<Expression> arguments, SourcePosition position) {
        this.className = className;
        this.arguments = arguments;
        this.position = position;
    }

    public String getClassName() {
        return className;
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
