package de.dercompiler.ast.printer;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.BasicType;
import de.dercompiler.ast.type.Type;

import java.util.Objects;
import java.util.Stack;


public class PrettyPrinter implements ASTNodeVisitor {

    private static final String INDENT = "\t";
    private final PrinterState state;
    private StringBuilder sb;
    private final boolean strictParenthesis;

    public PrettyPrinter(boolean strictParenthesis) {
        this.strictParenthesis = strictParenthesis;
        this.sb = new StringBuilder();
        this.state = new PrinterState();
    }

    private static boolean isAtomicExpression(Expression expr) {
        return expr instanceof IntegerValue || expr instanceof BooleanValue || expr instanceof NullValue || expr instanceof ThisValue || expr instanceof Variable;
    }

    public void visitProgram(Program program) {
        for (ClassDeclaration cls : program.getClasses()) {
            cls.accept(this);
        }
    }

    public void visitClassDeclaration(ClassDeclaration decl) {
        sb.append(INDENT.repeat(state.getIndent()));
        sb.append("class ");
        sb.append(decl.getIdentifier());
        sb.append(" {\n");
        this.state.save();
        this.state.indent();
        for (ClassMember member : decl.getMembers()) {
            member.accept(this);
        }
        this.state.restore();
        sb.append(INDENT.repeat(state.getIndent()));
        sb.append("}\n");
    }

    public void visitClassMember(ClassMember member) {
        if (member instanceof Field f) {
            f.accept(this);
        } else if (member instanceof Method m) {
            m.accept(this);
        } else if (member instanceof MainMethod main) {
            main.accept(this);
        }
    }

    @Override
    public void visitEmptyStatement(EmptyStatement emptyStatement) {
        beforeStatement();
        sb.append(";");
        afterStatement();
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

    public void visitMainMethod(MainMethod main) {
        sb.append(INDENT.repeat(this.state.getIndent()));
        sb.append("public static void ");
        sb.append(main.getIdentifier());

        sb.append("(");
        main.getParameterType().accept(this);
        sb.append(" ");
        sb.append(main.getParameterName());

        sb.append(") ");
        visitMethodRest(main.getMethodRest());
        main.getBlock().accept(this);

        sb.append("\n");
        sb.append(INDENT.repeat(this.state.getIndent() - 1));
    }

    public void visitField(Field f) {
        sb.append(INDENT.repeat(this.state.getIndent()));
        sb.append("public ");
        f.getType().accept(this);
        sb.append(" ");
        sb.append(f.getIdentifier());
        sb.append(";\n");
    }

    @Override
    public void visitFieldAccess(FieldAccess fieldAccess) {
        Expression encapsulated = fieldAccess.getEncapsulated();
        if (needsParentheses(encapsulated)) {
            sb.append("(");
            encapsulated.accept(this);
            sb.append(")");
        } else {
            encapsulated.accept(this);
        }
        sb.append(".");
        sb.append(fieldAccess.getFieldName());
    }

    public void visitMethod(Method m) {
        sb.append(INDENT.repeat(this.state.getIndent()));
        sb.append("public ");
        m.getType().accept(this);
        sb.append(" ");
        sb.append(m.getIdentifier());
        sb.append("(");

        int paramCount = m.getParameters().size();
        for (int i = 0; i < paramCount - 1; i++) {
            m.getParameters().get(i).accept(this);
            sb.append(", ");
        }
        if (paramCount > 0) {
            visitParameter(m.getParameters().get(paramCount - 1));
        }
        sb.append(") ");

        MethodRest rest = m.getMethodRest();
        if (!Objects.isNull(rest)) {
            rest.accept(this);
        }

        m.getBlock().accept(this);
        sb.append("\n");
    }

    public void visitType(Type type) {
        sb.append(type.getBasicType().toString());
        sb.append("[]".repeat(type.getArrayDimension()));
    }

    @Override
    public void visitUninitializedValue(UninitializedValue uninitializedValue) {

    }

    public void visitBasicBlock(BasicBlock block) {
        sb.append("{\n");
        sb.append(INDENT.repeat(this.state.getIndent()));
        this.state.save();
        this.state.indent();
        this.state.setStatementFormat(true, true);
        for (Statement statement : block.getStatements()) {
            statement.accept(this);
        }
        this.state.restore();
        sb.append("} ");
    }

    public void visitParameter(Parameter param) {
        param.getType().accept(this);
        sb.append(" ");
        sb.append(param.getIdentifier());
    }

    @Override
    public void visitPrimaryExpression(PrimaryExpression primaryExpression) {
        sb.append(primaryExpression.toString());
    }

    public void visitExpressionStatement(ExpressionStatement exprStmt) {
        beforeStatement();
        exprStmt.getExpression().accept(this);
        sb.append(";");
        afterStatement();
    }

    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement def) {
        beforeStatement();
        def.getType().accept(this);
        sb.append(" ");
        sb.append(def.getIdentifier());
        Expression expr = def.getExpression();
        if (!Objects.isNull(expr)) {
            sb.append(" = ");
            expr.accept(this);
        }
        sb.append(";");
        afterStatement();
    }

