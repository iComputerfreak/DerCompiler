package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;
import firm.Entity;
import firm.Mode;

/**
 * Represents a jump or call target.
 */
public class LabelOperand implements Operand {

    private String label;

    public LabelOperand(String label) {
        this.label = label;
    }

    public static Operand forMethod(Entity methodEntity) {
        return new LabelOperand(methodEntity.getName());
    }

    @Override
    public String getIdentifier() {
        return label;
    }


    @Override
    public String toString() {
        return getIdentifier();
    }
}
