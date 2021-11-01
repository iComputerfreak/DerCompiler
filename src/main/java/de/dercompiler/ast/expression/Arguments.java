package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Arguments implements ASTNode {

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

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof Arguments a) {
            if (a.arguments.size() != arguments.size()) return false;
            boolean result = true;
            Iterator<AbstractExpression> itThis = arguments.iterator();
            Iterator<AbstractExpression> itO = a.arguments.iterator();
            while (itThis.hasNext()) {
                AbstractExpression expThis = itThis.next();
                AbstractExpression expO = itO.next();
                result &= expThis.syntaxEqual(expO);
            }
            return result;
        }
        return false;
    }
}