    public void visitReturnStatement(ReturnStatement ret) {
        beforeStatement();
        sb.append("return");
        if (!(ret.getExpression() instanceof VoidExpression)) sb.append(" ");
        ret.getExpression().accept(this);
        sb.append(";");
        afterStatement();
    }

    private void beforeStatement() {
        if (this.state.indentBefore) sb.append(INDENT);
    }

    private void afterStatement() {
        if (this.state.newLineAfter) {
            sb.append("\n");
            sb.append(INDENT.repeat(state.getIndent() - 1));
        }
    }

    public void visitIfStatement(IfStatement ifElse) {
        beforeStatement();
        sb.append("if (");
        ifElse.getCondition().accept(this);
        sb.append(") ");
        this.state.save();
        if (ifElse.getThenStatement() instanceof BasicBlock) {
            this.state.setStatementFormat(false, false);
            ifElse.getThenStatement().accept(this);
        } else {
            sb.append("\n");
            sb.append(INDENT.repeat(this.state.getIndent()));
            this.state.indent();
            this.state.setStatementFormat(true, ifElse.hasElse());
            ifElse.getThenStatement().accept(this);
        }
        this.state.restore();

        if (ifElse.hasElse()) {
            sb.append("else ");
            this.state.save();
            if (ifElse.getElseStatement() instanceof BasicBlock || ifElse.getElseStatement() instanceof IfStatement) {
                this.state.setStatementFormat(false, false);
                ifElse.getElseStatement().accept(this);
            } else {
                sb.append("\n");
                sb.append(INDENT.repeat(this.state.getIndent()));
                this.state.indent();
                this.state.setStatementFormat(true, false);
                ifElse.getElseStatement().accept(this);
            }
            this.state.restore();
        }
        afterStatement();
    }

    public void visitWhileStatement(WhileStatement loop) {
        beforeStatement();
        sb.append("while (");
        loop.getCondition().accept(this);
        sb.append(") ");
        this.state.save();
        if (loop.getStatement() instanceof BasicBlock block) {
            block.accept(this);
        } else {
            sb.append("\n");
            sb.append(INDENT.repeat(this.state.getIndent()));
            this.state.indent();
            this.state.setStatementFormat(true, false);
            loop.getStatement().accept(this);
        }
        this.state.restore();
        afterStatement();
    }

    public void visitLogicalNotExpression(LogicalNotExpression not) {
        sb.append("!");
        Expression encapsulated = not.getEncapsulated();
        if (encapsulated instanceof PrimaryExpression primary) {
            primary.accept(this);
        } else if (!strictParenthesis && encapsulated instanceof UnaryExpression unary) {
            unary.accept(this);
        } else {
            sb.append("(");
            encapsulated.accept(this);
            sb.append(")");
        }
    }

    public void visitNegativeExpression(NegativeExpression neg) {
        sb.append("-");
        Expression encapsulated = neg.getEncapsulated();
        if (encapsulated instanceof PrimaryExpression primary) {
            primary.accept(this);
        } else {
            sb.append("(");
            encapsulated.accept(this);
            sb.append(")");
        }
    }

