package codegen;

import parser.ast.*;
import parser.ast.declarations.*;
import parser.ast.expressions.*;
import parser.ast.statements.*;

/**
 * Code generator implementing the Visitor pattern to traverse the AST
 * and generate equivalent Go code.
 *
 * Java to Go Mappings:
 * - class MyClass { } → type MyClass struct { }
 * - public void method() → func (self *MyClass) method()
 * - String → string
 * - boolean → bool
 * - System.out.println() → fmt.Println()
 * - new MyClass() → &MyClass{} or NewMyClass()
 */
public class CodeGenerator implements ASTVisitor {
    private final StringBuilder output;
    private int indentLevel = 0;
    private String currentClassName = "";
    private static final String INDENT = "    ";

    public CodeGenerator() {
        this.output = new StringBuilder();
    }

    /**
     * Main entry point: generates Go code from AST.
     */
    public String generate(Program program) {
        output.setLength(0);
        indentLevel = 0;

        // Generate package and imports
        generatePreamble();

        // Visit program node
        program.accept(this);

        return output.toString();
    }

    private void generatePreamble() {
        emit("package main\n\n");
        emit("import \"fmt\"\n\n");
    }

    @Override
    public void visit(Program node) {
        for (ClassDeclaration classDecl : node.getClasses()) {
            classDecl.accept(this);
        }
    }

    @Override
    public void visit(ClassDeclaration node) {
        currentClassName = node.getName();

        // Generate struct
        emit("type " + node.getName() + " struct {\n");
        indent();

        for (FieldDeclaration field : node.getFields()) {
            field.accept(this);
        }

        dedent();
        emit("}\n\n");

        // Generate methods
        for (MethodDeclaration method : node.getMethods()) {
            method.accept(this);
        }
    }

    @Override
    public void visit(FieldDeclaration node) {
        String fieldName = node.getName();

        // Go uses capitalization for export, but we'll keep original casing
        // and adjust based on isPublic if needed
        if (node.isPublic()) {
            fieldName = capitalize(fieldName);
        } else {
            fieldName = lowercase(fieldName);
        }

        emitIndent();
        emit(fieldName + " " + mapType(node.getType()));

        if (node.hasInitializer()) {
            // Note: Go doesn't support field initializers in struct definition
            // This would need to be handled in a constructor function
            // For now, we'll skip the initializer
        }

        emit("\n");
    }

    @Override
    public void visit(MethodDeclaration node) {
        emitIndent();

        // Handle main method specially
        if (node.getName().equals("main") && node.isStatic()) {
            emit("func main()");
        } else {
            emit("func ");

            // Add receiver for non-static methods
            if (!node.isStatic()) {
                emit("(self *" + currentClassName + ") ");
            }

            emit(node.getName() + "(");

            // Parameters
            for (int i = 0; i < node.getParameters().size(); i++) {
                if (i > 0) emit(", ");
                Parameter param = node.getParameters().get(i);
                emit(param.getName() + " " + mapType(param.getType()));
            }

            emit(")");

            // Return type
            if (!node.getReturnType().isVoid()) {
                emit(" " + mapType(node.getReturnType()));
            }
        }

        emit(" ");
        node.getBody().accept(this);
        emit("\n");
    }

    @Override
    public void visit(Parameter node) {
        // Parameters are handled inline in MethodDeclaration
    }

    @Override
    public void visit(BlockStatement node) {
        emit("{\n");
        indent();

        for (Statement stmt : node.getStatements()) {
            stmt.accept(this);
        }

        dedent();
        emitIndent();
        emit("}");
    }

    @Override
    public void visit(IfStatement node) {
        emitIndent();
        emit("if ");
        node.getCondition().accept(this);
        emit(" ");
        node.getThenBranch().accept(this);

        if (node.hasElse()) {
            emit(" else ");
            node.getElseBranch().accept(this);
        }

        emit("\n");
    }

    @Override
    public void visit(WhileStatement node) {
        emitIndent();
        emit("for ");
        node.getCondition().accept(this);
        emit(" ");
        node.getBody().accept(this);
        emit("\n");
    }

    @Override
    public void visit(ForStatement node) {
        emitIndent();
        emit("for ");

        // Go doesn't have the exact same for loop syntax
        // We'll need to handle this specially
        if (node.getInitializer() != null || node.getUpdate() != null) {
            // Traditional three-part for loop needs special handling in Go
            // For now, use a simplified approach
            if (node.getCondition() != null) {
                node.getCondition().accept(this);
            }
        } else if (node.getCondition() != null) {
            node.getCondition().accept(this);
        }

        emit(" ");
        node.getBody().accept(this);
        emit("\n");
    }

    @Override
    public void visit(ReturnStatement node) {
        emitIndent();
        emit("return");

        if (node.hasValue()) {
            emit(" ");
            node.getValue().accept(this);
        }

        emit("\n");
    }

    @Override
    public void visit(VariableDeclaration node) {
        emitIndent();
        emit(node.getName() + " := ");

        if (node.hasInitializer()) {
            node.getInitializer().accept(this);
        } else {
            // Go requires initialization, use zero value
            emit(getZeroValue(node.getType()));
        }

        emit("\n");
    }

