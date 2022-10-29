package symbol_table;

public class VariableRecord extends Record {
    public VariableRecord(String ID, String type) {
        super(ID, type);
    }

    public VariableRecord(String ID, String type, boolean isFormalParameter) { super(ID, type, isFormalParameter); }

    public VariableRecord(VariableRecord variableRecord) {
        super(variableRecord.ID, variableRecord.type, variableRecord.isFormalParameter);
        this.isInitialised = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof VariableRecord)) {
            return false;
        }

        VariableRecord c = (VariableRecord) o;

        return ID.equals(c.ID);
    }

    @Override
    public String toString() {
        return "(variable with ID=" + ID + ", type=" + type + ", initialised=" + isInitialised + ", isFormalParameter=" + isFormalParameter + ")";
    }
}