    private boolean needsParentheses(Expression expression) {
        return strictParenthesis && !isAtomicExpression(expression);
    }

    public void visitNewArrayExpression(NewArrayExpression consArray) {
        sb.append("new ");
        sb.append(consArray.getBasicType());
        sb.append("[");
        consArray.getSize().accept(this);
        sb.append("]");
        sb.append("[]".repeat(consArray.getDimension() - 1));
    }

    public void visitNewObjectExpression(NewObjectExpression cons) {
        sb.append("new ");
        sb.append(cons.getObjectType().getIdentifier());
        sb.append("()");
    }

    public void visitMethodInvocation(MethodInvocationOnObject invocation) {
        Expression referenceObject = invocation.getReferenceObject();
        if (needsParentheses(referenceObject)) {
            sb.append("(");
            referenceObject.accept(this);
            sb.append(")");
        } else {
            referenceObject.accept(this);
        }
        sb.append(".");
        sb.append(invocation.getFunctionName());
        sb.append("(");
        invocation.getArguments().accept(this);
        sb.append(")");
    }

    public void visitBinaryExpression(BinaryExpression binaryExpr) {
        Expression lhs = binaryExpr.getLhs();
        if (needsParentheses(lhs) || (lhs instanceof BinaryExpression binaryLeft && binaryLeft.getOperator().getPrecedence() < binaryExpr.getOperator().getPrecedence())) {
            sb.append("(");
            lhs.accept(this);
            sb.append(")");
        } else {
            lhs.accept(this);
        }
        sb.append(" %s ".formatted(binaryExpr.getOperator()));

        Expression rhs = binaryExpr.getRhs();
        if (needsParentheses(rhs) || (rhs instanceof BinaryExpression binaryRight && binaryRight.getOperator().getPrecedence() < binaryExpr.getOperator().getPrecedence())) {
            sb.append("(");
            rhs.accept(this);
            sb.append(")");
        } else {
            rhs.accept(this);
        }
    }

    @Override
    public void visitVoidExpression(VoidExpression voidExpression) {

    }

    public void visitMethodRest(MethodRest rest) {
        if (Objects.isNull(rest)) {
            return;
        }
        sb.append("throws ");
        sb.append(rest.getIdentifier());
        sb.append(" ");
    }

    public void visitArguments(Arguments args) {
        for (int i = 0; i < args.getLength() - 1; i++) {
            args.get(i).accept(this);
            sb.append(", ");
        }
        if (args.getLength() > 0) {
            args.get(args.getLength() - 1).accept(this);
        }
    }

    @Override
    public void visitArrayAccess(ArrayAccess arrayAccess) {
        arrayAccess.getEncapsulated().accept(this);
        sb.append("[");
        arrayAccess.getIndex().accept(this);
        sb.append("]");
    }

    public void visitBasicType(BasicType type) {
        sb.append(type.toString());
    }

    public String flush() {
        String res = sb.toString();
        sb = new StringBuilder();
        return res;
    }

    private static class PrinterState {
        private int indent;
        private boolean indentBefore;
        private boolean newLineAfter;

        private static Stack<PrinterState> saved = new Stack<>();

        public PrinterState() {
            this(0, false, false);
        }

        public PrinterState(int indent, boolean indentBefore, boolean newLineAfter) {
            this.indent = indent;
            this.indentBefore = indentBefore;
            this.newLineAfter = newLineAfter;
        }

        public int getIndent() {
            return indent;
        }

        public void indent() {
            indent += 1;
        }

        public void setStatementFormat(boolean indentBefore, boolean newLineAfter) {
            this.indentBefore = indentBefore;
            this.newLineAfter = newLineAfter;
        }

        public void save() {
            saved.push(new PrinterState(this.indent, this.indentBefore, this.newLineAfter));
        }

        public void restore() {
            PrinterState old = saved.pop();
            this.indent = old.indent;
            this.indentBefore = old.indentBefore;
            this.newLineAfter = old.newLineAfter;
        }
    }
}