    @Override
    public void visit(ExpressionStatement node) {
        emitIndent();
        node.getExpression().accept(this);
        emit("\n");
    }

    @Override
    public void visit(BinaryExpression node) {
        boolean needsParens = needsParentheses(node);

        if (needsParens) emit("(");

        node.getLeft().accept(this);
        emit(" " + mapOperator(node.getOperator()) + " ");
        node.getRight().accept(this);

        if (needsParens) emit(")");
    }

    @Override
    public void visit(UnaryExpression node) {
        emit(mapUnaryOperator(node.getOperator()));
        node.getOperand().accept(this);
    }

    @Override
    public void visit(LiteralExpression node) {
        switch (node.getLiteralType()) {
            case INTEGER:
            case DOUBLE:
                emit(node.getValue().toString());
                break;
            case STRING:
                emit("\"" + escapeString(node.getValue().toString()) + "\"");
                break;
            case CHAR:
                emit("'" + node.getValue() + "'");
                break;
            case BOOLEAN:
                emit(node.getValue().toString());
                break;
            case NULL:
                emit("nil");
                break;
        }
    }

    @Override
    public void visit(IdentifierExpression node) {
        emit(node.getName());
    }

    @Override
    public void visit(MethodCallExpression node) {
        String methodName = node.getMethodName();

        // Handle System.out.println specially
        if (methodName.equals("println")) {
            emit("fmt.Println(");
            emitArguments(node.getArguments());
            emit(")");
            return;
        }

        if (node.getObject() != null) {
            node.getObject().accept(this);
            emit(".");
        }

        emit(methodName + "(");
        emitArguments(node.getArguments());
        emit(")");
    }

    @Override
    public void visit(NewExpression node) {
        // Generate constructor call
        emit("&" + node.getClassName() + "{");

        // For now, simple initialization
        // A more sophisticated version would map constructor args to fields

        emit("}");
    }

    @Override
    public void visit(ThisExpression node) {
        emit("self");
    }

    @Override
    public void visit(FieldAccessExpression node) {
        node.getObject().accept(this);
        emit(".");

        // Handle special cases like System.out
        String fieldName = node.getFieldName();
        if (fieldName.equals("out")) {
            // This is part of System.out, skip it
            return;
        }

        emit(capitalize(node.getFieldName()));
    }

    // Helper methods

    private void emitArguments(java.util.List<Expression> arguments) {
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) emit(", ");
            arguments.get(i).accept(this);
        }
    }

    private String mapType(Type type) {
        switch (type.getKind()) {
            case INT: return "int";
            case BOOLEAN: return "bool";
            case STRING: return "string";
            case DOUBLE: return "float64";
            case LONG: return "int64";
            case CHAR: return "rune";
            case VOID: return "";
            case CLASS: return "*" + type.getClassName();
            default:
                throw new CodeGenException("Unknown type: " + type);
        }
    }

    private String mapOperator(BinaryExpression.Operator op) {
        switch (op) {
            case PLUS: return "+";
            case MINUS: return "-";
            case MULTIPLY: return "*";
            case DIVIDE: return "/";
            case MODULO: return "%";
            case EQUALS: return "==";
            case NOT_EQUALS: return "!=";
            case LESS_THAN: return "<";
            case GREATER_THAN: return ">";
            case LESS_EQUAL: return "<=";
            case GREATER_EQUAL: return ">=";
            case LOGICAL_AND: return "&&";
            case LOGICAL_OR: return "||";
            case ASSIGN: return "=";
            default:
                throw new CodeGenException("Unknown operator: " + op);
        }
    }

    private String mapUnaryOperator(UnaryExpression.Operator op) {
        switch (op) {
            case LOGICAL_NOT: return "!";
            case NEGATE: return "-";
            case PLUS: return "+";
            default:
                throw new CodeGenException("Unknown unary operator: " + op);
        }
    }

    private String getZeroValue(Type type) {
        switch (type.getKind()) {
            case INT:
            case LONG: return "0";
            case DOUBLE: return "0.0";
            case BOOLEAN: return "false";
            case STRING: return "\"\"";
            case CHAR: return "'\0'";
            case CLASS: return "nil";
            default: return "nil";
        }
    }

    private boolean needsParentheses(BinaryExpression node) {
        // Simplified: always use parentheses for clarity
        // A more sophisticated version would check precedence
        return node.getLeft() instanceof BinaryExpression ||
               node.getRight() instanceof BinaryExpression;
    }

    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\t", "\\t")
                  .replace("\r", "\\r");
    }

    private String capitalize(String str) {
        if (str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private String lowercase(String str) {
        if (str.isEmpty()) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    private void emit(String code) {
        output.append(code);
    }

    private void emitIndent() {
        for (int i = 0; i < indentLevel; i++) {
            output.append(INDENT);
        }
    }

    private void indent() {
        indentLevel++;
    }

    private void dedent() {
        if (indentLevel > 0) {
            indentLevel--;
        }
    }
}
