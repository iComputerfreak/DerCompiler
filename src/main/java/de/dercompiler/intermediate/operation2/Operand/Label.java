package de.dercompiler.intermediate.operation2.Operand;

public class Label implements Operand{
    String label;

    public Label(String label){
        this.label = label;
    }

    @Override
    public String getIdentifier() {
        return label;
    }
}
