package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.ConstantValue;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

public class ShiftOperation extends BinaryOperation {
    public ShiftOperation(OperationType operationType, Operand target, Operand source) {
        super(operationType, target, source);
        if (!(target instanceof Register || target instanceof Address)
        || !(source instanceof Register || source instanceof ConstantValue c)) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Incompatible operand types: " + this.getIntelSyntax());
        }
    }
}
