package de.dercompiler.intermediate.operation.ConstantOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.ConstantOperation;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;

public class Cqto extends ConstantOperation {
    /*
        Operand is implicit. CQTO works on RAX only
     */

    public Cqto() {
        super(OperationType.CQTO, true);
        setMode(Datatype.QWORD, Signedness.UNSIGNED);
    }

    @Override
    public String getIntelSyntax() {
        return "cqo";
    }
}
