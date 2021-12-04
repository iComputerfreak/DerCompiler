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

    private Stack<Block> trueBlockStack;
    private Stack<Block> falseBlockStack;


    private Stack<Block> blockStack;
    private Stack<Statement> statementStack;

    private boolean hasReturn = false;

    public TransformationState(GlobalScope scope) {
        this.globalScope = scope;
        graph = null;
        construction = null;
        trueBlockStack = new Stack<>();
        falseBlockStack = new Stack<>();

        blockStack = new Stack<>();
        statementStack = new Stack<>();
    }

    public boolean isCondition() {
        boolean wellFormed = Objects.isNull(trueBlock()) == Objects.isNull(falseBlock());
        if (!wellFormed) {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("We have a miss-formed TransformationState!");
        }

        return !Objects.isNull(trueBlock());
    }

    public void swapTrueFalseBlock() {
        Stack<Block> tmp = trueBlockStack;
        trueBlockStack = falseBlockStack;
        falseBlockStack = tmp;
    }

    public void markReturn() {
        hasReturn = true;
    }

    public boolean noReturnYet() {
        //TODO add !
        return !hasReturn;
    }

    public void clear() {
        assert(trueBlock() == null);
        assert(falseBlock() == null);
        assert(statementStack.size() == 0);
        assert(blockStack.size() == 0);
        graph = null;
        construction = null;
        hasReturn = false;
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

    public void pushBranches(Block trueBlock, Block falseBlock) {
        trueBlockStack.push(trueBlock);
        falseBlockStack.push(falseBlock);
    }

    public void popBranches() {
        assert(trueBlockStack.size() == falseBlockStack.size());
        trueBlockStack.pop();
        falseBlockStack.pop();

    }

    public Block exchangeTrueBlock(Block block) {
        Block top = trueBlockStack.pop();
        trueBlockStack.push(block);
        return top;
    }

    public Block exchangeFalseBlock(Block block) {
        Block top = falseBlockStack.pop();
        falseBlockStack.push(block);
        return top;
    }

    public Block trueBlock() {
        if (trueBlockStack.empty()) return null;
        return trueBlockStack.peek();
    }

    public Block falseBlock() {
        if (falseBlockStack.empty()) return null;
        return falseBlockStack.peek();
    }

}
