package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.LabelOperand;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;
import jdk.jfr.Label;

public abstract class JumpOperation extends UnaryOperation {
    public JumpOperation(OperationType operationType, Operand operand) {
        super(operationType, operand, true);
    }

    public abstract JumpOperation invert(LabelOperand otherTarget);

    @Override
    public boolean needsDefinition() {
        return false;
    }

    public abstract JumpOperation setTo(LabelOperand labelOperand);

    public LabelOperand getTarget() {
        return (LabelOperand) this.getArgs()[0];
    }
}
