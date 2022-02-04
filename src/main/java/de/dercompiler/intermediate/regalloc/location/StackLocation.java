package de.dercompiler.intermediate.regalloc.location;

import de.dercompiler.intermediate.operand.Address;
import de.dercompiler.intermediate.operand.IRRegister;

//always RBP as base
public record StackLocation(IRRegister irr, Address address) implements Location {

}
