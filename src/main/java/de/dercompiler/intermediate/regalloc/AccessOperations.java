package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.operation.Operation;

import java.util.LinkedList;
import java.util.List;

//list of operations of spill code, register the result is stored
public record AccessOperations(LinkedList<Operation> operations, AccessTiming timing) {

    public boolean empty() {
        return operations.isEmpty();
    }

    public enum AccessTiming {
        START_OF_SHARD, //insert before the first real operation
        END_OF_SHARD, //insert before the last operation
        AFTER_SHARD, //insert after the last operation
        AT_TIME; //insert before Operation of call
    }
}
