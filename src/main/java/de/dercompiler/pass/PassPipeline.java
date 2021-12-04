package de.dercompiler.pass;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.passes.ASTReferencePass;
import de.dercompiler.pass.passes.ASTReferencePullPass;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

class PassPipeline {



    static class PassStep  implements ASTStatementVisitor {

        enum DEPTH {
            UNINITALIZED,
            CLASS,
            METHOD,
            BASICBLOCK,
            STATEMENT,
            EXPRESSION,
        }

        private final LinkedList<ClassPass> td_classPasses;
        private final LinkedList<MethodPass> td_methodPasses;
        private final LinkedList<BasicBlockPass> td_basicBlockPasses;
        private final LinkedList<StatementPass> td_statementPasses;
        private final LinkedList<ExpressionPass> td_expressionPasses;

        private final LinkedList<ExpressionPass> bu_expressionPasses;
        private final LinkedList<StatementPass> bu_statementPasses;
        private final LinkedList<BasicBlockPass> bu_basicBlockPasses;
        private final LinkedList<MethodPass> bu_methodPasses;
        private final LinkedList<ClassPass> bu_classPasses;

        private final PassManager manager;
        private int num;
        private DEPTH minDepth;
        private DEPTH maxDepth;

        public PassStep(PassManager manager) {
            minDepth = DEPTH.UNINITALIZED;
            maxDepth = DEPTH.UNINITALIZED;
            td_classPasses = new LinkedList<>();
            td_methodPasses = new LinkedList<>();
            td_basicBlockPasses = new LinkedList<>();
            td_statementPasses = new LinkedList<>();
            td_expressionPasses = new LinkedList<>();

            bu_expressionPasses = new LinkedList<>();
            bu_statementPasses = new LinkedList<>();
            bu_basicBlockPasses = new LinkedList<>();
            bu_methodPasses = new LinkedList<>();
            bu_classPasses = new LinkedList<>();

            this.manager = manager;
            this.num = 0;
        }

        private void updateDepth(DEPTH d) {
            if (d == DEPTH.UNINITALIZED) return;
            if (minDepth == DEPTH.UNINITALIZED) {
                minDepth = d;
                maxDepth = d;
            }
            if (minDepth.ordinal() > d.ordinal()) {
                minDepth = d;
            }
            if (maxDepth.ordinal() < d.ordinal()) {
                maxDepth = d;
            }
        }

        private boolean overMinBound(DEPTH d) {
            return minDepth.ordinal() <= d.ordinal();
        }

        private boolean underMaxBound(DEPTH d) {
            return maxDepth.ordinal() >= d.ordinal();
        }

        private boolean inRange(DEPTH d) {
            return minDepth.ordinal() <= d.ordinal() && d.ordinal() <= maxDepth.ordinal();
        }

        void addPass(Pass pass) {
            num++;
            if (pass.getAnalysisDirection() == AnalysisDirection.TOP_DOWN) {
                if (pass instanceof ClassPass cp) {
                    td_classPasses.addLast(cp);
                    updateDepth(DEPTH.CLASS);
                }
                if (pass instanceof MethodPass mp) {
                    td_methodPasses.addLast(mp);
                    updateDepth(DEPTH.METHOD);
                }
                if (pass instanceof BasicBlockPass bbp) {
                    td_basicBlockPasses.addLast(bbp);
                    updateDepth(DEPTH.BASICBLOCK);
                }
                if (pass instanceof StatementPass sp) {
                    td_statementPasses.addLast(sp);
                    updateDepth(DEPTH.STATEMENT);
                }
                if (pass instanceof ExpressionPass ep) {
                    td_expressionPasses.addLast(ep);
                    updateDepth(DEPTH.EXPRESSION);
                }
            } else {
                if (pass instanceof ClassPass cp) {
                    bu_classPasses.addLast(cp);
                    updateDepth(DEPTH.CLASS);
                }
                if (pass instanceof MethodPass mp) {
                    bu_methodPasses.addLast(mp);
                    updateDepth(DEPTH.METHOD);
                }
                if (pass instanceof BasicBlockPass bbp) {
                    bu_basicBlockPasses.addLast(bbp);
                    updateDepth(DEPTH.BASICBLOCK);
                }
                if (pass instanceof StatementPass sp) {
                    bu_statementPasses.addLast(sp);
                    updateDepth(DEPTH.STATEMENT);
                }
                if (pass instanceof ExpressionPass ep) {
                    bu_expressionPasses.addLast(ep);
                    updateDepth(DEPTH.EXPRESSION);
                }
            }
        }

        public void printPass(PrintStream stream, String padding, String name) {
            stream.println(padding + name);
        }

