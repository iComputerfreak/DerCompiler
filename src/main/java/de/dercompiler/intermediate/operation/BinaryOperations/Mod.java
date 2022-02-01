package de.dercompiler.intermediate.operation.BinaryOperations;

import de.dercompiler.intermediate.operand.Operand;
import de.dercompiler.intermediate.operand.VirtualRegister;
import de.dercompiler.intermediate.operation.BinaryOperation;
import de.dercompiler.intermediate.operation.OperationType;

public class Mod extends BinArithOperation {

    public Mod(Operand target, Operand source) {
        super(OperationType.MOD, target, source, true);
    }

    @Override
    public BinaryOperation allocate(Operand target, Operand source){
        Mod mod = new Mod(target, source);
        mod.setMode(getMode());
        return mod;
    }
}