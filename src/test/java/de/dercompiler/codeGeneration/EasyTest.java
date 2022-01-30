package de.dercompiler.codeGeneration;

import de.dercompiler.intermediate.Function;
import de.dercompiler.intermediate.memory.BasicMemoryManager;
import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperations.*;
import de.dercompiler.intermediate.operation.NaryOperations.Call;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.TrivialRegisterAllocator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.LinkedList;

public class EasyTest {

    //@Test
    void test(){
        VirtualRegister v1 = new VirtualRegister();
        VirtualRegister v2 = new VirtualRegister();
        VirtualRegister v3 = new VirtualRegister();
        VirtualRegister v4 = new VirtualRegister();
        VirtualRegister v5 = new VirtualRegister();
        ParameterRegister p1 = new ParameterRegister(1);



        Operation o4 = new Call(new LabelOperand("haha"),true, v1, v2,v1,v2,v1,v2,v1);

        Add o1 = new Add(new Address(3,v3,v4,8), new Address(2, v1, v2, 4));
        Operation o2 = new Sub(v3, o1.getDefinition());
        Operation o3 = new Ret(o2.getDefinition());

        LinkedList<Operation> ops = new LinkedList<Operation>();
        ops.add(o1);
        ops.add(o2);
        ops.add(o3);
        ops.add(o4);

        Function testFunc = new Function("test", ops);

        TrivialRegisterAllocator regAlloc = new TrivialRegisterAllocator(new BasicMemoryManager());
        regAlloc.allocateRegisters(testFunc);
    }
}
