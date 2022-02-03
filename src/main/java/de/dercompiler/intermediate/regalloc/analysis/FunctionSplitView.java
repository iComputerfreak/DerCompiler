package de.dercompiler.intermediate.regalloc.analysis;

import de.dercompiler.Function;
import de.dercompiler.intermediate.operand.IRRegister;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.RegAllocUtil;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

public class FunctionSplitView  {

    public static class FunctionShard {
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
            if (index < start && end <= index) return null;
            int end = this.end;
            this.end = index;
            List<IRRegister> rel = relevant;
            relevant = new LinkedList<>();
            return new FunctionShard(index, end, rel);
        }
    }

    private LinkedList<FunctionShard> shards;
    private Function func;

    public FunctionSplitView(Function func) {
        int start = func.getOperations().getFirst().getIndex();
        int end = func.getOperations().getLast().getIndex();
        shards = new LinkedList<>();
        if (func.getOperations().getLast() instanceof Ret ret) {
            shards.add(new FunctionShard(start, end, RegAllocUtil.collectIRRegisters(ret.getArgs())));
        } else {
            shards.add(new FunctionShard(start, end));
        }
        this.func = func;
    }

    public void split(int idx) {
        //reverse order, since we search top down
        for (int i = shards.size() - 1; i >= 0 ; i--) {
            if (shards.get(i).start < idx) {
                FunctionShard tmp = shards.get(i).split(idx);
                if (tmp == null) return; //this should never happen
                shards.add(i + 1, tmp);
                break;
            }
        }
    }

    public int getNumShards() {
        return shards.size();
    }

    public void calculateInformation() {
        Iterator<FunctionShard> it = shards.iterator();
        FunctionShard shard = it.next();
        for (Operation op : func.getOperations()) {
            if (op.getIndex() > shard.end) {
                int end = shard.end;
                shard = it.next();
                assert (end == shard.start);
            }
            shard.used.addAll(RegAllocUtil.collectIRRegisters(op.getArgs()));
            if (op.hasDefinition()) {
                shard.used.addAll(RegAllocUtil.collectIRRegisters(op.getDefinition()));
            }
        }
    }
}
