package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;

import java.util.Arrays;

public class VariableLifetimeTable {

    private static class RegisterLifetime {

        private int min;
        private int max;

        public RegisterLifetime(int index) {
            min = index;
            max = index;
        }

        void update(int operationIndex) {
            if (min > operationIndex) {
                min = operationIndex;
            }

            if (max < operationIndex) {
                max = operationIndex;
            }
        }
    }

    private final RegisterLifetime[] rlt;

    private final int CALL_ABI_NUM_ARGUMENTS;

    public VariableLifetimeTable(Function func, int CallAbiNumArguments) {
        rlt = new RegisterLifetime[func.getNumVirtualRegisters()];
        Arrays.fill(rlt, null);
        CALL_ABI_NUM_ARGUMENTS = CallAbiNumArguments;
    }

    private void update(int index, int operation) {
        if (rlt[index] == null) {
            rlt[index] = new RegisterLifetime(operation);
        } else {
            rlt[index].update(operation);
        }
    }

    public void updateTarget(int index, int operation) {
        update(index,operation - 1);
    }

    public void updateDefinition(int index, int operation) {
        update(index, operation);
    }

    public VariableLifetimeTable generate() {

        return this;
    }

}
