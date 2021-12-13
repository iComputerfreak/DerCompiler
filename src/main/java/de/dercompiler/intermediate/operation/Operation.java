package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.Operand;

public sealed interface Operation permits ConstantOperation, UnaryOperation, BinaryOperation {
    Operand[] getArgs();
    OperationType getOperationType();
}
