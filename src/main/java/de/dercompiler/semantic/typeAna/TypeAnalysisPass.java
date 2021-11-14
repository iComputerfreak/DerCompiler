package de.dercompiler.semantic.typeAna;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.printer.ASTExpressionVisitor;
import de.dercompiler.ast.statement.IfStatement;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.ReturnStatement;
import de.dercompiler.ast.statement.WhileStatement;
import de.dercompiler.ast.type.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.StringTable;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.Symbol;

import java.util.ArrayList;
import java.util.List;

public class TypeAnalysisPass implements MethodPass, ExpressionPass, ASTExpressionVisitor {

    private final OutputMessageHandler logger;
    private StringTable stringTable;

    public TypeAnalysisPass() {
        this.stringTable = StringTable.getInstance();
        this.logger = new OutputMessageHandler(MessageOrigin.PASSES);
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        return false;
    }

    @Override
    public boolean checkExpression(Expression expression) {
        return ExpressionPass.super.checkExpression(expression);
    }

    @Override
    public boolean runOnMethod(Method method) {
        return false;
    }

    @Override
    public boolean checkMethod(Method method) {
        return MethodPass.super.checkMethod(method);
    }

    @Override
    public PassDependencyType getMinDependencyType() {
        return MethodPass.super.getMinDependencyType();
    }

    @Override
    public PassDependencyType getMaxDependencyType() {
        return MethodPass.super.getMaxDependencyType();
    }

    @Override
    public void doInitialization(Program program) {

    }

    @Override
    public void doFinalization(Program program) {

    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        return null;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return null;
    }

    @Override
    public void registerPassManager(PassManager manager) {

    }

    @Override
    public long registerID(long id) {
        return 0;
    }

    @Override
    public long getID() {
        return 0;
    }

    private void failTypeCheck(Expression expr, String description) {
        // TODO implement
    }

    private void assertTypeEquals(Expression expr, BasicType type, int dim, String description) {
        if (expr.getType().getBasicType().syntaxEquals(type) || expr.getType().getArrayDimension() != dim) {
            failTypeCheck(expr, description);
        }
    }

    private void assertTypeEqual(Expression lhs, Expression rhs, String description) {
        if (!lhs.getType().syntaxEquals(rhs.getType())) {
            failTypeCheck(rhs, description);
        }
    }

    private void assertTypeEquals(Expression expr, Type type, String description) {
        if (!expr.getType().syntaxEquals(type)) {

        }
    }

    private CustomType assertCustomBasicType(Expression expr, String description) {
        if (expr.getType().getBasicType() instanceof CustomType type) {
            return type;
        }

        failTypeCheck(expr, description);
        return null;
    }

    private void assertNotVoid(Expression expr, String description) {
        if (expr.getType().getBasicType() instanceof VoidType) {
            failTypeCheck(expr, description);
        }
    }

    private void assertNotArray(Expression expr, String description) {
        if (expr.getType().getArrayDimension() > 0) {
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

        if (!(arrayExpr.getType().getArrayDimension() == 0)) {
            failTypeCheck(arrayExpr, "array expression");
        }

        if (!(arrayAccess.getType().getBasicType() instanceof IntType index)) {
            failTypeCheck(indexExpr, "index expression");
        }

        // Range check not required as per the specs, so we are all good now

        arrayAccess.setType(new Type(pos, arrayExpr.getType().getBasicType(), arrayExpr.getType().getArrayDimension() - 1));
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
                binaryExpression.setType(new Type(lhs.getSourcePosition(), rhs.getType().getBasicType(), 0));
                break;

            case AND_LAZY, OR_LAZY:
                assertTypeEquals(lhs, new BooleanType(null), 0, "operand of comparison operation");
                assertTypeEquals(rhs, new BooleanType(null), 0, "operand of comparison operation");
                // Position is synthesized from left operator
                binaryExpression.setType(lhs.getType());
                break;

            case EQUAL, NOT_EQUAL:
                assertTypeEqual(lhs, rhs, "operands of equality operation");
                break;

            case LESS_THAN, LESS_THAN_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL:
                assertTypeEquals(lhs, new IntType(null), 0, "operand of comparison operation");
                assertTypeEquals(rhs, new IntType(null), 0, "operand of comparison operation");
                // Position is synthesized from left operator
                binaryExpression.setType(new Type(lhs.getSourcePosition(), new BooleanType(lhs.getSourcePosition()), 0));
                break;

            case PLUS, MINUS, STAR, SLASH, PERCENT_SIGN:
                assertTypeEquals(lhs, new IntType(null), 0, "operand of arithmetic operation");
                assertTypeEquals(rhs, new IntType(null), 0, "operand of arithmetic operation");
                // Position is synthesized from left operator
                binaryExpression.setType(lhs.getType());
                break;

            case BAR, OR_SHORT, XOR, XOR_SHORT, AMPERSAND, AND_SHORT:
                assertTypeEqual(lhs, rhs, "operands of logical operation");
                BasicType basicType = lhs.getType().getBasicType();
                if (!(basicType instanceof IntType || basicType instanceof BooleanType) || lhs.getType().getArrayDimension() > 0) {
                    // fail
                    failTypeCheck(lhs, "operand of logical operation");
                }
                binaryExpression.setType(new Type(lhs.getSourcePosition(), lhs.getType().getBasicType(), 0));
                break;

            default:
                failTypeCheck(binaryExpression, "unknown binary operator");
        }
    }

