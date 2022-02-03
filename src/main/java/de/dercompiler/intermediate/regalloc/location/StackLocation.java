package de.dercompiler.intermediate.regalloc.location;

import de.dercompiler.intermediate.operand.Address;

//always RBP as base
public record StackLocation(Address address) implements Location {
}
