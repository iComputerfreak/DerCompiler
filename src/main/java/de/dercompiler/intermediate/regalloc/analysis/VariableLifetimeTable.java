package de.dercompiler.intermediate.regalloc.analysis;

import de.dercompiler.Function;
import de.dercompiler.intermediate.operand.IRRegister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.ObjectUtils.max;

public class VariableLifetimeTable {

    private static class RegisterLifetime {

        private int min;
        private int max;
        private final IRRegister irr;
        private final LinkedList<Integer> usage;

        //we assume we have always an element in the list
        public void addInOrder(int use) {
            if (usage.getFirst() > use) {
                usage.addFirst(use);
            } else if (usage.getLast() < use) {
                usage.addLast(use);
            } else {
                int i = 0;
                while (usage.get(i) < use) {
                    i++;
                }
                usage.add(i, use);
            }
        }

        public RegisterLifetime(IRRegister irr, int index) {
            min = index;
            max = index;
            this.irr = irr;
            usage = new LinkedList<>();
            usage.add(index);
        }

        void update(int operationIndex) {
            if (min > operationIndex) {
                min = operationIndex;
            }

            if (max < operationIndex) {
                max = operationIndex;
            }
            addInOrder(operationIndex);
        }

        @Override
        public String toString() {
            return "{ [" + min + ", " + max + "] " + irr + " }";
        }
    }

    private static class RegisterNeeds {
        private final List<IRRegister> live;
        private final List<IRRegister> stack;

        public RegisterNeeds() {
            live = new ArrayList<>();
            stack = new ArrayList<>();
        }

        public int addRegister(IRRegister irr) {
            live.add(irr);
            return live.size();
        }
    }

    private final RegisterLifetime[] rlt;
    private final RegisterNeeds[] usage;

    private final int CALL_ABI_NUM_ARGUMENTS;
    private int max_registers_active;

    public VariableLifetimeTable(Function func, int CallAbiNumArguments) {
        rlt = new RegisterLifetime[func.getNumVirtualRegisters()];
        usage = new RegisterNeeds[func.getNumVirtualRegisters()];
        Arrays.fill(rlt, null);
        for (int i = 0; i < usage.length; i++) {
            usage[i] = new RegisterNeeds();
        }
        CALL_ABI_NUM_ARGUMENTS = CallAbiNumArguments;
    }

    private void update(IRRegister register, int operation) {
        int index = register.getId();
        if (rlt[index] == null) {
            rlt[index] = new RegisterLifetime(register, operation);
        } else {
            rlt[index].update(operation);
        }
    }

    public void updateTarget(IRRegister vr, int operation) {
        update(vr,operation - 1);
    }

    public void updateDefinition(IRRegister vr, int operation) {
        update(vr, operation);
    }

    public VariableLifetimeTable generate() {
        System.out.println(Arrays.toString(rlt));
        for (RegisterLifetime rl : rlt) {
            if (rl == null) continue;
            for (int i = rl.min; i <= rl.max; i++) {
                max_registers_active = max(max_registers_active, usage[i].addRegister(rl.irr));
            }
        }
        return this;
    }

    public int getDefinition(int id) {
        return rlt[CALL_ABI_NUM_ARGUMENTS + id].min;
    }

    public int getLastUsage(int id) {
        return getLastUsage(id, false);
    }

    public List<IRRegister> getDyingRegisters(int min, int max) {
        List<IRRegister> registers = new LinkedList<>();
        for (RegisterLifetime rl : rlt) {
            if (rl == null) continue;
            if (min <= rl.max && rl.max < max) {
                registers.add(rl.irr);
            }
        }
        return registers;
    }

    public int getLastUsage(int id, boolean parameter) {
        int effectiveID = id;
        if (parameter) effectiveID += CALL_ABI_NUM_ARGUMENTS;
        return rlt[effectiveID].max;
    }

    public int getNumRegistersMaximallyActive() {
        return max_registers_active;
    }
}
