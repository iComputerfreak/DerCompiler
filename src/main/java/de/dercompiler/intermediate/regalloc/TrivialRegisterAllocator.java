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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.dercompiler.intermediate.operand.X86Register.*;


public class TrivialRegisterAllocator extends RegisterAllocator {


    private final OutputMessageHandler outputMessageHandler = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);

    /**
     * Number of entries on the stack.
     */
    private int varCount = 0;

    private LinkedList<Integer> saveStates = new LinkedList<>();

    private  X86Register[] parameterRegister;

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
        manager.setCallingConvention(callingConvention);
        parameterRegister = callingConvention.getArgumentRegisters();
    }

    /**
     * Use these for temp address
     */
    private List<X86Register> freeScratchRegisters;

    /**
     * Maps virtual registers to their location on the stack.
     */
    private final Map<Integer, Operand> vrMap = new HashMap<>();

    List<Operation> ops;
    Function function;

    @Override
    public void allocateRegisters(Function function) {
        this.function = function;
        varCount = 0;
        manager.reset();
        ops = new LinkedList<>();

        resetScratchRegisters();


        varCount = 0;
        LinkedList<Operation> setupOps = new LinkedList<>();
        ops = new LinkedList<>();

        manager.setOutput(setupOps::add);

        // set dyn_link and rbp
        setupOps.push(new Push(RBP));
        setupOps.add(new Mov(RBP, RSP));

        manager.setOutput(ops::add);

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
                Operand operandAddr = getOperand(operand, uao.getDatatype(), true);
                ops.add(uao.allocate(operandAddr));
            } else {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).debugPrint("Could not handle  " + op.toString());
            }
            resetScratchRegisters();
        }

        // resetting bp is handled in handleRet

        Lea setupStack = new Lea(RSP, manager.getStackEnd());
        setupStack.setComment("set up stack frame -- %d entries".formatted(manager.getStackSize()));
        setupOps.add(setupStack);

        ops.addAll(0, setupOps);
        function.setOperations(ops);
    }

    private void resetScratchRegisters() {
        freeScratchRegisters = IntStream.range(0, callingConvention.getNumberOfScratchRegisters())
                .mapToObj(callingConvention::getScratchRegister)
                .collect(Collectors.toList());
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
            manager.pushValue(manager.getArgument(i), " - save " + (i == 0 && !function.isStatic() ? "this ptr" : "parameter register #" + i));
        }


        // write call parameters - arg[0] is method label
        for (int i = 1; i < args.length && i <= 6; i++) {
            // if (i < args.length && args[i] instanceof ParameterRegister oldParam && oldParam.getId() == i - 1) continue;
            Operand param = getOperand(args[i], Datatype.QWORD, true);
            handleBinaryOperation(new Mov(getParamReg(i - 1), param, true));

        }
        // write even more parameters!
        for (int i = 7; i < args.length; i++) {
            manager.pushValue(getOperand(args[i], Datatype.QWORD, true));
        }


        Call allocate = call.allocate();
        allocate.setComment(call.toString());
        ops.add(allocate);

        // restore parameters
        for (int i = paramCount - 1; i >= 0; i--) {
            //   if (i < args.length && args[i] instanceof ParameterRegister oldParam && oldParam.getId() == i - 1) continue;
            String comment = "restore " + (i == 0 ? "this ptr" : ("parameter register #" + i));
            storeInRegister(manager.getArgument(i), manager.getStackEnd().offsetPtr(8 * i), Datatype.QWORD, comment);
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
                Add add = new Add(RSP, new ConstantValue(8));
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
            Operand operand = getOperand(ret.getArgs()[0], ret.getDatatype(), true);
            storeInRegister(callingConvention.getReturnRegister(), operand, ret.getDatatype());
        }

        // reset stack pointer
        if (manager.getStackSize() > 0) {
            Mov mov = new Mov(RSP, RBP);
            mov.setComment("Reset stack - " + manager.getStackSize() + " entries");
            ops.add(mov);
        }

        // reset base pointer
        ops.add(new Pop(RBP));
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
            opTgt = getOperand(operands[0], bo.getDatatype(), true);
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
                    ops.add(new Lea(R10, opTgt));
                    destReg = Address.ofOperand(R10);
                }
            } else if (bo.needsDefinition()) {
                // load target value into destination location
                VirtualRegister targetVR = (VirtualRegister) bo.getDefinition();
                Operand stackLocation = storeInVirtualRegister(targetVR.getId(), opTgt);
                destReg = stackLocation;
            } else {
                if (bo instanceof Cmp cmp && operands[0] instanceof ConstantValue c1 && operands[1] instanceof ConstantValue c2) {
                    /* TODO Cmp instructions with two constants are not allowed. If optimization is active, we can just omit this instruction. */
                    Operation next = function.getOperations().get(bo.getIndex() + 1);
                    if (next instanceof Jmp || !(next instanceof JumpOperation)) {
                        // No cmp necessary, jump is already unconditional / optimized away.
                        return;
                    } else {
                        ConstantValue zero = new ConstantValue(0);
                        storeInRegister(R11, new ConstantValue(1), bo.getDatatype());
                        Register one = R11;
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


            opSrc = getOperand(operands[1], bo.getDatatype(), true);
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
                Mov loadSrc = new Mov(R11, opSrc);
                loadSrc.setMode(bo.getMode());
                loadSrc.setComment("load binop source register");
                ops.add(loadSrc);
                srcReg = R11;
            }

            if (bo instanceof Cmp cmp && destReg == null) {
                destReg = opTgt;
            }

            // add operation code
            ops.add(bo.allocate(destReg, srcReg));
        }
    }

    private void handleMovLea(BinaryOperation bo, Operand[] operands) {
        Operand opTgt;
        Operand opSrc;
        Operand dest = operands[0];
        opSrc = getOperand(operands[1], bo.getDatatype(), true);
        if (dest instanceof VirtualRegister vrDest) {
            storeInVirtualRegister(vrDest.getId(), getOperand(opSrc, bo.getDatatype(), true), bo.getComment());
            return;
        }

        opTgt = getOperand(dest, bo.getDatatype(), true);

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
        Operand fctTarget = getOperand(mul.getTarget(), mul.getDatatype(), true);
        Operand fctSource = getOperand(mul.getSource(), mul.getDatatype(), true);
        Operand definition = mul.getDefinition();
        Operand dest;
        if (definition instanceof VirtualRegister vreg && !vrMap.containsKey(vreg.getId())) {
            dest = definition; // gets allocated by handleBinaryOperation
        } else {
            dest = getOperand(definition, mul.getDatatype(), true);
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
            ops.add(new Mov(R11, RDX));
        }

        // load dividend
        storeInRegister(RAX, getOperand(dividend, Datatype.QWORD, true), Datatype.QWORD);

        // load divisor
        Operand dsrAddr = getOperand(divisor, Datatype.DWORD, true);
        if (dsrAddr.equals(RDX)) {
            dsrAddr = R11;
        } else if (dsrAddr instanceof ConstantValue) {
            dsrAddr = storeInRegister(dsrAddr, Datatype.QWORD);
        }

        // convert to 128 bit
        ops.add(new Cqto());

        // instruction call
        ops.add(new IDiv(dsrAddr));

        // save result
        X86Register result = op instanceof Div ? RAX : RDX;
        storeInVirtualRegister(((VirtualRegister) op.getDefinition()).getId(), result);

        if (paramCount >= 3) {
            // restore %rdx
            ops.add(new Mov(RDX, R11));
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
     * @param operand      IR or x86 operand
     * @param datatype     datatype of the operation, in case any value must be loaded
     * @param allowAddress if operand is an address and !allowAddress, the address is stored in a register.
     * @return the x86 operand
     */
    private Operand getOperand(Operand operand, Datatype datatype, boolean allowAddress) {
        if (operand == null) {
            return null;
        } else if (operand instanceof ConstantValue constant) {
            return constant;
        } else if (operand instanceof VirtualRegister vr) {
            return getOperand(loadVirtualRegister(vr.getId()), datatype, allowAddress);
        } else if (operand instanceof ParameterRegister pr) {
            return getOperand(getParamReg(pr.getId()), datatype, allowAddress);
        } else if (operand instanceof Address address) {
            if (address.getBase() instanceof IRRegister || address.getBase() instanceof Address) {
                Operand base = getOperand(address.getBase(), Datatype.QWORD, false);
                Operand index = getOperand(address.getIndex(), Datatype.DWORD, false);
                if (allowAddress && index == null && address.getOffset() == 0) return Address.ofOperand(base);
                address = address.allocate((X86Register) base, (X86Register) index);
            }

            if (allowAddress) {
                return address;
            } else {
                X86Register regOperand = storeInRegister(address, datatype);
                return regOperand;
            }
        } else {
            return operand;
        }
    }

    private X86Register allocateRegister() {
        X86Register reg = freeScratchRegisters.remove(0);
        return reg;
    }

    private void freeRegister(X86Register reg) {
        // TODO: What if reg is not the "last" register? Chaos ensues!
        if (!freeScratchRegisters.contains(reg)) {
            freeScratchRegisters.add(reg);
        }
    }

    private X86Register storeInRegister(Operand operand, Datatype datatype) {
        X86Register reg = allocateRegister();
        Mov mov = new Mov(reg, operand);
        mov.setMode(datatype, datatype == Datatype.BYTE ? Signedness.UNSIGNED : Signedness.SIGNED);
        ops.add(mov);

        return reg;
    }

    private void storeInRegister(X86Register dest, Operand source, Datatype datatype) {
        storeInRegister(dest, source, datatype, null);
    }

    private void storeInRegister(X86Register dest, Operand source, Datatype datatype, String comment) {
        Mov mov = new Mov(dest, source);
        mov.setMode(datatype, datatype == Datatype.BYTE ? Signedness.UNSIGNED : Signedness.SIGNED);
        mov.setComment(comment);
        ops.add(mov);

    }

    private Operand loadVirtualRegister(int id) {
        if (!vrMap.containsKey(id)) {
            return storeInVirtualRegister(id, null); // increment stack size, no instructions
        }
        return vrMap.get(id);
    }

    private Operand storeInVirtualRegister(int id, Operand value) {
        return storeInVirtualRegister(id, value, null);
    }

    private Operand storeInVirtualRegister(int id, Operand value, String comment) {
        if (value instanceof Address) {
            if (Objects.equals(vrMap.get(id), value)) {
                return loadVirtualRegister(id);
            }
            X86Register temp = callingConvention.getScratchRegister(0);
            Mov push = new Mov(temp, value);
            ops.add(push);
            value = temp;
        }

        if (!vrMap.containsKey(id)) {
            vrMap.put(id, manager.pushValue(value, comment));
        } else {
            Mov mov = new Mov(vrMap.get(id), value);
            mov.setComment(comment);
            ops.add(mov);
        }
        return loadVirtualRegister(id);
    }

}
