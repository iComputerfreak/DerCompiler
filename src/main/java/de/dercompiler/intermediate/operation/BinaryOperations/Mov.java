package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.IRMode;
import de.dercompiler.intermediate.selection.Signedness;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

public class Mov extends BinaryOperation {

    public Mov(Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.MOV, target, source, isMemoryOperation);
        if (target instanceof ConstantValue) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Incompatible operands for operation " + getAtntSyntax());
        }
        // should be overwritten for writing int fields etc
        setMode(IRMode.PTR);
    }

    public Mov(Operand target, Operand source) {
        this(target, source, true);
    }

    @Override
    public boolean needsDefinition() {
        return false;
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        Mov mov = new Mov(target, source, true);
        mov.setComment(getComment());
        mov.setMode(getMode());
        return mov;
    }
}
