package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperations.Add;
import de.dercompiler.intermediate.operation.BinaryOperations.Mov;
import de.dercompiler.intermediate.operation.NaryOperation;
import de.dercompiler.intermediate.operation.NaryOperations.Call;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.LinkedList;
import java.util.List;


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

    //maxVar gibt an welche die Variable im Stack mit der niedrigsten Adresse ist
    private int maxVar = -1;
    private X86Register[] parameterRegister = new X86Register[]{X86Register.RDI, X86Register.RSI, X86Register.RDX, X86Register.RCX, X86Register.R8, X86Register.R9};
    /*
    parameter 1 bis 6 stehen in den parameter Registern und der Rest steht auf dem Stack
     */
    private Operand getParam(int n){
        if (n < 7){
            return parameterRegister[n-1];
        } else {
            return manager.getArgument(n - 6);
        }
    }

    private Operand getOperand(Operand operand){
        if (operand instanceof VirtualRegister vr){
            return getLocalVar((int) vr.getId());
        } else if (operand instanceof ParameterRegister pr){
            return getParam(pr.getId());
        } else {
            return null;
        }
    }

    private Operand getLocalVar(int n){
        maxVar = Integer.max(maxVar, n);
        return manager.getVar(n);
    }

    @Override
    public void allocateRegisters(Function function) {
        List<Operation> ops = new LinkedList<Operation>();

        resetAssembly();
        for (Operation op : function.getOperations()){
            if (op instanceof BinaryOperation bo){
                Operand[] operands = bo.getArgs();

                //Operand 1 wird geladen
                addAssembly("MOV %r10," + getOperand(operands[0]).getIdentifier());
                ops.add(new Mov(X86Register.R10, getOperand(operands[0]), true));

                //Operand 2 wird geladen
                addAssembly("MOV %r11," + getOperand(operands[1]).getIdentifier());
                ops.add(new Mov(X86Register.R11, getOperand(operands[1]), true));


                //Operation durchführen
                addAssembly(op.getOperationType().getSyntax() + " %r10, %r11");
                ops.add(bo.allocate(X86Register.R10, X86Register.R11));

                //Ergebnis wieder auf den Stack schreiben
                addAssembly("MOV " + getOperand(bo.getDefinition()).getIdentifier() + ", %r10");
                ops.add(new Mov(getOperand(bo.getDefinition()), X86Register.R10, true));


            } else if (op instanceof Ret ret){

                if (ret.getArgs().length != 0){
                    Operand operand = ret.getArgs()[0];
                    addAssembly("MOV " +  manager.getReturnValue().getIdentifier() + "," +  getOperand(operand).getIdentifier());
                    ops.add(new Mov(manager.getReturnValue(), getOperand(operand), true));
                }
                addAssembly("RET");
                ops.add(new Ret());


            } else if (op instanceof Call call){
                //args[0] ist LabelOperand
                Operand[] args = call.getArgs();
                int tempMaxVar = maxVar;
                //Die 6 ParameterRegister müssen gesichert werden
                for (int i = 0; i < 6; i++){
                    addAssembly("MOV " +  getLocalVar(maxVar + 1).getIdentifier() + "," + getParam(i+1).getIdentifier());
                    ops.add(new Mov(getLocalVar(maxVar+1), getParam(i+1), true));
                }
                //Die Parameter müssen in Regsiter bzw auf den Stack geschrieben werden
                for (int i = 1; i < args.length && i < 7; i++){
                    addAssembly("MOV " + getParam(i).getIdentifier() + "," + getOperand(args[i]).getIdentifier());
                    ops.add(new Mov(getParam(i), getOperand(args[i]), true));

                }
                //Die restlichen Parameter müssen auf den Stack geschreiben werden
                for (int i = 7; i < args.length; i++){
                    addAssembly("MOV " + getLocalVar(maxVar + 1).getIdentifier() + "," + getOperand(args[i]).getIdentifier());
                    ops.add(new Mov(getLocalVar(maxVar + 1), getOperand(args[i]), true));
                }

                //Die Funktion aufrufen
                addAssembly("CALL " +  args[0].getIdentifier());
                ops.add(call.allocate());

                //Die 6 Parameter wieder vom Stack in die Register schreiben
                maxVar = tempMaxVar;
                for (int i = 0; i < 6; i++){
                    addAssembly("MOV " + getParam(i+1).getIdentifier() + "," +getLocalVar(maxVar + 1).getIdentifier());
                    ops.add(new Mov(getParam(i+1), getLocalVar(maxVar+1), true));
                }
                maxVar = tempMaxVar;
            }

        }
        printAssembly();
        System.out.println("haha ab hier");
        ops.forEach(x -> System.out.println(x.getIntelSyntax()));
    }
}
