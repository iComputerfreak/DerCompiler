package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.AssignmentExpression;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.expression.MethodInvocationOnObject;
import de.dercompiler.ast.expression.Variable;
import de.dercompiler.ast.statement.ExpressionStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;

import java.util.List;

public class SpecificationConformityPass implements MethodPass, StatementPass, ExpressionPass {

    private PassManager passManager;
    private static long id;
    private MainMethod main;
    private GlobalScope globalScope;

    @Override
    public void doInitialization(Program program) {
         globalScope = program.getGlobalScope();
    }


    private void failSpecs(ASTNode node, String message) {
        System.err.println(getPassManager().getLexer().printSourceText(node.getSourcePosition()));
        new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.SPECS_VIOLATION, message);
    }

    @Override
    public boolean runOnMethod(Method method) {
        if (method.isStatic()) {
            if (main != null) {
                failSpecs(method, "Duplicate main method; there must be exactly one in the program.");
            }
            main = (MainMethod) method;

            if (!method.getIdentifier().equals("main")) {
                failSpecs(method, "Illegal name for static method; must be 'main'");
            }

            List<Parameter> parameters = method.getParameters();
            switch (parameters.size()) {
                case 0 -> failSpecs(method, "Missing parameter of main method");
                case 1 -> {
                    Parameter parameter = parameters.get(0);
                    Type type = parameter.getType();
                    Type stringType = new Type(null, new CustomType(null, "String"), 1);
                    if (!type.syntaxEquals(stringType)) {
                        failSpecs(parameter, "Illegal type %s for main method parameter; must be String[]".formatted(parameter.getType().toString()));
                    }
                }
                default -> failSpecs(parameters.get(1), "Too many parameters for main method; must be 1");
            }
        }
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        if (statement instanceof ExpressionStatement exprStmt) {
            Expression expr = exprStmt.getExpression();
            if (!(expr instanceof MethodInvocationOnObject || expr instanceof AssignmentExpression)) {
                failSpecs(expr, "Illegal statement: must have side-effect.");
            }
        }
        return false;
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        if (getPassManager().getCurrentMethod().isStatic()) {
            List<Expression> subExpressions = new ReferencesCollector(true, false, false, false, false).analyze(expression);
            Variable argsRef = (Variable) subExpressions.stream().filter(ex -> ex instanceof Variable var && var.getDefinition() instanceof Parameter).findAny().orElse(null);
            if (argsRef != null) {
                failSpecs(argsRef, "Illegal reference to parameter %s of main method".formatted(argsRef.getName()));
            }
        }
        return false;
    }

    @Override
    public void doFinalization(Program program) {
        if (main == null) {
            failSpecs(null, "No main method found.");
        }
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.needsAnalysis(TypeAnalysisPass.class);
        usage.setDependency(DependencyType.RUN_IN_NEXT_STEP);
        return usage;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return usage;
    }

    @Override
    public void registerPassManager(PassManager manager) {
        this.passManager = manager;
    }

    @Override
    public PassManager getPassManager() {
        return passManager;
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
        return AnalysisDirection.TOP_DOWN;
    }

}
