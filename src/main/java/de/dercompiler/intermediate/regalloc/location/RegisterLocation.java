package de.dercompiler.intermediate.regalloc.location;

import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operand.X86Register;

public record RegisterLocation(IRRegister irr, X86Register rr) implements Location {

}
