package symbol_table;

import java.util.LinkedList;
import java.util.List;

public class MethodRecord extends Record {
    private final List<VariableRecord> parameters;
    int params = 0;

    public MethodRecord(String id, String type) {
        super(id, type);
        parameters = new LinkedList<>();
    }

    public void addParameter(VariableRecord parameter) {
        this.parameters.add(parameter);
        params++;
    }

    public boolean containsParameter(Record parameter) {
        if(parameter instanceof VariableRecord) {
            return parameters.contains(parameter);
        }
        return false;
    }

    public int numberOfParameters() {
        return parameters.size();
    }

    public String getReturnType() {
        return this.type;
    }

    public void printParameterMap() {
        System.out.print("( ");

        for(VariableRecord p : parameters) {
            System.out.print(p.toString());
        }

        System.out.print(" )\n");
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof MethodRecord)) {
            return false;
        }

        MethodRecord c = (MethodRecord) o;

        return ID.equals(c.ID);
    }

    public List<VariableRecord> getParameters() {
        return parameters;
    }
}
