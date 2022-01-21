package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;
import firm.Mode;

public sealed interface Operation permits ConstantOperation, UnaryOperation, BinaryOperation {
    Operand[] getArgs();
    OperationType getOperationType();
    String getIntelSyntax();
    Mode getMode();
    void setMode(Mode bu);
    String toString();
}
