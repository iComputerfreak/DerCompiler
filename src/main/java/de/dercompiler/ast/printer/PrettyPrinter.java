package de.dercompiler.ast.printer;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.util.Objects;


public class PrettyPrinter {

    private static final int TAB_SIZE = 4;

    public void printProgram(Program program, StringBuilder sb, int indent) {
        for (ClassDeclaration cls : program.getClasses()) {
            printClassDeclaration(cls, sb, indent);
        }
    }

    public void printClassDeclaration(ClassDeclaration decl, StringBuilder sb, int indent) {
        sb.append(" ".repeat(indent * TAB_SIZE));
        sb.append("class ");
        sb.append(decl.getIdentifier());
        sb.append(" {\n");
        for (ClassMember member : decl.getMembers()) {
            printClassMember(member, sb, indent + 1);
        }
        sb.append(" ".repeat(indent * TAB_SIZE));
        sb.append("}");
    }

    private void printClassMember(ClassMember member, StringBuilder sb, int indent) {
        if (member instanceof Field f) {
            printField(f, sb, indent);
        } else if (member instanceof Method m) {
            printMethod(m, sb, indent);
        } else if (member instanceof MainMethod main) {
            printClassMember(main, sb, indent);
        }
    }

    private void printField(Field f, StringBuilder sb, int indent) {
        sb.append(" ".repeat(indent * TAB_SIZE));
        sb.append("public ");
        printType(f.getType(), sb);
        sb.append(" ");
        sb.append(f.getIdentifier());
        sb.append(";\n");
    }

    private void printMethod(Method m, StringBuilder sb, int indent) {
        sb.append(" ".repeat(indent * TAB_SIZE));
        sb.append("public ");
        printType(m.getType(), sb);
        sb.append(" ");
        sb.append(m.getIdentifier());
        sb.append("(");

        int paramCount = m.getParameters().size();
        for (int i = 0; i < paramCount - 1; i++) {
            printParameter(m.getParameters().get(i), sb);
            sb.append(", ");
        }
        if (paramCount > 0) {
            printParameter(m.getParameters().get(paramCount - 1), sb);
        }
        sb.append(") ");

        printMethodRest(m.getRest(), sb);
        sb.append("{\n");

        printBasicBlock(m.getBlock(), sb, indent + 1);
        sb.append(" ".repeat(indent * TAB_SIZE));
        sb.append("}\n");
    }

    private void printType(Type type, StringBuilder sb) {
        sb.append(type.getBasicType().toString());
        sb.append("[]".repeat(type.getArrayDimension()));
    }

    private void printBasicBlock(BasicBlock block, StringBuilder sb, int indent) {
        for (Statement statement : block.getStatements()) {
            printStatement(statement, sb, indent, true);
        }
    }

    private void printParameter(Parameter param, StringBuilder sb) {
        printType(param.getType(), sb);
        sb.append(" ");
        sb.append(param.getIdentifier());
    }

