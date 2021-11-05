package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.lexer.SourcePosition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Arguments extends ASTNode {

    private LinkedList<AbstractExpression> arguments;

    public Arguments(SourcePosition position) {
        super(position);
        this.arguments = new LinkedList<>();
    }

    public Arguments(SourcePosition position, List<AbstractExpression> arguments) {
        this(position);
        for (AbstractExpression ae : arguments) {
            this.arguments.addLast(ae);
        }
    }

    public void addArgument(AbstractExpression expression) {
        this.arguments.addLast(expression);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof Arguments a) {
            if (a.arguments.size() != arguments.size()) return false;
            boolean result = true;
            Iterator<AbstractExpression> itThis = arguments.iterator();
            Iterator<AbstractExpression> itO = a.arguments.iterator();
            while (itThis.hasNext()) {
                AbstractExpression expThis = itThis.next();
                AbstractExpression expO = itO.next();
                result &= expThis.syntaxEquals(expO);
            }
            return result;
        }
        return false;
    }

    public int getLength() {
        return arguments.size();
    }

    public AbstractExpression get(int index) {
        return arguments.get(index);
    }
}
