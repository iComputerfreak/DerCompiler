package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.type.MethodType;
import de.dercompiler.semantic.type.Type;

import java.util.Objects;

public final class MethodInvocationOnObject extends UnaryExpression {

    private final Arguments arguments;
    private final String functionName;
    private boolean implicitThis;
    private MethodType methodType;

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

    public void setImplicitThis(boolean implicitThis) {
        this.implicitThis = implicitThis;
    }

    public boolean hasImplicitThis() {
        return implicitThis;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public void setMethodType(MethodType methodType) {
        if (!this.getType().isCompatibleTo(methodType.getReturnType())) {
            new OutputMessageHandler(MessageOrigin.PASSES).internalError("Type of methodInvocationExpression (%s) should be equal to the return type of the method (%s), but is not! Weird!".formatted(getType(), methodType.getReturnType()));
        }
        this.methodType = methodType;
    }

    @Override
    public void setInternal(boolean internal) {
        super.setInternal(internal);
    }
}
