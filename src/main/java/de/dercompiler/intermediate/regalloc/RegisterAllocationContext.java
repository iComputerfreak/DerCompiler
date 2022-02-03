package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.regalloc.analysis.FunctionSplitView;
import de.dercompiler.intermediate.regalloc.analysis.VariableLifetimeTable;

import java.util.EnumSet;

public record RegisterAllocationContext(EnumSet<X86Register> avalableRegisters, EnumSet<X86Register> spillRegisters, VariableLifetimeTable vlt, FunctionSplitView splitview) {
}
