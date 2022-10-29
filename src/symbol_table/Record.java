package symbol_table;

import java.util.Objects;

public class Record {
    protected String ID;
    protected String type;
    protected boolean isInitialised;
    protected boolean isFormalParameter;
    protected boolean isInstanceVariable;

    public Record (String ID, String type){
        this.ID = ID;
        this.type = type;
        this.isInitialised = false;
    }

    public Record (String ID, String type, boolean isFormalParameter){
        this.ID = ID;
        this.type = type;
        this.isInitialised = false;
        this.isFormalParameter = isFormalParameter;
    }

    public String getID(){
        return this.ID;
    }

    public String getType(){
        return this.type;
    }

    @Override
    public String toString() {
        return "(record with ID=" + ID + ", type=" + type + ")";
    }

    public boolean isInitialised() {
        if(isFormalParameter || isInstanceVariable) return true;
        else return isInitialised;
    }

    public void initialise() {
        this.isInitialised = true;
    }

    public void setIsInstanceVariable(boolean isInstanceVariable) {
        this.isInstanceVariable = isInstanceVariable;
    }

    public boolean isInstanceVariable() {
        return isInstanceVariable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, type);
    }
}
