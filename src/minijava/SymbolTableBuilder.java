package minijava;

import symbol_table.*;

public class SymbolTableBuilder extends MiniJavaGrammarBaseListener {
    SymbolTable symbolTable;
    MethodRecord currMethodRecord;
    ClassRecord currClass;

    public SymbolTableBuilder(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public void enterProgram(MiniJavaGrammarParser.ProgramContext ctx) {
        symbolTable.setCurrentScopeNameAndType("program", ScopeTypes.ROOT.toString());
    }

    @Override
    public void enterMainclass(MiniJavaGrammarParser.MainclassContext ctx) {
        String  ID = ctx.ID().get(0).toString();

        // create record
        currClass = new ClassRecord( ID, "class");
        // add class to PROGRAM SCOPE
        symbolTable.put(currClass);

        // enter class scope
        symbolTable.enterScope();
        // set scope name & type
        symbolTable.setCurrentScopeNameAndType( ID, ScopeTypes.CLASS.toString());
        // update the class
        symbolTable.setCurrentScopeClass(currClass);

        MethodRecord mainMethodRecord = new MethodRecord("main", "void");

        // add main method to symbol table.
        symbolTable.put(mainMethodRecord);
        // enter method scope
        symbolTable.enterScope();
        // set scope name
        symbolTable.setCurrentScopeNameAndType("main", ScopeTypes.METHOD.toString());
        // add method to currentClass
        currClass.addMethod(mainMethodRecord);
        //inherit current class from parent scope
        symbolTable.setCurrentScopeClass(currClass);
    }

    @Override
    public void exitMainclass(MiniJavaGrammarParser.MainclassContext ctx) {
        // exit both main method and main class scope
        symbolTable.exitScope();
        symbolTable.exitScope();
    }

    @Override
    public void enterClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
        String type = ctx.CLASS().getText();
        String ID = ctx.ID(0).toString();

        currClass = new ClassRecord(ID, type);
        if(symbolTable.lookup(currClass)) {
            Utils.errorMessage(ctx, "duplicated class name "+  ID);
            symbolTable.setError();
        }

        // add class to program scope
        symbolTable.put(currClass);
        //enter class scope
        symbolTable.enterScope();
        // set scope name & type
        symbolTable.setCurrentScopeNameAndType(ID, ScopeTypes.CLASS.toString());
        symbolTable.setCurrentScopeClass(currClass);
    }

    @Override
    public void exitClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
		symbolTable.exitScope();
    }

    @Override
    public void enterVardecl(MiniJavaGrammarParser.VardeclContext ctx) {
        String type = ctx.type().getText();
        String ID = ctx.ID().toString();

        VariableRecord newField = new VariableRecord(ID, type);

        if(symbolTable.getCurrentScope().getScopeType().equals(ScopeTypes.CLASS.toString())) {
            newField.setIsInstanceVariable(true);
        }

        if(symbolTable.lookup(newField)) {
            Utils.errorMessage(ctx, "duplicated identifier " +  ID);
            symbolTable.setError();
        }

        // insert record into scope
        currClass.addField(newField);
        symbolTable.setCurrentScopeClass(currClass);
        symbolTable.put(newField);
    }

    @Override
    public void enterMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
        String type = ctx.type().getText();
        String ID = ctx.ID().toString();

        if(currClass.containsMethod(ID)){
            Utils.errorMessage(ctx, "method " +  ID + " duplicated in class "+ currClass.getID());
            symbolTable.setError();
        }

        currMethodRecord = new MethodRecord(ID, type);
        symbolTable.put(currMethodRecord);

        symbolTable.enterScope();
        symbolTable.setCurrentScopeNameAndType(ID, ScopeTypes.METHOD.toString());
        currClass.addMethod(currMethodRecord);
        symbolTable.setCurrentScopeClass(currClass);
    }

    @Override
    public void exitMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
        symbolTable.exitScope();
    }

    @Override
    public void enterFormallist(MiniJavaGrammarParser.FormallistContext ctx) {
        String type = ctx.type().getText();
        String ID = ctx.ID().toString(); // get ID

        VariableRecord parameter = new VariableRecord(ID, type, true);

        if(symbolTable.lookup(parameter)) {
            Utils.errorMessage(ctx, "this formal parameter identifier is already used in this class");
            symbolTable.setError();
        }
        else {
            currMethodRecord.addParameter(parameter);
        }

        // insert record into scope
        symbolTable.put(parameter);
    }


    @Override
    public void enterFormalrest(MiniJavaGrammarParser.FormalrestContext ctx) {
        String type = ctx.type().getText();
        String  ID = ctx.ID().toString();

        VariableRecord parameter = new VariableRecord(ID, type, true);

        if(currMethodRecord.containsParameter(parameter)) {
            Utils.errorMessage(ctx, "formal parameter " + parameter.getID() + " is duplicated in method "+ currMethodRecord.getID());
            symbolTable.setError();
        }
        else if(symbolTable.lookup(parameter)) {
            Utils.errorMessage(ctx, "this formal parameter identifier is already used in this class");
            symbolTable.setError();
        }
        else {
            currMethodRecord.addParameter(parameter);
        }

        // insert record into scope
        symbolTable.put(parameter);
    }

    @Override
    public void enterType(MiniJavaGrammarParser.TypeContext ctx) {
    }

    @Override
    public void enterStatement(MiniJavaGrammarParser.StatementContext ctx) {
    }
}