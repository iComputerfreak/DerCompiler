package de.dercompiler.ast.expression;

import de.dercompiler.ast.ASTNode;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.visitor.ASTExpressionVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.*;
import de.dercompiler.transformation.LibraryMethods;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import de.dercompiler.transformation.node.ArrayNode;
import de.dercompiler.transformation.node.ObjectNode;
import de.dercompiler.transformation.node.RValueNode;
import de.dercompiler.transformation.node.ReferenceNode;
import firm.Entity;
import firm.Mode;
import firm.nodes.Call;
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

    public boolean isLibraryCall() {
        return encapsulated.getType() instanceof InternalClass;
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

    public ClassType getClassType(){
        return methodDefinition.getReferenceType();
    }

    @Override
    public ReferenceNode createNode(TransformationState state) {
        state.pushExpectValue();

        ClassType classType = getClassType();
        String classname = classType.getIdentifier();
        MethodDefinition methodDef = state.globalScope.getMethod(classname, functionName);
        Entity methodEntity;

        int argsCount = arguments.getLength();
        int baseIdx = 0;
        if (!isLibraryCall()) {
            baseIdx = 1;    // 'this' object is 0th argument
            methodEntity = state.globalScope.getMemberEntity(classname , methodDef.getMethod().getMangledIdentifier());
        } else {
            methodEntity = LibraryMethods.forName(methodDef.getIdentifier());
        }
        //baseIdx == 0, if and only if this method is a library call
        Node[] argNodes = new Node[baseIdx + argsCount];
        if (!isLibraryCall()) {
            ReferenceNode objRef = encapsulated.createNode(state);
            argNodes[0] = objRef.getObjectCallBase(state).getBase();
        }
        for (int i = 0; i < argsCount; i++) {
            argNodes[baseIdx + i] = arguments.get(i).createNode(state).genLoad(state);
        }

        state.popExpect();

        Node mem = state.construction.getCurrentMem();
        Node call = state.construction.newCall(mem, state.construction.newAddress(methodEntity), argNodes, methodEntity.getType());

        state.construction.setCurrentMem(state.construction.newProj(call, Mode.getM(), Call.pnM));
        Node tuple = state.construction.newProj(call, Mode.getT(), Call.pnTResult);

        if (methodDef.getType().getReturnType().isCompatibleTo(new VoidType())) {
            return new RValueNode(state.construction.newBad(Mode.getANY()), getType());
        }
        //we don't have to return 2 so always 0
        firm.Type resType = methodDef.getType().getReturnType().getFirmType();
        Node res = state.construction.newProj(tuple, resType.getMode(),  0);

        de.dercompiler.semantic.type.Type ret = methodDef.getType().getReturnType();
        if (ret instanceof ArrayType at) {
            return new ArrayNode(res, at.getElementType(), at.getDimension());
        } else if (ret instanceof ClassType ct) {
            return new ObjectNode(res, ct);
        } else if (ret instanceof BooleanType) {
            if (!state.expectValue()) {
                TransformationHelper.booleanValueToConditionalJmp(state, res);
                return null;
            }
        }
        //assume everything else is a basic-type
        return new RValueNode(res, getType());
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
