package parser.ast.expressions;

import parser.ast.ASTVisitor;
import util.SourcePosition;

/**
 * Represents a unary expression (e.g., -x, !flag).
 */
public class UnaryExpression implements Expression {
    public enum Operator {
        LOGICAL_NOT, NEGATE, PLUS
    }

    private final Operator operator;
    private final Expression operand;
    private final SourcePosition position;

    public UnaryExpression(Operator operator, Expression operand, SourcePosition position) {
        this.operator = operator;
        this.operand = operand;
        this.position = position;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
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
