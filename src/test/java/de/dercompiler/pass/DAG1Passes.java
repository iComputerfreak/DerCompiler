package de.dercompiler.pass;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.Statement;

public class DAG1Passes {
    /*
     *  <-- Run in next Step
     *  <== Run direct after
     *
     *    ------ D <------
     *    |              |    F <----- G
     *    v              |
     *    A              D <==== E
     *    ^              |
     *    |              |    H <----- I
     *    ------ C <------
     *
     */

    public static class A_DAG1 implements ClassPass {

        public A_DAG1() {

        }

        @Override
        public boolean runOnClass(ClassDeclaration classDeclaration) {
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

    public static class B_DAG1 implements ClassPass {

        public B_DAG1() {

        }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(A_DAG1.class);
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

        @Override
        public boolean runOnClass(ClassDeclaration classDeclaration) {
            return false;
        }
    }

    public static class C_DAG1 implements ClassPass {

        public C_DAG1() { }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(A_DAG1.class);
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

        @Override
        public boolean runOnClass(ClassDeclaration classDeclaration) {
            return false;
        }
    }

    public static class D_DAG1 implements ClassPass {

        public D_DAG1() { }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(C_DAG1.class);
            usage.requireAnalysis(B_DAG1.class);
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

        @Override
        public boolean runOnClass(ClassDeclaration classDeclaration) {
            return false;
        }
    }

    public static class E_DAG1 implements ClassPass {

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(D_DAG1.class);
            usage.setDependency(DependencyType.RUN_DIRECT_AFTER);
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

        @Override
        public boolean runOnClass(ClassDeclaration classDeclaration) {
            return false;
        }
    }

    public static class F_DAG1 implements ExpressionPass {

        public F_DAG1() { }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
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

        @Override
        public boolean runOnExpression(Expression expression) {
            return false;
        }
    }

    public static class G_DAG1 implements StatementPass {

        public G_DAG1() { }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(F_DAG1.class);
            usage.setDependency(DependencyType.RUN_DIRECT_AFTER);
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

        @Override
        public boolean runOnStatement(Statement statement) {
            return false;
        }
    }

    public static class H_DAG1 implements MethodPass {

        public H_DAG1() { }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
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

        @Override
        public boolean runOnMethod(Method method) {
            return false;
        }
    }

    public static class I_DAG1 implements MethodPass {

        public I_DAG1() {}

        @Override
        public boolean runOnMethod(Method method) {
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
            usage.requireAnalysis(H_DAG1.class);
            usage.setDependency(DependencyType.RUN_DIRECT_AFTER);
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
}
