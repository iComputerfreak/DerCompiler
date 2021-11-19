package de.dercompiler.pass.passes;

import de.dercompiler.ast.Field;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.ast.statement.IfStatement;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.ReturnStatement;
import de.dercompiler.ast.statement.WhileStatement;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.type.*;

import java.lang.reflect.Array;
import java.util.List;
import java.util.stream.Collectors;

public class TypeAnalysisPass implements MethodPass, ExpressionPass, ASTExpressionVisitor  {

    private final OutputMessageHandler logger;
    private Program.ProgramNameSpace globalNameSpace;


    public TypeAnalysisPass() {
        this.logger = new OutputMessageHandler(MessageOrigin.PASSES);
    }

    @Override
    public void doInitialization(Program program) {
        this.globalNameSpace = program.getNameSpace();
    }

    @Override
    public void doFinalization(Program program) {

    }
   
    @Override
    public boolean runOnExpression(Expression expression) {
        expression.accept(this);
        return false;
    }

    @Override
    public boolean shouldRunOnExpression(Expression expression) {
        return ExpressionPass.super.shouldRunOnExpression(expression);
    }

    @Override
    public boolean runOnMethod(Method method) {
        return false;
    }

    @Override
    public boolean shouldRunOnMethod(Method method) {
        return MethodPass.super.shouldRunOnMethod(method);
    }


    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(VariableAnalysisCheckPass.class);
        usage.setDependency(DependencyType.RUN_DIRECT_AFTER);
        return usage;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return usage;
    }

    private static long id = 0;
    private PassManager manager = null;

    @Override
    public void registerPassManager(PassManager manager) {
        this.manager = manager;
    }

    @Override
    public PassManager getPassManager() {
        return manager;
    }

    @Override
    public long registerID(long rid) {
        if (id != 0) return id;
        id = rid;
        return id;
    }

    @Override
    public long getID() {
        return id;
    }

    @Override
    public AnalysisDirection getAnalysisDirection() {
        // TODO: Change direction?
        return AnalysisDirection.TOP_DOWN;
    }

    private void failTypeCheck(Expression expr, String description) {
        // TODO implement
    }

    private void assertTypeEqual(Expression lhs, Expression rhs, String description) {
        if (!lhs.getType().isCompatibleTo(rhs.getType())) {
            failTypeCheck(rhs, description);
        }
    }

    private void assertTypeEquals(Expression expr, Type type, String description) {
        if (!expr.getType().isCompatibleTo(type)) {

        }
    }

    private CustomType assertCustomBasicType(Expression expr, String description) {
        if (expr.getType() instanceof CustomType type) {
            return type;
        }

        failTypeCheck(expr, description);
        return null;
    }

    private void assertNotVoid(Expression expr, String description) {
        if (expr.getType() instanceof VoidType) {
            failTypeCheck(expr, description);
        }
    }

    private void assertNotArray(Expression expr, String description) {
        if (expr.getType() instanceof ArrayType array) {
            failTypeCheck(expr, description);
        }
    }


    @Override
    public void visitArrayAccess(ArrayAccess arrayAccess) {
        SourcePosition pos = arrayAccess.getSourcePosition();
        Expression arrayExpr = arrayAccess.getEncapsulated();
        Expression indexExpr = arrayAccess.getIndex();

        arrayExpr.accept(this);
        indexExpr.accept(this);


        if (!(arrayExpr.getType() instanceof ArrayType)) {
            failTypeCheck(arrayExpr, "array expression");
        }

        ArrayType arrayType = (ArrayType) arrayExpr.getType();

        if (!(arrayAccess.getType() instanceof IntegerType index)) {
            failTypeCheck(indexExpr, "index expression");
        }

        // Range check not required as per the specs, so we are all good now

        arrayAccess.setType(arrayType.getElementType());
    }

    @Override
    public void visitBooleanValue(BooleanValue booleanValue) {
        booleanValue.setType(new BooleanType());
    }

    @Override
    public void visitBinaryExpression(BinaryExpression binaryExpression) {
        Expression lhs = binaryExpression.getLhs();
        lhs.accept(this);
        Expression rhs = binaryExpression.getRhs();
        rhs.accept(this);
        switch (binaryExpression.getOperator()) {
            case ASSIGN:
                assertTypeEqual(lhs, rhs, "assignment");
                binaryExpression.setType(rhs.getType());
                break;

            case AND_LAZY, OR_LAZY:
                assertTypeEquals(lhs, new BooleanType(), "operand of comparison operation");
                assertTypeEquals(rhs, new BooleanType(),  "operand of comparison operation");
                // Position is synthesized from left operator
                binaryExpression.setType(lhs.getType());
                break;

            case EQUAL, NOT_EQUAL:
                assertTypeEqual(lhs, rhs, "operands of equality operation");
                break;

            case LESS_THAN, LESS_THAN_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL:
                assertTypeEquals(lhs, new IntegerType(),  "operand of comparison operation");
                assertTypeEquals(rhs, new IntegerType(),  "operand of comparison operation");
                // Position is synthesized from left operator
                binaryExpression.setType(new BooleanType());
                break;

            case PLUS, MINUS, STAR, SLASH, PERCENT_SIGN:
                assertTypeEquals(lhs, new IntegerType(),  "operand of arithmetic operation");
                assertTypeEquals(rhs, new IntegerType(),  "operand of arithmetic operation");
                // Position is synthesized from left operator
                binaryExpression.setType(lhs.getType());
                break;

            case BAR, OR_SHORT, XOR, XOR_SHORT, AMPERSAND, AND_SHORT:
                assertTypeEqual(lhs, rhs, "operands of logical operation");
                Type type = lhs.getType();
                if (!(type instanceof IntegerType || type instanceof BooleanType)) {
                    // fail
                    failTypeCheck(lhs, "operand of logical operation");
                }
                binaryExpression.setType(lhs.getType());
                break;

            default:
                failTypeCheck(binaryExpression, "unknown binary operator");
        }
    }

    @Override
    public void visitErrorExpression(ErrorExpression errorExpression) {

    }

    @Override
    public void visitFieldAccess(FieldAccess fieldAccess) {
        // TODO: Where to find Field Type in SymbolTable?
        fieldAccess.getEncapsulated().accept(this);
        if (fieldAccess.getType() instanceof CustomType type) {
            Field field = globalNameSpace.getField(type.getIdentifier(), fieldAccess.getFieldName());
            fieldAccess.setType(field.getRefType());
        }

        //TODO: fieldAccess.getType() has no fields!
    }

    @Override
    public void visitIntegerValue(IntegerValue integerValue) {
        integerValue.setType(new IntegerType());
    }

    @Override
    public void visitLogicalNotExpression(LogicalNotExpression logicalNotExpression) {
        Expression expr = logicalNotExpression.getEncapsulated();
        SourcePosition pos = logicalNotExpression.getSourcePosition();

        expr.accept(this);
        assertTypeEquals(expr, new BooleanType(), "boolean operation");

        logicalNotExpression.setType(new BooleanType());
    }

    @Override
    public void visitMethodInvocation(MethodInvocationOnObject methodInvocation) {
        Expression refObj = methodInvocation.getEncapsulated();
        refObj.accept(this);

        CustomType type = assertCustomBasicType(refObj, "reference object of method invocation");

        Method method = globalNameSpace.getMethod(type.getIdentifier(), methodInvocation.getFunctionName());
        Type returnType = method.getType();

        methodInvocation.setType(returnType);

        List<Type> parameterTypes = method.getParameters().stream().map(Parameter::getRefType).collect(Collectors.toList());
        Arguments arguments = methodInvocation.getArguments();
        arguments.setExpectedTypes(parameterTypes);
        this.visitArguments(arguments);
    }



    @Override
    public void visitNegativeExpression(NegativeExpression negativeExpression) {
        Expression expr = negativeExpression.getEncapsulated();
        SourcePosition pos = negativeExpression.getSourcePosition();

        expr.accept(this);
        assertTypeEquals(expr, new IntegerType(), "integer operation");
        negativeExpression.setType(new IntegerType());

    }

    @Override
    public void visitNewArrayExpression(NewArrayExpression newArrayExpression) {
        Expression dimExpr = newArrayExpression.getSize();
        SourcePosition pos = newArrayExpression.getSourcePosition();

        dimExpr.accept(this);
        assertTypeEquals(dimExpr, new IntegerType(), "dimension expression");

        BasicType basicType = newArrayExpression.getBasicType();
        if (basicType instanceof de.dercompiler.ast.type.VoidType) {
            failTypeCheck(newArrayExpression, "array type");
        }
        // TODO: get semantic type of element type
        newArrayExpression.setType(new ArrayType());
    }

    @Override
    public void visitNewObjectExpression(NewObjectExpression newObjectExpression) {
        SourcePosition pos = newObjectExpression.getSourcePosition();

        de.dercompiler.ast.type.CustomType objectType = newObjectExpression.getObjectType();

        // TODO where to get semantic type out of ClassDeclaration globalNameSpace.getClass(objectType.getIdentifier())
        // TODO newObjectExpression.setType(....);
    }

    @Override
    public void visitNullValue(NullValue nullValue) {
    nullValue.setType(new NullType());
    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression primaryExpression) {
        // abstract type
    }

    @Override
    public void visitUninitializedValue(UninitializedValue uninitializedValue) {
        // do nothing
    }

    @Override
    public void visitVariable(Variable variable) {
        ASTDefinition declaration = variable.getDefinition();
        variable.setType(declaration.getRefType());
    }

    @Override
    public void visitVoidExpression(VoidExpression voidExpression) {
        SourcePosition pos = voidExpression.getSourcePosition();
        voidExpression.setType(new VoidType());
    }

    public void visitArguments(Arguments arguments) {
        List<Type> expectedTypes = arguments.getExpectedTypes();
        for (int i = 0; i < arguments.getLength(); i++) {
            arguments.get(i).accept(this);
            assertTypeEquals(arguments.get(i), expectedTypes.get(i), "argument for expected type " + expectedTypes.get(i).toString());
        }
    }

    public void visitIfStatement(IfStatement ifStatement) {
        ifStatement.getCondition().accept(this);
        assertTypeEquals(ifStatement.getCondition(), new BooleanType(),  "if condition");
    }

    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement) {
        Expression expr = localVariableDeclarationStatement.getExpression();
        expr.accept(this);
        // TODO: how to get _current_ variable definition from SymbolTable?
        // assertTypeEquals(expr, type, "assignment to new %s variable".formatted(type);
    }

    public void visitReturnStatement(ReturnStatement returnStatement) {
        Expression expr = returnStatement.getExpression();
        expr.accept(this);

        assertTypeEquals(expr, returnStatement.getSurroundingMethod().getRefType(), "return value for %s method");
    }

    public void visitWhileStatement(WhileStatement whileStatement) {
        whileStatement.getCondition().accept(this);
        assertTypeEquals(whileStatement.getCondition(), new BooleanType(), "while condition");
    }
}
