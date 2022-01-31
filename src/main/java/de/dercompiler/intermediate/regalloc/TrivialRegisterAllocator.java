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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class TrivialRegisterAllocator extends RegisterAllocator {


    private final OutputMessageHandler outputMessageHandler = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);

    //maxVar gibt an welche die Variable im Stack mit der niedrigsten Adresse ist
    private int varCount = 0;

    private LinkedList<Integer> saveStates = new LinkedList<>();
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

    public TrivialRegisterAllocator(MemoryManager manager) {
        super(manager);
        manager.setRegisterMgmt(this);
    }


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

    /**
     * Maps virtual registers to their location on the stack.
     */
    private Map<Integer, Operand> vrMap = new HashMap<>();

    private Operand getParam(int n) {
        if (n == 0) {
            return manager.getThis();
        } else if (n < 7) {
            return parameterRegister[n - 1];
        } else {
            return manager.getArgument(n - 6);
        }
    }

    private Operand getOperand(Operand operand) {
        if (operand instanceof VirtualRegister vr) {
            return loadVirtualRegister((int) vr.getId());
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

    private Operand loadVirtualRegister(int id) {
        return vrMap.computeIfAbsent(id, id_ -> storeLocalVar());
    }

    private Operand getLocalVar(int n) {
        return manager.getVar(n);
    }

    public Operand popLocalVar() {
        return manager.getVar(--varCount);
    }

    public Operand storeLocalVar() {
        return manager.getVar(varCount++);
    }

    private void saveState() {
        saveStates.push(varCount);
    }

    private void restoreState() {
        varCount = saveStates.pop();
    }

    List<Operation> ops;

    @Override
    public void allocateRegisters(Function function) {
        ops = new LinkedList<>();

        for (Operation op : function.getOperations()) {
            if (op instanceof Div div) {
                handleDiv(div);
            } else if (op instanceof BinaryOperation bo) {
                handleBinaryOperation(bo);
            } else if (op instanceof Ret ret) {
                handleReturn(ret);
            } else if (op instanceof Call call) {
                handleCall(call);
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
            } else {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).debugPrint("Could not handle " + op.toString());
            }
            freeRegisterIndex = 0;
        }
        function.setOperations(ops);
    }

    private void handleCall(Call call) {
        //args[0] ist LabelOperand
        Operand[] args = call.getArgs();

        // Zielstackeintrag jetzt schon reservieren
        if (call.getDatatype() != Datatype.NODATA) {
            getOperand(call.getDefinition());
        }

        //Die 6 ParameterRegister müssen gesichert werden
        for (int i = 0; i < 6; i++) {
            ops.add(new Mov(storeLocalVar(), getParam(i + 1), true));
        }

        saveState();


        //Die Parameter müssen in Regsiter bzw auf den Stack geschrieben werden
        for (int i = 1; i < args.length && i < 7; i++) {
            ops.add(new Mov(getParam(i), getOperand(args[i]), true));

        }
        //Die restlichen Parameter müssen auf den Stack geschreiben werden
        for (int i = 7; i < args.length; i++) {
            ops.add(new Mov(storeLocalVar(), getOperand(args[i]), true));
        }


        //Die Funktion aufrufen
        ops.add(call.allocate());

        if (call.getDatatype() != Datatype.NODATA) {
            ops.add(new Mov(getOperand(call.getDefinition()), manager.getReturnValue(), false));
        }

        restoreState();
        //Die 6 Parameter wieder vom Stack in die Register schreiben
        for (int i = 5; i >= 0; i--) {
            ops.add(new Mov(getParam(i + 1), popLocalVar(), true));
        }

    }

    private void handleReturn(Ret ret) {
        if (ret.getArgs().length != 0) {
            Operand operand = ret.getArgs()[0];
            ops.add(new Mov(manager.getReturnValue(), storeLocalVar(), true));
        }
        ops.add(new Ret());
    }

    private void handleBinaryOperation(BinaryOperation bo) {
        Operand[] operands = bo.getArgs();

        Operand opTgt = getOperand(operands[0]);
        Operand opSrc = getOperand(operands[1]);

        if (bo instanceof Mov || bo instanceof Lea) {
            ops.add(bo.allocate(opTgt, opSrc));
        } else {

            //Operand 1 wird geladen
            ops.add(new Mov(X86Register.R10, opTgt, true));

            //Operand 2 wird geladen
            ops.add(new Mov(X86Register.R11, opSrc, true));

            //Operation durchführen
            ops.add(bo.allocate(X86Register.R10, X86Register.R11));


            //Ergebnis wieder auf den Stack schreiben
            if (bo.needsDefinition()) {
                ops.add(new Mov(getOperand(bo.getDefinition()), X86Register.R10, true));
            }
        }
    }

    private void handleDiv(Div div) {
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
    }
}
