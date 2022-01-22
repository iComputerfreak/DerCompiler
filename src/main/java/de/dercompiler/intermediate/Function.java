package de.dercompiler.intermediate;

import de.dercompiler.intermediate.operation.Operation;

import java.util.LinkedList;
import java.util.*;

public class Function {

    private final LinkedList<Operation> operations;
    private final int num;
    private final String functionName;

    public Function(String functionName, LinkedList<Operation> ops) {
        this.functionName = functionName;

        int i = 0;
        Iterator<Operation> it = ops.iterator();
        while (it.hasNext()) {
            it.next().setIndex(i++);
        }
        operations = ops;
        num = i;
    }

    public LinkedList<Operation> getOperations() {
        return operations;
    }

    public int getNumVirtualRegisters() {
        return num;
    }
}
