package de.dercompiler.ast.expression;

import java.util.LinkedList;
import java.util.List;

public class Arguments {

    private LinkedList<AbstractExpression> arguments;

    public Arguments() { arguments = new LinkedList<>(); }

    public Arguments(List<AbstractExpression> arguments) {
        this();
        for (AbstractExpression ae : arguments) {
            this.arguments.addLast(ae);
        }
    }

    public void addArgument(AbstractExpression expression) {
        this.arguments.addLast(expression);
    }
}
