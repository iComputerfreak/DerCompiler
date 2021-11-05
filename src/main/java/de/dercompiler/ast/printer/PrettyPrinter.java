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
    private static final String INDENT = "\t";
    private StringBuilder sb;
    private boolean strictParenthesis = false;

    public PrettyPrinter(boolean strictParenthesis) {
        this.strictParenthesis = strictParenthesis;
        this.sb = new StringBuilder();
    }

    public void printProgram(Program program, int indent) {
        for (ClassDeclaration cls : program.getClasses()) {
            printClassDeclaration(cls, indent);
        }
    }

    public void printClassDeclaration(ClassDeclaration decl, int indent) {
        sb.append(INDENT.repeat(indent));
        sb.append("class ");
        sb.append(decl.getIdentifier());
        sb.append(" {\n");
        for (ClassMember member : decl.getMembers()) {
            printClassMember(member, indent + 1);
        }
        sb.append(INDENT.repeat(indent));
        sb.append("}\n");
    }

    private void printClassMember(ClassMember member, int indent) {
        if (member instanceof Field f) {
            printField(f, indent);
        } else if (member instanceof Method m) {
            printMethod(m, indent);
        } else if (member instanceof MainMethod main) {
            printMainMethod(main, indent);
        }
    }

    private void printMainMethod(MainMethod main, int indent) {
        sb.append(INDENT.repeat(indent));
        sb.append("public static void ");
        sb.append(main.getIdentifier());

        sb.append("(");

        printType(main.getParameterType());
        sb.append(" ");
        sb.append(main.getParameterName());

        sb.append(") ");
        printMethodRest(main.getMethodRest());


        printBasicBlock(main.getBlock(), indent);
        sb.append("\n");
        sb.append(INDENT.repeat(indent - 1));
    }

    private void printField(Field f, int indent) {
        sb.append(INDENT.repeat(indent));
        sb.append("public ");
        printType(f.getType());
        sb.append(" ");
        sb.append(f.getIdentifier());
        sb.append(";\n");
    }

    private void printMethod(Method m, int indent) {
        sb.append(INDENT.repeat(indent));
        sb.append("public ");
        printType(m.getType());
        sb.append(" ");
        sb.append(m.getIdentifier());
        sb.append("(");

        int paramCount = m.getParameters().size();
        for (int i = 0; i < paramCount - 1; i++) {
            printParameter(m.getParameters().get(i));
            sb.append(", ");
        }
        if (paramCount > 0) {
            printParameter(m.getParameters().get(paramCount - 1));
        }
        sb.append(") ");

        printMethodRest(m.getMethodRest());

        printBasicBlock(m.getBlock(), indent);
        sb.append("\n");
    }

    private void printType(Type type) {
        sb.append(type.getBasicType().toString());
        sb.append("[]".repeat(type.getArrayDimension()));
    }

    private void printBasicBlock(BasicBlock block, int indent) {
        sb.append("{\n");
        sb.append(INDENT.repeat(indent));
        for (Statement statement : block.getStatements()) {
            if (statement instanceof EmptyStatement) continue;
            printStatement(statement, indent + 1, true, true);
        }
        sb.append("} ");
    }

    private void printParameter(Parameter param) {
        printType(param.getType());
        sb.append(" ");
        sb.append(param.getIdentifier());
    }


    private void printStatement(Statement statement, int indent, boolean indentFirst, boolean breakLineAfter) {
        // At this point, the text shall be either
        // - empty but indented to the level of the _surrounding_ block
        // - non-empty with the following statement in the same line

        if (indentFirst) sb.append(INDENT);
        if (statement instanceof BasicBlock block) {
            printBasicBlock(block, indent);
            return;
        } else if (statement instanceof EmptyStatement) {
            sb.append(";\n");
        } else if (statement instanceof ExpressionStatement expr) {
            printExpressionStatement(expr);
        } else if (statement instanceof IfStatement ifElse) {
            printIfStatement(ifElse, indent);
        } else if (statement instanceof LocalVariableDeclarationStatement def) {
            printLocalVariableDeclarationStatement(def);
        } else if (statement instanceof ReturnStatement ret) {
            printReturnStatement(ret);
        } else if (statement instanceof WhileStatement loop) {
            printWhileStatement(loop, indent);
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + statement.getClass().getName());
        }

        if (breakLineAfter) {
            sb.append("\n");
            sb.append(INDENT.repeat(indent - 1));
        }
    }

    private void printExpressionStatement(ExpressionStatement exprStmt) {
        printExpression(exprStmt.getExpression());
        sb.append(";");
    }

    private void printLocalVariableDeclarationStatement(LocalVariableDeclarationStatement def) {
        printType(def.getType());
        sb.append(" ");
        sb.append(def.getIdentifier());
        AbstractExpression expr = def.getExpression();
        if (!Objects.isNull(expr)) {
            sb.append(" = ");
            printExpression(expr);
        }
        sb.append(";");
    }

    private void printReturnStatement(ReturnStatement ret) {
        sb.append("return");
        if (!(ret.getExpression() instanceof VoidExpression)) sb.append(" ");
        printExpression(ret.getExpression());
        sb.append(";");
    }

    private void printIfStatement(IfStatement ifElse, int indent) {
        sb.append("if (");
        printExpression(ifElse.getCondition());
        sb.append(") ");
        if (ifElse.getThenStatement() instanceof BasicBlock) {
            printStatement(ifElse.getThenStatement(), indent, false, true);
        } else {
            sb.append("\n");
            sb.append(INDENT.repeat(indent));
            printStatement(ifElse.getThenStatement(), indent + 1, true, true);
        }

        if (ifElse.hasElse()) {
            sb.append("else ");
            if (ifElse.getElseStatement() instanceof BasicBlock || ifElse.getElseStatement() instanceof IfStatement) {
                printStatement(ifElse.getElseStatement(), indent, false, false);
            } else {
                sb.append("\n");
                sb.append(INDENT.repeat(indent));
                printStatement(ifElse.getElseStatement(), indent + 1, true, true);
            }
        }
    }

    private void printWhileStatement(WhileStatement loop, int indent) {
        sb.append("while (");
        printExpression(loop.getCondition());
        sb.append(") ");
        if (loop.getStatement() instanceof BasicBlock block) {
            printBasicBlock(block, indent);
        } else {
            sb.append("\n");
            sb.append(INDENT.repeat(indent));
            printStatement(loop.getStatement(), indent + 1, true, true);
        }
    }

    private void printExpression(AbstractExpression expr) {
        if (expr instanceof BinaryExpression binary) {
            printBinaryExpression(binary);
        } else if (expr instanceof UnaryExpression unary) {
            printUnaryExpression(unary);
        } else if (expr instanceof PrimaryExpression primary) {
            printPrimaryExpression(primary);
        } else if (expr instanceof VoidExpression) {
            // do nothing
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + expr.getClass().getName());
        }
    }

    private void printUnaryExpression(UnaryExpression unary) {
        if (unary instanceof NegativeExpression neg) {
            printNegativeExpression(neg);
        } else if (unary instanceof MethodInvocationOnObject invocation) {
            printMethodInvocation(invocation);
        } else if (unary instanceof PostfixExpression postfix) {
            printPostfixExpression(postfix);
        } else if (unary instanceof LogicalNotExpression not) {
            printLogicalNotExpression(not);
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + unary.getClass().getName());
        }
    }

    private void printLogicalNotExpression(LogicalNotExpression not) {
        sb.append("!");
        AbstractExpression encapsulated = not.getEncapsulated();
        if (encapsulated instanceof PrimaryExpression primary) {
            printPrimaryExpression(primary);
        } else if (!strictParenthesis && encapsulated instanceof UnaryExpression unary) {
            printUnaryExpression(unary);
        } else {
            sb.append("(");
            printExpression(encapsulated);
            sb.append(")");
        }
    }

    private void printPostfixExpression(PostfixExpression postfix) {
        if (postfix instanceof FieldAccess access) {
            AbstractExpression encapsulated = access.getEncapsulated();
            if (needsParentheses(encapsulated)) {
                sb.append("(");
                printExpression(encapsulated);
                sb.append(")");
            } else {
                printExpression(encapsulated);
            }
            sb.append(".");
            sb.append(access.getFieldName());
        } else if (postfix instanceof ArrayAccess access) {
            printExpression(access.getEncapsulated());
            sb.append("[");
            printExpression(access.getIndex());
            sb.append("]");
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + postfix.getClass().getName());
        }
    }

    private void printNegativeExpression(NegativeExpression neg) {
        sb.append("-");
        AbstractExpression encapsulated = neg.getEncapsulated();
        if (encapsulated instanceof PrimaryExpression primary) {
            printPrimaryExpression(primary);
        } else {
            sb.append("(");
            printExpression(encapsulated);
            sb.append(")");
        }
    }

    private void printPrimaryExpression(PrimaryExpression primary) {
        if (primary instanceof Variable var) {
            printVariable(var);
        } else if (primary instanceof IntegerValue integer) {
            printIntegerValue(integer);
        } else if (primary instanceof NullValue) {
            printNullValue(sb);
        } else if (primary instanceof ThisValue) {
            printThisValue(sb);
        } else if (primary instanceof BooleanValue bool) {
            printBooleanValue(bool);
        } else if (primary instanceof NewObjectExpression cons) {
            printNewObjectExpression(cons);
        } else if (primary instanceof NewArrayExpression consArray) {
            printNewArrayExpression(consArray);
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print is not yet implemented for " + primary.getClass().getName());
        }
    }

    private static boolean isAtomicExpression(AbstractExpression expr) {
        return expr instanceof IntegerValue || expr instanceof BooleanValue || expr instanceof NullValue || expr instanceof Variable;
    }

    private boolean needsParentheses(AbstractExpression expression) {
        return strictParenthesis && !isAtomicExpression(expression);
    }

    private void printNewArrayExpression(NewArrayExpression consArray) {
        sb.append("new ");
        sb.append(consArray.getType());
        sb.append("[");
        printExpression(consArray.getSize());
        sb.append("]");
        sb.append("[]".repeat(consArray.getDimension()));
    }

    private void printNewObjectExpression(NewObjectExpression cons) {
        sb.append("new ");
        sb.append(cons.getType().getIdentifier());
        sb.append("()");
    }

    private void printBooleanValue(BooleanValue bool) {
        sb.append(bool.getValue() ? true : false);
    }

    private void printThisValue(StringBuilder sb) {
        sb.append("this");
    }

    private void printMethodInvocation(MethodInvocationOnObject invocation) {
        AbstractExpression referenceObject = invocation.getReferenceObject();
        if (needsParentheses(referenceObject)) {
            sb.append("(");
            printExpression(referenceObject);
            sb.append(")");
        } else {
            printExpression(referenceObject);
        }
        sb.append(".");
        sb.append(invocation.getFunctionName());
        sb.append("(");
        printArguments(invocation.getArguments());
        sb.append(")");
    }


    private void printNullValue(StringBuilder sb) {
        sb.append("null");
    }

    private void printIntegerValue(IntegerValue integer) {
        sb.append(integer.toString());
    }

    private void printBinaryExpression(BinaryExpression binaryExpr) {
        AbstractExpression lhs = binaryExpr.getLhs();
        if (needsParentheses(lhs) || (lhs instanceof BinaryExpression binaryLeft && binaryLeft.getOperator().getPrecedence() < binaryExpr.getOperator().getPrecedence())) {
            sb.append("(");
            printExpression(lhs);
            sb.append(")");
        } else {
            printExpression(lhs);
        }
        sb.append(" %s ".formatted(binaryExpr.getOperator()));

        AbstractExpression rhs = binaryExpr.getRhs();
        if (needsParentheses(rhs) || (rhs instanceof BinaryExpression binaryRight && binaryRight.getOperator().getPrecedence() < binaryExpr.getOperator().getPrecedence())) {
            sb.append("(");
            printExpression(rhs);
            sb.append(")");
        } else {
            printExpression(rhs);
        }
    }

    private void printVariable(Variable var) {
        sb.append(var.getName());
    }

    private void printMethodRest(MethodRest rest) {
        if (Objects.isNull(rest)) {
            return;
        }

        sb.append("throws ");
        sb.append(rest.getIdentifier());
        sb.append(" ");
    }


    public void printNode(ASTNode node) {
        if (node instanceof Program program) {
            printProgram(program, 0);
        } else if (node instanceof ClassDeclaration decl) {
            printClassDeclaration(decl, 0);
        } else if (node instanceof ClassMember member) {
            printClassMember(member, 0);
        } else if (node instanceof MethodRest rest) {
            printMethodRest(rest);
        } else if (node instanceof Statement statement) {
            printStatement(statement, 0, false, true);
        } else if (node instanceof AbstractExpression expr) {
            printExpression(expr);
        } else if (node instanceof BasicType type) {
            printBasicType(type);
        } else if (node instanceof Type type) {
            printType(type);
        } else if (node instanceof Arguments args) {
            printArguments(args);
        } else {
            new OutputMessageHandler(MessageOrigin.PARSER).internalError("print not yet implemented for " + node.getClass().getName());
        }
    }

    private void printArguments(Arguments args) {
        for (int i = 0; i < args.getLength() - 1; i++) {
            printExpression(args.get(i));
            sb.append(", ");
        }
        if (args.getLength() > 0) {
            printExpression(args.get(args.getLength() - 1));
        }
    }

    private void printBasicType(BasicType type) {
        sb.append(type.toString());
    }

    public String flush() {
        String res = sb.toString();
        sb = new StringBuilder();
        return res;
    }
}
