package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperations.*;
import de.dercompiler.intermediate.operation.NaryOperations.Call;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.Cltq;
import de.dercompiler.intermediate.operation.UnaryOperations.JumpOperation;
import de.dercompiler.intermediate.operation.UnaryOperations.LabelOperation;
import de.dercompiler.intermediate.operation.UnaryOperations.UnaryArithmeticOperation;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.LinkedList;
import java.util.List;


public class TrivialRegisterAllocator extends RegisterAllocator {

    public TrivialRegisterAllocator(MemoryManager manager) {
        super(manager);
    }

    private final OutputMessageHandler outputMessageHandler = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);

    //maxVar gibt an welche die Variable im Stack mit der niedrigsten Adresse ist
    private int maxVar = -1;
    /*
    parameter 1 bis 6 stehen in den parameter Registern und der Rest steht auf dem Stack
     */
    private static final X86Register[] parameterRegister = new X86Register[]{
            X86Register.RDI,
            X86Register.RSI,
            X86Register.RDX,
            X86Register.RCX,
            X86Register.R8,
            X86Register.R9
    };
    /*
    Diese Register werden gebraucht, wenn ein ein binärer Operator aus zwei Adressen besteht die je Base und Index haben
     */
    private static final X86Register[] freeRegister = new X86Register[]{
            X86Register.R12,
            X86Register.R13,
            X86Register.R14,
            X86Register.R15
    };
    private int freeRegisterIndex = 0;

    private Operand getParam(int n) {
        if (n < 7) {
            return parameterRegister[n - 1];
        } else {
            return manager.getArgument(n - 6);
        }
    }

    private Operand getOperand(Operand operand) {
        if (operand instanceof VirtualRegister vr) {
            return getLocalVar((int) vr.getId());
        } else if (operand instanceof ParameterRegister pr) {
            return getParam(pr.getId());
        } else if (operand instanceof Address address) {
            X86Register x86RegisterBase = freeRegister[freeRegisterIndex++];
            ops.add(new Mov(x86RegisterBase, getOperand(address.getBase()), true));
            X86Register x86RegisterIndex = null;
            Register indexRegister = address.getIndex();
            if (indexRegister != null) {
                x86RegisterIndex = freeRegister[freeRegisterIndex++];
                ops.add(new Mov(x86RegisterIndex, getOperand(indexRegister), true));
            }
            return address.allocate(x86RegisterBase, x86RegisterIndex);
        } else {
            return operand;
        }
    }

    private Operand getLocalVar(int n) {
        maxVar = Integer.max(maxVar, n);
        return manager.getVar(n);
    }

    List<Operation> ops;

    @Override
    public void allocateRegisters(Function function) {
        ops = new LinkedList<>();

        for (Operation op : function.getOperations()) {
            if (op instanceof Div div) {
                Operand[] operands = div.getArgs();
                Operand dividend = operands[0];
                Operand divisor = operands[1];

                //Parameterregister RDX in R11 sichern, weil 128 bit Dividend in RAX:RDX stehen muss
                ops.add(new Mov(X86Register.RDX, X86Register.R11, true));

                //Dividend hohlen
                ops.add(new Mov(X86Register.RAX, getOperand(dividend), true));

                //auf 64 bit Breite
                ops.add(new Movslq(X86Register.RAX, X86Register.RAX, true));

                //auf 128 bit Breite
                ops.add(new Cltq(X86Register.RAX, true));

                //Divisor hohlen
                ops.add(new Mov(X86Register.R10, getOperand(divisor), true));

                //auf 64 bit Breite
                ops.add(new Movslq(X86Register.R10, X86Register.R10, true));

                //Division ausführen
                ops.add(div.allocate(X86Register.R10, null));

                //Ergebnis zurückschreiben
                ops.add(new Mov(getOperand(div.getDefinition()), X86Register.RAX, true));

                //RDI wiederherstellen
                ops.add(new Mov(X86Register.R11, X86Register.RDX, true));

            } else if (op instanceof BinaryOperation bo) {
                Operand[] operands = bo.getArgs();

                //Operand 1 wird geladen
                ops.add(new Mov(X86Register.R10, getOperand(operands[0]), true));

                //Operand 2 wird geladen
                ops.add(new Mov(X86Register.R11, getOperand(operands[1]), true));


                //Operation durchführen
                ops.add(bo.allocate(X86Register.R10, X86Register.R11));


                //Ergebnis wieder auf den Stack schreiben
                if (bo.needsDefinition()) {
                    ops.add(new Mov(getOperand(bo.getDefinition()), X86Register.R10, true));
                }


            } else if (op instanceof Ret ret) {

                if (ret.getArgs().length != 0) {
                    Operand operand = ret.getArgs()[0];
                    ops.add(new Mov(manager.getReturnValue(), getOperand(operand), true));
                }
                ops.add(new Ret());


            } else if (op instanceof Call call) {
                //args[0] ist LabelOperand
                Operand[] args = call.getArgs();
                int tempMaxVar = maxVar;
                //Die 6 ParameterRegister müssen gesichert werden
                for (int i = 0; i < 6; i++) {
                    ops.add(new Mov(getLocalVar(maxVar + 1), getParam(i + 1), true));
                }
                //Die Parameter müssen in Regsiter bzw auf den Stack geschrieben werden
                for (int i = 1; i < args.length && i < 7; i++) {
                    ops.add(new Mov(getParam(i), getOperand(args[i]), true));

                }
                //Die restlichen Parameter müssen auf den Stack geschreiben werden
                for (int i = 7; i < args.length; i++) {
                    ops.add(new Mov(getLocalVar(maxVar + 1), getOperand(args[i]), true));
                }

                //Die Funktion aufrufen
                ops.add(call.allocate());

                //Die 6 Parameter wieder vom Stack in die Register schreiben
                maxVar = tempMaxVar;
                for (int i = 0; i < 6; i++) {
                    ops.add(new Mov(getParam(i + 1), getLocalVar(maxVar + 1), true));
                }
                maxVar = tempMaxVar;
            } else if (op instanceof LabelOperation lbl) {
                ops.add(lbl);
            } else if (op instanceof JumpOperation jo) {
                ops.add(jo);
            } else if (op instanceof UnaryArithmeticOperation uao) {
                Operand operand = uao.getArgs()[0];

                //Operand in Register hohlen
                ops.add(new Mov(X86Register.R10, getOperand(operand), true));

                //Operation ausführen
                ops.add(uao.allocate(X86Register.R10));

                //Ergebnis zurückschreiben
                ops.add(new Mov(getOperand(operand), X86Register.R10, true));
            }
            freeRegisterIndex = 0;
        }
        ops.forEach(x -> System.out.println(x.getAtntSyntax(Datatype.WORD)));
    }
}
