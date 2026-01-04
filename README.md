# J2GO: Java to Go Transpiler

A Java to Go transpiler built to demonstrate compiler design principles and theory of computation concepts.

## Overview

J2GO is an academic transpiler that converts a subset of Java code to equivalent Go code. The project demonstrates:

- **Finite Automata (DFA)** for lexical analysis
- **Context-Free Grammars (CFG)** for syntax analysis
- **Recursive Descent Parsing** with LL(1) grammar
- **Abstract Syntax Tree (AST)** construction
- **Visitor Pattern** for code generation

## Features

### Supported Java Subset

- **Types**: `int`, `boolean`, `String`, `double`, `long`, `char`, `void`
- **Classes**: Class declarations with fields and methods
- **Methods**: Instance and static methods with parameters
- **Statements**: if/else, while, for, return, variable declarations, blocks
- **Expressions**: Binary operators (+, -, *, /, %, ==, !=, <, >, <=, >=, &&, ||), unary operators (!, -, +), literals, identifiers, method calls, field access
- **OOP**: Object instantiation (`new`), field access, method calls

### Compiler Phases

1. **Lexical Analysis**: DFA-based tokenizer recognizing keywords, identifiers, operators, literals
2. **Syntax Analysis**: Recursive descent parser building AST from CFG production rules
3. **Code Generation**: Visitor pattern traversing AST to emit Go code

## Project Structure

```
J2GO/
├── src/
│   ├── main/Main.java              # CLI entry point
│   ├── lexer/                      # Lexical analysis (DFA)
│   │   ├── Lexer.java
│   │   ├── Token.java
│   │   └── TokenType.java
│   ├── parser/                     # Syntax analysis (CFG)
│   │   ├── Parser.java
│   │   ├── ParserException.java
│   │   └── ast/                    # AST node hierarchy
│   ├── codegen/                    # Code generation (Visitor)
│   │   └── CodeGenerator.java
│   └── util/                       # Utilities
├── test/                           # Unit tests
├── examples/                       # Test cases
└── README.md
```

## Building

```bash
# Compile the transpiler
mkdir build
cd src
find . -name "*.java" | xargs javac -d ../build
```

## Usage

```bash
# Transpile a Java file to Go
java -cp build main.Main <input.java> [output.go]

# Example
java -cp build main.Main examples/simple/Calculator.java

# This generates Calculator.go
```

## Examples

### Input (Java)

```java
class Calculator {
    int add(int a, int b) {
        return a + b;
    }
}
```

### Output (Go)

```go
package main

import "fmt"

type Calculator struct {
}

func (self *Calculator) add(a int, b int) int {
    return a + b
}
```

## Theory of Computation Concepts

### 1. Finite Automata (Lexer)

- DFA state machines for token recognition
- Pattern matching for identifiers: `[a-zA-Z_][a-zA-Z0-9_]*`
- Multi-character operator recognition (==, !=, &&, ||)

### 2. Context-Free Grammar (Parser)

Formal grammar in BNF notation:

```
Program ::= ClassDeclaration+
ClassDeclaration ::= "class" ID "{" ClassMember* "}"
Statement ::= If | While | For | Return | VarDecl | ExprStmt | Block
Expression ::= Assignment | LogicalOr | ... | Primary
```

### 3. Recursive Descent Parsing

- Each grammar production rule maps to a parsing method
- Top-down parsing with one-token lookahead
- Operator precedence encoded in grammar hierarchy

### 4. Abstract Syntax Tree

- Tree data structure representing program structure
- Hierarchical representation of syntactic elements
- Foundation for semantic analysis and code generation

### 5. Visitor Pattern

- Tree traversal for code generation
- Clean separation of AST structure from operations

## Testing

### Lexer Tests

```bash
cd test/lexer
javac -cp ../../src LexerTest.java
java -cp ../../src:. LexerTest
```

### End-to-End Tests

The `examples/` directory contains test cases:

- `examples/simple/` - Basic classes and methods
- `examples/expressions/` - Arithmetic operations
- `examples/control-flow/` - If/else, loops
- `examples/oop/` - Object-oriented features

## Limitations

Current implementation supports a core Java subset for educational purposes:

- No arrays or collections
- No interfaces or inheritance
- No exception handling
- No generics or annotations
- Simplified `main()` method (no String[] args)

## Future Enhancements

- Array and collection support
- Interfaces and polymorphism
- Exception handling (try-catch)
- Semantic analysis (type checking, symbol tables)
- Optimization passes
- Multiple file support

## Educational Value

This project demonstrates:

- **Automata Theory**: DFA/NFA in lexical analysis
- **Formal Languages**: CFG, BNF notation, derivations
- **Parsing Algorithms**: Recursive descent, LL parsing
- **Data Structures**: Trees (AST), Hash tables (symbol tables), Stacks (parser)
- **Design Patterns**: Visitor pattern for AST traversal
- **Software Engineering**: Separation of concerns, modularity, testing

## License

Educational project for learning compiler design and theory of computation.
