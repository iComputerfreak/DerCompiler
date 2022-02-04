package de.dercompiler.intermediate.regalloc.analysis;

import de.dercompiler.Function;
import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.regalloc.RegAllocUtil;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

public class FunctionSplitView  {

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
            if (shards.get(i).getStart() <= idx && idx <= shards.get(i).getEnd()) {
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
            if (op.getIndex() > shard.getEnd()) {
                int end = shard.getEnd();
                shard = it.next();
                assert (end + 1 == shard.getStart());
            }
            shard.addUsage(RegAllocUtil.collectIRRegisters(op.getArgs()));
            if (op.hasDefinition()) {
                shard.addUsage(RegAllocUtil.collectIRRegisters(op.getDefinition()));
            }
        }
    }

    public FunctionShard getShard(int idx) {
        if (idx < 0 || idx >= shards.size()) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Shard is non existens ony allowed 0 <= index < " + shards.size() + "!" );
        }
        return shards.get(idx);
    }

    public void print() {
        OutputMessageHandler out = new OutputMessageHandler(MessageOrigin.CODE_GENERATION);
        out.printInfo("Num shards: " + shards.size());
        int k = 0;
        List<Operation> ops = func.getOperations();
        out.printInfo(" --- StartShard ---");
        for (FunctionShard shard : shards) {
            for (int i = shard.getStart(); i <= shard.getEnd(); i++) {
                Operation op = ops.get(i);
                out.printInfo(op.getIndex() + " " + op.getAtntSyntax());
            }
            if (k < shards.size() - 1) {
                out.printInfo(" --- ShardNum " + k++ + " --- ");
            }
        }
        out.printInfo(" --- EndShard ---");
    }

    public Function getFunction() {
        return func;
    }
}
