package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

public class TrivialRegisterAllocator extends RegisterAllocator{

    public TrivialRegisterAllocator(MemoryManager manager) {
        super(manager);
    }

    private final OutputMessageHandler outputMessageHandler = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);

    @Override
    public void allocateRegisters(Function function) {
        for (Operation op : function.getOperations()){
            if (op instanceof BinaryOperation bo){
                Operand[] operands = bo.getArgs();

                //Operand 1 wird geladen
                if (operands[0] instanceof VirtualRegister vr){
                    outputMessageHandler.printInfo("MOV %r10," + manager.getVar((int) vr.getId()).getIdentifier());

                } else if (operands[0] instanceof ParameterRegister pr){
                    outputMessageHandler.printInfo("MOV %r10," + manager.getArgument((int) pr.getId()).getIdentifier());

                }
                //Operand 2 wird geladen
                if (operands[1] instanceof VirtualRegister vr){

                    outputMessageHandler.printInfo("MOV %r11," + manager.getVar((int) vr.getId()).getIdentifier());
                } else if (operands[1] instanceof ParameterRegister pr){
                    outputMessageHandler.printInfo("MOV %r11," + manager.getArgument((int) pr.getId()).getIdentifier());

                }

                outputMessageHandler.printInfo(op.getOperationType().getSyntax() + " %r10, r11");

                outputMessageHandler.printInfo("MOV " + manager.getVar((int) ((VirtualRegister) ((BinaryOperation) op).getDefinition()).getId()).getIdentifier() + ", %r10");

            } else if (op instanceof Ret ret){

                if (ret.getArgs().length != 0){
                    VirtualRegister returnValue = (VirtualRegister) ret.getArgs()[0];

                    Operand operand = ret.getArgs()[0];
                    if (operand instanceof VirtualRegister vr){

                        manager.getArgument((int) vr.getId()).getIdentifier();
                        outputMessageHandler.printInfo("MOV " +  manager.getReturnValue().getIdentifier() + "," + manager.getVar((int) vr.getId()).getIdentifier());
                    } else if (operand instanceof ParameterRegister pr){
                        outputMessageHandler.printInfo("MOV " +  manager.getReturnValue().getIdentifier() + "," + manager.getArgument((int) pr.getId()).getIdentifier());
                    }
                }
                outputMessageHandler.printInfo("RET");

            }
        }
    }
}
