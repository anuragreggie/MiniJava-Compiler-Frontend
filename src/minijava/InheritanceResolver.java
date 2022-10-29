package minijava;

import symbol_table.*;

import java.util.*;

public class InheritanceResolver extends MiniJavaGrammarBaseListener {
    SymbolTable symbolTable;
    int passCount;
    Set<String> resolvedClasses;

    public InheritanceResolver(SymbolTable symbolTable, int passCount) {
        this.symbolTable = symbolTable;
        this.passCount = passCount;
        this.resolvedClasses = new HashSet<>();
    }

    @Override
    public void enterProgram(MiniJavaGrammarParser.ProgramContext ctx) {
        symbolTable.setCurrentScopeNameAndType("program", ScopeTypes.ROOT.toString());
    }

    @Override
    public void enterMainclass(MiniJavaGrammarParser.MainclassContext ctx) {
        // enter class cope
        symbolTable.enterScope();
        // enter method scope
        symbolTable.enterScope();
    }

    @Override
    public void exitMainclass(MiniJavaGrammarParser.MainclassContext ctx) {
        // exit the main method and main class scope
        symbolTable.exitScope();
        symbolTable.exitScope();
    }
    
    @Override
    public void enterClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
        symbolTable.enterScope();

        if(ctx.EXTENDS() == null) return;

        ClassRecord extendedClass;
        ClassRecord childClass = symbolTable.getClass(ctx.ID(0).getText());

        // on the first pass locate the parent classes for each class if they exist.
        if(passCount == 1) {
            if(ctx.EXTENDS() != null) {
                String extendedClassID = ctx.ID(1).getText();
                extendedClass = symbolTable.getClass(extendedClassID);

                if(extendedClass == null) {
                    Utils.errorMessage(ctx, "class " + extendedClassID + " does not exist in this file");
                    symbolTable.setError();
                }
                else if(extendedClass.containsMethod(new MethodRecord("main", "void"))) {
                    Utils.errorMessage(ctx, "the main class " + extendedClass.getID() + " cannot be extended");
                    symbolTable.setError();
                }
                else {
                    childClass.addParent(extendedClass);
                }
            }
        }
        // on the second pass we check the inheritance hierarchy
        else {
            if(containsCycle(childClass)) {
                Utils.errorMessage(ctx, "inheritance hierarchy for class " + childClass.getID() + " contains a cycle");
                symbolTable.setError();
            }
            else resolveInheritance(ctx, childClass);
        }
    }

    /**
     * Resolve inheritance by adding methods and variables from parent class to child class.
     * Errors are thrown if methods are not overridden properly.
     * @param ctx class declaration context
     * @param currClass the current class we are adding methods and variables to
     * @return the current class after it has been resolved
     */
    private ClassRecord resolveInheritance(MiniJavaGrammarParser.ClassdeclContext ctx, ClassRecord currClass) {
        if(currClass.getParentClass() == null || resolvedClasses.contains(currClass.getID())) {
            return currClass;
        }

        ClassRecord parentClass = resolveInheritance(ctx, currClass.getParentClass());

        List<MethodRecord> parentMethodRecords = parentClass.getMethods();
        List<MethodRecord> currMethodRecords = currClass.getMethods();
        List<VariableRecord> parentFields = parentClass.getFields();
        List<VariableRecord> currFields = currClass.getFields();

        Set<MethodRecord> methodsToAdd = new HashSet<>();
        Set<MethodRecord> methodsToRemove = new HashSet<>();
        Set<VariableRecord> fieldsToAddToChild = new HashSet<>();

        for(MethodRecord childMethodRecord : currMethodRecords) {
            if (parentClass.containsMethod(childMethodRecord.getID())) {
                MethodRecord parentMethodRecord = parentClass.getMethod(childMethodRecord.getID());
                if (!methodsAreEqual(parentMethodRecord, childMethodRecord)) {
                    Utils.errorMessage(ctx, "method " + childMethodRecord.getID() + " is not overridden properly in " + currClass.getID());
                    // replace the invalid overridden method with the correct one, so we can continue type checking as if it were overridden properly.
                    methodsToRemove.add(childMethodRecord);
                    methodsToAdd.add(parentMethodRecord);
                    symbolTable.setError();
                }
            }
        }

        // copy over fields from the parent to child.
        for(VariableRecord parentField : parentFields) {
            if(currClass.containsField(parentField.getID())) {
                VariableRecord childField = currClass.getField(parentField.getID());
                // if the ID of the child matches the ID in the parent and the variable was also an instance variable in the parent we have an error.
                if(parentField.isInstanceVariable()) {
                    Utils.errorMessage(ctx, "cannot redeclare the identifier " + parentField.getID() + " from the parent class " + currClass.getID());
                    symbolTable.setError();
                }
            }
            else {
                fieldsToAddToChild.add(new VariableRecord(parentField));
            }
        }

        methodsToAdd.addAll(parentMethodRecords);
        methodsToRemove.forEach(currMethodRecords::remove);
        currMethodRecords.addAll(methodsToAdd);
        currFields.addAll(fieldsToAddToChild);

        currClass.setFields(new ArrayList<>(fieldsToAddToChild));
        currClass.setMethods(new ArrayList<>(methodsToAdd));

        resolvedClasses.add(currClass.getID());
        return currClass;
    }

    /**
     * Check if there is a cycle in the inheritance hierarchy.
     * @param currClass the current class we are adding methods and variables to
     * @return whether there is a cycle.
     */
    private boolean containsCycle(ClassRecord currClass) {
        ClassRecord temp = currClass;
        Set<String> parentIDs = new HashSet<>();

        while(temp != null) {
            if(parentIDs.contains(temp.getID())) {
                return true;
            }
            parentIDs.add(temp.getID());
            temp = temp.getParentClass();
        }

        return false;
    }

    /**
     * Helper function to check if two methods are equal
     * @param m1 method 1
     * @param m2 method 2
     * @return whether they are equal
     */
    private boolean methodsAreEqual(MethodRecord m1, MethodRecord m2) {
        if(m1.numberOfParameters() != m2.numberOfParameters()) return false;
        if(!m1.getReturnType().equals(m2.getReturnType())) return false;

        for(int i = 0; i < m1.numberOfParameters(); i++) {
            VariableRecord v1 = m1.getParameters().get(i);
            VariableRecord v2 = m2.getParameters().get(i);

            if(!v1.getType().equals(v2.getType())) return false;
        }

        return true;
    }

    @Override
    public void exitClassdecl(MiniJavaGrammarParser.ClassdeclContext ctx) {
        symbolTable.exitScope();
    }

    @Override
    public void enterMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
        symbolTable.enterScope();
    }

    @Override
    public void exitMethoddecl(MiniJavaGrammarParser.MethoddeclContext ctx) {
        symbolTable.exitScope();
    }

}