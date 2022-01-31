package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;

public class MethodReference extends LabelOperand {
    public MethodReference(String target) {
        super(target);
    }

    @Override
    public String getIdentifier() {
        return getTarget(); // no 'L'!
    }

    @Override
    public String getIdentifier(Datatype datatype) {
        return getTarget(); // no 'L'!
    }
}
