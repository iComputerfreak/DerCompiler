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

import java.util.*;


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

    /**
     * The number of parameters of the current Function.
     */
    private int paramCount;
    private boolean skipNext;

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

    List<Operation> ops;
    Function function;

    @Override
    public void allocateRegisters(Function function) {
        this.function = function;
        ops = new LinkedList<>();
        paramCount = function.getParamCount();
        for (Operation op : function.getOperations()) {
            if (skipNext) {
                skipNext = false;
                continue;
            }
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
                ops.add(new Mov(X86Register.R10, getOperand(operand)));

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

        // allocate stack entry for result
        if (call.getDatatype() != Datatype.NODATA) {
            getOperand(call.getDefinition());
        }

        // save current parameters
        for (int i = 0; i < paramCount; i++) {
            handleBinaryOperation(new Mov(storeLocalVar(), getParamReg(i)));
        }

        saveState();

        // write call parameters
        for (int i = 1; i < args.length && i < 7; i++) {
            handleBinaryOperation(new Mov(getParamReg(i), getOperand(args[i]), true));

        }
        // write even more parameters!
        for (int i = 7; i < args.length; i++) {
            handleBinaryOperation(new Mov(storeLocalVar(), getOperand(args[i])));
        }

        Call allocate = call.allocate();
        allocate.setComment(call.toString());
        ops.add(allocate);

        // write return value to designated stack entry
        if (call.getDatatype() != Datatype.NODATA) {
            Mov mov = new Mov(getOperand(call.getDefinition()), manager.getReturnValue());
            mov.setComment("Save result to designated location");
            handleBinaryOperation(mov);
        }

        // delete any local variables of called method
        restoreState();

        // restore parameters
        for (int i = paramCount - 1; i >= 0; i--) {
            handleBinaryOperation(new Mov(getParamReg(i), popLocalVar()));
        }

    }

    private void handleReturn(Ret ret) {
        if (ret.getArgs().length != 0) {
            Operand operand = ret.getArgs()[0];
            handleBinaryOperation(new Mov(storeLocalVar(), operand));
        }
        ops.add(new Ret());
    }

    private void handleBinaryOperation(BinaryOperation bo) {
        Operand[] operands = bo.getArgs();

        Operand opTgt = getOperand(operands[0]);
        Operand opSrc = getOperand(operands[1]);

        if (bo instanceof Mov || bo instanceof Lea) {
            if (opTgt.equals(opSrc)) return;
            if (opSrc instanceof Address && opTgt instanceof Address) {
                Operand temp = freeRegister[freeRegisterIndex++];
                ops.add(new Lea(temp, opSrc));
                ops.add(bo.allocate(opTgt, temp));
                freeRegisterIndex--;
            } else {
                ops.add(bo.allocate(opTgt, opSrc));
            }

        } else {
            /*
                BINARY OPERATION MODES IN GENERAL: (not idiv or imul)
                op src, target
                where
                         src = const or reg or (addr)
                    and dest = reg or (addr)

                    but not src = (addr) and dest = (addr) at the same time

             */

            /*
                if next operation writes result back to target of this operation
                   and destination of this operation dies after next operation
                then destination is throwaway register
                -> we can operate on target directly!
            */
            boolean throwaway = tryThrowaway(bo.getTarget(), bo.getDefinition(), bo.getIndex());

            Operand destReg = null;
            if (throwaway) {
                skipNext = true;
                if (opTgt instanceof X86Register) {
                    destReg = opTgt;
                } else if (!opSrc.equals(opTgt)) {
                    // load address of target register
                    ops.add(new Lea(X86Register.R10, opTgt));
                    destReg = Address.ofOperand(X86Register.R10);
                }
            }
            if (destReg == null) {
                // copy value of target register
                ops.add(new Mov(X86Register.R10, opTgt));
                destReg = X86Register.R10;
            }

            // load source register, if necessary
            Operand srcReg = null;
            if (bo instanceof BinArithOperation) {
                if (opSrc instanceof ConstantValue) {
                    srcReg = opSrc;
                } else if (destReg instanceof Register && opSrc.equals(opTgt)) {
                    srcReg = destReg;
                }
            }

            if (srcReg == null) {
                ops.add(new Mov(X86Register.R11, opSrc));
                srcReg = X86Register.R11;
            }

            // add operation code
            ops.add(bo.allocate(destReg, srcReg));

            // push result onto stack
            if (bo.needsDefinition() && !throwaway) {
                // if it were not R10, it would have been directly stored at the target location
                ops.add(new Mov(getOperand(bo.getDefinition()), X86Register.R10));
            }
        }
    }

    private boolean tryThrowaway(Operand target, Operand definition, int index) {
        Operation writeBack = function.getOperations().get(index + 1);
        if (writeBack instanceof Mov mov
                && mov.getSource().equals(definition)
                && mov.getTarget().equals(target)) {
            return function.getOperations().stream()
                    .skip(index + 2)
                    .noneMatch(op -> Arrays.asList(op.getArgs()).contains(definition));
        }
        return false;
    }

    private void handleDiv(Div div) {
        Operand[] operands = div.getArgs();
        Operand dividend = operands[0];
        Operand divisor = operands[1];

        //Parameterregister RDX in R11 sichern, weil 128 bit Dividend in RAX:RDX stehen muss
        ops.add(new Mov(X86Register.RDX, X86Register.R11));

        //Dividend hohlen
        ops.add(new Mov(X86Register.RAX, getOperand(dividend)));

        //auf 64 bit Breite
        ops.add(new Movslq(X86Register.RAX, X86Register.RAX));

        //auf 128 bit Breite
        ops.add(new Cltq(X86Register.RAX, true));

        //Divisor hohlen
        ops.add(new Mov(X86Register.R10, getOperand(divisor)));

        //auf 64 bit Breite
        ops.add(new Movslq(X86Register.R10, X86Register.R10));

        //Division ausführen
        ops.add(div.allocate(X86Register.R10, null));

        //Ergebnis zurückschreiben
        ops.add(new Mov(getOperand(div.getDefinition()), X86Register.RAX));

        //RDI wiederherstellen
        ops.add(new Mov(X86Register.R11, X86Register.RDX));
    }

    private Operand getParamReg(int n) {
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
            return getParamReg(pr.getId());
        } else if (operand instanceof Address address) {
            Operand base = getOperand(address.getBase());
            X86Register x86RegisterBase;
            if (base instanceof X86Register baseReg) {
                x86RegisterBase = baseReg;
            } else {
                x86RegisterBase = freeRegister[freeRegisterIndex++];
                ops.add(new Mov(x86RegisterBase, base));
            }
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
}
