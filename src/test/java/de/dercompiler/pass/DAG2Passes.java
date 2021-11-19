package de.dercompiler.pass;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;

public class DAG2Passes {

    /*
     *  <-- Run in next Step
     *  <== Run direct after
     *
     *   A(TD) <== B(BU)
     *   ^         ^
     *   |         |
     *   C(TD) <-- D(TD) <-- G(BU)
     *                       |
     *   E(TD) <== F (TD) <---
     */

    public static class A_DAG2 implements MethodPass {

        public A_DAG2() { }

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
        public PassManager getPassManager() {
            return manager;
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

    public static class B_DAG2 implements MethodPass {

        public B_DAG2() { }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(A_DAG2.class);
            usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
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
        public PassManager getPassManager() {
            return manager;
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
        public boolean runOnMethod(Method method) {
            return false;
        }
    }

    public static class C_DAG2 implements MethodPass {

        public C_DAG2() { }
        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(A_DAG2.class);
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
        public PassManager getPassManager() {
            return manager;
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

    public static class D_DAG2 implements MethodPass {

        public D_DAG2() { }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(B_DAG2.class);
            usage.requireAnalysis(C_DAG2.class);
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
        public PassManager getPassManager() {
            return manager;
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

    public static class E_DAG2 implements MethodPass {

        public E_DAG2() { }

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
        public PassManager getPassManager() {
            return manager;
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

    public static class F_DAG2 implements MethodPass {

        public F_DAG2() { }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(E_DAG2.class);
            usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
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
        public PassManager getPassManager() {
            return manager;
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

    public static class G_DAG2 implements MethodPass {

        public G_DAG2() { }

        @Override
        public void doInitialization(Program program) {

        }

        @Override
        public void doFinalization(Program program) {

        }

        @Override
        public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
            usage.requireAnalysis(D_DAG2.class);
            usage.requireAnalysis(F_DAG2.class);
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
        public PassManager getPassManager() {
            return manager;
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
        public boolean runOnMethod(Method method) {
            return false;
        }
    }
}
