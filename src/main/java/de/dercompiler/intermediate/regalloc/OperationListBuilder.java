package de.dercompiler.intermediate.regalloc;

import de.dercompiler.intermediate.operation.NaryOperations.Ret;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import javax.swing.text.html.HTMLDocument;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

public class OperationListBuilder {

    LinkedList<Operation> startOfShard;
    LinkedList<Operation> endOfShard;
    LinkedList<Operation> afterShard;
    LinkedList<Operation> atTime;

    LinkedList<Operation> processed;

    LinkedList<Operation> current;

    boolean finished = false;

    public static <T> void appendAndClear(LinkedList<T> list, LinkedList<T> append_) {
        append(list, append_);
        append_.clear();
    }

    public static <T> void append(LinkedList<T> list, LinkedList<T> append) {
        Iterator<T> it = append.iterator();
        while (it.hasNext()) {
            list.addLast(it.next());
        }
        append.clear();
    }

    public OperationListBuilder() {
        startOfShard = new LinkedList<>();
        endOfShard = new LinkedList<>();
        afterShard = new LinkedList<>();
        atTime = new LinkedList<>();

        processed = new LinkedList<>();

        current = new LinkedList<>();
    }

    public void processAccessOperations(AccessOperations ops) {
        Iterator<Operation> it = ops.operations().iterator();
        LinkedList<Operation> target = switch (ops.timing()) {
            case START_OF_SHARD -> startOfShard;
            case END_OF_SHARD -> endOfShard;
            case AFTER_SHARD -> afterShard;
            case AT_TIME -> atTime;
        };
        appendAndClear(target, ops.operations());
    }

    public void appendOperation(Operation op) {
        appendAndClear(current, atTime);
        current.addLast(op);
    }

    public void finishShard() {
        appendAndClear(current, endOfShard);
        appendAndClear(current, afterShard);

        appendAndClear(processed, current);
    }

    public List<Operation> finalizeFunction(LinkedList<Operation> head, LinkedList<Operation> registerToStack, LinkedList<Operation> stackToRegister) {
        LinkedList<Operation> result = new LinkedList<>();
        append(result, head);
        append(result, registerToStack);
        Iterator<Operation> it = processed.iterator();
        while (it.hasNext()) {
            Operation op = it.next();
            //TODO change to Leave (currently not implemented)
            if (op instanceof Ret) {
                append(result, stackToRegister);
            }
            result.addLast(op);
        }
        return result;
    }

    private void checkNotFinished() {
        if (finished) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("Call to Builder after it was finished");
        }
    }
}
