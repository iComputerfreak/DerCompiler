package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.generation.OperandTranslator;
import de.dercompiler.intermediate.selection.Datatype;

public class ConstantValue implements Operand {
    
    private final int value;

    public ConstantValue(int value) {
        this.value = value;
    }
    
    @Override
    public String getIdentifier() {
        return "0x%X".formatted(value);
    }

    @Override
    public String getIdentifier(Datatype datatype) {
        return "$" + value;
    }

    @Override
    public String acceptTranslator(OperandTranslator translator, Datatype dt) {
        return translator.translate(this, dt);
    }

    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }
}
