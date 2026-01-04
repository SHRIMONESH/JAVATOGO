package main;

import lexer.*;
import parser.*;
import parser.ast.Program;
import codegen.*;
import util.ErrorReporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Main entry point for the J2GO transpiler.
 *
 * Pipeline:
 * 1. Lexical Analysis: Source code → Token stream
 * 2. Syntax Analysis: Token stream → Abstract Syntax Tree (AST)
 * 3. Code Generation: AST → Go source code
 *
 * Usage: java main.Main <input.java> [output.go]
 */
public class Main {
    private static final ErrorReporter errorReporter = new ErrorReporter();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: j2go <input.java> [output.go]");
            System.err.println("  Transpiles Java source code to Go");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args.length > 1 ? args[1] : deriveOutputFilename(inputFile);

        try {
            // Read source file
            String source = readFile(inputFile);

            // Run transpiler pipeline
            String goCode = transpile(source, inputFile);

            if (errorReporter.hasErrors()) {
                errorReporter.printSummary();
                System.exit(1);
            }

            // Write output file
            writeFile(outputFile, goCode);

            System.out.println("Successfully transpiled " + inputFile + " to " + outputFile);

        } catch (LexerException | ParserException | CodeGenException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Main transpilation pipeline: Java source → Go source
     */
    public static String transpile(String source, String filename) {
        // Phase 1: Lexical Analysis (DFA-based tokenization)
        System.out.println("[1/3] Lexical Analysis...");
        Lexer lexer = new Lexer(source);
        List<Token> tokens = new ArrayList<>();

        Token token;
        do {
            token = lexer.nextToken();
            tokens.add(token);
        } while (token.getType() != TokenType.EOF);

        System.out.println("  → " + (tokens.size() - 1) + " tokens generated");

        // Phase 2: Syntax Analysis (Recursive Descent Parsing)
        System.out.println("[2/3] Syntax Analysis...");
        Parser parser = new Parser(tokens);
        Program program = parser.parseProgram();
        System.out.println("  → AST constructed with " + program.getClasses().size() + " class(es)");

        // Phase 3: Code Generation (Visitor Pattern)
        System.out.println("[3/3] Code Generation...");
        CodeGenerator generator = new CodeGenerator();
        String goCode = generator.generate(program);
        System.out.println("  → Go code generated (" + goCode.split("\n").length + " lines)");

        return goCode;
    }

    private static String readFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }

    private static void writeFile(String filename, String content) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }

    private static String deriveOutputFilename(String inputFile) {
        if (inputFile.endsWith(".java")) {
            return inputFile.substring(0, inputFile.length() - 5) + ".go";
        }
        return inputFile + ".go";
    }
}
