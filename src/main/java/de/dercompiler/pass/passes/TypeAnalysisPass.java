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
        failTypeCheck(expr, description, "Illegal type " + expr.getType());
    }


    private void failTypeCheck(ASTNode expr, String locationDescription, String errorDescription) {
        System.err.println(getPassManager().getLexer().printSourceText(expr.getSourcePosition()));
        logger.printErrorAndExit(PassErrorIds.TYPE_MISMATCH, errorDescription + " for " + locationDescription);
        getPassManager().quitOnError();
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

        if (!(indexExpr.getType() instanceof IntegerType index)) {
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
                if (!(lhs instanceof Variable || lhs instanceof FieldAccess || lhs instanceof ArrayAccess)) {
                    failTypeCheck(lhs, "assignee".formatted(lhs.getClass().getName()));
                }
                assertTypeEqual(lhs, rhs, "assignment");
                Type assType = lhs.getType(); // rhs might be null which would invalidate method and field calls
                binaryExpression.setType(assType);
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
        setIntegerValue(integerValue);
    }

    private void setIntegerValue(IntegerValue integerValue) {
        String strValue = integerValue.toString();
        int value;
        try {
            value = Integer.parseUnsignedInt(strValue);
        } catch (NumberFormatException e) {
            failTypeCheck(integerValue, "integer literal");
            return;
        }
        // Expected values: "0" to "2147483648". "2147483648" is represented as -214783648,
        // all other negative values indicate too large values
        // e.g. "2147483649" -> -214783647, so not allowed.
        if ((value == Integer.MIN_VALUE && (!integerValue.isNegative()) || integerValue.isInParentheses()) || (Integer.MIN_VALUE < value && value < 0)) {
            failTypeCheck(integerValue, "integer literal");
        }

        integerValue.setValue(value);
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

        if (type == null) return;

        Method method = globalScope.getMethod(type.getIdentifier(), methodInvocation.getFunctionName());
        MethodType methodType = method.getReferenceType();

        methodInvocation.setMethodType(methodType);

        Arguments arguments = methodInvocation.getArguments();
        arguments.setExpectedTypes(methodType.getParameterTypes());
        this.visitArguments(arguments);
    }


    @Override
    public void visitNegativeExpression(NegativeExpression negativeExpression) {
        Expression expr = negativeExpression.getEncapsulated();

        if (expr instanceof IntegerValue intValue) {
            intValue.setNegative(!negativeExpression.isNegative());
        } else if (expr instanceof NegativeExpression negValue) {
            negValue.setNegative(!negativeExpression.isNegative());
        }

        expr.accept(this);
        assertTypeEquals(expr, new IntegerType(), "integer operation");
        negativeExpression.setType(new IntegerType());

    }

    @Override
    public void visitNewArrayExpression(NewArrayExpression newArrayExpression) {
        Expression dimExpr = newArrayExpression.getSize();

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
        if (classType.getIdentifier().equals("String")) {
            failTypeCheck(newObjectExpression, "new object");
        }
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
        if (getPassManager().getCurrentMethod().isStatic()) {
            failTypeCheck(thisValue, "static method", "Illegal access to 'this'");
        }
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
        if (declaration instanceof Field && getPassManager().getCurrentMethod().isStatic()) {
            failTypeCheck(variable, "illegal reference to object attribute inside static method");
        }

        variable.setType(declaration.getRefType());
    }

    @Override
    public void visitVoidExpression(VoidExpression voidExpression) {
        voidExpression.setType(new VoidType());
    }

    public void visitArguments(Arguments arguments) {
        List<Type> expectedTypes = arguments.getExpectedTypes();

        // Maybe for later: SourcePosition of Error
        ASTNode node;
        switch (Integer.signum(arguments.getLength() - expectedTypes.size())) {
            case 1:
                node = arguments.get(expectedTypes.size());
                failTypeCheck(node, "Too many arguments", "method (expected %d)".formatted(expectedTypes.size()));
                break;
            case -1:
                int index = arguments.getLength() - 1;
                node = index >= 0 ? arguments.get(index) : arguments;
                failTypeCheck(node, "Too few arguments", "method (expected %d)".formatted(expectedTypes.size()));
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

        if (decl.getType().getBasicType() instanceof de.dercompiler.ast.type.VoidType) {
            failTypeCheck(decl, "variable declaration", "illegal type 'void'");
        } else if (decl.getRefType() instanceof LibraryClass) {
            //System and String are illegal variable types
            failTypeCheck(decl, "variable declaration", "Illegal type '%s'".formatted(decl.getRefType()));
        }

        if (!(expr instanceof UninitializedValue)) {
            Type expectedType = decl.getRefType();
            assertTypeEquals(expr, expectedType, "assignment to new %s variable".formatted(expectedType));
        }
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
