package de.dercompiler.intermediate.regalloc.analysis;

import de.dercompiler.intermediate.operand.IRRegister;

import java.util.LinkedList;
import java.util.List;

public class FunctionShard {
    private int start;
    private int end;
    private List<IRRegister> relevant;
    private List<IRRegister> used;


    public FunctionShard(int min, int max) {
        this(min, max, new LinkedList<>());
    }

    public FunctionShard(int min, int max, List<IRRegister> relevant) {
        start = min;
        end = max;
        this.relevant = relevant;
        used = new LinkedList<>();
    }

    public FunctionShard split(int index) {
        if (index <= start && end < index) return null;
        int end = this.end;
        this.end = index;
        List<IRRegister> rel = relevant;
        relevant = new LinkedList<>();
        return new FunctionShard(index + 1, end, rel);
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void addUsage(List<IRRegister> registers) {
        used.addAll(registers);
    }

    public int getNumOperations() {
        return end - start + 1;
    }
}
