package de.dercompiler.transformation;

import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.semantic.GlobalScope;
import firm.Construction;
import firm.Graph;
import firm.nodes.Block;
import firm.nodes.Node;

import java.util.Objects;

public class TransformationState {

    public Construction construction;
    public Graph graph;
    public final GlobalScope globalScope;
    public Node lhs;
    public Node rhs;
    public Node res;

    public Block trueB;
    public Block falseB;

    public TransformationState(GlobalScope scope) {
        this.globalScope = scope;
        graph = null;
        construction = null;
        trueB = null;
        falseB = null;
    }

    public boolean isCondition() {
        if (Objects.isNull(trueB)) {
            if (Objects.isNull(falseB)) {
                return false;
            } else {
                new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("We have a miss-formed TransformationState!");
                return true; //we never return
            }
        } else {
            if (Objects.isNull(falseB)) {
                new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("We have a miss-formed TransformationState!");
                return false; //we never return
            } else {
                return true;
            }
        }
    }

    public void swapTrueFalseBlock() {
        Block tmp = trueB;
        trueB = falseB;
        falseB = tmp;
    }

}
