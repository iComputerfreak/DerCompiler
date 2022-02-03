package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.memory.BasicMemoryManager;
import de.dercompiler.intermediate.memory.BasicRegisterManager;
import de.dercompiler.intermediate.operand.IRLocation;
import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.analysis.FunctionSplitView;
import de.dercompiler.intermediate.regalloc.analysis.VariableLifetimeTable;
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;
import de.dercompiler.intermediate.regalloc.location.Location;
import de.dercompiler.intermediate.regalloc.location.RegisterLocation;
import de.dercompiler.intermediate.regalloc.location.StackLocation;

import java.util.List;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class IRLocationOrganizer {

    public record AccessOperations(List<Operation> operations, X86Register register) {

    }

    private final BasicMemoryManager stack;
    private final BasicRegisterManager register;
    private final CallingConvention convention;
    private final Map<IRLocation, Location> locationMap;
    private final VariableLifetimeTable vlt;
    private final FunctionSplitView fsv;

    public IRLocationOrganizer(EnumSet<X86Register> avalable, CallingConvention callingConvention, ParameterRegister[] params, VariableLifetimeTable varTimes, FunctionSplitView splitView) {
        stack = new BasicMemoryManager();
        register = new BasicRegisterManager(avalable);
        convention = callingConvention;
        locationMap = new HashMap<>();
        vlt = varTimes;
        fsv = splitView;
        for (ParameterRegister p : params) {
            //bind register
            if (p.getId() < convention.getNumberOfArgumentsRegisters()) {
                locationMap.put(p, new RegisterLocation(callingConvention.getArgumentRegister(p.getId())));
            } else { //bind stack
                locationMap.put(p, new StackLocation(stack.get));
            }
        }
    }

    public void nextSection() {

    }

    public AccessOperations makeAvailable(IRRegister register) {
        return null;
    }

    public AccessOperations bindVirtualRegister(IRRegister register, X86Register realRegister) {
        return null;
    }

}
