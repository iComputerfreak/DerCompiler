package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;

import java.util.Objects;

public final class MethodInvocationOnObject extends UnaryExpression {

    private Arguments arguments;
    private String functionName;

    public MethodInvocationOnObject(AbstractExpression encapsulated, String functionName, Arguments arguments) {
        super(encapsulated);
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public boolean syntaxEqual(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof MethodInvocationOnObject mioo) {
            return functionName.equals(mioo.functionName)
                    && arguments.syntaxEqual(mioo.arguments)
                    && syntaxEqualEncapsulated(mioo);
        }
        return false;
    }
}
