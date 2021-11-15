package de.dercompiler.semantic.typeAna;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.printer.ASTNodeVisitor;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.semantic.StringTable;

import java.util.ArrayList;
import java.util.List;

public class TypeCheckVisitor implements ASTNodeVisitor {

    private final StringTable stringTable;
    private final OutputMessageHandler logger;

    public TypeCheckVisitor() {
        this.logger = new OutputMessageHandler(MessageOrigin.PASSES);
        //TODO: how to access correct instance of StringTable?
        this.stringTable = null;
    }


    @Override
    public void visitArguments(Arguments arguments) {
        List<Type> argTypes = new ArrayList<>();
        for (int i = 0; i < arguments.getLength(); i++) {
            arguments.get(i).accept(this);
            argTypes.add(arguments.get(i).getType());
        }
        //TODO: compare argument types with parameter types

    }

    @Override
    public void visitArrayAccess(ArrayAccess arrayAccess) {
        SourcePosition pos = arrayAccess.getSourcePosition();
        Expression arrayExpr = arrayAccess.getEncapsulated();
        Expression indexExpr = arrayAccess.getIndex();

        arrayExpr.accept(this);
        indexExpr.accept(this);

        if (!(arrayAccess.getType().getBasicType() instanceof IntType index)) {
            // TODO: Handle TypeError: non-integer array index
        }

        // Range check not required as per the specs, so we are all good now

        arrayAccess.setType(new Type(pos, arrayExpr.getType().getBasicType(), arrayExpr.getType().getArrayDimension() - 1));
    }

    @Override
    public void visitBasicBlock(BasicBlock basicBlock) {

    }

    @Override
    public void visitBasicType(BasicType basicType) {

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

    @Override
    public void visitClassDeclaration(ClassDeclaration classDeclaration) {

    }

    @Override
    public void visitEmptyStatement(EmptyStatement emptyStatement) {

    }

    @Override
    public void visitErrorClassMember(ErrorClassMember errorClassMember) {

    }

    @Override
    public void visitErrorExpression(ErrorExpression errorExpression) {

    }

    @Override
    public void visitErrorStatement(ErrorStatement errorStatement) {

    }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) {

    }

    @Override
    public void visitField(Field field) {

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
    public void visitIfStatement(IfStatement ifStatement) {
        ifStatement.getCondition().accept(this);
        assertTypeEquals(ifStatement.getCondition(), new BooleanType(null), 0, "if condition");
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement localVariableDeclarationStatement) {
        Expression expr = localVariableDeclarationStatement.getExpression();
        expr.accept(this);
        // TODO: how to get variable definition from SymbolTable?
        // assertTypeEquals(expr, type, "assignment to new %s variable".formatted(type);
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
    public void visitMainMethod(MainMethod mainMethod) {

    }

    @Override
    public void visitMethod(Method method) {

    }

    @Override
    public void visitMethodInvocation(MethodInvocationOnObject methodInvocation) {

    }

    @Override
    public void visitMethodRest(MethodRest methodRest) {

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

        newArrayExpression.setType(new Type(pos, newArrayExpression.getBasicType(), newArrayExpression.getDimension()));
    }

    @Override
    public void visitNewObjectExpression(NewObjectExpression newObjectExpression) {
        SourcePosition pos = newObjectExpression.getSourcePosition();

        newObjectExpression.setType(new Type(pos, newObjectExpression.getObjectType(), 0));
    }

    @Override
    public void visitParameter(Parameter parameter) {

    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression primaryExpression) {

    }

    @Override
    public void visitProgram(Program program) {

    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        // ... visit operand?

        Expression expr = returnStatement.getExpression();
        assertTypeEquals(expr, returnStatement.getSurroundingMethod().getType(), "return value for %s method");
    }

    @Override
    public void visitType(Type type) {

    }

    @Override
    public void visitUninitializedValue(UninitializedValue uninitializedValue) {

    }

    @Override
    public void visitVoidExpression(VoidExpression voidExpression) {
        SourcePosition pos = voidExpression.getSourcePosition();
        voidExpression.setType(new Type(pos, new VoidType(pos), 0));
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        whileStatement.getCondition().accept(this);
        assertTypeEquals(whileStatement.getCondition(), new BooleanType(null), 0, "while condition");
    }
}
