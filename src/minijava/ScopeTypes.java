package minijava;

public enum ScopeTypes {
    ROOT("root"),
    CLASS("class"),
    METHOD("method");

    private final String text;

    ScopeTypes(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}