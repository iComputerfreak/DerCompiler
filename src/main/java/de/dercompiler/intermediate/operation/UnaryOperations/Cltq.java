package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.generation.IntelTranslator;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;

import java.util.Objects;


public final class Cltq extends UnaryArithmeticOperation {

    public Cltq(Operand operand, boolean isMemoryOperation) {
        super(OperationType.CLTQ, operand, isMemoryOperation);
    }

    @Override
    public UnaryArithmeticOperation allocate(Operand operand) {
        Cltq cltq = new Cltq(operand, false);
        cltq.setDefinition(getDefinition());
        cltq.setComment(getComment());
        cltq.setMode(getMode());
        return cltq;
    }

    @Override
    public String getIntelSyntax() {
        return "cdqe" + (Objects.isNull(operand) ? "" :  " " + operand.acceptTranslator(IntelTranslator.getInstance(), getDatatype()));
    }
}
