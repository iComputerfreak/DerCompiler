package de.dercompiler.intermediate.regalloc.location;

import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.List;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class IRRegisterBinding {

    private EnumMap<X86Register, IRRegister> regToIR;
    private Map<IRRegister, X86Register> irToReg;
    private Map<IRRegister, StackLocation> stackEntries;
    private EnumSet<X86Register> available;
    private EnumSet<X86Register> spare;
    private EnumSet<X86Register> use;

    public IRRegisterBinding(Map<IRRegister, Location> initState, EnumSet<X86Register> available, EnumSet<X86Register> spare) {
        regToIR = new EnumMap<>(X86Register.class);
        irToReg = new HashMap<>();
        stackEntries = new HashMap<>();
        for (Map.Entry<IRRegister, Location> entry : initState.entrySet()) {
            if (entry.getValue() instanceof RegisterLocation rl) {
                bindRegister(rl);
            } else if (entry.getValue() instanceof StackLocation sl) {
                bindStackLocation(sl);
            }
        }
        this.available = EnumSet.copyOf(available);
        this.spare = EnumSet.copyOf(spare);
        use = EnumSet.noneOf(X86Register.class);
    }

    private void checkIRRegister(IRRegister irr) {
        if (irToReg.containsKey(irr) || stackEntries.containsKey(irr)) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Something went wrong, reg or IRRegister is bind already");
        }
    }

    private void checkX86Register(X86Register rr) {
        if (regToIR.containsKey(rr)) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Something went wrong, reg or X86Register is bind already");
        }
    }

    public void bindStackLocation(StackLocation sl) {
        checkIRRegister(sl.irr());
        stackEntries.put(sl.irr(), sl);
    }

    public void bindRegister(RegisterLocation rl) {
        checkIRRegister(rl.irr());
        checkX86Register(rl.rr());
        regToIR.put(rl.rr(), rl.irr());
        irToReg.put(rl.irr(), rl.rr());
        if (available.contains(rl.rr())) {
            available.remove(rl.rr());
        } else if (spare.contains(rl.rr())) {
            spare.remove(rl.rr());
        } else {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("try to bind register, but not available!");
        }
        use.add(rl.rr());
    }

    public void unbindRegister(IRRegister irr) {
        if (irToReg.containsKey(irr)) {
            X86Register reg = irToReg.get(irr);
            irToReg.remove(irr);
            regToIR.remove(reg);
            available.add(reg);
        } else if (stackEntries.containsKey(irr)) {
            stackEntries.remove(irr);
        } else {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("IRRegister has no Location at the Moment");
        }
    }

    public boolean usedRegister(X86Register rr) {
        return regToIR.containsKey(rr);
    }

    public boolean hasAvailableRegisters() {
        return !available.isEmpty();
    }

    public X86Register getUnusedRegister() {
        return available.iterator().next();
    }

    public Location findLocation(IRRegister irr) {
        if (irToReg.containsKey(irr)) {
            return new RegisterLocation(irr, irToReg.get(irr));
        } else if (stackEntries.containsKey(irr)) {
            return stackEntries.get(irr);
        } else {
            return new UninitializedLocation();
        }
    }

    public EnumSet<X86Register> getUsage() {
        return use;
    }

    public void apply(Map<IRRegister, Location> locationMap) {
        locationMap.clear();
        for (Map.Entry<IRRegister, X86Register> entry : irToReg.entrySet()) {
            locationMap.put(entry.getKey(), new RegisterLocation(entry.getKey(), entry.getValue()));
        }
        locationMap.putAll(stackEntries);
    }
}
