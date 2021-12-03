package de.dercompiler.transformation;

import de.dercompiler.ast.statement.Statement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.GlobalScope;
import firm.Construction;
import firm.Graph;
import firm.nodes.Block;
import firm.nodes.Node;

import java.util.Objects;
import java.util.Stack;

public class TransformationState {

    public Construction construction;
    public Graph graph;
    public final GlobalScope globalScope;
    public Node lhs;
    public Node rhs;
    public Node res;

    public Block trueBlock;
    public Block falseBlock;

    private Stack<Block> blockStack;
    private Stack<Statement> statementStack;

    private boolean hasReturn = false;

    public TransformationState(GlobalScope scope) {
        this.globalScope = scope;
        graph = null;
        construction = null;
        trueBlock = null;
        falseBlock = null;

        blockStack = new Stack<>();
        statementStack = new Stack<>();
    }

    public boolean isCondition() {
        boolean wellFormed = Objects.isNull(trueBlock) == Objects.isNull(falseBlock);
        if (!wellFormed) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("We have a miss-formed TransformationState!");
        }

        return !Objects.isNull(trueBlock);
    }

    public void swapTrueFalseBlock() {
        Block tmp = trueBlock;
        trueBlock = falseBlock;
        falseBlock = tmp;
    }

    public void markReturn() {
        hasReturn = true;
    }

    public boolean noReturnYet() {
        //TODO add !
        return !hasReturn;
    }

    public void clear() {
        graph = null;
        construction = null;
        assert(trueBlock == null);
        assert(falseBlock == null);
        hasReturn = false;
        blockStack.clear();
    }

    public void pullBlock() {
        //skip block because we need to work on it
        if (blockStack.size() != 0 && blockStack.peek() != construction.getCurrentBlock()) {
            construction.setCurrentBlock(blockStack.pop());
        }
    }

    public void pushBlock(Block block) {
        blockStack.push(block);
    }

    public int stackSize() {
        return blockStack.size();
    }

    public void markStatementToPullBlock(Statement statement) {
        statementStack.push(statement);
    }

    public boolean removeStatementIfMarked(Statement statement) {
        if (!(getNumMarkedStatements() > 0 && statement == statementStack.peek())) return false;
        statementStack.pop();
        return true;
    }

    public int getNumMarkedStatements() {
        return statementStack.size();
    }

}
