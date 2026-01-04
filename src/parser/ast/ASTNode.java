package parser.ast;

import util.SourcePosition;

/**
 * Base interface for all Abstract Syntax Tree nodes.
 * The AST is a tree data structure representing the syntactic structure of the source code.
 *
 * Theory of Computation:
 * - AST is the output of the parser (syntax analysis phase)
 * - Represents the hierarchical structure derived from the CFG
 * - Used as input for semantic analysis and code generation
 */
public interface ASTNode {
    /**
     * Accept a visitor for traversal (Visitor pattern).
     * This enables code generation and other tree-walking operations.
     */
    void accept(ASTVisitor visitor);

    /**
     * Get the source position of this node for error reporting.
     */
    SourcePosition getPosition();
}
