package de.dercompiler.intermediate.generation;

import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.selection.Datatype;

public interface OperandTranslator {

    String translate(Address address, Datatype dt);
    String translate(CondTarget target, Datatype dt);
    String translate(ConstantValue value, Datatype dt);
    String translate(LabelOperand operand, Datatype dt);
    String translate(MethodReference mr, Datatype dt);
    String translate(ParameterRegister pr, Datatype dt);
    String translate(VirtualRegister vr, Datatype dt);
    String translate(X86Register rr, Datatype dt);
}
