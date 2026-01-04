package parser;

import lexer.*;
import parser.ast.*;
import parser.ast.declarations.*;
import parser.ast.expressions.*;
import parser.ast.statements.*;
import util.SourcePosition;

import java.util.ArrayList;
import java.util.List;

/**
 * Recursive descent parser implementing the Java subset CFG.
 *
 * Theory of Computation:
 * - Implements LL(1) parsing with one-token lookahead
 * - Each grammar production rule maps to a parsing method
 * - Top-down parsing strategy (starts from Program, works down to terminals)
 * - Predictive parsing without backtracking
 *
 * Grammar (simplified):
 * Program ::= ClassDeclaration+
 * ClassDeclaration ::= "class" ID "{" ClassMember* "}"
 * Statement ::= If | While | For | Return | VarDecl | ExprStmt | Block
 * Expression ::= Assignment | LogicalOr | ... | Primary
 */
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Main entry point: parses the entire program.
     * Program ::= ClassDeclaration+
     */
    public Program parseProgram() {
        List<ClassDeclaration> classes = new ArrayList<>();
        SourcePosition position = peek().getPosition();

        while (!isAtEnd()) {
            classes.add(parseClassDeclaration());
        }

        if (classes.isEmpty()) {
            throw new ParserException("Expected at least one class declaration", position);
        }

        return new Program(classes, position);
    }

    /**
     * ClassDeclaration ::= "class" ID "{" ClassMember* "}"
     */
    private ClassDeclaration parseClassDeclaration() {
        SourcePosition position = peek().getPosition();

        consume(TokenType.CLASS, "Expected 'class' keyword");
        String className = consume(TokenType.IDENTIFIER, "Expected class name").getLexeme();
        consume(TokenType.LBRACE, "Expected '{' after class name");

        List<FieldDeclaration> fields = new ArrayList<>();
        List<MethodDeclaration> methods = new ArrayList<>();

        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            // Parse class member (field or method)
            boolean isStatic = false;
            boolean isPublic = false;

            // Parse modifiers
            if (match(TokenType.PUBLIC)) {
                isPublic = true;
            } else if (match(TokenType.PRIVATE)) {
                isPublic = false;
            }

            if (match(TokenType.STATIC)) {
                isStatic = true;
            }

            // Parse type
            Type type = parseType();
            String memberName = consume(TokenType.IDENTIFIER, "Expected member name").getLexeme();

            if (check(TokenType.LPAREN)) {
                // Method declaration
                methods.add(parseMethodDeclaration(type, memberName, isStatic, isPublic, position));
            } else {
                // Field declaration
                fields.add(parseFieldDeclaration(type, memberName, isPublic, isStatic, position));
            }
        }

        consume(TokenType.RBRACE, "Expected '}' after class body");

        return new ClassDeclaration(className, fields, methods, position);
    }

    /**
     * Parse field declaration: TYPE ID ("=" Expression)? ";"
     */
    private FieldDeclaration parseFieldDeclaration(Type type, String name,
                                                    boolean isPublic, boolean isStatic,
                                                    SourcePosition position) {
        Expression initializer = null;

        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }

        consume(TokenType.SEMICOLON, "Expected ';' after field declaration");

        return new FieldDeclaration(type, name, initializer, isPublic, isStatic, position);
    }

    /**
     * Parse method declaration: TYPE ID "(" ParameterList? ")" Block
     */
    private MethodDeclaration parseMethodDeclaration(Type returnType, String name,
                                                     boolean isStatic, boolean isPublic,
                                                     SourcePosition position) {
        consume(TokenType.LPAREN, "Expected '(' after method name");

        List<Parameter> parameters = new ArrayList<>();
        if (!check(TokenType.RPAREN)) {
            do {
                Type paramType = parseType();
                String paramName = consume(TokenType.IDENTIFIER, "Expected parameter name").getLexeme();
                parameters.add(new Parameter(paramType, paramName, peek().getPosition()));
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RPAREN, "Expected ')' after parameters");

        BlockStatement body = parseBlockStatement();

        return new MethodDeclaration(returnType, name, parameters, body,
                                    isStatic, isPublic, position);
    }

    /**
     * Parse type: "int" | "boolean" | "String" | "void" | ID
     */
    private Type parseType() {
        if (match(TokenType.INT)) return new Type(Type.Kind.INT);
        if (match(TokenType.BOOLEAN)) return new Type(Type.Kind.BOOLEAN);
        if (match(TokenType.STRING)) return new Type(Type.Kind.STRING);
        if (match(TokenType.DOUBLE)) return new Type(Type.Kind.DOUBLE);
        if (match(TokenType.LONG)) return new Type(Type.Kind.LONG);
        if (match(TokenType.CHAR)) return new Type(Type.Kind.CHAR);
        if (match(TokenType.VOID)) return new Type(Type.Kind.VOID);

        if (check(TokenType.IDENTIFIER)) {
            return new Type(advance().getLexeme());
        }

        throw new ParserException("Expected type", peek().getPosition());
    }

    /**
     * Statement ::= If | While | For | Return | VarDecl | ExprStmt | Block
     */
    private Statement parseStatement() {
        if (check(TokenType.IF)) return parseIfStatement();
        if (check(TokenType.WHILE)) return parseWhileStatement();
        if (check(TokenType.FOR)) return parseForStatement();
        if (check(TokenType.RETURN)) return parseReturnStatement();
        if (check(TokenType.LBRACE)) return parseBlockStatement();

        // Check for variable declaration (type identifier)
        // Need to distinguish from expression statement starting with identifier
        if (isTypeKeyword(peek().getType()) && !isExpressionStatement()) {
            return parseVariableDeclaration();
        }

        // Otherwise, expression statement
        return parseExpressionStatement();
    }

    /**
     * IfStatement ::= "if" "(" Expression ")" Statement ("else" Statement)?
     */
    private IfStatement parseIfStatement() {
        SourcePosition position = peek().getPosition();
        consume(TokenType.IF, "Expected 'if'");
        consume(TokenType.LPAREN, "Expected '(' after 'if'");
        Expression condition = parseExpression();
        consume(TokenType.RPAREN, "Expected ')' after condition");

        Statement thenBranch = parseStatement();
        Statement elseBranch = null;

        if (match(TokenType.ELSE)) {
            elseBranch = parseStatement();
        }

        return new IfStatement(condition, thenBranch, elseBranch, position);
    }

    /**
     * WhileStatement ::= "while" "(" Expression ")" Statement
     */
    private WhileStatement parseWhileStatement() {
        SourcePosition position = peek().getPosition();
        consume(TokenType.WHILE, "Expected 'while'");
        consume(TokenType.LPAREN, "Expected '(' after 'while'");
        Expression condition = parseExpression();
        consume(TokenType.RPAREN, "Expected ')' after condition");

        Statement body = parseStatement();

        return new WhileStatement(condition, body, position);
    }

    /**
     * ForStatement ::= "for" "(" ForInit? ";" Expression? ";" ForUpdate? ")" Statement
     */
    private ForStatement parseForStatement() {
        SourcePosition position = peek().getPosition();
        consume(TokenType.FOR, "Expected 'for'");
        consume(TokenType.LPAREN, "Expected '(' after 'for'");

        // Initializer
        Statement initializer = null;
        if (!check(TokenType.SEMICOLON)) {
            if (isTypeKeyword(peek().getType())) {
                initializer = parseVariableDeclaration();
            } else {
                initializer = parseExpressionStatement();
            }
        } else {
            consume(TokenType.SEMICOLON, "Expected ';'");
        }

        // Condition
        Expression condition = null;
        if (!check(TokenType.SEMICOLON)) {
            condition = parseExpression();
        }
        consume(TokenType.SEMICOLON, "Expected ';' after condition");

        // Update
        Expression update = null;
        if (!check(TokenType.RPAREN)) {
            update = parseExpression();
        }
        consume(TokenType.RPAREN, "Expected ')' after for clauses");

        Statement body = parseStatement();

        return new ForStatement(initializer, condition, update, body, position);
    }

    /**
     * ReturnStatement ::= "return" Expression? ";"
     */
    private ReturnStatement parseReturnStatement() {
        SourcePosition position = peek().getPosition();
        consume(TokenType.RETURN, "Expected 'return'");

        Expression value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }

        consume(TokenType.SEMICOLON, "Expected ';' after return statement");

        return new ReturnStatement(value, position);
    }

    /**
     * BlockStatement ::= "{" Statement* "}"
     */
    private BlockStatement parseBlockStatement() {
        SourcePosition position = peek().getPosition();
        consume(TokenType.LBRACE, "Expected '{'");

        List<Statement> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statements.add(parseStatement());
        }

        consume(TokenType.RBRACE, "Expected '}'");

        return new BlockStatement(statements, position);
    }

    /**
     * VariableDeclaration ::= Type ID ("=" Expression)? ";"
     */
    private VariableDeclaration parseVariableDeclaration() {
        SourcePosition position = peek().getPosition();
        Type type = parseType();
        String name = consume(TokenType.IDENTIFIER, "Expected variable name").getLexeme();

        Expression initializer = null;
        if (match(TokenType.ASSIGN)) {
            initializer = parseExpression();
        }

        consume(TokenType.SEMICOLON, "Expected ';' after variable declaration");

        return new VariableDeclaration(type, name, initializer, position);
    }

    /**
     * ExpressionStatement ::= Expression ";"
     */
    private ExpressionStatement parseExpressionStatement() {
        SourcePosition position = peek().getPosition();
        Expression expr = parseExpression();
        consume(TokenType.SEMICOLON, "Expected ';' after expression");
        return new ExpressionStatement(expr, position);
    }

    /**
     * Expression ::= Assignment
     */
    private Expression parseExpression() {
        return parseAssignment();
    }

    /**
     * Assignment ::= LogicalOr ("=" Assignment)?
     */
    private Expression parseAssignment() {
        Expression expr = parseLogicalOr();

        if (match(TokenType.ASSIGN)) {
            SourcePosition position = previous().getPosition();
            Expression value = parseAssignment();
            return new BinaryExpression(expr, BinaryExpression.Operator.ASSIGN, value, position);
        }

        return expr;
    }

    /**
     * LogicalOr ::= LogicalAnd ("||" LogicalAnd)*
     */
    private Expression parseLogicalOr() {
        Expression expr = parseLogicalAnd();

        while (match(TokenType.LOGICAL_OR)) {
            SourcePosition position = previous().getPosition();
            Expression right = parseLogicalAnd();
            expr = new BinaryExpression(expr, BinaryExpression.Operator.LOGICAL_OR, right, position);
        }

        return expr;
    }

    /**
     * LogicalAnd ::= Equality ("&&" Equality)*
     */
    private Expression parseLogicalAnd() {
        Expression expr = parseEquality();

        while (match(TokenType.LOGICAL_AND)) {
            SourcePosition position = previous().getPosition();
            Expression right = parseEquality();
            expr = new BinaryExpression(expr, BinaryExpression.Operator.LOGICAL_AND, right, position);
        }

        return expr;
    }

    /**
     * Equality ::= Relational (("==" | "!=") Relational)*
     */
    private Expression parseEquality() {
        Expression expr = parseRelational();

        while (match(TokenType.EQUALS, TokenType.NOT_EQUALS)) {
            Token operator = previous();
            SourcePosition position = operator.getPosition();
            Expression right = parseRelational();

            BinaryExpression.Operator op = operator.getType() == TokenType.EQUALS ?
                BinaryExpression.Operator.EQUALS : BinaryExpression.Operator.NOT_EQUALS;

            expr = new BinaryExpression(expr, op, right, position);
        }

        return expr;
    }

    /**
     * Relational ::= Additive (("<" | ">" | "<=" | ">=") Additive)*
     */
    private Expression parseRelational() {
        Expression expr = parseAdditive();

        while (match(TokenType.LESS_THAN, TokenType.GREATER_THAN,
                    TokenType.LESS_EQUAL, TokenType.GREATER_EQUAL)) {
            Token operator = previous();
            SourcePosition position = operator.getPosition();
            Expression right = parseAdditive();

            BinaryExpression.Operator op;
            switch (operator.getType()) {
                case LESS_THAN: op = BinaryExpression.Operator.LESS_THAN; break;
                case GREATER_THAN: op = BinaryExpression.Operator.GREATER_THAN; break;
                case LESS_EQUAL: op = BinaryExpression.Operator.LESS_EQUAL; break;
                case GREATER_EQUAL: op = BinaryExpression.Operator.GREATER_EQUAL; break;
                default: throw new ParserException("Unknown operator", position);
            }

            expr = new BinaryExpression(expr, op, right, position);
        }

        return expr;
    }

    /**
     * Additive ::= Multiplicative (("+" | "-") Multiplicative)*
     */
    private Expression parseAdditive() {
        Expression expr = parseMultiplicative();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            SourcePosition position = operator.getPosition();
            Expression right = parseMultiplicative();

            BinaryExpression.Operator op = operator.getType() == TokenType.PLUS ?
                BinaryExpression.Operator.PLUS : BinaryExpression.Operator.MINUS;

            expr = new BinaryExpression(expr, op, right, position);
        }

        return expr;
    }

    /**
     * Multiplicative ::= Unary (("*" | "/" | "%") Unary)*
     */
    private Expression parseMultiplicative() {
        Expression expr = parseUnary();

        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            Token operator = previous();
            SourcePosition position = operator.getPosition();
            Expression right = parseUnary();

            BinaryExpression.Operator op;
            switch (operator.getType()) {
                case STAR: op = BinaryExpression.Operator.MULTIPLY; break;
                case SLASH: op = BinaryExpression.Operator.DIVIDE; break;
                case PERCENT: op = BinaryExpression.Operator.MODULO; break;
                default: throw new ParserException("Unknown operator", position);
            }

            expr = new BinaryExpression(expr, op, right, position);
        }

        return expr;
    }

    /**
     * Unary ::= ("!" | "-" | "+") Unary | Postfix
     */
    private Expression parseUnary() {
        if (match(TokenType.LOGICAL_NOT, TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            SourcePosition position = operator.getPosition();
            Expression operand = parseUnary();

            UnaryExpression.Operator op;
            switch (operator.getType()) {
                case LOGICAL_NOT: op = UnaryExpression.Operator.LOGICAL_NOT; break;
                case MINUS: op = UnaryExpression.Operator.NEGATE; break;
                case PLUS: op = UnaryExpression.Operator.PLUS; break;
                default: throw new ParserException("Unknown operator", position);
            }

            return new UnaryExpression(op, operand, position);
        }

        return parsePostfix();
    }

    /**
     * Postfix ::= Primary ("." ID | "(" ArgumentList? ")")*
     */
    private Expression parsePostfix() {
        Expression expr = parsePrimary();

        while (true) {
            if (match(TokenType.DOT)) {
                SourcePosition position = previous().getPosition();
                String fieldName = consume(TokenType.IDENTIFIER, "Expected field name after '.'").getLexeme();

                if (match(TokenType.LPAREN)) {
                    // Method call
                    List<Expression> args = parseArgumentList();
                    consume(TokenType.RPAREN, "Expected ')' after arguments");
                    expr = new MethodCallExpression(expr, fieldName, args, position);
                } else {
                    // Field access
                    expr = new FieldAccessExpression(expr, fieldName, position);
                }
            } else if (check(TokenType.LPAREN) && expr instanceof IdentifierExpression) {
                // Direct method call (without dot)
                SourcePosition position = peek().getPosition();
                String methodName = ((IdentifierExpression) expr).getName();
                match(TokenType.LPAREN);
                List<Expression> args = parseArgumentList();
                consume(TokenType.RPAREN, "Expected ')' after arguments");
                expr = new MethodCallExpression(null, methodName, args, position);
            } else {
                break;
            }
        }

        return expr;
    }

    /**
     * Primary ::= INTEGER | DOUBLE | STRING | CHAR | BOOLEAN | NULL
     *           | ID | "this" | "new" ID "(" ArgumentList? ")"
     *           | "(" Expression ")"
     */
    private Expression parsePrimary() {
        SourcePosition position = peek().getPosition();

        // Literals
        if (match(TokenType.INTEGER_LITERAL)) {
            Token token = previous();
            return new LiteralExpression(token.getLiteral(),
                LiteralExpression.LiteralType.INTEGER, position);
        }

        if (match(TokenType.DOUBLE_LITERAL)) {
            Token token = previous();
            return new LiteralExpression(token.getLiteral(),
                LiteralExpression.LiteralType.DOUBLE, position);
        }

        if (match(TokenType.STRING_LITERAL)) {
            Token token = previous();
            return new LiteralExpression(token.getLiteral(),
                LiteralExpression.LiteralType.STRING, position);
        }

        if (match(TokenType.CHAR_LITERAL)) {
            Token token = previous();
            return new LiteralExpression(token.getLiteral(),
                LiteralExpression.LiteralType.CHAR, position);
        }

        if (match(TokenType.TRUE, TokenType.FALSE)) {
            Token token = previous();
            return new LiteralExpression(token.getLiteral(),
                LiteralExpression.LiteralType.BOOLEAN, position);
        }

        if (match(TokenType.NULL)) {
            return new LiteralExpression(null, LiteralExpression.LiteralType.NULL, position);
        }

        // this
        if (match(TokenType.THIS)) {
            return new ThisExpression(position);
        }

        // new ClassName(args)
        if (match(TokenType.NEW)) {
            String className = consume(TokenType.IDENTIFIER, "Expected class name after 'new'").getLexeme();
            consume(TokenType.LPAREN, "Expected '(' after class name");
            List<Expression> args = parseArgumentList();
            consume(TokenType.RPAREN, "Expected ')' after arguments");
            return new NewExpression(className, args, position);
        }

        // Identifier
        if (match(TokenType.IDENTIFIER)) {
            return new IdentifierExpression(previous().getLexeme(), position);
        }

        // Parenthesized expression
        if (match(TokenType.LPAREN)) {
            Expression expr = parseExpression();
            consume(TokenType.RPAREN, "Expected ')' after expression");
            return expr;
        }

        throw new ParserException("Expected expression", position);
    }

    /**
     * ArgumentList ::= Expression ("," Expression)*
     */
    private List<Expression> parseArgumentList() {
        List<Expression> args = new ArrayList<>();

        if (!check(TokenType.RPAREN)) {
            do {
                args.add(parseExpression());
            } while (match(TokenType.COMMA));
        }

        return args;
    }

    // Helper methods

    private boolean isTypeKeyword(TokenType type) {
        return type == TokenType.INT || type == TokenType.BOOLEAN ||
               type == TokenType.STRING || type == TokenType.DOUBLE ||
               type == TokenType.LONG || type == TokenType.CHAR ||
               type == TokenType.VOID || type == TokenType.IDENTIFIER;
    }

    /**
     * Lookahead to check if this is an expression statement (not a variable declaration).
     * Expression statements starting with identifiers have patterns like:
     * - ID.ID... (field access/method call)
     * - ID(...) (method call)
     * - ID = ... (assignment to existing variable)
     */
    private boolean isExpressionStatement() {
        if (current + 1 < tokens.size()) {
            Token next = tokens.get(current + 1);
            // If next token is . or ( or =, it's an expression statement
            return next.getType() == TokenType.DOT ||
                   next.getType() == TokenType.LPAREN ||
                   next.getType() == TokenType.ASSIGN;
        }
        return false;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new ParserException(message, peek().getPosition());
    }
}