    @Override
    public void visitErrorExpression(ErrorExpression errorExpression) {

    }

    @Override
    public void visitExpression(Expression expression) {
        // abstract parent class
    }

    @Override
    public void visitFieldAccess(FieldAccess fieldAccess) {
        // TODO: Where to find Field Type in SymbolTable?
        fieldAccess.getEncapsulated().accept(this);
        if (!(fieldAccess.getType().getBasicType() instanceof CustomType) || fieldAccess.getType().getArrayDimension() != 0) {
            //TODO: fieldAccess.getType() has no fields!
        }

        Type fieldType = null; // symbolTable.lookupClass(fieldAccess.getType();
        fieldAccess.setType(fieldType);
    }

    @Override
    public void visitLogicalNotExpression(LogicalNotExpression logicalNotExpression) {
        Expression expr = logicalNotExpression.getEncapsulated();
        SourcePosition pos = logicalNotExpression.getSourcePosition();

        expr.accept(this);
        assertTypeEquals(expr, new BooleanType(null), 0, "boolean operation");

        logicalNotExpression.setType(new Type(pos, new BooleanType(pos), 0));
    }

    @Override
    public void visitMethodInvocation(MethodInvocationOnObject methodInvocation) {
        Expression refObj = methodInvocation.getEncapsulated();
        refObj.accept(this);

        assertNotArray(refObj, "reference object of method invocation");
        CustomType type = assertCustomBasicType(refObj, "reference object of method invocation");

        Symbol method = null; //stringTable.findOrInsertMethod(methodInvocation.getFunctionName());
        Symbol cDecl = stringTable.findOrInsertClass(type.getIdentifier());

        // TODO: get Method declaration out of there
        cDecl.getCurrentDef();

    }



    @Override
    public void visitNegativeExpression(NegativeExpression negativeExpression) {
        Expression expr = negativeExpression.getEncapsulated();
        SourcePosition pos = negativeExpression.getSourcePosition();

        expr.accept(this);
        assertTypeEquals(expr, new IntType(null), 0, "integer operation");
        negativeExpression.setType(new Type(pos, new IntType(pos), 0));

    }

    @Override
    public void visitNewArrayExpression(NewArrayExpression newArrayExpression) {
        Expression dimExpr = newArrayExpression.getSize();
        SourcePosition pos = newArrayExpression.getSourcePosition();

        dimExpr.accept(this);
        assertTypeEquals(dimExpr, new IntType(null), 0, "dimension expression");

        BasicType basicType = newArrayExpression.getBasicType();
        if (basicType instanceof VoidType) {
            failTypeCheck(newArrayExpression, "array type");
        }

        newArrayExpression.setType(new Type(pos, basicType, newArrayExpression.getDimension()));
    }

    @Override
    public void visitNewObjectExpression(NewObjectExpression newObjectExpression) {
        SourcePosition pos = newObjectExpression.getSourcePosition();

        newObjectExpression.setType(new Type(pos, newObjectExpression.getObjectType(), 0));
    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression primaryExpression) {
        // abstract type
    }

    @Override
    public void visitVoidExpression(VoidExpression voidExpression) {
        SourcePosition pos = voidExpression.getSourcePosition();
        voidExpression.setType(new Type(pos, new VoidType(pos), 0));
    }

    public void visitArguments(Arguments arguments) {
        List<Type> argTypes = new ArrayList<>();
        for (int i = 0; i < arguments.getLength(); i++) {
            arguments.get(i).accept(this);
            argTypes.add(arguments.get(i).getType());
        }
        //TODO: compare argument types with parameter types
    }

    public void visitIfStatement(IfStatement ifStatement) {
        ifStatement.getCondition().accept(this);
        assertTypeEquals(ifStatement.getCondition(), new BooleanType(null), 0, "if condition");
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

        assertTypeEquals(expr, returnStatement.getSurroundingMethod().getType(), "return value for %s method");
    }

    public void visitWhileStatement(WhileStatement whileStatement) {
        whileStatement.getCondition().accept(this);
        assertTypeEquals(whileStatement.getCondition(), new BooleanType(null), 0, "while condition");
    }
}
