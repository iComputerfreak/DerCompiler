package de.dercompiler.intermediate.operation.UnaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operation.OperationType;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;

public class IDiv extends UnaryOperation {

    public IDiv(Operand divisor) {
        super(OperationType.IDIV, divisor, true);
        setMode(Datatype.QWORD, Signedness.SIGNED);
    }

    public IDiv allocate(Operand divisor){
        IDiv div = new IDiv(divisor);
        div.setMode(getMode());
        return div;
    }

    public Operand getDivisor() {
        return super.getArg();
    }

    @Override
    public String getIntelSyntax() {
        return operationType.getSyntax() + " " + getDivisor().getIdentifier();
    }

    @Override
    public String getAtntSyntax(){
        return getIntelSyntax();
    }
}
