package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.ParameterRegister;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operand.X86Register;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.NaryOperations.Call;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

public class TrivialRegisterAllocator extends RegisterAllocator{

    public TrivialRegisterAllocator(MemoryManager manager) {
        super(manager);
    }

    private final OutputMessageHandler outputMessageHandler = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);

    private String assembly;

    private void addAssembly(String string){
        assembly += string + "\n";
    }

    private void resetAssembly(){
        assembly = "\n";
    }

    private void printAssembly(){
        outputMessageHandler.printInfo(assembly);
    }

    private X86Register[] parameterRegister = new X86Register[]{X86Register.RDI, X86Register.RSI, X86Register.RDX, X86Register.RCX, X86Register.R8, X86Register.R9};
    /*
    parameter 1 bis 6 stehen in den parameter Registern und der Rest steht auf dem Stack
     */
    private String getParam(int n){
        if (n < 7){
            return parameterRegister[n-1].getIdentifier();
        } else {
            return manager.getArgument(n - 6).getIdentifier();
        }
    }

    private String getLocalVar(int n){
        return manager.getVar(n).getIdentifier();
    }

    @Override
    public void allocateRegisters(Function function) {
        resetAssembly();
        for (Operation op : function.getOperations()){
            if (op instanceof BinaryOperation bo){
                Operand[] operands = bo.getArgs();

                //Operand 1 wird geladen
                if (operands[0] instanceof VirtualRegister vr){
                    getLocalVar((int) vr.getId());
                    addAssembly("MOV %r10," + getLocalVar((int) vr.getId()));
                } else if (operands[0] instanceof ParameterRegister pr){
                    addAssembly("MOV %r10," + getParam(pr.getId()));
                }
                //Operand 2 wird geladen
                if (operands[1] instanceof VirtualRegister vr){
                    addAssembly("MOV %r11," + getLocalVar((int) vr.getId()));
                } else if (operands[1] instanceof ParameterRegister pr){
                    addAssembly("MOV %r11," + getParam(pr.getId()));
                }
                //Operation durchführen
                addAssembly(op.getOperationType().getSyntax() + " %r10, %r11");
                //Ergebnis wieder auf den Stack schreiben
                getLocalVar((int) ((VirtualRegister) bo.getDefinition()).getId());
                addAssembly("MOV " + getLocalVar((int) ((VirtualRegister) bo.getDefinition()).getId()) + ", %r10");

            } else if (op instanceof Ret ret){

                if (ret.getArgs().length != 0){
                    Operand operand = ret.getArgs()[0];
                    if (operand instanceof VirtualRegister vr){
                        getLocalVar((int) vr.getId());
                        addAssembly("MOV " +  manager.getReturnValue().getIdentifier() + "," +  getLocalVar((int) vr.getId()));
                    } else if (operand instanceof ParameterRegister pr){
                        addAssembly("MOV " +  manager.getReturnValue().getIdentifier() + "," + getParam((int) pr.getId()));
                    }
                }
                addAssembly("RET");

            } else if (op instanceof Call call){
                Operand[] args = call.getArgs();
                for (int i = 0; i < 6 && i < args.length; i++){

                    //Annahme: die Call argumente sind alles VirtualRegister
                    //Werte auf dem Stack müssen noch gesicher werden
                }
            }

        }
        printAssembly();
    }
}
