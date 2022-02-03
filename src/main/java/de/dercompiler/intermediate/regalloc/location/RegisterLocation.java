package de.dercompiler.intermediate.regalloc.location;

import de.dercompiler.intermediate.operand.X86Register;

public record RegisterLocation(X86Register register) implements Location {

}
