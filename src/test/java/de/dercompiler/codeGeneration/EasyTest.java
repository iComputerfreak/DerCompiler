package de.dercompiler.codeGeneration;

import de.dercompiler.Function;
import de.dercompiler.intermediate.memory.BasicMemoryManager;
import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperations.*;
import de.dercompiler.intermediate.operation.NaryOperations.Call;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Dec;
import de.dercompiler.intermediate.operation.UnaryOperations.Jne;
import de.dercompiler.intermediate.regalloc.TrivialRegisterAllocator;
import org.junit.jupiter.api.Test;


import java.util.LinkedList;

public class EasyTest {

    /*@Test*/
    void test(){
        VirtualRegister v1 = new VirtualRegister();
        VirtualRegister v2 = new VirtualRegister();
        VirtualRegister v3 = new VirtualRegister();
        VirtualRegister v4 = new VirtualRegister();
        VirtualRegister v5 = new VirtualRegister();
        ParameterRegister p1 = new ParameterRegister(1);




        Add o1 = new Add(v1, new ConstantValue(10));
        Operation o2 = new Sub(new Address(7, p1, v5, 1), o1.getDefinition());
        Operation o3 = new Ret(o2.getDefinition());
        Operation o4 = new Call(new MethodReference("haha"),true, v1, v2,v1,v2,v1,v2,v1);
        Operation o5 = new Jne(new LabelOperand("testziel"));
        Operation o6 = new Dec(v1, true);
        Operation o7 = new Div(v1, v2);


        LinkedList<Operation> ops = new LinkedList<>();
        ops.add(o1);
        ops.add(o2);
        ops.add(o3);
        ops.add(o4);
        ops.add(o5);
        ops.add(o6);
        ops.add(o7);

        Function testFunc = new Function(null);
        testFunc.setOperations(ops);

        TrivialRegisterAllocator regAlloc = new TrivialRegisterAllocator(new BasicMemoryManager());
        regAlloc.allocateRegisters(testFunc);
    }
}
