package de.dercompiler.intermediate.regalloc;

import de.dercompiler.Function;
import de.dercompiler.intermediate.CodeGenerationErrorIds;
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
import de.dercompiler.intermediate.regalloc.calling.CallingConvention;
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

    public TrivialRegisterAllocator(MemoryManager manager, CallingConvention callingConvention) {
        super(manager, callingConvention);
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
    private final Map<Integer, Address> vrMap = new HashMap<>();

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
            if (op instanceof BinaryOperation bo) {
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
                Operand operandAddr = getOperand(operand, uao.getDatatype(), true, true);
                ops.add(uao.allocate(operandAddr));
            } else {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).debugPrint("Could not handle " + op.toString());
            }
            freeRegisterIndex = 0;
        }

        function.setOperations(ops);
    }

    @Override
    public int getVarCount() {
        return varCount;
    }

    private void handleCall(Call call) {
        //args[0] ist LabelOperand
        Operand[] args = call.getArgs();

        // save current parameters
        for (int i = 0; i < paramCount; i++) {
            // if (i < args.length && args[i] instanceof ParameterRegister oldParam && oldParam.getId() == i - 1) continue;
            Push push = new Push(getParamReg(i));
            handleUnaryOperation(push);
            push.setComment(push.getComment() + " - save " + (i == 0 ? "this ptr" : "parameter register #" + i));
        }



        // write call parameters - arg[0] is method label
        for (int i = 1; i < args.length && i <= 6; i++) {
            // if (i < args.length && args[i] instanceof ParameterRegister oldParam && oldParam.getId() == i - 1) continue;
            Operand param = getOperand(args[i], Datatype.QWORD, true, true);
            handleBinaryOperation(new Mov(getParamReg(i - 1), param, true));

        }
        // write even more parameters!
        for (int i = 7; i < args.length; i++) {
            handleUnaryOperation(new Push(getOperand(args[i], Datatype.QWORD, true, true)));
        }


        Call allocate = call.allocate();
        allocate.setComment(call.toString());
        ops.add(allocate);

        // restore parameters
        for (int i = paramCount - 1; i >= 0; i--) {
            //   if (i < args.length && args[i] instanceof ParameterRegister oldParam && oldParam.getId() == i - 1) continue;
            Pop uop = new Pop(getParamReg(i));
            uop.setComment("restore " + (i == 0 ? "this ptr" : ("parameter register #" + i)));
            handleUnaryOperation(uop);
        }

        // write return value to designated stack entry
        if (call.getDatatype() != Datatype.NODATA) {
            Mov mov = new Mov(call.getDefinition(), manager.getReturnValue());
            mov.setMode(call.getMode());
            mov.setComment("store call result");
            handleBinaryOperation(mov);
        }
    }

    private void handleUnaryOperation(UnaryOperation uop) {
        if (uop instanceof Push p) {
            varCount++;
            p.addComment("= %d(%%rbp)".formatted(-8 * (varCount)));
            ops.add(p);
        } else if (uop instanceof Pop p) {
            varCount--;
            if (p.getTarget() == null) {
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

        if (bo instanceof DivModOperation dmo) {
            handleDivMod(dmo);
        } else if (bo instanceof IMul mul) {
            handleMul(mul);
        } else if (bo instanceof Mov || bo instanceof Lea) {
            handleMovLea(bo, operands);
        } else {
            /*
                BINARY OPERATION MODES IN GENERAL: (not idiv or imul)
                op src, target
                where
                         src = const or reg or (addr)
                    and dest = reg or (addr)

                    but not src = (addr) and dest = (addr) at the same time

             */
            opTgt = getOperand(operands[0], bo.getDatatype(), true, true);
            /*
                if next operation writes result back to target of this operation
                   <and> destination of this operation dies after next operation
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
            } else if (bo.needsDefinition()) {
                // load target value into destination location
                VirtualRegister targetVR = (VirtualRegister) bo.getDefinition();
                Address stackLocation = storeInVirtualRegister(targetVR.getId(), opTgt);
                destReg = isTopStack(stackLocation) ? getTopStack() : stackLocation;
            } else {
                if (bo instanceof Cmp cmp && operands[0] instanceof ConstantValue c1 && operands[1] instanceof ConstantValue c2) {
                    /* TODO Cmp instructions with two constants are not allowed. If optimization is active, we can just omit this instruction. */
                    Operation next = function.getOperations().get(bo.getIndex() + 1);
                    if (next instanceof Jmp || !(next instanceof JumpOperation)) {
                        // No cmp necessary, jump is already unconditional / optimized away.
                        return;
                    } else {
                        ConstantValue zero = new ConstantValue(0);
                        storeInRegister(X86Register.R11, new ConstantValue(1), bo.getDatatype());
                        Register one = X86Register.R11;
                        ConstantValue two = new ConstantValue(2);
                        bo = switch (Integer.compare(c1.getValue(), c2.getValue())) {
                            case -1 -> bo.allocate(one, two);
                            case 0 -> bo.allocate(one, one);
                            default -> bo.allocate(one, zero);
                        };
                        bo.addComment("dummy cmp to replace cmp /w two constants");
                        ops.add(bo);
                        return;
                    }
                }
            }


            opSrc = getOperand(operands[1], bo.getDatatype(), true, true);
            // load source register, if necessary
            Operand srcReg = null;
            if (bo instanceof BinArithOperation || bo instanceof ShiftOperation) {
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

    private void handleMovLea(BinaryOperation bo, Operand[] operands) {
        Operand opTgt;
        Operand opSrc;
        Operand dest = operands[0];
        opSrc = getOperand(operands[1], bo.getDatatype(), true, true);
        if (dest instanceof VirtualRegister vrDest && !vrMap.containsKey(vrDest.getId())) {
            Push uop = new Push(opSrc);
            uop.addComment(bo.getComment());
            handleUnaryOperation(uop);
            vrMap.put(vrDest.getId(), getLocalVar(varCount - 1));
            return;
        }

        opTgt = getOperand(dest, bo.getDatatype(), true, true);

        if (opTgt.equals(opSrc)) return;
        // if both ops are relative addresses, load the source value.
        if (opSrc instanceof Address && opTgt instanceof Address) {
            X86Register temp = storeInRegister(opSrc, bo.getDatatype());
            ops.add(bo.allocate(opTgt, temp));
            freeRegister(temp);
        } else {
            ops.add(bo.allocate(opTgt, opSrc));
        }
    }

    private void handleMul(IMul mul) {
        Operand fctTarget = getOperand(mul.getTarget(), mul.getDatatype(), true, false);
        Operand fctSource = getOperand(mul.getSource(), mul.getDatatype(), true, true);
        Operand definition = mul.getDefinition();
        Operand dest;
        if (definition instanceof VirtualRegister vreg && !vrMap.containsKey(vreg.getId())) {
            dest = definition; // gets allocated by handleBinaryOperation
        } else {
            dest = getOperand(definition, mul.getDatatype(), true, true);
        }

        int configuration = evalMul(fctTarget, fctSource, dest);

        loop:
        while (true) {

            switch (configuration) {
                case 5, 21, 30, 31 -> {
                    // 5/21:  imul const, const -> addr/reg
                    // 30:    imul   reg,   mem -> reg
                    // 31:    imul   reg,   reg -> reg
                    handleBinaryOperation(new Mov(dest, fctTarget));
                    ops.add(mul.allocate(dest, fctSource));
                    break loop;
                }
                case 6, 7, 11, 22, 23, 27 -> {
                    // 6/22: imul const, addr -> addr/reg
                    // 7/23: imul const, reg  -> addr/reg
                    // 11:   imul  addr, reg  -> addr
                    // 27:   imul  addr, reg  -> reg
                    // Handle symmetric case instead
                    Operand temp = fctSource;
                    fctSource = fctTarget;
                    fctTarget = temp;
                    configuration = evalMul(fctTarget, fctSource, dest);
                    continue loop;
                }
                case 9, 13 -> {
                    // imul addr/reg, const -> addr
                    X86Register target = allocateRegister();
                    ops.add(mul.allocateIMul3((ConstantValue) fctSource, fctTarget, target));
                    handleBinaryOperation(new Mov(dest, target));
                    freeRegister(target);
                    break loop;
                }
                case 14 -> {
                    // imul reg, addr -> addr
                    X86Register temp = storeInRegister(fctSource, mul.getDatatype());
                    ops.add(mul.allocate(temp, fctTarget));
                    handleBinaryOperation(new Mov(dest, temp));
                    break loop;
                }
                case 10, 15, 26 -> {
                    // 15:    imul  reg,  reg -> addr
                    // 10/26: imul addr, addr -> addr/reg
                    X86Register temp = storeInRegister(fctSource, mul.getDatatype());
                    ops.add(mul.allocate(temp, fctTarget));
                    handleBinaryOperation(new Mov(dest, temp));
                    freeRegister(temp);
                    break loop;
                }
                case 25, 29 -> {
                    // 25/29: imul addr/reg, const -> reg
                    ops.add(mul.allocateIMul3((ConstantValue) fctSource, fctTarget, dest));
                    break loop;
                }

                default -> new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.UNDEFINED_OPERAND_CONFIGURATION,
                        "Could not handle the kind of operands for imul. Better set up more cases! (Case %d -- %s)".formatted(configuration, mul.getAtntSyntax()));
            }
        }


    }

    private int evalMul(Operand factor1, Operand factor2, Operand dest) {
        int configuration = 0;
        configuration += Objects.isNull(dest) || dest instanceof X86Register ? 1 : 0; // else: Address
        configuration <<= 2;
        configuration += factor1 instanceof X86Register ? 3 : factor1 instanceof Address ? 2 : factor1 instanceof ConstantValue ? 1 : 0;
        configuration <<= 2;
        configuration += factor2 instanceof X86Register ? 3 : factor2 instanceof Address ? 2 : factor2 instanceof ConstantValue ? 1 : 0;
        return configuration;
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


    private void handleDivMod(DivModOperation op) {
        Operand[] operands = op.getArgs();

        Operand dividend = operands[0];
        Operand divisor = operands[1];

        if (paramCount >= 3) {
            // save %rdx as it is overwritten by idiv
            ops.add(new Mov(X86Register.R11, X86Register.RDX));
        }

        // load dividend
        storeInRegister(X86Register.RAX, getOperand(dividend, Datatype.QWORD, true, true), Datatype.QWORD);

        // load divisor
        Operand dsrAddr = getOperand(divisor, Datatype.DWORD, true, true);
        if (dsrAddr.equals(X86Register.RDX)) {
            dsrAddr = X86Register.R11;
        } else if (dsrAddr instanceof ConstantValue) {
            dsrAddr = storeInRegister(dsrAddr, Datatype.QWORD);
        }

        // convert to 128 bit
        ops.add(new Cqto());

        // instruction call
        ops.add(new IDiv(dsrAddr));

        // save result
        X86Register result = op instanceof Div ? X86Register.RAX : X86Register.RDX;
        storeInVirtualRegister(((VirtualRegister) op.getDefinition()).getId(), result);

        if (paramCount >= 3) {
            // restore %rdx
            ops.add(new Mov(X86Register.RDX, X86Register.R11));
        }
    }


    private Operand getParamReg(int n) {
        if (n < 6) {
            return parameterRegister[n];
        } else {
            return manager.getArgument(n - 6);
        }
    }

    /**
     * Returns the given operand in a form that is better usable for the machine code.
     *
     * @param operand       IR or x86 operand
     * @param datatype      datatype of the operation, in case any value must be loaded
     * @param allowAddress  if operand is an address and !allowAddress, the address is stored in a register.
     * @param allowTopStack Set this to true if you dont want to invalidate the returned address by pushing something onto the stack.
     * @return the x86 operand
     */
    private Operand getOperand(Operand operand, Datatype datatype, boolean allowAddress, boolean allowTopStack) {
        if (operand == null) {
            return null;
        } else if (operand instanceof ConstantValue constant) {
            return constant;
        } else if (operand instanceof VirtualRegister vr) {
            return getOperand(loadVirtualRegister(vr.getId()), datatype, allowAddress, allowTopStack);
        } else if (operand instanceof ParameterRegister pr) {
            return getOperand(getParamReg(pr.getId()), datatype, allowAddress, true);
        } else if (operand instanceof Address address) {
            if (address.getBase() instanceof IRRegister || address.getBase() instanceof Address) {
                Operand base = getOperand(address.getBase(), Datatype.QWORD, false, false);
                Operand index = getOperand(address.getIndex(), Datatype.DWORD, false, true);
                if (allowAddress && index == null && address.getOffset() == 0) return Address.ofOperand(base);
                address = address.allocate((X86Register) base, (X86Register) index);
            }

            if (allowAddress) {
                return (allowTopStack && isTopStack(address)) ? getTopStack() : address;
            } else {
                X86Register regOperand = storeInRegister(address, datatype);
                return regOperand;
            }
        } else {
            return operand;
        }
    }

    private X86Register allocateRegister() {
        X86Register reg = freeRegister[freeRegisterIndex++];
        return reg;
    }

    private void freeRegister(X86Register reg) {
        // TODO: What if reg is not the "last" register? Chaos ensues!
        if (reg == freeRegister[freeRegisterIndex - 1]) {
            freeRegisterIndex--;
        }
    }

    private X86Register storeInRegister(Operand operand, Datatype datatype) {
        X86Register reg = freeRegister[freeRegisterIndex++];
        Mov mov = new Mov(reg, operand);
        mov.setMode(datatype, datatype == Datatype.BYTE ? Signedness.UNSIGNED : Signedness.SIGNED);
        ops.add(mov);

        return reg;
    }

    private void storeInRegister(X86Register reg, Operand address, Datatype datatype) {
        Mov mov = new Mov(reg, address);
        mov.setMode(datatype, datatype == Datatype.BYTE ? Signedness.UNSIGNED : Signedness.SIGNED);
        ops.add(mov);
    }

    private boolean isTopStack(Address addr) {
        return addr.getBase().equals(X86Register.RSP) && addr.getOffset() == 0 || addr.getBase().equals(X86Register.RBP) && (addr.getOffset() == 8 * -varCount);
    }


    private Address loadVirtualRegister(int id) {
        if (!vrMap.containsKey(id)) {
            handleUnaryOperation(new Push(new ConstantValue(0)));
            vrMap.put(id, getLocalVar(varCount - 1));
        }
        Address addr = vrMap.get(id);
        return addr.getBase().equals(X86Register.RSP) ? addr : new Address((8 * varCount) + addr.getOffset(), X86Register.RSP);
    }

    //TODO Where to use?
    private Address storeInVirtualRegister(int id, Operand value) {
        vrMap.computeIfAbsent(id, id_ -> {
            handleUnaryOperation(new Push(value));
            return getLocalVar(varCount - 1);
        });
        return loadVirtualRegister(id);
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
