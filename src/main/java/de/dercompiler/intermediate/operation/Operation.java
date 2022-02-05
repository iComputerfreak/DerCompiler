package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.IRMode;
import de.dercompiler.intermediate.selection.Signedness;
import firm.Mode;

import java.util.Objects;

public sealed abstract class Operation permits BinaryOperation, ConstantOperation, NaryOperation, TenaryOperation, UnaryOperation {

    private Operand definition = null;
    protected IRMode mode;
    private String comment;

    public abstract Operand[] getArgs();
    public abstract OperationType getOperationType();
    public abstract String getIntelSyntax();
    public abstract String getAtntSyntax();

    private int index;
    private final boolean isMemoryOperation;
    
    protected Operation(boolean isMemoryOperation) {
        if (this.needsDefinition()) {
            this.definition = new VirtualRegister();
        }
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

    public void setMode(IRMode mode) {
        setMode(mode.type(), mode.signedness());
    }

    public void setMode(Mode mode) {
        Datatype datatype = Datatype.forMode(mode);
        this.mode = new IRMode(datatype, mode.isSigned() ? Signedness.SIGNED : Signedness.UNSIGNED);
    }

    public Datatype getDatatype() {
        return mode != null ? mode.type() : null;
    }

    public boolean isSigned() {
        return mode.isSigned();
    }
    
    public boolean isMemoryOperation() {
        return isMemoryOperation;
    }

    public IRMode getMode() {
        return mode;
    }

    public boolean hasDefinition() {
        return Objects.nonNull(definition);
    }

    public Operand getDefinition() {
        return definition;
    }

    public boolean needsDefinition() {
        return true;
    }

    public void setDefinition(Operand targetReg) {
        this.definition = targetReg;
    }

    @Override
    public String toString() {
        return "%s (%s%s)%s".formatted(
                getAtntSyntax(),
                mode,
                isMemoryOperation()? "/M" : "",
                needsDefinition() ? " --> " + getDefinition() : ""
        );
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void addComment(String comment) {
        this.comment = (this.comment == null) ? comment : (this.comment + "  --  " + comment);
    }
}
