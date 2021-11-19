package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.type.Type;
import de.dercompiler.lexer.SourcePosition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Arguments extends ASTNode {

    private LinkedList<Expression> arguments;

    private List<Type> expectedTypes;

    public Arguments(SourcePosition position) {
        super(position);
        this.arguments = new LinkedList<>();
    }

    public Arguments(SourcePosition position, List<Expression> arguments) {
        this(position);
        for (Expression ae : arguments) {
            this.arguments.addLast(ae);
        }
    }

    public void addArgument(Expression expression) {
        this.arguments.addLast(expression);
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof Arguments a) {
            if (a.arguments.size() != arguments.size()) return false;
            boolean result = true;
            Iterator<Expression> itThis = arguments.iterator();
            Iterator<Expression> itO = a.arguments.iterator();
            while (itThis.hasNext()) {
                Expression expThis = itThis.next();
                Expression expO = itO.next();
                result &= expThis.syntaxEquals(expO);
            }
            return result;
        }
        return false;
    }

    public void setExpectedTypes(List<Type> expectedTypes) {
        this.expectedTypes = expectedTypes;
    }

    public List<Type> getExpectedTypes() {
        return expectedTypes;
    }

    @Override
    public void accept(ASTNodeVisitor astNodeVisitor) {
        astNodeVisitor.visitArguments(this);
    }

    public int getLength() {
        return arguments.size();
    }

    public Expression get(int index) {
        return arguments.get(index);
    }
}
