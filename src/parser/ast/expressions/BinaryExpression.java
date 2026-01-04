package parser.ast.expressions;

import parser.ast.ASTVisitor;
import util.SourcePosition;

/**
 * Represents a binary expression (e.g., a + b, x == y).
 * Demonstrates operator precedence in the grammar.
 */
public class BinaryExpression implements Expression {
    public enum Operator {
        // Arithmetic
        PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
        // Comparison
        EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN, LESS_EQUAL, GREATER_EQUAL,
        // Logical
        LOGICAL_AND, LOGICAL_OR,
        // Assignment
        ASSIGN
    }

    private final Expression left;
    private final Operator operator;
    private final Expression right;
    private final SourcePosition position;

    public BinaryExpression(Expression left, Operator operator, Expression right, SourcePosition position) {
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.position = position;
    }

    public Expression getLeft() {
        return left;
    }

    public Operator getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
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
