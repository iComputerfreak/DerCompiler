package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.memory.MemoryManager;
import de.dercompiler.intermediate.operand.*;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.BinaryOperations.*;
import de.dercompiler.intermediate.operation.ConstantOperations.Cqto;
import de.dercompiler.intermediate.operation.NaryOperations.Call;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperation;
import de.dercompiler.intermediate.operation.UnaryOperations.*;
import de.dercompiler.intermediate.selection.Datatype;
import de.dercompiler.intermediate.selection.Signedness;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.*;


public class TrivialRegisterAllocator extends RegisterAllocator {


    private final OutputMessageHandler outputMessageHandler = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);

    /**
     * Number of entries on the stack.
     */
    private int varCount = 0;

    private LinkedList<Integer> saveStates = new LinkedList<>();

    private static final X86Register[] parameterRegister = new X86Register[]{
            X86Register.RDI, X86Register.RSI, X86Register.RDX,
            X86Register.RCX, X86Register.R8, X86Register.R9
    };

    /**
     * The number of parameters of the current Function.
     */
    private int paramCount;

    private boolean skipNext;

    /**
     * The number of temporarily used registers for a single instruction
     */
    private int tempRegister = 0;

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
    private final Map<Long, Address> vrMap = new HashMap<>();

    List<Operation> ops;
    Function function;

    @Override
    public void allocateRegisters(Function function) {
        this.function = function;
        varCount = 0;
        ops = new LinkedList<>();

        // set dyn_link and rbp
        ops.add(new Push(X86Register.RBP));
        ops.add(new Mov(X86Register.RBP, X86Register.RSP));

        paramCount = function.getParamCount();
        for (Operation op : function.getOperations()) {
            if (skipNext) {
                skipNext = false;
                continue;
            }
            if (op instanceof IDiv div) {
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
                Operand operandAddr = getOperand(operand, uao.getDatatype(), true);
                ops.add(uao.allocate(operandAddr));
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
            getOperand(call.getDefinition(), call.getDatatype(), true);
        }

        // save current parameters
        for (int i = 0; i < paramCount; i++) {
            Push push = new Push(getParamReg(i));
            handleUnaryOperation(push);
            push.setComment(push.getComment() + " - save " + (i == 0 ? "this ptr" : "parameter register #" + i));

        }

        // write call parameters - arg[0] is method label
        for (int i = 1; i < args.length && i <= 7; i++) {
            handleBinaryOperation(new Mov(getParamReg(i - 1), getOperand(args[i], Datatype.QWORD, true), true));

        }
        // write even more parameters!
        for (int i = 8; i < args.length; i++) {
            handleUnaryOperation(new Push(getOperand(args[i], Datatype.QWORD, true)));
        }


        Call allocate = call.allocate();
        allocate.setComment(call.toString());
        ops.add(allocate);

        // write return value to designated stack entry
        if (call.getDatatype() != Datatype.NODATA) {
            Mov mov = new Mov(getOperand(call.getDefinition(), call.getDatatype(), true), manager.getReturnValue());
            mov.setComment("store call result");
            handleBinaryOperation(mov);
        }

        // restore parameters
        for (int i = paramCount - 1; i >= 0; i--) {
            Pop uop = new Pop(getParamReg(i));
            uop.setComment("restore " + (i == 0 ? "this ptr" : ("parameter register #" + i)));
            handleUnaryOperation(uop);
        }

    }

    private void handleUnaryOperation(UnaryOperation uop) {
        if (uop instanceof Push p) {
            varCount++;
            p.setComment("= %d(%%rsp)".formatted(-8 * (varCount)));
            ops.add(p);
        } else if (uop instanceof Pop p) {
            varCount--;
            if (p.getArg() == null) {
                // free stack entry w/o saving the value
                Add add = new Add(X86Register.RSP, new ConstantValue(8));
                add.setMode(Datatype.QWORD, Signedness.UNSIGNED);
                add.setComment(p.getComment());
                ops.add(add);
            } else {
                // write value into register
                ops.add(p);
            }
        }
    }

    private void handleReturn(Ret ret) {
        if (ret.getArgs().length != 0) {
            Operand operand = ret.getArgs()[0];
            handleBinaryOperation(new Mov(manager.getReturnValue(), operand));
        }

        // reset stack pointer
        if (varCount > 0) {
            Mov mov = new Mov(X86Register.RSP, X86Register.RBP);
            mov.setComment("Reset stack - " + varCount + " entries");
            ops.add(mov);
        }

        // reset base pointer
        ops.add(new Pop(X86Register.RBP));

        ops.add(new Ret());
    }

    private void handleBinaryOperation(BinaryOperation bo) {
        Operand[] operands = bo.getArgs();

        Operand opSrc;
        Operand opTgt; //= getOperand(operands[0], true);

        if (bo instanceof Mov || bo instanceof Lea) {
            Operand dest = operands[0];
            opSrc = getOperand(operands[1], bo.getDatatype(), true);
            if (dest instanceof VirtualRegister vrDest && !vrMap.containsKey(vrDest.getId())) {
                handleUnaryOperation(new Push(opSrc));
                vrMap.put(vrDest.getId(), getLocalVar(varCount - 1));
                return;
            }

            opTgt = getOperand(dest, bo.getDatatype(), true);

            if (opTgt.equals(opSrc)) return;
            // if both ops are relative addresses, load the source value.
            if (opSrc instanceof Address && opTgt instanceof Address) {
                Operand temp = freeRegister[freeRegisterIndex++];
                Mov mov = new Mov(temp, opSrc);
                mov.setMode(bo.getMode());
                ops.add(mov);
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
            opTgt = getOperand(operands[0], bo.getDatatype(), true);
            /*
                if next operation writes result back to target of this operation
                   and destination of this operation dies after next operation
                then destination is throwaway register
                -> we can operate on target directly!
            */
            boolean throwaway = tryThrowaway(bo.getTarget(), bo.getDefinition(), bo.getIndex());

            Operand destReg = null;
            if (throwaway) {
                // overwrite value directly; skipping the write-back operation
                skipNext = true;
                if (opTgt instanceof X86Register) {
                    destReg = opTgt;
                } else if (!operands[0].equals(operands[1])) {
                    // load address of target register
                    ops.add(new Lea(X86Register.R10, opTgt));
                    destReg = Address.ofOperand(X86Register.R10);
                }
            }
            if (destReg == null) {

                if (bo.needsDefinition() && !throwaway) {
                    // load target value into destination location
                    VirtualRegister targetVR = (VirtualRegister) bo.getDefinition();
                    Address stackLocation = storeInVirtualRegister(targetVR.getId(), getOperand(operands[0], bo.getDatatype(), true));
                    destReg = isTopStack(stackLocation) ? getTopStack() : stackLocation;
                } else {
                    // copy value of target register
                    Mov loadDest = new Mov(X86Register.R10, opTgt);
                    loadDest.setMode(bo.getMode());
                    loadDest.setComment("safe binop destination register -- cannot overwrite");
                    ops.add(loadDest);
                    destReg = X86Register.R10;
                }
            }

            opSrc = getOperand(operands[1], bo.getDatatype(), true);
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
                Mov loadSrc = new Mov(X86Register.R11, opSrc);
                loadSrc.setMode(bo.getMode());
                loadSrc.setComment("load binop source register");
                ops.add(loadSrc);
                srcReg = X86Register.R11;
            }

            // add operation code
            ops.add(bo.allocate(destReg, srcReg));
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

    private void handleDiv(IDiv div) {
        Operand[] operands = div.getArgs();
        Operand dividend = operands[0];
        Operand divisor = operands[1];

        if (paramCount > 4) {
            // save %rdx as it is overwritten by idiv
            ops.add(new Mov(X86Register.RDX, X86Register.R11));
        }

        // load dividend
        ops.add(new Mov(X86Register.RAX, getOperand(dividend, Datatype.DWORD, true)));

        // convert to 64 bit
        ops.add(new Movslq(X86Register.RAX, X86Register.RAX));

        // convert to 128 bit
        ops.add(new Cqto());

        // load divisor
        ops.add(new Mov(X86Register.R10, getOperand(divisor, Datatype.DWORD, true)));

        // convert to 64 bit
        ops.add(new Movslq(X86Register.R10, X86Register.R10));

        // instruction call
        ops.add(div.allocate(X86Register.R10, null));

        // save result
        ops.add(new Mov(getOperand(div.getDefinition(), Datatype.QWORD, true), X86Register.RAX));

        if (paramCount > 4) {
            // restore %rdx
            ops.add(new Mov(X86Register.R11, X86Register.RDX));
        }
    }

    private Operand getParamReg(int n) {
        if (n < 7) {
            return parameterRegister[n];
        } else {
            return manager.getArgument(n - 7);
        }
    }

    /**
     * Returns the given operand in a form that is better usable for the machine code.
     *
     * @param operand      IR or x86 operand
     * @param datatype     datatype of the operation, in case any value must be loaded
     * @param allowAddress if operand is an address and !allowAddress, the address is stored in a register.
     * @return the x86 operand
     */
    private Operand getOperand(Operand operand, Datatype datatype, boolean allowAddress) {
        if (operand == null) {
            return null;
        } else if (operand instanceof VirtualRegister vr) {
            return getOperand(loadVirtualRegister(vr.getId()), datatype, allowAddress);
        } else if (operand instanceof ParameterRegister pr) {
            return getOperand(getParamReg(pr.getId()), datatype, allowAddress);
        } else if (operand instanceof Address address) {
            if (address.getBase() instanceof IRRegister reg) {
                Operand base = getOperand(address.getBase(), Datatype.QWORD, false);
                Operand index = getOperand(address.getIndex(), Datatype.DWORD, false);
                if (allowAddress && index == null && address.getOffset() == 0) return Address.ofOperand(base);
                address = address.allocate((X86Register) base, (X86Register) index);
            }

            if (allowAddress) {
                return (isTopStack(address)) ? getTopStack() : address;
            } else {
                X86Register regOperand = storeInRegister(address, datatype);
                return regOperand;
            }
        } else {
            return operand;
        }
    }

    private X86Register storeInRegister(Address address, Datatype datatype) {
        X86Register reg = freeRegister[freeRegisterIndex++];
        Mov mov = new Mov(reg, address);
        mov.setMode(datatype, datatype == Datatype.BYTE ? Signedness.UNSIGNED : Signedness.SIGNED);
        ops.add(mov);


        return reg;
    }

    private boolean isTopStack(Address addr) {
        return addr.getBase().equals(X86Register.RBP) && (addr.getOffset() == 8 * -varCount);
    }


    private Operand loadVirtualRegister(long id) {
        return vrMap.computeIfAbsent(id, id_ -> {
            handleUnaryOperation(new Push(new ConstantValue(0)));
            return getLocalVar(varCount - 1);
        });
    }

    //TODO Where to use?
    private Address storeInVirtualRegister(long id, Operand value) {
        return vrMap.computeIfAbsent(id, id_ -> {
            handleUnaryOperation(new Push(value));
            return getLocalVar(varCount - 1);
        });
    }

    private Operand getTopStack() {
        return Address.ofOperand(X86Register.RSP);
    }

    private Address getLocalVar(int n) {
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
