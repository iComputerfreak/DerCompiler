package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.lexer.SourcePosition;

import java.util.Objects;

public final class MethodInvocationOnObject extends UnaryExpression {

    private Arguments arguments;
    private String functionName;

    public MethodInvocationOnObject(SourcePosition position, Expression encapsulated, String functionName, Arguments arguments) {
        super(position, encapsulated);
        this.functionName = functionName;
        this.arguments = arguments;
    }

    @Override
    public boolean syntaxEquals(ASTNode other) {
        if (Objects.isNull(other)) return false;
        if (other instanceof MethodInvocationOnObject mioo) {
            return functionName.equals(mioo.functionName)
                    && arguments.syntaxEquals(mioo.arguments)
                    && syntaxEqualEncapsulated(mioo);
        }
        return false;
    }

    public Arguments getArguments() {
        return arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public Expression getReferenceObject() {
        return this.encapsulated;
    }

    @Override
    public void accept(ASTExpressionVisitor astExpressionVisitor) {
        astExpressionVisitor.visitMethodInvocation(this);
    }
}
