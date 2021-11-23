package de.dercompiler.pass.passes;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.AssignmentExpression;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.expression.MethodInvocationOnObject;
import de.dercompiler.ast.expression.Variable;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.CustomType;
import de.dercompiler.ast.type.Type;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.FieldDefinition;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.ClassType;
import de.dercompiler.semantic.type.InternalClass;
import de.dercompiler.semantic.type.VoidType;
import de.dercompiler.pass.passes.ReferencesCollector.ReferenceType;

import java.util.List;

/**
 *  (Pass 8) Checks for specified constraints.
 */
public class SpecificationConformityPass implements ClassPass, MethodPass, StatementPass, ExpressionPass {

    private PassManager passManager;
    private static long id;
    private MainMethod main;
    private GlobalScope globalScope;

    @Override
    public void doInitialization(Program program) {
        globalScope = program.getGlobalScope();
    }


    private void failSpecs(ASTNode node, String message, boolean quit) {
        System.err.println(getPassManager().getLexer().printSourceText(node.getSourcePosition()));
        new OutputMessageHandler(MessageOrigin.PASSES).printErrorAndExit(PassErrorIds.SPECS_VIOLATION, message);
        if (quit) getPassManager().quitOnError();
    }

    @Override
    public boolean runOnMethod(Method method) {
        if (method.isStatic()) {
            if (main != null) {
                failSpecs(method, "Duplicate main method; there must be exactly one in the program.", true);
            }
            main = (MainMethod) method;

            if (!method.getIdentifier().equals("main")) {
                failSpecs(method, "Illegal name for static method; must be 'main'", true);
            }

            List<Parameter> parameters = method.getParameters();
            switch (parameters.size()) {
                case 0 -> failSpecs(method, "Missing parameter of main method", true);
                case 1 -> {
                    Parameter parameter = parameters.get(0);
                    Type type = parameter.getType();
                    Type stringType = new Type(null, new CustomType(null, "String"), 1);
                    if (!type.syntaxEquals(stringType)) {
                        failSpecs(parameter, "Illegal type %s for main method parameter; must be String[]".formatted(parameter.getType().toString()), true);
                    }
                }
                default -> failSpecs(parameters.get(1), "Too many parameters for main method; must be 1", true);
            }
        }

        ClassType type = globalScope.getClass(getPassManager().getCurrentClass().getIdentifier());
        MethodDefinition methodDef = type.getMethod(method.getIdentifier());
        if (!methodDef.getType().getReturnType().isCompatibleTo(new VoidType())) {
            if (!findReturnStatement(method.getBlock())) {
                failSpecs(method, "Method is missing a return statement", true);
            }
        }

        return false;
    }

    private boolean findReturnStatement(Statement stmt) {
        if (stmt instanceof ReturnStatement) return true;
        else if (stmt instanceof WhileStatement loop && findReturnStatement(loop.getStatement())) return false;
        else if (stmt instanceof IfStatement ifElse && findReturnStatement(ifElse.getThenStatement())
                && findReturnStatement(ifElse.getElseStatement())) return true;
        else if (stmt instanceof BasicBlock block
                && block.getStatements().stream().map(this::findReturnStatement)
                .reduce(false, (acc, b) -> acc || b)) return true;
        else return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        if (statement instanceof ExpressionStatement exprStmt) {
            Expression expr = exprStmt.getExpression();
            if (!(expr instanceof MethodInvocationOnObject || expr instanceof AssignmentExpression)) {
                failSpecs(expr, "Illegal statement: must have side-effect.", true);
            }
        }
        return false;
    }

    @Override
    public boolean runOnExpression(Expression expression) {
            List<Expression> references = new ReferencesCollector(ReferenceType.VARIABLE, ReferenceType.FIELD).analyze(expression);
        if (getPassManager().getCurrentMethod().isStatic()) {
            Variable argsRef = (Variable) references.stream().filter(ex -> ex instanceof Variable var && var.getDefinition() instanceof Parameter).findAny().orElse(null);
            if (argsRef != null) {
                failSpecs(argsRef, "Illegal reference to parameter %s of main method".formatted(argsRef.getName()), true);
            }
        } else {
            Expression internalRef = references.stream().filter(Expression::isInternal).findAny().orElse(null);
            if (internalRef != null) {
                failSpecs(internalRef, "Illegal reference to internal construct", true);
            }
        }

        List<Expression> methodInvocations = new ReferencesCollector(ReferenceType.METHOD_INVOCATION).analyze(expression);
        MethodInvocationOnObject mainMethodCall = (MethodInvocationOnObject) methodInvocations.stream()
                .filter(ex -> ex instanceof MethodInvocationOnObject methodCall && methodCall.getMethodType().isStaticMethod())
                .findAny().orElse(null);
        if (mainMethodCall != null) {
            failSpecs(mainMethodCall, "Illegal call to main method", true);
        }
        return false;
    }

    @Override
    public void doFinalization(Program program) {
        if (main == null) {
            failSpecs(program, "No main method found.", false);
        }
    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(LeaveScopePass.class);
        usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
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
        return AnalysisDirection.BOTTOM_UP;
    }

    @Override
    public boolean runOnClass(ClassDeclaration classDeclaration) {
        ClassType type = globalScope.getClass(classDeclaration.getIdentifier());
        List<FieldDefinition> fields = type.getFields();
        for (FieldDefinition field : fields) {
            if (field.getType() instanceof InternalClass) {
                failSpecs(field.getNode(), "Illegal attribute type '%s'".formatted(field.getType()), true);
            }
        }
        return false;
    }
}
