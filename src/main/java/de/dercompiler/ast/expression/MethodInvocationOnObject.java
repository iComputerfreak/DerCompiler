package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.MethodType;
import de.dercompiler.semantic.type.VoidType;
import de.dercompiler.transformation.TransformationState;
import firm.Entity;
import firm.Firm;
import firm.Mode;
import firm.nodes.Node;

import java.util.Objects;

public final class MethodInvocationOnObject extends UnaryExpression {

    private final Arguments arguments;
    private final String functionName;
    private boolean implicitThis;
    private MethodDefinition methodDefinition;

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

    private Method getMethod(){
        return methodDefinition.getMethod();
    }

    public ClassDeclaration getClassDeclaration(){
        return methodDefinition.getReferenceType().getDecl();
    }

    @Override
    public Node createNode(TransformationState state) {
        //TODO get classname, check if internal
        String classname = null;
        MethodDefinition methodDef = state.globalScope.getMethod(classname, functionName);
        Entity method = state.globalScope.getMemberEntity(classname ,functionName);

        Node object = encapsulated.createNode(state);
        int numArgs = arguments.getLength() + 1;
        Node[] args = new Node[numArgs];
        args[0] = object;
        for (int i = 1; i < numArgs; i++) {
            args[i] = arguments.get(i).createNode(state);
        }

        Node mem = state.construction.getCurrentMem();
        Node res = state.construction.newCall(mem, null, args, null);

        if (methodDef.getType().getReturnType().isCompatibleTo(new VoidType())) {
            return state.construction.newBad(Mode.getANY());
        }
        return res;
    }

    public void setImplicitThis(boolean implicitThis) {
        this.implicitThis = implicitThis;
    }

    public boolean hasImplicitThis() {
        return implicitThis;
    }

    public MethodType getMethodType() {
        return methodDefinition.getType();
    }

    public void setDefinition(MethodDefinition methodDefinition){
        if (!this.getType().isCompatibleTo(methodDefinition.getType().getReturnType())){
            new OutputMessageHandler(MessageOrigin.PASSES).internalError("Type of methodInvocationExpression (%s) should be equal to the return type of the method (%s), but is not! Weird!".formatted(getType(), methodDefinition.getType().getReturnType()));
        }
        this.methodDefinition = methodDefinition;
    }



    @Override
    public void setInternal(boolean internal) {
        super.setInternal(internal);
    }
}
