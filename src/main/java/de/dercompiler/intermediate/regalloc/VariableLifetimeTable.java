package de.dercompiler.intermediate.regalloc;

import de.dercompiler.ast.Program;
import de.dercompiler.intermediate.Function;

import java.util.Arrays;
import java.util.HashMap;

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

    public VariableLifetimeTable(Function func) {
        rlt = new RegisterLifetime[func.getNumVirtualRegisters()];
        Arrays.fill(rlt, null);
    }

    public void updateOperand(int index, int operation) {
        updateTarget(index, operation - 1);
    }

    public void updateTarget(int index, int operation) {
        rlt[index].update(operation);
    }

    public VariableLifetimeTable generate() {


        return this;
    }

}
