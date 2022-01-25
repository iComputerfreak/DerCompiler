package de.dercompiler.codeGeneration;

import de.dercompiler.intermediate.Function;
import de.dercompiler.intermediate.memory.BasicMemoryManager;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperations.*;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.TrivialRegisterAllocator;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class EasyTest {

    @Test
    void test(){
        VirtualRegister v1 = new VirtualRegister();
        VirtualRegister v2 = new VirtualRegister();
        VirtualRegister v3 = new VirtualRegister();
        VirtualRegister v4 = new VirtualRegister();
        VirtualRegister v5 = new VirtualRegister();


        Operation o1 = new Add(v3, v2, v1, true);
        Operation o2 = new Sub(v5, v3, v4, true);


        LinkedList<Operation> ops = new LinkedList<Operation>();
        ops.add(o1);
        ops.add(o2);

        Function testFunc = new Function("test", ops);

        TrivialRegisterAllocator regAlloc = new TrivialRegisterAllocator(new BasicMemoryManager());
        regAlloc.allocateRegisters(testFunc);
    }
}
