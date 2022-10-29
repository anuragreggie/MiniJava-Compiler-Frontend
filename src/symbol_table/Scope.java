package symbol_table;

import java.util.*;

public final class Scope {
    private int nextChild;
    private final Scope parentScope;
    private final List<Scope> childScopes;
    private final Set<Record> recordsInCurrScope;

    public Set<Record> getRecordsInCurrScope() {
        return recordsInCurrScope;
    }

    ClassRecord containingClass = new ClassRecord("program", "root");
    String scopeName = "";
    String scopeType = "";

    public Scope(Scope parent) {
        this.parentScope = parent;
        nextChild = 0;
        childScopes = new ArrayList<>();
        recordsInCurrScope = new HashSet<>();
    }

    public void setScopeNameAndType(String scopeName, String scopeType) {
        this.scopeName = scopeName;
        this.scopeType = scopeType;
    }

    public String getScopeName(){
        return this.scopeName;
    }

    public String getScopeType(){
        return scopeType;
    }

    /**
     * Get the type of a given identifier contained in the current scope.
     * @param identifier that we are looking for
     * @return type of the identifier, null if the identifier does not exist.
     */
    public String getTypeOfIdentifier(String identifier) {
        if(scopeType.equals("programScope") || scopeType.equals("class")) return null;

        Scope methodScope = this;
        Scope classScope = getParentScope();

        // if the identifier is "this" when we know the type is the name of the current class.
        if(identifier.equals("this")) {
            return getScopeName();
        }

        for(Record record : classScope.recordsInCurrScope) {
            if(record.getID().equals(identifier)) {
                String type = record.getType();
                if(type != null) return type;
            }
        }

        for(Record record : methodScope.recordsInCurrScope) {
            if(record.getID().equals(identifier)) {
                String type = record.getType();
                if(type != null) return type;
            }
        }

        return null;
    }

    /**
     * Finds the method with a given method ID in the class containing the current scope.
     * @param methodIdentifier that we are searching for
     * @return method associated with that ID
     */
    public MethodRecord getMethodFromCurrentClass(String methodIdentifier) {
        if(scopeType.equals("root")) return null;

        Scope classScope;
        // if we are in a method right now, we want to move one scope back to get to the class level scope.
        if(scopeType.equals("method")) {
            classScope = getParentScope();
        }
        else classScope = this;

        for(Record record : classScope.recordsInCurrScope) {
            if(record instanceof MethodRecord && record.getID().equals(methodIdentifier)) {
                return (MethodRecord) record;
            }
        }

        return null;
    }

    /**
     * Finds the variable with a given variable ID in the class containing the current scope.
     * @param variableIdentifier that we are searching for
     * @param type the type of the variable we are searching for
     * @return variable associated with that ID
     */
    public VariableRecord getVariableFromCurrentScope(String variableIdentifier, String type) {
        if(scopeType.equals("root")) return null;

        Scope classScope;
        Scope methodScope = null;
        // if we are in a method right now, we want to move one scope back to get to the class level scope.
        if(scopeType.equals("method")) {
            methodScope = this;
            classScope = getParentScope();
        }
        else classScope = this;

        for(Record record : classScope.recordsInCurrScope) {
            if(record instanceof VariableRecord && record.getID().equals(variableIdentifier)
            && record.type.equals(type)) {
                return (VariableRecord) record;
            }
        }

        if(methodScope != null) {
            for(Record record : methodScope.recordsInCurrScope) {
                if(record instanceof VariableRecord && record.getID().equals(variableIdentifier)
                        && record.type.equals(type)) {
                    return (VariableRecord) record;
                }
            }
        }

        return null;
    }

    // same as above but without a type specified.
    public VariableRecord getVariableFromCurrentScope(String variableIdentifier) {
        if(scopeType.equals("root")) return null;

        Scope classScope;
        Scope methodScope = null;
        // if we are in a method right now, we want to move one scope back to get to the class level scope.
        if(scopeType.equals("method")) {
            methodScope = this;
            classScope = getParentScope();
        }
        else classScope = this;


        for(VariableRecord variableRecord : classScope.containingClass.getFields()) {
            if(variableRecord.getID().equals(variableIdentifier)) {
                return variableRecord;
            }
        }

        if(methodScope != null) {
            for(Record record : methodScope.recordsInCurrScope) {
                if(record instanceof VariableRecord && record.getID().equals(variableIdentifier)) {
                    return (VariableRecord) record;
                }
            }
        }

        return null;
    }

    /**
     * Finds and returns a method for a given class, null is returned if the method does not exist.
     * @param classID class to search for
     * @param methodID method to search for in class
     * @return the located method
     */
    public MethodRecord getMethodFromProvidedClass(String classID, String methodID) {
        ClassRecord classMethodBelongsIn = null;

        // check if the class exists
        for(Record record : recordsInCurrScope) {
            if(record instanceof ClassRecord) {
                ClassRecord classRecord = (ClassRecord) record;
                if(classRecord.getID().equals(classID)) {
                    classMethodBelongsIn = classRecord;
                    break;
                }
            }
        }

        if(classMethodBelongsIn == null) {
            return null;
        }

        return classMethodBelongsIn.getMethod(methodID);
    }

    public ClassRecord getContainingClass() {
        return containingClass;
    }

    public void setContainingClass(ClassRecord containingClass) {
        this.containingClass = containingClass;
    }

    /**
     * method to print scopes, used to print the symbol table.
     */
    public void dfsPrintScopes() {
        for(Record record : recordsInCurrScope) {
            String typeAndID = record.getType() + " " + record.getID();
            if(record instanceof ClassRecord) {
                ClassRecord classRecord = (ClassRecord) record;
                if(classRecord.getParentClass() != null) {
                    typeAndID += " (extends " + classRecord.getParentClass().ID + ")";
                }
            }
            if(record instanceof MethodRecord) {
                typeAndID  = "method " + record.getID() + " (returns " + record.getType() + ")";
            }

            System.out.printf("%" + 20 + "s %" + 40 + "s %" + 30 + "s %n",
                    record.getID(),
                    typeAndID,
                    scopeName + " (" + scopeType + ")");
        }

        for (Scope child : childScopes) {
            child.dfsPrintScopes();
        }
    }

    public void addRecord(Record record) {
        recordsInCurrScope.add(record);
    }

    public Scope nextChild() {
        Scope nextChild;

        // get the next scope in line if it exists, otherwise add a new scope.
        if (this.nextChild >= childScopes.size()) {
            nextChild = new Scope(this);
            childScopes.add(nextChild);
        } else {
            nextChild = childScopes.get(this.nextChild);
        }
        this.nextChild++;
        return nextChild;
    }

    /**
     * Check whether a record exists within the current scope
     * @param record we are searching for
     * @return whether the record exists
     */
    public boolean lookup(Record record) {
        if(record.getID().equals("this")){
            return true;
        }
        if (recordsInCurrScope.contains(record)) { //
            return true;
        } else {
            // move the scope to parent scope
            if (parentScope == null) {
                return false; // identifier is not contained
            } else {
                return parentScope.lookup(record); // recurse to parent
            }
        }
    }

    public Scope getParentScope() {
        return this.parentScope;
    }

    /**
     * Used to reset scopes after a pass with the walker to ensure the next pass begins at the correct point.
     */
    public void resetScope() {
        nextChild = 0;
        for (Scope childScope : childScopes) {
            childScope.resetScope();
        }
    }
}
