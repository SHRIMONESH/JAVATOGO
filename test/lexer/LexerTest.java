import lexer.*;

/**
 * Unit tests for the Lexer.
 * Tests DFA implementation for various token types.
 */
public class LexerTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("Running Lexer Tests...\n");

        testKeywords();
        testIdentifiers();
        testIntegerLiterals();
        testDoubleLiterals();
        testStringLiterals();
        testOperators();
        testDelimiters();
        testComments();
        testComplexExpression();

        System.out.println("\n" + testsPassed + "/" + testsRun + " tests passed");
        if (testsPassed == testsRun) {
            System.out.println("All tests passed!");
        } else {
            System.out.println((testsRun - testsPassed) + " tests failed");
            System.exit(1);
        }
    }

    private static void testKeywords() {
        String source = "class public private int boolean void if else while for return";
        Lexer lexer = new Lexer(source);

        assertToken(lexer.nextToken(), TokenType.CLASS, "class");
        assertToken(lexer.nextToken(), TokenType.PUBLIC, "public");
        assertToken(lexer.nextToken(), TokenType.PRIVATE, "private");
        assertToken(lexer.nextToken(), TokenType.INT, "int");
        assertToken(lexer.nextToken(), TokenType.BOOLEAN, "boolean");
        assertToken(lexer.nextToken(), TokenType.VOID, "void");
        assertToken(lexer.nextToken(), TokenType.IF, "if");
        assertToken(lexer.nextToken(), TokenType.ELSE, "else");
        assertToken(lexer.nextToken(), TokenType.WHILE, "while");
        assertToken(lexer.nextToken(), TokenType.FOR, "for");
        assertToken(lexer.nextToken(), TokenType.RETURN, "return");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }

    private static void testIdentifiers() {
        String source = "myVariable _underscore CamelCase snake_case var123";
        Lexer lexer = new Lexer(source);

        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "myVariable");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "_underscore");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "CamelCase");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "snake_case");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "var123");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }

    private static void testIntegerLiterals() {
        String source = "0 42 12345";
        Lexer lexer = new Lexer(source);

        Token t1 = lexer.nextToken();
        assertToken(t1, TokenType.INTEGER_LITERAL, "0");
        assertEqual(t1.getLiteral(), 0, "Integer value for 0");

        Token t2 = lexer.nextToken();
        assertToken(t2, TokenType.INTEGER_LITERAL, "42");
        assertEqual(t2.getLiteral(), 42, "Integer value for 42");

        Token t3 = lexer.nextToken();
        assertToken(t3, TokenType.INTEGER_LITERAL, "12345");
        assertEqual(t3.getLiteral(), 12345, "Integer value for 12345");

        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }

    private static void testDoubleLiterals() {
        String source = "3.14 0.5 123.456";
        Lexer lexer = new Lexer(source);

        Token t1 = lexer.nextToken();
        assertToken(t1, TokenType.DOUBLE_LITERAL, "3.14");
        assertEqual(t1.getLiteral(), 3.14, "Double value for 3.14");

        Token t2 = lexer.nextToken();
        assertToken(t2, TokenType.DOUBLE_LITERAL, "0.5");

        Token t3 = lexer.nextToken();
        assertToken(t3, TokenType.DOUBLE_LITERAL, "123.456");

        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }

    private static void testStringLiterals() {
        String source = "\"hello\" \"world\" \"with\\nescapes\"";
        Lexer lexer = new Lexer(source);

        Token t1 = lexer.nextToken();
        assertToken(t1, TokenType.STRING_LITERAL, "\"hello\"");
        assertEqual(t1.getLiteral(), "hello", "String value for hello");

        Token t2 = lexer.nextToken();
        assertToken(t2, TokenType.STRING_LITERAL, "\"world\"");
        assertEqual(t2.getLiteral(), "world", "String value for world");

        Token t3 = lexer.nextToken();
        assertToken(t3, TokenType.STRING_LITERAL, "\"with\\nescapes\"");
        assertEqual(t3.getLiteral(), "with\nescapes", "String with escape");

        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }

    private static void testOperators() {
        String source = "+ - * / % ++ -- == != < > <= >= && || ! =";
        Lexer lexer = new Lexer(source);

        assertToken(lexer.nextToken(), TokenType.PLUS, "+");
        assertToken(lexer.nextToken(), TokenType.MINUS, "-");
        assertToken(lexer.nextToken(), TokenType.STAR, "*");
        assertToken(lexer.nextToken(), TokenType.SLASH, "/");
        assertToken(lexer.nextToken(), TokenType.PERCENT, "%");
        assertToken(lexer.nextToken(), TokenType.INCREMENT, "++");
        assertToken(lexer.nextToken(), TokenType.DECREMENT, "--");
        assertToken(lexer.nextToken(), TokenType.EQUALS, "==");
        assertToken(lexer.nextToken(), TokenType.NOT_EQUALS, "!=");
        assertToken(lexer.nextToken(), TokenType.LESS_THAN, "<");
        assertToken(lexer.nextToken(), TokenType.GREATER_THAN, ">");
        assertToken(lexer.nextToken(), TokenType.LESS_EQUAL, "<=");
        assertToken(lexer.nextToken(), TokenType.GREATER_EQUAL, ">=");
        assertToken(lexer.nextToken(), TokenType.LOGICAL_AND, "&&");
        assertToken(lexer.nextToken(), TokenType.LOGICAL_OR, "||");
        assertToken(lexer.nextToken(), TokenType.LOGICAL_NOT, "!");
        assertToken(lexer.nextToken(), TokenType.ASSIGN, "=");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }

    private static void testDelimiters() {
        String source = "( ) { } [ ] ; , . :";
        Lexer lexer = new Lexer(source);

        assertToken(lexer.nextToken(), TokenType.LPAREN, "(");
        assertToken(lexer.nextToken(), TokenType.RPAREN, ")");
        assertToken(lexer.nextToken(), TokenType.LBRACE, "{");
        assertToken(lexer.nextToken(), TokenType.RBRACE, "}");
        assertToken(lexer.nextToken(), TokenType.LBRACKET, "[");
        assertToken(lexer.nextToken(), TokenType.RBRACKET, "]");
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";");
        assertToken(lexer.nextToken(), TokenType.COMMA, ",");
        assertToken(lexer.nextToken(), TokenType.DOT, ".");
        assertToken(lexer.nextToken(), TokenType.COLON, ":");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }

    private static void testComments() {
        String source = "int x; // single line comment\nint y; /* multi\nline\ncomment */ int z;";
        Lexer lexer = new Lexer(source);

        assertToken(lexer.nextToken(), TokenType.INT, "int");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "x");
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";");
        assertToken(lexer.nextToken(), TokenType.INT, "int");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "y");
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";");
        assertToken(lexer.nextToken(), TokenType.INT, "int");
        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "z");
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }

    private static void testComplexExpression() {
        String source = "x = 5 + 3 * 2;";
        Lexer lexer = new Lexer(source);

        assertToken(lexer.nextToken(), TokenType.IDENTIFIER, "x");
        assertToken(lexer.nextToken(), TokenType.ASSIGN, "=");
        assertToken(lexer.nextToken(), TokenType.INTEGER_LITERAL, "5");
        assertToken(lexer.nextToken(), TokenType.PLUS, "+");
        assertToken(lexer.nextToken(), TokenType.INTEGER_LITERAL, "3");
        assertToken(lexer.nextToken(), TokenType.STAR, "*");
        assertToken(lexer.nextToken(), TokenType.INTEGER_LITERAL, "2");
        assertToken(lexer.nextToken(), TokenType.SEMICOLON, ";");
        assertToken(lexer.nextToken(), TokenType.EOF, "");
    }

    private static void assertToken(Token token, TokenType expectedType, String expectedLexeme) {
        testsRun++;
        if (token.getType() == expectedType && token.getLexeme().equals(expectedLexeme)) {
            testsPassed++;
            System.out.println("✓ Token: " + expectedType + " '" + expectedLexeme + "'");
        } else {
            System.out.println("✗ Expected: " + expectedType + " '" + expectedLexeme + "', " +
                             "Got: " + token.getType() + " '" + token.getLexeme() + "'");
        }
    }

    private static void assertEqual(Object actual, Object expected, String description) {
        testsRun++;
        if ((actual == null && expected == null) ||
            (actual != null && actual.equals(expected))) {
            testsPassed++;
            System.out.println("✓ " + description);
        } else {
            System.out.println("✗ " + description + ": Expected " + expected + ", Got " + actual);
        }
    }
}
