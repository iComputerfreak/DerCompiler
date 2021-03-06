package de.dercompiler.intermediate.operation;

import de.dercompiler.intermediate.generation.AtntTranslator;
import de.dercompiler.intermediate.generation.IntelTranslator;
import de.dercompiler.intermediate.operand.Operand;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;
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
        return operationType.getSyntax() + " "
                + Arrays.stream(operands).map(operand -> operand.acceptTranslator(IntelTranslator.getInstance(), getDatatype())).collect(Collectors.joining(","));
    }

    @Override
    public String getAtntSyntax() {
        return operationType.getAtntSyntax(getDatatype()) + " "
                + Arrays.stream(operands).map(operand -> operand.acceptTranslator(AtntTranslator.getInstance(), getDatatype())).collect(Collectors.joining(","));
    }

}

