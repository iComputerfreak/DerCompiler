package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;
import firm.Mode;

public sealed abstract class Operation permits ConstantOperation, UnaryOperation, BinaryOperation {
    public abstract Operand[] getArgs();
    public abstract OperationType getOperationType();
    public abstract String getIntelSyntax();
    public abstract Mode getMode();
    public abstract void setMode(Mode bu);

    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
