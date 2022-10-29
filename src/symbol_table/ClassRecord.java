package symbol_table;

import java.util.*;

public class ClassRecord extends Record {
    private ClassRecord parentClass;
    private final Map<String, MethodRecord> methodRecords;
    private final Map<String, VariableRecord> fields;

    public ClassRecord(String ID, String type) {
        super(ID, type);
        methodRecords = new HashMap<>();
        fields = new HashMap<>();
        parentClass = null;
    }
    public void addMethod(MethodRecord methodRecord){
        methodRecords.put(methodRecord.ID, methodRecord);
    }

    public void setFields(List<VariableRecord> fields) {
        for(VariableRecord v : fields) {
            this.fields.put(v.ID, v);
        }
    }

    public void setMethods(List<MethodRecord> methods) {
        for(MethodRecord m : methods) {
            this.methodRecords.put(m.ID, m);
        }
    }

    public boolean containsMethod(MethodRecord methodRecord) {
        return methodRecords.containsKey(methodRecord.ID);
    }

    public boolean containsMethod(String ID) {
        return methodRecords.containsKey(ID);
    }

    public boolean containsField(String ID) {
        return fields.containsKey(ID);
    }

    public MethodRecord getMethod(String methodID) {
        return methodRecords.get(methodID);
    }



    public List<MethodRecord> getMethods() {
        return new ArrayList<>(methodRecords.values());
    }

    public VariableRecord getField(String variableID) {
        return fields.get(variableID);
    }

    public List<VariableRecord> getFields() {
        return new ArrayList<>(fields.values());
    }

    public void addField(VariableRecord fieldRecord){
        this.fields.put(fieldRecord.ID, fieldRecord);
    }

    public ClassRecord getParentClass() {
        return parentClass;
    }

    public void addParent(ClassRecord parentClass) {
        this.parentClass = parentClass;
    }

    public void printMethodMap() {
        for(String ID : methodRecords.keySet()) {
            MethodRecord methodRecord = getMethod(ID);
            System.out.print("\t->  " + methodRecord.getType() + " " + methodRecord.getID());
            methodRecord.printParameterMap();
        }
    }

    public void printFieldMap() {
        for(String ID : fields.keySet()) {
            VariableRecord field = getField(ID);
            System.out.print("\t FIELD:  " + field.getType() + " " + field.getID());
        }
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof ClassRecord)) {
            return false;
        }

        ClassRecord c = (ClassRecord) o;

        return ID.equals(c.ID);
    }

}