    private void printStatement(Statement statement, StringBuilder sb, int indent, boolean indentFirst) {
        if (indentFirst) sb.append(" ".repeat(indent * TAB_SIZE));
        if (statement instanceof BasicBlock block) {
            printBasicBlock(block, sb, indent);
            return;
        } else if (statement instanceof EmptyStatement) {
            sb.append(";\n");
        } else if (statement instanceof ExpressionStatement expr) {
            printExpressionStatement(expr, sb);
        } else if (statement instanceof IfStatement ifElse) {
            printIfStatement(ifElse, sb, indent);
        } else if (statement instanceof LocalVariableDeclarationStatement def) {
            printLocalVariableDeclarationStatement(def, sb);
        } else if (statement instanceof ReturnStatement ret) {
            printReturnStatement(ret, sb);
        } else if (statement instanceof WhileStatement loop) {
            printWhileStatement(loop, sb, indent);
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + statement.getClass().getName());
        }
    }

    private void printExpressionStatement(ExpressionStatement exprStmt, StringBuilder sb) {
        printExpression(exprStmt.getExpression(), sb);
        sb.append(";\n");
    }

    private void printLocalVariableDeclarationStatement(LocalVariableDeclarationStatement def, StringBuilder sb) {
        printType(def.getType(), sb);
        sb.append(" ");
        sb.append(def.getIdentifier());
        if (!Objects.isNull(def.getExpression())) {
            sb.append(" = ");
            printExpression(def.getExpression(), sb);
        }
        sb.append(";\n");
    }

    private void printReturnStatement(ReturnStatement ret, StringBuilder sb) {
        sb.append("return");
        if (!(ret.getExpression() instanceof VoidExpression)) sb.append(" ");
        printExpression(ret.getExpression(), sb);
        sb.append(";\n");
    }

    private void printIfStatement(IfStatement ifElse, StringBuilder sb, int indent) {
        sb.append("if (");
        printExpression(ifElse.getCondition(), sb);
        printStatement(ifElse.getThenStatement(), sb, indent, false);
        if (ifElse.hasElse()) {
            sb.append(" else ");
            printStatement(ifElse.getElseStatement(), sb, indent, false);
        }
    }

    private void printWhileStatement(WhileStatement loop, StringBuilder sb, int indent) {
        sb.append("while (");
        printExpression(loop.getCondition(), sb);
        sb.append(") ");
        printStatement(loop.getStatement(), sb, indent, false);
    }

    private void printExpression(AbstractExpression expr, StringBuilder sb) {
        if (expr instanceof BinaryExpression binary) {
            printBinaryExpression(binary, sb);
        } else if (expr instanceof UnaryExpression unary) {
            printUnaryExpression(unary, sb);
        } else if (expr instanceof PrimaryExpression primary) {
            printPrimaryExpression(primary, sb);
        } else if (expr instanceof VoidExpression) {
            // do nothing
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + expr.getClass().getName());
        }
    }

    private void printUnaryExpression(UnaryExpression unary, StringBuilder sb) {
        if (unary instanceof NegativeExpression neg) {
            printNegativeExpression(neg, sb);
        } else if (unary instanceof MethodInvocationOnObject invocation) {
            printMethodInvocation(invocation, sb);
        } else if (unary instanceof PostfixExpression postfix) {
            printPostfixExpression(postfix, sb);
        } else if (unary instanceof LogicalNotExpression not) {
            printLogicalNotExpression(not, sb);
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + unary.getClass().getName());
        }
    }

    private void printLogicalNotExpression(LogicalNotExpression not, StringBuilder sb) {
        sb.append("!");
        AbstractExpression encapsulated = not.getEncapsulated();
        if (encapsulated instanceof PrimaryExpression primary) {
            printPrimaryExpression(primary, sb);
        } else {
            sb.append("(");
            printExpression(encapsulated, sb);
            sb.append(")");
        }
    }

    private void printPostfixExpression(PostfixExpression postfix, StringBuilder sb) {
        if (postfix instanceof FieldAccess access) {
            printExpression(access.getEncapsulated(), sb);
            sb.append(".");
            sb.append(access.getFieldName());
        } else if (postfix instanceof ArrayAccess access) {
            printExpression(access.getEncapsulated(), sb);
            sb.append("[");
            printExpression(access.getIndex(), sb);
            sb.append("]");
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + postfix.getClass().getName());
        }
    }

    private void printNegativeExpression(NegativeExpression neg, StringBuilder sb) {
        sb.append("-");
        AbstractExpression encapsulated = neg.getEncapsulated();
        if (encapsulated instanceof PrimaryExpression primary) {
            printPrimaryExpression(primary, sb);
        } else {
            sb.append("(");
            printExpression(encapsulated, sb);
            sb.append(")");
        }
    }

    private void printPrimaryExpression(PrimaryExpression primary, StringBuilder sb) {
        if (primary instanceof Variable var) {
            printVariable(var, sb);
        } else if (primary instanceof IntegerValue integer) {
            printIntegerValue(integer, sb);
        } else if (primary instanceof NullValue) {
            printNullValue(sb);
        } else if (primary instanceof ThisValue) {
            printThisValue(sb);
        } else if (primary instanceof BooleanValue bool) {
            printBooleanValue(bool, sb);
        } else if (primary instanceof NewObjectExpression cons) {
            printNewObjectExpression(cons, sb);
        } else if (primary instanceof NewArrayExpression consArray) {
            printNewArrayExpression(consArray, sb);
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + primary.getClass().getName());
        }
    }

    private void printNewArrayExpression(NewArrayExpression consArray, StringBuilder sb) {
        sb.append("new ");
        sb.append(consArray.getType());
        sb.append("[");
        printExpression(consArray.getSize(), sb);
        sb.append("]");
        sb.append("[]".repeat(consArray.getDimension()));
    }

    private void printNewObjectExpression(NewObjectExpression cons, StringBuilder sb) {
        sb.append("new ");
        sb.append(cons.getType().getIdentifier());
        sb.append("()");
    }

    private void printBooleanValue(BooleanValue bool, StringBuilder sb) {
        sb.append(bool.getValue() ? true : false);
    }

    private void printThisValue(StringBuilder sb) {
        sb.append("this");
    }

    private void printMethodInvocation(MethodInvocationOnObject invocation, StringBuilder sb) {
        printExpression(invocation.getReferenceObject(), sb);
        sb.append(".");
        sb.append(invocation.getFunctionName());
        sb.append("(");
        printArguments(invocation.getArguments(), sb);
        sb.append(")");
    }


    private void printNullValue(StringBuilder sb) {
        sb.append("null");
    }

    private void printIntegerValue(IntegerValue integer, StringBuilder sb) {
        sb.append(integer.toString());
    }

    private void printBinaryExpression(BinaryExpression binaryExpr, StringBuilder sb) {
        AbstractExpression lhs = binaryExpr.getLhs();
        if (lhs instanceof BinaryExpression binaryLeft && binaryLeft.getOperator().getPrecedence() < binaryExpr.getOperator().getPrecedence()) {
            sb.append("(");
            printBinaryExpression(binaryLeft, sb);
            sb.append(")");
        } else {
            printExpression(lhs, sb);
        }
        sb.append(" %s ".formatted(binaryExpr.getOperator()));

        AbstractExpression rhs = binaryExpr.getRhs();
        if (rhs instanceof BinaryExpression binaryRight && binaryRight.getOperator().getPrecedence() < binaryExpr.getOperator().getPrecedence()) {
            sb.append("(");
            printBinaryExpression(binaryRight, sb);
            sb.append(")");
        } else {
            printExpression(rhs, sb);
        }
    }

    private void printVariable(Variable var, StringBuilder sb) {
        sb.append(var.getName());
    }

    private void printMethodRest(MethodRest rest, StringBuilder sb) {
        if (Objects.isNull(rest)) {
            return;
        }

        sb.append("throws ");
        sb.append(rest.getIdentifier());
        sb.append(" ");
    }


    public void printNode(ASTNode node, StringBuilder sb) {
        if (node instanceof Program program) {
            printProgram(program, sb, 0);
        } else if (node instanceof ClassDeclaration decl) {
            printClassDeclaration(decl, sb, 0);
        } else if (node instanceof ClassMember member) {
            printClassMember(member, sb, 0);
        } else if (node instanceof MethodRest rest) {
            printMethodRest(rest, sb);
        } else if (node instanceof Statement statement) {
            printStatement(statement, sb, 0, false);
        } else if (node instanceof AbstractExpression expr) {
            printExpression(expr, sb);
        } else if (node instanceof BasicType type) {
            printBasicType(type, sb);
        } else if (node instanceof Type type) {
            printType(type, sb);
        } else if (node instanceof Arguments args) {
            printArguments(args, sb);
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print not yet implemented for " + node.getClass().getName());
        }
    }

    private void printArguments(Arguments args, StringBuilder sb) {
        for (int i = 0; i < args.getLength() - 1; i++) {
            printExpression(args.get(i), sb);
            sb.append(", ");
        }
        if (args.getLength() > 0) {
            printExpression(args.get(args.getLength() - 1), sb);
        }
    }

    private void printBasicType(BasicType type, StringBuilder sb) {
    }
}
