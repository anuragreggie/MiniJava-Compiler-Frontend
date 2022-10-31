# Compiler Frontend for the MiniJava Language
## Description
The MiniJava language is a subset of the Java language and the grammar for MiniJava can be seen in `src/minijava/MiniJavaGrammar.g`.
The compiler provided here performs parsing and semantic analysis on MiniJava programs. The semantic analysis phase
consists of both scope checking and type checking. 

If a MiniJava program is semantically valid the compiler prints out the symbol table, as a list of
identifiers, types and an indication of which scope they belong to. 

## File Structure
- the `symbol_table` package in `src` contains all files needed to represent the symbol table used for scope checking.
- the `minijava` package contains all other files.
    - `MiniJavaMain.java` contains the main method which performs parsing and semantic analysis.
    - other files prefixed with `MiniJava` are files used by ANLTR for parsing.
    - all other files are used to carry out semantic analysis. `TypeChecker.java`, `SymbolTableBuilder.java` and `InheritanceResolver.java`
    are the files that carry out the heavy lifting by traversing the parse tree.
- the "tests" folder contains some example MiniJava programs.

## How to run
- the first step is to add ANTLR to the classpath, the ANTLR 4.7.2 jar is in the "lib" folder of the submission.
- type the following from "src" to compile and run with a test file: `javac minijava/*.java && java minijava.MiniJavaMain <path-to-minijava-program-file>`
