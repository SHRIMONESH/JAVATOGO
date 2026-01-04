package parser.ast;

import parser.ast.declarations.*;
import parser.ast.expressions.*;
import parser.ast.statements.*;

/**
 * Visitor interface for traversing the AST.
 * Implements the Visitor design pattern for tree traversal.
 */
public interface ASTVisitor {
    // Program
    void visit(Program node);

    // Declarations
    void visit(ClassDeclaration node);
    void visit(MethodDeclaration node);
    void visit(FieldDeclaration node);
    void visit(Parameter node);

    // Statements
    void visit(BlockStatement node);
    void visit(IfStatement node);
    void visit(WhileStatement node);
    void visit(ForStatement node);
    void visit(ReturnStatement node);
    void visit(VariableDeclaration node);
    void visit(ExpressionStatement node);

    // Expressions
    void visit(BinaryExpression node);
    void visit(UnaryExpression node);
    void visit(LiteralExpression node);
    void visit(IdentifierExpression node);
    void visit(MethodCallExpression node);
    void visit(NewExpression node);
    void visit(ThisExpression node);
    void visit(FieldAccessExpression node);
}
