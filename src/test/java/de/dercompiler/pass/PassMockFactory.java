package de.dercompiler.pass;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.statement.Statement;

import java.util.List;
import java.util.function.Function;

public class PassMockFactory {

    public interface ClassMethodPass extends ClassPass, MethodPass {

    }

    public interface MethodStatementPass extends MethodPass, StatementPass {

    }

    public interface StatementExpressionPass extends StatementPass, ExpressionPass {

    }

    public interface RunOnXY extends Function<Void, Void> {

    }

    public static RunOnXY DoNothing = new RunOnXY() {
        @Override
        public Void apply(Void unused) {
            return null;
        }
    };

    public static Pass generateClassPass(AnalysisUsage usage, AnalysisDirection direction, RunOnXY run) {

        return new ClassPass() {
            private long id = 0;
            private final AnalysisDirection dir = direction;
            private final AnalysisUsage use = usage;
            private PassManager manager;

            @Override
            public boolean runOnClass(ClassDeclaration classDeclaration) {
                run.apply(null);
                return false;
            }

            @Override
            public void doInitialization(Program program) { }

            @Override
            public void doFinalization(Program program) { }

            @Override
            public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
                for (Class<? extends Pass> pass : use.getAnalyses()) {
                    usage.requireAnalysis(pass);
                }
                return usage;
            }

            @Override
            public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
                return usage;
            }

            @Override
            public void registerPassManager(PassManager manager) {
                this.manager = manager;
            }

            @Override
            public PassManager getPassManager() {
                return manager;
            }

            @Override
            public long registerID(long id) {
                if (this.id != 0) {
                    return (this.id = id);
                }
                return this.id;
            }

            @Override
            public long getID() {
                return id;
            }

