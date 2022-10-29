package symbol_table;

public class SymbolTable {

    private final Scope root ;
    private Scope current;
    private boolean containsError;

    public SymbolTable() {
        this.root = new Scope(null);
        this.current = root;
        this.containsError = false;
    }

    public Scope getCurrentScope() { return this.current; }

    public String getCurrentClassName() {
        return this.current.getContainingClass().getID();
    }

    public String getCurrentScopeName() {
        return this.current.getScopeName();
    }

    public String getCurrentScopeType() {
        return this.current.getScopeType();
    }

    public void setCurrentScopeNameAndType(String scopeName, String scopeType) {
        this.current.setScopeNameAndType(scopeName, scopeType);
    }

    public void enterScope() {
        current = current.nextChild();
    }

    public void setCurrentScopeClass(ClassRecord containingClass) {
        this.current.setContainingClass(containingClass);
    }

    public void exitScope() {
        current = current.getParentScope();
    }

    public void put(Record record) {
        current.addRecord(record);
    }

    public boolean lookup(Record record) {
        return current.lookup(record);
    }

    public boolean lookupFromRoot(Record record) { return root.lookup(record); }

    public ClassRecord getClass(String ID) {
        for(Record record : root.getRecordsInCurrScope()) {
            if(record instanceof ClassRecord && record.ID.equals(ID)) {
                return (ClassRecord) record;
            }
        }

        return null;
    }

    public String getTypeOfIdentifier(String identifier) { return current.getTypeOfIdentifier(identifier); }
    public MethodRecord getMethodFromCurrentClass(String methodID) {
        return current.getMethodFromCurrentClass(methodID);
    }

    public MethodRecord getMethodFromProvidedClass(String classID, String methodID) {
        return root.getMethodFromProvidedClass(classID, methodID);
    }

    public VariableRecord getVariableFromCurrentScope(String variableIdentifier, String type) {
        return getCurrentScope().getVariableFromCurrentScope(variableIdentifier, type);
    }

    // same as above but without a type specified.
    public VariableRecord getVariableFromCurrentScope(String variableIdentifier) {
        return getCurrentScope().getVariableFromCurrentScope(variableIdentifier);
    }

    public void printTable() {
        System.out.println();
        System.out.printf("%s %n", "+-------------------------------------------------------------------------------------------+");
        System.out.printf("%" + 18 + "s %" + 32 + "s %" + 38 + "s %n", "ID", "RECORD", "SCOPE (scope type)");
        System.out.printf("%s %n", "+-------------------------------------------------------------------------------------------+");
        root.dfsPrintScopes();
        System.out.printf("%s %n", "+-------------------------------------------------------------------------------------------+");
    }

    public void resetTable() {
        root.resetScope();
    }

    public boolean containsError() {
        return containsError;
    }

    public void setError() {
        this.containsError = true;
    }
}
