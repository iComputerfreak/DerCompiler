package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.type.*;

import java.util.List;
import java.util.stream.Collectors;

public class TypeAnalysisPass implements StatementPass, ExpressionPass, ASTExpressionVisitor {

    private final OutputMessageHandler logger;
    private GlobalScope globalScope;


    public TypeAnalysisPass() {
        this.logger = new OutputMessageHandler(MessageOrigin.PASSES);
    }

    @Override
    public void doInitialization(Program program) {
        TypeFactory.getInstance().initialize(program);
        this.globalScope = program.getGlobalScope();
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
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(VariableAnalysisCheckPass.class);
        usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
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
        return AnalysisDirection.BOTTOM_UP;
    }

    private void failTypeCheck(Expression expr, String description) {
        logger.printErrorAndExit(PassErrorIds.TYPE_MISMATCH, "Illegal type " + expr.getType() + " for " + description);
    }

    private void assertTypeEqual(Expression lhs, Expression rhs, String description) {
        if (!lhs.getType().isCompatibleTo(rhs.getType())) {
            failTypeCheck(rhs, description);
        }
    }

    private void assertTypeEquals(Expression expr, Type type, String description) {
        if (!expr.getType().isCompatibleTo(type)) {
            failTypeCheck(expr, description);
        }
    }

    private ClassType assertCustomBasicType(Expression expr, String description) {
        if (expr.getType() instanceof ClassType type) {
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
                assertTypeEquals(rhs, new BooleanType(), "operand of comparison operation");
                // Position is synthesized from left operator
                binaryExpression.setType(lhs.getType());
                break;

            case EQUAL, NOT_EQUAL:
                assertTypeEqual(lhs, rhs, "operands of equality operation");
                binaryExpression.setType(new BooleanType());
                break;

            case LESS_THAN, LESS_THAN_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL:
                assertTypeEquals(lhs, new IntegerType(), "operand of comparison operation");
                assertTypeEquals(rhs, new IntegerType(), "operand of comparison operation");
                // Position is synthesized from left operator
                binaryExpression.setType(new BooleanType());
                break;

            case PLUS, MINUS, STAR, SLASH, PERCENT_SIGN:
                assertTypeEquals(lhs, new IntegerType(), "operand of arithmetic operation");
                assertTypeEquals(rhs, new IntegerType(), "operand of arithmetic operation");
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

        Expression refObj = fieldAccess.getEncapsulated();
        refObj.accept(this);
        if (refObj.getType() instanceof ClassType type) {
            Field field = globalScope.getField(type.getIdentifier(), fieldAccess.getFieldName());
            fieldAccess.setType(field.getRefType());
        } else {
            failTypeCheck(fieldAccess, "field access");
        }
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

        ClassType type = assertCustomBasicType(refObj, "reference object of method invocation");

        Method method = globalScope.getMethod(type.getIdentifier(), methodInvocation.getFunctionName());
        MethodType methodType = method.getReferenceType();

        methodInvocation.setType(methodType.getReturnType());

        List<Type> parameterTypes = method.getParameters().stream().map(Parameter::getRefType).collect(Collectors.toList());
        Arguments arguments = methodInvocation.getArguments();
        arguments.setExpectedTypes(methodType.getParameterTypes());
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

        newArrayExpression.setType(TypeFactory.getInstance().create(new de.dercompiler.ast.type.Type(null, basicType, newArrayExpression.getDimension())));
    }

    @Override
    public void visitNewObjectExpression(NewObjectExpression newObjectExpression) {
        CustomType objectType = newObjectExpression.getObjectType();
        ClassType classType = globalScope.getClass(objectType.getIdentifier());
        newObjectExpression.setType(classType);
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
    public void visitThisValue(ThisValue thisValue) {
        ClassDeclaration classDecl = getPassManager().getCurrentClass();
        ClassType classType = globalScope.getClass(classDecl.getIdentifier());
        thisValue.setType(classType);
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

        ASTNode expr;
        switch (Integer.signum(arguments.getLength() - expectedTypes.size())) {
            case 1:
                expr = arguments.get(expectedTypes.size());
                logger.printErrorAndExit(PassErrorIds.ARGUMENTS_MISMATCH, "Too many arguments");
                break;
            case -1:
                // Maybe for later: SourcePosition of Error
                int index = arguments.getLength() - 1;
                expr = index >= 0 ? arguments.get(index) : arguments;
                logger.printErrorAndExit(PassErrorIds.ARGUMENTS_MISMATCH, "Too few arguments");
                break;
        }

        for (int i = 0; i < arguments.getLength(); i++) {
            arguments.get(i).accept(this);
            assertTypeEquals(arguments.get(i), expectedTypes.get(i), "expected %s argument".formatted(expectedTypes.get(i).toString()));
        }
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        if (statement instanceof IfStatement ifStatement) visitIfStatement(ifStatement);
        else if (statement instanceof LocalVariableDeclarationStatement decl)
            visitLocalVariableDeclarationStatement(decl);
        else if (statement instanceof ReturnStatement returnStatement) visitReturnStatement(returnStatement);
        else if (statement instanceof WhileStatement whileStatement) visitWhileStatement(whileStatement);
        return false;
    }

    public void visitIfStatement(IfStatement ifStatement) {
        ifStatement.getCondition().accept(this);
        assertTypeEquals(ifStatement.getCondition(), new BooleanType(), "if condition");
    }

    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement decl) {
        Expression expr = decl.getExpression();
        expr.accept(this);

        Type expectedType = decl.getRefType();
        assertTypeEquals(expr, expectedType, "assignment to new %s variable".formatted(expectedType));
    }

    public void visitReturnStatement(ReturnStatement returnStatement) {
        Expression expr = returnStatement.getExpression();
        Type returnType = getPassManager().getCurrentMethod().getReferenceType().getReturnType();

        if (expr instanceof UninitializedValue uninitialized) {
            uninitialized.setType(new VoidType());
            if (!returnType.isCompatibleTo(new VoidType())) {
                failTypeCheck(uninitialized, "return value for %s method".formatted(returnType));
            }

        } else {
            expr.accept(this);
            assertTypeEquals(expr, returnType, "return value for %s method".formatted(returnType));
        }
    }

    public void visitWhileStatement(WhileStatement whileStatement) {
        whileStatement.getCondition().accept(this);
        assertTypeEquals(whileStatement.getCondition(), new BooleanType(), "while condition");
    }

}
