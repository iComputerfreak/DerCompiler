package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.selection.IRMode;
import de.dercompiler.intermediate.selection.Signedness;

public class LabelOperation extends UnaryOperation {
    public LabelOperation(String lbl) {
        super(OperationType.LBL, new LabelOperand(lbl), false);
        setMode(IRMode.Datatype.NODATA, Signedness.UNSIGNED);
    }

    @Override
    public boolean needsDefinition() {
        return false;
    }

    @Override
    public String getIntelSyntax() {
        return "L%s:".formatted(getArg());
    }
}
