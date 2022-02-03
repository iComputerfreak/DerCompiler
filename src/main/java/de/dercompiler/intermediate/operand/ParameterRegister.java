package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;

import java.util.Objects;

public class ParameterRegister implements IRRegister {

    private final int number;
    public ParameterRegister(int number) {
        this.number = number;
    }

    @Override
    public String getIdentifier() {
        return "@P%d".formatted(number);
    }

    @Override
    public String getIdentifier(Datatype datatype) {
        return getIdentifier();
    }

    @Override
    public int getId(){
        return number;
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

    @Override
    public int hashCode() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.isNull(obj)) return false;
        if (!(obj instanceof ParameterRegister pr)) return false;
        return number == pr.number;
    }
}
