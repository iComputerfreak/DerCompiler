package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.operand.Operand;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract non-sealed class NaryOperation extends Operation {

    private final Operand[] operands;

    private final OperationType operationType;

    public NaryOperation(OperationType operationType, boolean isMemoryOperation, Operand... operands) {
        super(isMemoryOperation);
        this.operationType = operationType;
        if (!Objects.isNull(operands)) {
            this.operands = operands;
        } else {
            this.operands = new Operand[0];
        }
    }

    @Override
    public Operand[] getArgs() {
        return Arrays.copyOf(operands, operands.length);
    }

    public int getArgsCount() {
        return operands != null ? operands.length : 0;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax() + " " + Arrays.stream(operands).map(Operand::getIdentifier).collect(Collectors.joining(" "));
    }

    //we use for all syntaxes the same implementations, so no other overload needed
}

