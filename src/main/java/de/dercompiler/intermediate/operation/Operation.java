package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.IRMode;
import de.dercompiler.intermediate.selection.Signedness;
import firm.Mode;

public sealed abstract class Operation permits BinaryOperation, ConstantOperation, NaryOperation, UnaryOperation {

    protected IRMode mode;

    public abstract Operand[] getArgs();
    public abstract OperationType getOperationType();
    public abstract String getIntelSyntax();

    private int index;
    private final boolean isMemoryOperation;
    
    protected Operation(boolean isMemoryOperation) {
        this.isMemoryOperation = isMemoryOperation;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setMode(Datatype datatype, Signedness signedness) {
        mode = new IRMode(datatype, signedness);
    }

    public void setMode(Mode mode) {
        Datatype datatype = Datatype.forMode(mode);
        this.mode = new IRMode(datatype, mode.isSigned() ? Signedness.SIGNED : Signedness.UNSIGNED);
    }

    public Datatype getDatatype() {
        return mode.type();
    }

    public boolean isSigned() {
        return mode.isSigned();
    }
    
    public boolean isMemoryOperation() {
        return isMemoryOperation;
    }
}
