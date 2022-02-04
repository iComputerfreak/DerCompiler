package de.dercompiler.intermediate.operand;

import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.IRMode;
import firm.Entity;

public class MethodReference extends LabelOperand {
    private final Entity method;

    public MethodReference(Entity method) {
        super(method.getName());
        this.method = method;
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