            @Override
            public AnalysisDirection getAnalysisDirection() {
                return dir;
            }
        };
    }

    public static Pass generateClassMethodPass(AnalysisUsage usage, AnalysisDirection direction, RunOnXY classRun, RunOnXY methodRun) {
        return new ClassMethodPass() {
            private long id = 0;
            private final AnalysisDirection dir = direction;
            private final AnalysisUsage use = usage;
            private PassManager manager;

            @Override
            public boolean runOnClass(ClassDeclaration classDeclaration) {
                classRun.apply(null);
                return false;
            }

            @Override
            public boolean runOnMethod(Method method) {
                methodRun.apply(null);
                return false;
            }

            @Override
            public void doInitialization(Program program) {

            }

            @Override
            public void doFinalization(Program program) {

            }

            @Override
            public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
                for (Class<? extends Pass> pass : use.getAnalyses()) {
                    usage.requireAnalysis(pass);
                }
                return usage;
            }

            @Override
            public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
                return usage;
            }

            @Override
            public void registerPassManager(PassManager manager) {
                this.manager = manager;
            }

            @Override
            public PassManager getPassManager() {
                return manager;
            }

            @Override
            public long registerID(long id) {
                if (this.id != 0) {
                    return (this.id = id);
                }
                return this.id;
            }

            @Override
            public long getID() {
                return id;
            }

            @Override
            public AnalysisDirection getAnalysisDirection() {
                return dir;
            }
        };
    }

    public static Pass generateMethodPass(AnalysisUsage usage, AnalysisDirection direction, RunOnXY run) {
        return new MethodPass() {
            private long id = 0;
            private final AnalysisDirection dir = direction;
            private final AnalysisUsage use = usage;
            private PassManager manager;

            @Override
            public boolean runOnMethod(Method method) {
                run.apply(null);
                return false;
            }

            @Override
            public void doInitialization(Program program) {

            }

            @Override
            public void doFinalization(Program program) {

            }

            @Override
            public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
                for (Class<? extends Pass> pass : use.getAnalyses()) {
                    usage.requireAnalysis(pass);
                }
                return usage;
            }

            @Override
            public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
                return usage;
            }

            @Override
            public void registerPassManager(PassManager manager) {
                this.manager = manager;
            }

            @Override
            public PassManager getPassManager() {
                return manager;
            }

            @Override
            public long registerID(long id) {
                if (this.id != 0) {
                    return (this.id = id);
                }
                return this.id;
            }

            @Override
            public long getID() {
                return id;
            }

            @Override
            public AnalysisDirection getAnalysisDirection() {
                return dir;
            }
        };
    }

    public static Pass generateBlockPass(AnalysisUsage usage, AnalysisDirection direction, RunOnXY run) {
        return new BasicBlockPass() {
            private long id = 0;
            private final AnalysisDirection dir = direction;
            private final AnalysisUsage use = usage;
            private PassManager manager;

            @Override
            public boolean runOnBasicBlock(BasicBlock block) {
                run.apply(null);
                return false;
            }

            @Override
            public void doInitialization(Program program) {

            }

            @Override
            public void doFinalization(Program program) {

            }

            @Override
            public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
                for (Class<? extends Pass> pass : use.getAnalyses()) {
                    usage.requireAnalysis(pass);
                }
                return usage;
            }

            @Override
            public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
                return usage;
            }

            @Override
            public void registerPassManager(PassManager manager) {
                this.manager = manager;
            }

            @Override
            public PassManager getPassManager() {
                return manager;
            }

            @Override
            public long registerID(long id) {
                if (this.id != 0) {
                    return (this.id = id);
                }
                return this.id;
            }

            @Override
            public long getID() {
                return id;
            }

            @Override
            public AnalysisDirection getAnalysisDirection() {
                return dir;
            }
        };
    }

    public static Pass generateMethodStatementPass(AnalysisUsage usage, AnalysisDirection direction, RunOnXY runMethod, RunOnXY runStatement) {
        return new MethodStatementPass() {
            private long id = 0;
            private final AnalysisDirection dir = direction;
            private final AnalysisUsage use = usage;
            private PassManager manager;
            @Override
            public boolean runOnMethod(Method method) {
                runMethod.apply(null);
                return false;
            }

            @Override
            public boolean runOnStatement(Statement statement) {
                runStatement.apply(null);
                return false;
            }

            @Override
            public void doInitialization(Program program) {

            }

            @Override
            public void doFinalization(Program program) {

            }

            @Override
            public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
                for (Class<? extends Pass> pass : use.getAnalyses()) {
                    usage.requireAnalysis(pass);
                }
                return usage;
            }

            @Override
            public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
                return usage;
            }

            @Override
            public void registerPassManager(PassManager manager) {
                this.manager = manager;
            }

            @Override
            public PassManager getPassManager() {
                return manager;
            }

            @Override
            public long registerID(long id) {
                if (this.id != 0) {
                    return (this.id = id);
                }
                return this.id;
            }

            @Override
            public long getID() {
                return id;
            }

            @Override
            public AnalysisDirection getAnalysisDirection() {
                return dir;
            }
        };
    }

    public static Pass generateStatementPass(AnalysisUsage usage, AnalysisDirection direction, RunOnXY run) {
        return new StatementPass() {
            private long id = 0;
            private final AnalysisDirection dir = direction;
            private final AnalysisUsage use = usage;
            private PassManager manager;

            @Override
            public boolean runOnStatement(Statement statement) {
                run.apply(null);
                return false;
            }

            @Override
            public void doInitialization(Program program) {

            }

            @Override
            public void doFinalization(Program program) {

            }

            @Override
            public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
                for (Class<? extends Pass> pass : use.getAnalyses()) {
                    usage.requireAnalysis(pass);
                }
                return usage;
            }

            @Override
            public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
                return usage;
            }

            @Override
            public void registerPassManager(PassManager manager) {
                this.manager = manager;
            }

            @Override
            public PassManager getPassManager() {
                return manager;
            }

            @Override
            public long registerID(long id) {
                if (this.id != 0) {
                    return (this.id = id);
                }
                return this.id;
            }

            @Override
            public long getID() {
                return id;
            }

            @Override
            public AnalysisDirection getAnalysisDirection() {
                return dir;
            }
        };
    }

    public static Pass generateStatementExpressionPass(AnalysisUsage usage, AnalysisDirection direction, RunOnXY runStatement, RunOnXY runExpression) {
        return new StatementExpressionPass() {
            private long id = 0;
            private final AnalysisDirection dir = direction;
            private final AnalysisUsage use = usage;
            private PassManager manager;

            @Override
            public boolean runOnExpression(Expression expression) {
                runExpression.apply(null);
                return false;
            }

            @Override
            public boolean runOnStatement(Statement statement) {
                runStatement.apply(null);
                return false;
            }

            @Override
            public void doInitialization(Program program) {

            }

            @Override
            public void doFinalization(Program program) {

            }

            @Override
            public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
                for (Class<? extends Pass> pass : use.getAnalyses()) {
                    usage.requireAnalysis(pass);
                }
                return usage;
            }

            @Override
            public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
                return usage;
            }

            @Override
            public void registerPassManager(PassManager manager) {
                this.manager = manager;
            }

            @Override
            public PassManager getPassManager() {
                return manager;
            }

            @Override
            public long registerID(long id) {
                if (this.id != 0) {
                    return (this.id = id);
                }
                return this.id;
            }

            @Override
            public long getID() {
                return id;
            }

            @Override
            public AnalysisDirection getAnalysisDirection() {
                return dir;
            }
        };
    }

    public static Pass generateExpressionPass(AnalysisUsage usage, AnalysisDirection direction, RunOnXY run) {
        return new ExpressionPass() {
            private long id = 0;
            private final AnalysisDirection dir = direction;
            private final AnalysisUsage use = usage;
            private PassManager manager;

            @Override
            public boolean runOnExpression(Expression expression) {
                run.apply(null);
                return false;
            }

            @Override
            public void doInitialization(Program program) {

            }

            @Override
            public void doFinalization(Program program) {

            }

            @Override
            public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
                for (Class<? extends Pass> pass : use.getAnalyses()) {
                    usage.requireAnalysis(pass);
                }
                return usage;
            }

            @Override
            public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
                return usage;
            }

            @Override
            public void registerPassManager(PassManager manager) {
                this.manager = manager;
            }

            @Override
            public PassManager getPassManager() {
                return manager;
            }

            @Override
            public long registerID(long id) {
                if (this.id != 0) {
                    return (this.id = id);
                }
                return this.id;
            }

            @Override
            public long getID() {
                return id;
            }

            @Override
            public AnalysisDirection getAnalysisDirection() {
                return dir;
            }
        };
    }
}
