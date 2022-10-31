package minijava;

import org.antlr.v4.runtime.tree.TerminalNode;
import symbol_table.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeChecker extends MiniJavaGrammarBaseVisitor<String> {
    private final SymbolTable symbolTable;
    private final Set<String> primitives;
    
    TypeChecker(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.primitives = new HashSet<>();
        primitives.add("int");
        primitives.add("boolean");
        primitives.add("int[]");
    }

    @Override
    public String visitProgram(MiniJavaGrammarParser.ProgramContext ctx) {
        visitMainclass(ctx.mainclass());

        for(MiniJavaGrammarParser.ClassdeclContext classdeclContext : ctx.classdecl()) {
            visitClassdecl(classdeclContext);
        }

        return null;
    }

    @Override
    public String visitMainclass(MiniJavaGrammarParser.MainclassContext ctx) {
        symbolTable.enterScope();

        if(ctx.statement() != null) {
            visitStatement(ctx.statement());
        }

        symbolTable.exitScope();

        return null;
    }

    @Override
    public String visitClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
        symbolTable.enterScope();

        for(MiniJavaGrammarParser.VardeclContext vardeclContext : ctx.vardecl()) {
            visitVardecl(vardeclContext);
        }

        for(MiniJavaGrammarParser.MethoddeclContext methoddeclContext : ctx.methoddecl()) {
            visitMethoddecl(methoddeclContext);
        }

        symbolTable.exitScope();

        return null;
    }

    @Override
    public String visitVardecl(MiniJavaGrammarParser.VardeclContext ctx) {
        String varType = ctx.type().getText();

        // check if the class associated with the current variable exists in the program.
        if(!primitives.contains(varType)) {
            if (!symbolTable.lookupFromRoot(new ClassRecord(ctx.type().getText(), "class"))) {
                Utils.errorMessage(ctx, "variable of type " + varType + " cannot be declared as this class does not exist in this file");
                symbolTable.setError();
            }
        }
        return super.visitVardecl(ctx);
    }

    @Override
    public String visitMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
        symbolTable.enterScope();

        // visit all variables
        for(MiniJavaGrammarParser.VardeclContext vardeclContext : ctx.vardecl()) {
            visitVardecl(vardeclContext);
        }
        // visit all statements
        for(MiniJavaGrammarParser.StatementContext statementContext : ctx.statement()) {
            visitStatement(statementContext);
        }

        // check that the return type matches the expected type
        String returnType = visitExpr(ctx.expr());
        if(returnType != null && !(returnType.equals(ctx.type().getText()) || isSubTypeOf(ctx.type().getText(), ctx.type().getText()))) {
            Utils.errorMessage(ctx, "expected return type of method " + ctx.ID() + " is "  + ctx.type().getText() + " but the type attempted to be returned is " + returnType);
            symbolTable.setError();
        }

        symbolTable.exitScope();

        return null;
    }

    @Override
    public String visitFormallist(MiniJavaGrammarParser.FormallistContext ctx) {
        String identifier = visit(ctx.ID());
        Record record = new Record(identifier, ctx.type().getText());

        // check if the formal list parameter identifier has already been used.
        if (symbolTable.lookup(record)) {
            Utils.errorMessage(ctx, "The identifier  " + identifier + " already exists");
            symbolTable.setError();
            return null;
        }

        return super.visitFormallist(ctx);
    }

    @Override
    public String visitFormalrest(MiniJavaGrammarParser.FormalrestContext ctx) {
        String identifier = visit(ctx.ID());
        Record record = new Record(identifier, ctx.type().getText());

        // check if the formal list parameter identifier has already been used.
        if (symbolTable.lookup(record)) {
            Utils.errorMessage(ctx, "The identifier " + identifier + " already exists");
            symbolTable.setError();
            return null;
        }

        return super.visitFormalrest(ctx);
    }

    @Override
    public String visitType(MiniJavaGrammarParser.TypeContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitStatement(MiniJavaGrammarParser.StatementContext ctx) {
        // { statement* }
        if(ctx.LBRACE() != null) {
            for(MiniJavaGrammarParser.StatementContext statementContext : ctx.statement()) {
                visitStatement(statementContext);
            }
        }
        // if(expr) statement else statement
        else if(ctx.IF() != null) {
            String ifReturnType = visitExpr(ctx.expr(0));
            if(!ifReturnType.equals("boolean")) {
                Utils.errorMessage(ctx, "if statement must evaluate to a boolean");
                symbolTable.setError();
            }
            visitStatement(ctx.statement(0));
            visitStatement(ctx.statement(1));
        }
        // while(expr) statement
        else if(ctx.WHILE() != null) {
            String whileReturnType = visitExpr(ctx.expr(0));
            if(!whileReturnType.equals("boolean")) {
                Utils.errorMessage(ctx, "while loop condition must evaluate to a boolean");
                symbolTable.setError();
            }

            for(MiniJavaGrammarParser.StatementContext statementContext : ctx.statement()) {
                visitStatement(statementContext);
            }
        }
        // identifier[expr] = expr;
        else if(ctx.ID() != null && ctx.LSQUARE() != null) {
            String arrayID = ctx.ID().getText();
            String indexType = visitExpr(ctx.expr(0));
            String assignedType  = visitExpr(ctx.expr(1));

            if(!indexType.equals("int")) {
                Utils.errorMessage(ctx, "array index must be of type int");
                symbolTable.setError();
            }
            if(!assignedType.equals("int")) {
                Utils.errorMessage(ctx, "arrays can only contain entries of type int");
                symbolTable.setError();
            }

            VariableRecord variableRecord = symbolTable.getVariableFromCurrentScope(arrayID, "int[]");
            if(variableRecord == null) {
                Utils.errorMessage(ctx, "identifier " + arrayID + " of type int[] does not exist in the current scope");
                symbolTable.setError();
                return null;
            }

            if(!variableRecord.isInitialised()) {
                Utils.errorMessage(ctx, "array " + arrayID + " has not been initialised");
                symbolTable.setError();
            }
        }
        // id = expr;
        else if(ctx.ID() != null && ctx.expr(0) != null) {
            String ID = ctx.ID().getText();
            String expressionType = visitExpr(ctx.expr(0));
            VariableRecord variableRecord = symbolTable.getVariableFromCurrentScope(ID);
            String typeOfVariable = variableRecord == null ? null : variableRecord.getType();

            // this ID has not been declared, so we do not continue, no need to throw error as the scope analysis phase will do this
            if(typeOfVariable == null) {
                Utils.errorMessage(ctx, "unbound variable " + ID);
                symbolTable.setError();
                return null;
            }
            if(expressionType == null) return null;

            if(!(expressionType.equals(typeOfVariable) || isSubTypeOf(expressionType, typeOfVariable))) {
                Utils.errorMessage(ctx, "cannot assign variable of typeOfVariable " + typeOfVariable + " to expression of typeOfVariable " + expressionType);
                symbolTable.setError();
            }

            // initialise variable
            variableRecord.initialise();
        }
        // System.out.println(expr);
        else if(ctx.SYSTEMOUT() != null && ctx.expr(0) != null) {
            String type = visitExpr(ctx.expr(0));
            if(type != null && !type.equals("int")) {
                Utils.errorMessage(ctx, "the argument to System.out.println must be of type int but was provided type " + type);
                symbolTable.setError();
            }
        }

        return null;
    }

    @Override
    public String visitExpr(MiniJavaGrammarParser.ExprContext ctx) {
        // expr op expr
        if(ctx.op() != null) {
            String leftType = visitExpr(ctx.expr(0));
            String rightType = visitExpr(ctx.expr(1));

            String opType = visitOp(ctx.op());

            if(opType.equals("&&")) {
                if(leftType != null && !leftType.equals("boolean")) {
                    Utils.errorMessage(ctx, "left side of operator \"" + opType +  "\" must be of type boolean but was provided type " + leftType);
                    symbolTable.setError();
                }
                if(rightType != null && !rightType.equals("boolean")) {
                    Utils.errorMessage(ctx, "right side of operator \"" + opType +  "\" must be of type boolean but was provided type " + rightType);
                    symbolTable.setError();
                }
            }
            else {
                if(leftType != null && !leftType.equals("int")) {
                    Utils.errorMessage(ctx, "left side of operator \"" + opType +  "\" must be of type int but was provided type " + leftType);
                    symbolTable.setError();
                }
                if(rightType != null && !rightType.equals("int")) {
                    Utils.errorMessage(ctx, "right side of operator \"" + opType +  "\" must be of type int but was provided type " + rightType);
                    symbolTable.setError();
                }
            }

            // check if the operator application yields an int or a boolean.
            if(opType.equals("<") || opType.equals("&&")) {
                return "boolean";
            }

            return "int";
        }
        // expr[expr]
        else if(ctx.expr(0) != null && ctx.expr(1) != null && ctx.LSQUARE() != null) {
            String leftExprType = visitExpr(ctx.expr(0));
            String rightExprType = visitExpr(ctx.expr(1));
            if(leftExprType != null && (leftExprType.equals("boolean") || leftExprType.equals("this"))) {
                Utils.errorMessage(ctx, "only identifiers of type int[] can be accessed using square brackets");
                symbolTable.setError();
            }

            String arrayID = ctx.expr(0).getText();

            VariableRecord variableRecord = symbolTable.getVariableFromCurrentScope(arrayID, "int[]");

            if(variableRecord == null) {
                Utils.errorMessage(ctx, "identifier " + arrayID + " of type int[] does not exist in the current scope");
                symbolTable.setError();
            }

            if(!rightExprType.equals("int")) {
                Utils.errorMessage(ctx, "array index must be of type int");
                symbolTable.setError();
            }

            return "int";
        }
        // new int[]
        else if(ctx.NEW() != null && ctx.INT() != null) {
            String arraySizeType = visitExpr(ctx.expr(0));
            if(!arraySizeType.equals("int")) {
                Utils.errorMessage(ctx, "size of an array being initialised must be of type int");
                symbolTable.setError();
            }

            return "int[]";
        }
        // expr.length
        else if(ctx.DOT() != null && ctx.LENGTH() != null) {
            String arrayID = visitExpr(ctx.expr(0));
            if(arrayID.equals("int") || arrayID.equals("boolean") || arrayID.equals("this")) {
                Utils.errorMessage(ctx, "length function can only be applied on identifiers but was provided type " + arrayID);
                symbolTable.setError();
            }
            else {
                VariableRecord variableRecord = symbolTable.getVariableFromCurrentScope(arrayID, "int[]");
                if(variableRecord == null) {
                    Utils.errorMessage(ctx, "the current class does not contain an int[] called " + arrayID);
                    symbolTable.setError();
                }
            }

            return "int";
        }
        // expr.ID(expr*)
        else if(ctx.DOT() != null && ctx.LPAREN() != null) {
            String typeOfObjectAccessed = visitExpr(ctx.expr(0));
            if(typeOfObjectAccessed == null) {
                return null;
            }
            if(primitives.contains(typeOfObjectAccessed)) {
                Utils.errorMessage(ctx, "method invocation can only be carried out on reference types");
                symbolTable.setError();
                return null;
            }
            else {
                String leftExprType = visitExpr(ctx.expr(0));
                if(leftExprType == null) return null;
                return visitMethodInvocation(ctx.expr(0), leftExprType, ctx.ID(), ctx.exprlist());
            }
        }
        // new ID()
        else if(ctx.NEW() != null && ctx.ID() != null) {
            String classType = ctx.ID().getText();
            boolean containsClass = symbolTable.lookupFromRoot(new ClassRecord(classType, "class")); // the object the method is being invoked on.
            if(!containsClass) {
                Utils.errorMessage(ctx, "class " + classType + " does not exist in this file");
                symbolTable.setError();
                return null;
            }
            else return classType;
        }
        // !expr
        else if(ctx.NOT() != null) {
            String exprEvalType = visitExpr(ctx.expr(0));
            if(!exprEvalType.equals("boolean")) {
                Utils.errorMessage(ctx, "! operator can only be applied to an expression that evaluates to a boolean");
                symbolTable.setError();
            }
            return "boolean";
        }
        // (expr)
        else if(ctx.LPAREN() != null && ctx.RPAREN() != null) {
            return visitExpr(ctx.expr(0));
        }
        else if(ctx.INTEGER() != null) return "int";
        else if(ctx.FALSE() != null || ctx.TRUE() != null) return "boolean";
        else if(ctx.THIS() != null) {
            return symbolTable.getCurrentClassName();
        }

        // if we match with none of the other cases we are in an identifier
        // if the identifier has not been declared we return an error.
        String ID = ctx.ID().getText();

        VariableRecord variableRecord = symbolTable.getVariableFromCurrentScope(ID);
        if(variableRecord == null) {
            Utils.errorMessage(ctx, "unbound variable " + ID);
            symbolTable.setError();
            return null;
        }

        Record record = symbolTable.getVariableFromCurrentScope(ID);
        if(record != null && !record.isInitialised()) {
            Utils.errorMessage(ctx, "variable " + record.getID() + " has not been initialised");
            symbolTable.setError();
        }
        else if(record == null) {
            System.out.println("did not find " + ID + " line " + ctx.getStart().getLine());
        }
        // lookup type of identifier.
        return symbolTable.getTypeOfIdentifier(ctx.ID().getText());
    }

    private String visitMethodInvocation(MiniJavaGrammarParser.ExprContext ctx, String leftExprType, TerminalNode id, MiniJavaGrammarParser.ExprlistContext exprlist) {
        String methodID = id.getText();
        MethodRecord methodRecordCalled;

        methodRecordCalled = symbolTable.getMethodFromProvidedClass(leftExprType, methodID);

        // case where the specified method does not exist
        if(methodRecordCalled == null) {
            Utils.errorMessage(ctx, "method " + id.getText() + " is not defined in class " + leftExprType);
            symbolTable.setError();
            return null;
        }

        List<VariableRecord> expectedParameters = methodRecordCalled.getParameters();
        List<MiniJavaGrammarParser.ExprContext> providedParameters = new ArrayList<>();

        int actualParameterCount = 0;

        if(exprlist != null && exprlist.expr() != null) {
            providedParameters.add(exprlist.expr());
            actualParameterCount += 1;
        }

        if(exprlist != null && exprlist.exprrest() != null) {
            for(MiniJavaGrammarParser.ExprrestContext exprrestContext : exprlist.exprrest()) {
                providedParameters.add(exprrestContext.expr());
            }
            actualParameterCount += exprlist.exprrest().size();
        }

        // case where the number of arguments provided do not match the expected amount.
        if(expectedParameters.size() != actualParameterCount) {
            Utils.errorMessage(ctx, "method " + id.getText() + " received " + actualParameterCount +
                    " arguments but expected " + methodRecordCalled.getParameters().size() + " arguments");
            symbolTable.setError();
            return methodRecordCalled.getReturnType();
        }

        // evaluate all the expressions provided as arguments.
        List<String> providedParameterTypes = new ArrayList<>(actualParameterCount);
        for(MiniJavaGrammarParser.ExprContext exprContext : providedParameters) {
            providedParameterTypes.add(visitExpr(exprContext));
        }

        // check whether the expected and provided types match.
        for(int i = 0; i < providedParameterTypes.size(); i++) {
            if(providedParameterTypes.get(i) != null &&
                    !(providedParameterTypes.get(i).equals(expectedParameters.get(i).getType()) ||
                            isSubTypeOf(providedParameterTypes.get(i), expectedParameters.get(i).getType()))) {
                Utils.errorMessage(ctx, "argument " + (i + 1) + " passed to method " + id.getText() +
                        " does not match the expected type of " + expectedParameters.get(i).getType());
                symbolTable.setError();
            }
        }

        return methodRecordCalled.getReturnType();
    }

    @Override
    public String visitOp(MiniJavaGrammarParser.OpContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitExprlist(MiniJavaGrammarParser.ExprlistContext ctx) {
        return super.visitExprlist(ctx);
    }

    @Override
    public String visitExprrest(MiniJavaGrammarParser.ExprrestContext ctx) {
        return super.visitExprrest(ctx);
    }

    /**
     * check if class A is a subtype of B by checking if B is extended at any point by A.
     * @return if A is a subtype of B
     */
    private boolean isSubTypeOf(String classIdentifierA, String classIdentifierB) {
        ClassRecord A = symbolTable.getClass(classIdentifierA);
        ClassRecord B = symbolTable.getClass(classIdentifierB);

        if(A == null || B == null) return false;

        Set<String> inheritanceChainForA = new HashSet<>();
        ClassRecord temp = A;
        while(temp != null) {
            inheritanceChainForA.add(temp.getID());
            temp = temp.getParentClass();
        }

        return inheritanceChainForA.contains(classIdentifierB);
    }

}
