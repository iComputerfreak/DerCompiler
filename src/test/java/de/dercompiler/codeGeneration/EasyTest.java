package de.dercompiler.codeGeneration;

import de.dercompiler.intermediate.Function;
import de.dercompiler.intermediate.memory.BasicMemoryManager;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperations.*;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.TrivialRegisterAllocator;

import java.util.LinkedList;

public class EasyTest {

    /*@Test*/
    void test(){
        VirtualRegister v1 = new VirtualRegister();
        VirtualRegister v2 = new VirtualRegister();
        VirtualRegister v3 = new VirtualRegister();
        VirtualRegister v4 = new VirtualRegister();
        VirtualRegister v5 = new VirtualRegister();
        ParameterRegister p1 = new ParameterRegister(0);

        Operation o1 = new Add(v2, p1);
        Operation o2 = new Sub(v3, v1);
        Operation o3 = new Ret(p1);

        LinkedList<Operation> ops = new LinkedList<Operation>();
        ops.add(o1);
        ops.add(o2);
        ops.add(o3);

        Function testFunc = new Function("test", ops);

        TrivialRegisterAllocator regAlloc = new TrivialRegisterAllocator(new BasicMemoryManager());
        regAlloc.allocateRegisters(testFunc);
    }
}
