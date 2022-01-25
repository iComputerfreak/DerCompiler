package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

public class Mov extends BinaryOperation {

    public Mov(Operand definition, Operand target, Operand source, boolean isMemoryOperation) {
        super(OperationType.MOV, definition, target, source, isMemoryOperation);
        if (target instanceof ConstantValue) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Incompatible operands for operation " + getIntelSyntax());
        }
    }

}
