package lexer;

import util.SourcePosition;
import java.util.HashMap;
import java.util.Map;

/**
 * Lexical analyzer implementing DFA (Deterministic Finite Automaton) for token recognition.
 *
 * Theory of Computation:
 * - Implements multiple DFAs for different token patterns
 * - Uses regular expressions conceptually (identifiers, numbers, strings)
 * - State transitions based on character classification
 * - Keyword recognition via hash table lookup after identifier DFA
 */
public class Lexer {
    private final String source;
    private int position = 0;
    private int line = 1;
    private int column = 1;
    private char currentChar;

    // Keyword lookup table - maps identifier strings to keyword token types
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("class", TokenType.CLASS);
        keywords.put("interface", TokenType.INTERFACE);
        keywords.put("extends", TokenType.EXTENDS);
        keywords.put("implements", TokenType.IMPLEMENTS);
        keywords.put("public", TokenType.PUBLIC);
        keywords.put("private", TokenType.PRIVATE);
        keywords.put("protected", TokenType.PROTECTED);
        keywords.put("static", TokenType.STATIC);
        keywords.put("final", TokenType.FINAL);
        keywords.put("void", TokenType.VOID);
        keywords.put("int", TokenType.INT);
        keywords.put("boolean", TokenType.BOOLEAN);
        keywords.put("String", TokenType.STRING);
        keywords.put("double", TokenType.DOUBLE);
        keywords.put("long", TokenType.LONG);
        keywords.put("char", TokenType.CHAR);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("for", TokenType.FOR);
        keywords.put("do", TokenType.DO);
        keywords.put("return", TokenType.RETURN);
        keywords.put("new", TokenType.NEW);
        keywords.put("this", TokenType.THIS);
        keywords.put("super", TokenType.SUPER);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("null", TokenType.NULL);
        keywords.put("break", TokenType.BREAK);
        keywords.put("continue", TokenType.CONTINUE);
    }

    public Lexer(String source) {
        this.source = source;
        this.currentChar = source.isEmpty() ? '\0' : source.charAt(0);
    }

    /**
     * Main DFA dispatcher - State 0.
     * Reads the next token from the input stream.
     */
    public Token nextToken() {
        skipWhitespace();

        if (isAtEnd()) {
            return new Token(TokenType.EOF, "", line, column);
        }

        int tokenLine = line;
        int tokenColumn = column;

        // DFA State 0: Dispatch based on first character
        if (isLetter(currentChar) || currentChar == '_') {
            return scanIdentifier(tokenLine, tokenColumn);
        }

        if (isDigit(currentChar)) {
            return scanNumber(tokenLine, tokenColumn);
        }

        if (currentChar == '"') {
            return scanString(tokenLine, tokenColumn);
        }

        if (currentChar == '\'') {
            return scanChar(tokenLine, tokenColumn);
        }

        return scanOperatorOrDelimiter(tokenLine, tokenColumn);
    }

    /**
     * DFA for identifier recognition: [a-zA-Z_][a-zA-Z0-9_]*
     *
     * State 0 (start): [a-zA-Z_] -> State 1
     * State 1 (accepting): [a-zA-Z0-9_] -> State 1, other -> accept
     */
    private Token scanIdentifier(int tokenLine, int tokenColumn) {
        StringBuilder lexeme = new StringBuilder();

        // State 1: Keep consuming identifier characters
        while (isLetterOrDigit(currentChar) || currentChar == '_') {
            lexeme.append(currentChar);
            advance();
        }

        String identifier = lexeme.toString();

        // Check if identifier is a keyword
        TokenType type = keywords.getOrDefault(identifier, TokenType.IDENTIFIER);

        // For boolean literals, store the actual value
        if (type == TokenType.TRUE) {
            return new Token(type, identifier, new SourcePosition(tokenLine, tokenColumn), true);
        } else if (type == TokenType.FALSE) {
            return new Token(type, identifier, new SourcePosition(tokenLine, tokenColumn), false);
        } else if (type == TokenType.NULL) {
            return new Token(type, identifier, new SourcePosition(tokenLine, tokenColumn), null);
        }

        return new Token(type, identifier, tokenLine, tokenColumn);
    }

    /**
     * DFA for number recognition: [0-9]+ or [0-9]+\.[0-9]+
     *
     * State 0: [0-9] -> State 1 (integer)
     * State 1: [0-9] -> State 1, '.' -> State 2, other -> accept as integer
     * State 2: [0-9] -> State 3 (double)
     * State 3: [0-9] -> State 3, other -> accept as double
     */
    private Token scanNumber(int tokenLine, int tokenColumn) {
        StringBuilder lexeme = new StringBuilder();

        // State 1: Consume digits
        while (isDigit(currentChar)) {
            lexeme.append(currentChar);
            advance();
        }

        // Check for decimal point (State 2)
        if (currentChar == '.' && peek() != '\0' && isDigit(peek())) {
            lexeme.append(currentChar);
            advance(); // consume '.'

            // State 3: Consume fractional digits
            while (isDigit(currentChar)) {
                lexeme.append(currentChar);
                advance();
            }

            String numStr = lexeme.toString();
            return new Token(TokenType.DOUBLE_LITERAL, numStr,
                           new SourcePosition(tokenLine, tokenColumn),
                           Double.parseDouble(numStr));
        }

        // Accept as integer
        String numStr = lexeme.toString();
        return new Token(TokenType.INTEGER_LITERAL, numStr,
                       new SourcePosition(tokenLine, tokenColumn),
                       Integer.parseInt(numStr));
    }

    /**
     * DFA for string literal recognition: "..."
     * Handles escape sequences.
     */
    private Token scanString(int tokenLine, int tokenColumn) {
        StringBuilder lexeme = new StringBuilder();
        StringBuilder value = new StringBuilder();

        lexeme.append(currentChar); // opening "
        advance();

        while (!isAtEnd() && currentChar != '"') {
            if (currentChar == '\n') {
                throw new LexerException("Unterminated string literal", tokenLine, tokenColumn);
            }

            if (currentChar == '\\') {
                lexeme.append(currentChar);
                advance();

                if (isAtEnd()) {
                    throw new LexerException("Unterminated string literal", tokenLine, tokenColumn);
                }

                // Handle escape sequences
                char escaped = currentChar;
                lexeme.append(escaped);

                switch (escaped) {
                    case 'n': value.append('\n'); break;
                    case 't': value.append('\t'); break;
                    case 'r': value.append('\r'); break;
                    case '\\': value.append('\\'); break;
                    case '"': value.append('"'); break;
                    default: value.append('\\').append(escaped);
                }
                advance();
            } else {
                lexeme.append(currentChar);
                value.append(currentChar);
                advance();
            }
        }

        if (isAtEnd()) {
            throw new LexerException("Unterminated string literal", tokenLine, tokenColumn);
        }

        lexeme.append(currentChar); // closing "
        advance();

        return new Token(TokenType.STRING_LITERAL, lexeme.toString(),
                       new SourcePosition(tokenLine, tokenColumn),
                       value.toString());
    }

    /**
     * DFA for character literal recognition: '.'
     */
    private Token scanChar(int tokenLine, int tokenColumn) {
        StringBuilder lexeme = new StringBuilder();

        lexeme.append(currentChar); // opening '
        advance();

        if (isAtEnd() || currentChar == '\'') {
            throw new LexerException("Empty character literal", tokenLine, tokenColumn);
        }

        char value;
        if (currentChar == '\\') {
            lexeme.append(currentChar);
            advance();

            if (isAtEnd()) {
                throw new LexerException("Unterminated character literal", tokenLine, tokenColumn);
            }

            char escaped = currentChar;
            lexeme.append(escaped);

            switch (escaped) {
                case 'n': value = '\n'; break;
                case 't': value = '\t'; break;
                case 'r': value = '\r'; break;
                case '\\': value = '\\'; break;
                case '\'': value = '\''; break;
                default: value = escaped;
            }
            advance();
        } else {
            value = currentChar;
            lexeme.append(currentChar);
            advance();
        }

        if (isAtEnd() || currentChar != '\'') {
            throw new LexerException("Unterminated character literal", tokenLine, tokenColumn);
        }

        lexeme.append(currentChar); // closing '
        advance();

        return new Token(TokenType.CHAR_LITERAL, lexeme.toString(),
                       new SourcePosition(tokenLine, tokenColumn),
                       value);
    }

    /**
     * DFA for operators and delimiters.
     * Handles multi-character operators like ==, !=, <=, >=, &&, ||, ++, --, +=, etc.
     */
    private Token scanOperatorOrDelimiter(int tokenLine, int tokenColumn) {
        char c = currentChar;
        advance();

        switch (c) {
            // Two-character operators
            case '+':
                if (currentChar == '+') {
                    advance();
                    return new Token(TokenType.INCREMENT, "++", tokenLine, tokenColumn);
                } else if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.PLUS_ASSIGN, "+=", tokenLine, tokenColumn);
                }
                return new Token(TokenType.PLUS, "+", tokenLine, tokenColumn);

            case '-':
                if (currentChar == '-') {
                    advance();
                    return new Token(TokenType.DECREMENT, "--", tokenLine, tokenColumn);
                } else if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.MINUS_ASSIGN, "-=", tokenLine, tokenColumn);
                }
                return new Token(TokenType.MINUS, "-", tokenLine, tokenColumn);

            case '*':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.STAR_ASSIGN, "*=", tokenLine, tokenColumn);
                }
                return new Token(TokenType.STAR, "*", tokenLine, tokenColumn);

            case '/':
                if (currentChar == '/') {
                    // Single-line comment - skip to end of line
                    while (!isAtEnd() && currentChar != '\n') {
                        advance();
                    }
                    return nextToken(); // Get next token after comment
                } else if (currentChar == '*') {
                    // Multi-line comment
                    advance();
                    while (!isAtEnd()) {
                        if (currentChar == '*' && peek() == '/') {
                            advance(); // consume *
                            advance(); // consume /
                            break;
                        }
                        advance();
                    }
                    return nextToken(); // Get next token after comment
                } else if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.SLASH_ASSIGN, "/=", tokenLine, tokenColumn);
                }
                return new Token(TokenType.SLASH, "/", tokenLine, tokenColumn);

            case '%':
                return new Token(TokenType.PERCENT, "%", tokenLine, tokenColumn);

            case '=':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.EQUALS, "==", tokenLine, tokenColumn);
                }
                return new Token(TokenType.ASSIGN, "=", tokenLine, tokenColumn);

            case '!':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.NOT_EQUALS, "!=", tokenLine, tokenColumn);
                }
                return new Token(TokenType.LOGICAL_NOT, "!", tokenLine, tokenColumn);

            case '<':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.LESS_EQUAL, "<=", tokenLine, tokenColumn);
                }
                return new Token(TokenType.LESS_THAN, "<", tokenLine, tokenColumn);

            case '>':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.GREATER_EQUAL, ">=", tokenLine, tokenColumn);
                }
                return new Token(TokenType.GREATER_THAN, ">", tokenLine, tokenColumn);

            case '&':
                if (currentChar == '&') {
                    advance();
                    return new Token(TokenType.LOGICAL_AND, "&&", tokenLine, tokenColumn);
                }
                throw new LexerException("Unexpected character '&' (did you mean '&&'?)",
                                       tokenLine, tokenColumn);

            case '|':
                if (currentChar == '|') {
                    advance();
                    return new Token(TokenType.LOGICAL_OR, "||", tokenLine, tokenColumn);
                }
                throw new LexerException("Unexpected character '|' (did you mean '||'?)",
                                       tokenLine, tokenColumn);

            // Single-character delimiters
            case '(': return new Token(TokenType.LPAREN, "(", tokenLine, tokenColumn);
            case ')': return new Token(TokenType.RPAREN, ")", tokenLine, tokenColumn);
            case '{': return new Token(TokenType.LBRACE, "{", tokenLine, tokenColumn);
            case '}': return new Token(TokenType.RBRACE, "}", tokenLine, tokenColumn);
            case '[': return new Token(TokenType.LBRACKET, "[", tokenLine, tokenColumn);
            case ']': return new Token(TokenType.RBRACKET, "]", tokenLine, tokenColumn);
            case ';': return new Token(TokenType.SEMICOLON, ";", tokenLine, tokenColumn);
            case ',': return new Token(TokenType.COMMA, ",", tokenLine, tokenColumn);
            case '.': return new Token(TokenType.DOT, ".", tokenLine, tokenColumn);
            case ':': return new Token(TokenType.COLON, ":", tokenLine, tokenColumn);

            default:
                throw new LexerException("Unexpected character '" + c + "'", tokenLine, tokenColumn);
        }
    }

    /**
     * Skips whitespace characters (spaces, tabs, newlines).
     */
    private void skipWhitespace() {
        while (!isAtEnd() && Character.isWhitespace(currentChar)) {
            if (currentChar == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            position++;
            currentChar = isAtEnd() ? '\0' : source.charAt(position);
        }
    }

    /**
     * Advances to the next character in the source.
     */
    private void advance() {
        if (!isAtEnd()) {
            column++;
            position++;
            currentChar = isAtEnd() ? '\0' : source.charAt(position);
        }
    }

    /**
     * Looks ahead one character without consuming it.
     */
    private char peek() {
        if (position + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(position + 1);
    }

    /**
     * Checks if we've reached the end of the source.
     */
    private boolean isAtEnd() {
        return position >= source.length();
    }

    /**
     * Character classification helpers.
     */
    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }
}