        public void printStep(PrintStream stream) {
            stream.println("  TopDown:");
            for (Pass pass : td_classPasses) {
                printPass(stream, "  C - ", pass.getClass().getName());
            }
            for (Pass pass : td_methodPasses) {
                printPass(stream, "  M --- ", pass.getClass().getName());
            }
            for (Pass pass : td_basicBlockPasses) {
                printPass(stream, "  B ----- ", pass.getClass().getName());
            }
            for (Pass pass : td_statementPasses) {
                printPass(stream, "  S ------- ", pass.getClass().getName());
            }
            for (Pass pass : td_expressionPasses) {
                printPass(stream, "  E --------- ", pass.getClass().getName());
            }
            stream.println("  BottomUp:");
            for (Pass pass : bu_expressionPasses) {
                printPass(stream, "  E --------- ", pass.getClass().getName());
            }
            for (Pass pass : bu_statementPasses) {
                printPass(stream, "  S ------- ", pass.getClass().getName());
            }
            for (Pass pass : bu_basicBlockPasses) {
                printPass(stream, "  B ----- ", pass.getClass().getName());
            }
            for (Pass pass : bu_methodPasses) {
                printPass(stream, "  M --- ", pass.getClass().getName());
            }
            for (Pass pass : bu_classPasses) {
                printPass(stream, "  C - ", pass.getClass().getName());
            }
        }

        public List<Pass> getAllPasses() {
            List<Pass> all = new LinkedList<>();
            all.addAll(td_classPasses);
            all.addAll(td_methodPasses);
            all.addAll(td_basicBlockPasses);
            all.addAll(td_statementPasses);
            all.addAll(td_expressionPasses);
            all.addAll(bu_expressionPasses);
            all.addAll(bu_statementPasses);
            all.addAll(bu_basicBlockPasses);
            all.addAll(bu_methodPasses);
            all.addAll(bu_classPasses);
            return all;
        }

        //TODO check for return value and rerun passes if needed
        private void traverseExpression(Expression expression) {
            Expression old = manager.getCurrentExpression();
            manager.setCurrentExpression(expression);
            //no check needed we know it already
            for (ExpressionPass TDExpressionsPass : td_expressionPasses) {
                if (TDExpressionsPass.shouldRunOnExpression(expression)) TDExpressionsPass.runOnExpression(expression);
            }
            for (ExpressionPass BUExpressionPass : bu_expressionPasses) {
                if (BUExpressionPass.shouldRunOnExpression(expression)) BUExpressionPass.runOnExpression(expression);
            }
            manager.setCurrentExpression(old);
        }

        private void traverseStatement(Statement statement) {
            Statement old = manager.getCurrentStatement();
            manager.setCurrentStatement(statement);
            if (inRange(DEPTH.STATEMENT)) {
                for (StatementPass TDStatementPass : td_statementPasses) {
                    if (TDStatementPass.shouldRunOnStatement(statement)) TDStatementPass.runOnStatement(statement);
                }
            }

            statement.accept(this);

            if (inRange(DEPTH.STATEMENT)) {
                for (StatementPass BUStatementPass : bu_statementPasses) {
                    if (BUStatementPass.shouldRunOnStatement(statement)) BUStatementPass.runOnStatement(statement);
                }
            }
            manager.setCurrentStatement(old);
        }

        @Override
        public void visitBasicBlock(BasicBlock basicBlock) {
            traverseBasicBlock(basicBlock);
        }

        @Override
        public void visitEmptyStatement(EmptyStatement emptyStatement) {

        }

        @Override
        public void visitErrorStatement(ErrorStatement errorStatement) {

        }

        @Override
        public void visitExpressionStatement(ExpressionStatement expressionStatement) {
            if (underMaxBound(PassStep.DEPTH.EXPRESSION)) traverseExpression(expressionStatement.getExpression());
        }

        @Override
        public void visitIfStatement(IfStatement ifStatement) {
            if (underMaxBound(DEPTH.EXPRESSION)) traverseExpression(ifStatement.getCondition());
            Statement then = ifStatement.getThenStatement();

            then.accept(this);
            if (ifStatement.hasElse()) {
                Statement else_ = ifStatement.getElseStatement();
                else_.accept(this);
            }
        }

        @Override
        public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement decl) {
            if (underMaxBound(PassStep.DEPTH.EXPRESSION)) traverseExpression(decl.getExpression());
        }

        @Override
        public void visitReturnStatement(ReturnStatement returnStatement) {
            if (underMaxBound(DEPTH.EXPRESSION)) traverseExpression(returnStatement.getExpression());
        }

        @Override
        public void visitWhileStatement(WhileStatement whileStatement) {
            if (underMaxBound(DEPTH.EXPRESSION)) traverseExpression(whileStatement.getCondition());
            Statement body = whileStatement.getLoop();
            body.accept(this);
        }

