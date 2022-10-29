package minijava;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import symbol_table.SymbolTable;

import java.io.FileInputStream;
import java.io.InputStream;

public class MiniJavaMain {

    public static void main(String[] args) throws Exception {
        String inputFile = null;

        if (args.length > 0 )
            inputFile = args[0];

        InputStream is = System.in;

        if (inputFile != null ) is = new FileInputStream(inputFile);

        ANTLRInputStream input = new ANTLRInputStream(is);

        MiniJavaGrammarLexer lexer = new MiniJavaGrammarLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        MiniJavaGrammarParser parser = new MiniJavaGrammarParser(tokens);

        MiniJavaGrammarParser.ProgramContext tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();

        SymbolTable symbolTable = new SymbolTable();

        // perform semantic analysis.

        // first pass to build symbol table.
        SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder(symbolTable);
        walker.walk(symbolTableBuilder, tree);
        if(symbolTable.containsError()) System.exit(-1);

        // second pass to record the parent classes for each class, if a parent class does not exist in the file then the parent is null.
        symbolTable.resetTable();
        InheritanceResolver inheritanceResolver = new InheritanceResolver(symbolTable, 1);
        walker.walk(inheritanceResolver, tree);
        if(symbolTable.containsError()) System.exit(-1);

        // resolve inheritance by comparing variable and method declarations in child classes & parent classes.
        // ensures that a child class has access to its parents methods
        symbolTable.resetTable();
        inheritanceResolver = new InheritanceResolver(symbolTable, 2);
        walker.walk(inheritanceResolver, tree);
        if(symbolTable.containsError()) System.exit(-1);


        // perform type checking.
        symbolTable.resetTable();
        TypeChecker typeChecker = new TypeChecker(symbolTable);
        typeChecker.visitProgram(tree);

        // print symbol table if all phases succeed.
        if(!symbolTable.containsError()) symbolTable.printTable();
        else System.exit(-1);
    }
}