        private void traverseBasicBlock(BasicBlock block) {
            BasicBlock old = manager.getCurrentBasicBlock();
            manager.setCurrentBasicBlock(block);
            if (overMinBound(DEPTH.BASICBLOCK)) {
                for (BasicBlockPass TDBasicBlockPass : td_basicBlockPasses) {
                    if (TDBasicBlockPass.checkClass(block)) TDBasicBlockPass.runOnBasicBlock(block);
                }
            }

            for (Statement statement : block.getStatements()) {
                traverseStatement(statement);
            }

            if (overMinBound(DEPTH.BASICBLOCK)) {
                for (BasicBlockPass BUBasicBlockPass : bu_basicBlockPasses) {
                    if (BUBasicBlockPass.checkClass(block)) BUBasicBlockPass.runOnBasicBlock(block);
                }
            }
            manager.setCurrentBasicBlock(old);
        }

        private void traverseMethod(Method method) {
            Method old = manager.getCurrentMethod();
            manager.setCurrentMethod(method);
            if (overMinBound(DEPTH.METHOD)) {
                for (MethodPass TDMethodPass : td_methodPasses) {
                    if (TDMethodPass.shouldRunOnMethod(method)) TDMethodPass.runOnMethod(method);
                }
            }

            if (underMaxBound(DEPTH.BASICBLOCK)) {
                traverseBasicBlock(method.getBlock());
            }
            if (overMinBound(DEPTH.METHOD)) {
                for (MethodPass BUMethodPass : bu_methodPasses) {
                    if (BUMethodPass.shouldRunOnMethod(method)) BUMethodPass.runOnMethod(method);
                }
            }
            manager.setCurrentMethod(old);
        }

        private void traverseClass(ClassDeclaration declaration) {
            ClassDeclaration old = manager.getCurrentClass();
            manager.setCurrentClassDeclaration(declaration);
            if (overMinBound(DEPTH.CLASS)) {
                for (ClassPass TDClassPass : td_classPasses) {
                    if (TDClassPass.shouldRunOnClass(declaration)) TDClassPass.runOnClass(declaration);
                }
            }

            if (underMaxBound(DEPTH.METHOD)) {
                for (ClassMember classMember : declaration.getMembers()) {
                    if (classMember instanceof Method m) {
                        traverseMethod(m);
                    }
                }
            }

            if (overMinBound(DEPTH.CLASS)) {
                for (ClassPass BUClassPass : bu_classPasses) {
                    if (BUClassPass.shouldRunOnClass(declaration)) BUClassPass.runOnClass(declaration);
                }
            }
            manager.setCurrentClassDeclaration(old);
        }

        public void traverseTree(Program program) {
            if (minDepth != DEPTH.UNINITALIZED) {
                for (ClassDeclaration declaration : program.getClasses()) {
                    traverseClass(declaration);
                }
            } else {
                new OutputMessageHandler(MessageOrigin.PASSES).printInfo("Empty Step detected, may run --print-pipeline, something may be odd.");
            }
        }
    }

    private final LinkedList<PassStep> steps;
    private int stepCount = 0;
    private final PassManager manager;

    public PassPipeline(PassManager manager) {
        steps = new LinkedList<>();
        this.manager = manager;
        nextStep();
    }

    public void addPass(Pass pass) {
        steps.getFirst().addPass(pass);
    }

    public void nextStep() {
        steps.addFirst(new PassStep(manager));
    }

    public void printPipeline(PrintStream stream) {
        stream.println("Pipeline:");
        for (int i = 0; i < steps.size(); i++) {
            stream.println("Step " + i + ":");
            steps.get(i).printStep(stream);
        }
    }

    public void compress() {
        steps.removeIf((step) -> step.num == 0);
    }

    public boolean traverseTreeStep(Program program) {
        if (steps.size() > stepCount) {
            steps.get(stepCount++).traverseTree(program);
        }
        return steps.size() > stepCount;
    }

    public void addASTReferencePass(Program program, PassManager passManager) {
        ASTReferencePass pass = new ASTReferencePass();
        ASTReferencePullPass pull = new ASTReferencePullPass();
        pull.setRefPass(pass);
        steps.getFirst().td_classPasses.addFirst(pass);
        steps.getFirst().td_methodPasses.addFirst(pass);
        steps.getFirst().td_basicBlockPasses.addFirst(pass);
        steps.getFirst().td_statementPasses.addFirst(pass);
        steps.getFirst().td_expressionPasses.addFirst(pass);
        steps.getFirst().maxDepth = PassStep.DEPTH.EXPRESSION;
        steps.getFirst().bu_basicBlockPasses.addFirst(pull);
        pass.doInitialization(program);
        pass.registerPassManager(passManager);
    }

    public int numberPasses() {
        int num = 0;
        for (PassStep step : steps) {
            num += step.num;
        }
        return num;
    }

    public int numberSteps() {
        return steps.size();
    }

    public List<Pass> getAllPassesOnlyForTesting() {
        List<Pass> passes = new LinkedList<>();
        for (PassStep step : steps) {
            passes.addAll(step.getAllPasses());
        }
        return passes;
    }

}
