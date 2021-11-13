package de.dercompiler.pass;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.ClassMember;
import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.*;
import de.dercompiler.pass.passes.ASTReferencePass;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;

class PassPipeline {

    static class PassSteps {

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

        public PassSteps(PassManager manager) {
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
        }

        void addPass(Pass pass) {
            if (pass.getAnalysisDirection() == AnalysisDirection.TOP_DOWN) {
                if (pass instanceof ClassPass cp) {
                    td_classPasses.addLast(cp);
                }
                if (pass instanceof MethodPass mp) {
                    td_methodPasses.addLast(mp);
                }
                if (pass instanceof BasicBlockPass bbp) {
                    td_basicBlockPasses.addLast(bbp);
                }
                if (pass instanceof StatementPass sp) {
                    td_statementPasses.addLast(sp);
                }
                if (pass instanceof ExpressionPass ep) {
                    td_expressionPasses.addLast(ep);
                }
            } else {
                if (pass instanceof ClassPass cp) {
                    bu_classPasses.addFirst(cp);
                }
                if (pass instanceof MethodPass mp) {
                    bu_methodPasses.addFirst(mp);
                }
                if (pass instanceof BasicBlockPass bbp) {
                    bu_basicBlockPasses.addLast(bbp);
                }
                if (pass instanceof StatementPass sp) {
                    bu_statementPasses.addFirst(sp);
                }
                if (pass instanceof ExpressionPass ep) {
                    bu_expressionPasses.addFirst(ep);
                }
            }
        }

        public void printPass(PrintStream stream, String padding, String name) {
            stream.println(padding + name);
        }

        public void printStep(PrintStream stream) {
            stream.println("  TopDown:\n");
            for (Pass pass : td_classPasses) {
                printPass(stream, "    ", pass.getClass().getName());
            }
            for (Pass pass : td_methodPasses) {
                printPass(stream, "      ", pass.getClass().getName());
            }
            for (Pass pass : td_basicBlockPasses) {
                printPass(stream, "        ", pass.getClass().getName());
            }
            for (Pass pass : td_statementPasses) {
                printPass(stream, "          ", pass.getClass().getName());
            }
            for (Pass pass : td_expressionPasses) {
                printPass(stream, "            ", pass.getClass().getName());
            }
            stream.println("  BottomUp:\n");
            for (Pass pass : bu_expressionPasses) {
                printPass(stream, "            ", pass.getClass().getName());
            }
            for (Pass pass : bu_statementPasses) {
                printPass(stream, "          ", pass.getClass().getName());
            }
            for (Pass pass : bu_basicBlockPasses) {
                printPass(stream, "        ", pass.getClass().getName());
            }
            for (Pass pass : bu_methodPasses) {
                printPass(stream, "      ", pass.getClass().getName());
            }
            for (Pass pass : bu_classPasses) {
                printPass(stream, "    ", pass.getClass().getName());
            }
        }

        //TODO check for return value and rerun passes if needed
        private void traverseExpression(Expression expression) {
            Expression old = manager.getCurrentExpression();
            manager.setCurrentExpression(expression);
            for (ExpressionPass TDExpressionsPass : td_expressionPasses) {
                if (TDExpressionsPass.checkExpression(expression)) TDExpressionsPass.runOnExpression(expression);
            }
            for (ExpressionPass BUExpressionPass : bu_expressionPasses) {
                if (BUExpressionPass.checkExpression(expression)) BUExpressionPass.runOnExpression(expression);
            }
            manager.setCurrentExpression(old);
        }

        private void traverseStatement(Statement statement) {
            Statement old = manager.getCurrentStatement();
            manager.setCurrentStatement(statement);
            for (StatementPass TDStatementPass : td_statementPasses) {
                if (TDStatementPass.checkStatement(statement)) TDStatementPass.runOnStatement(statement);
            }

            if (statement instanceof ExpressionStatement es) {
                traverseExpression(es.getExpression());
            } else if (statement instanceof LocalVariableDeclarationStatement lvds) {
                traverseExpression(lvds.getExpression());
            } else if (statement instanceof IfStatement ifs) {
                traverseExpression(ifs.getCondition());
                Statement then = ifs.getThenStatement();
                if (then instanceof BasicBlock bb) {
                    traverseBasicBlock(bb);
                } else {
                    traverseStatement(then);
                }
                if (ifs.hasElse()) {
                    Statement else_ = ifs.getElseStatement();
                    if (else_ instanceof BasicBlock bb) {
                        traverseBasicBlock(bb);
                    } else {
                        traverseStatement(else_);
                    }
                }
            } else if (statement instanceof WhileStatement ws) {
                traverseExpression(ws.getCondition());
                Statement body = ws.getStatement();
                if (body instanceof BasicBlock bb) {
                    traverseBasicBlock(bb);
                } else {
                    traverseStatement(body);
                }
            } else if (statement instanceof ReturnStatement rs) {
                traverseExpression(rs.getExpression());
            }

            for (StatementPass BUStatementPass : bu_statementPasses) {
                if (BUStatementPass.checkStatement(statement)) BUStatementPass.runOnStatement(statement);
            }
            manager.setCurrentStatement(old);
        }

        private void traverseBasicBlock(BasicBlock block) {
            BasicBlock old = manager.getCurrentBasicBlock();
            manager.setCurrentBasicBlock(block);
            for (BasicBlockPass TDBasicBlockPass : td_basicBlockPasses) {
                if (TDBasicBlockPass.checkClass(block)) TDBasicBlockPass.runOnBasicBlock(block);
            }

            for (Statement statement : block.getStatements()) {
                if (statement instanceof BasicBlock bb) {
                    traverseBasicBlock(bb);
                } else {
                    traverseStatement(statement);
                }
            }

            for (BasicBlockPass BUBasicBlockPass : bu_basicBlockPasses) {
                if (BUBasicBlockPass.checkClass(block)) BUBasicBlockPass.runOnBasicBlock(block);
            }
            manager.setCurrentBasicBlock(old);
        }

        private void traverseMethod(Method method) {
            Method old = manager.getCurrentMethod();
            manager.setCurrentMethod(method);
            for (MethodPass TDMethodPass : td_methodPasses) {
                if (TDMethodPass.checkMethod(method)) TDMethodPass.runOnMethod(method);
            }

            traverseBasicBlock(method.getBlock());

            for (MethodPass BUMethodPass : bu_methodPasses) {
                if (BUMethodPass.checkMethod(method)) BUMethodPass.runOnMethod(method);
            }
            manager.setCurrentMethod(old);
        }

        private void traverseClass(ClassDeclaration declaration) {
            ClassDeclaration old = manager.getCurrentClass();
            manager.setCurrentClassDeclaration(declaration);
            for (ClassPass TDClassPass : td_classPasses) {
                if (TDClassPass.checkClass(declaration)) TDClassPass.runOnClass(declaration);
            }

            for (ClassMember classMember : declaration.getMembers()) {
                if (classMember instanceof Method m) {
                    traverseMethod(m);
                }
            }

            for (ClassPass BUClassPass : bu_classPasses) {
                if (BUClassPass.checkClass(declaration)) BUClassPass.runOnClass(declaration);
            }
            manager.setCurrentClassDeclaration(old);
        }

        public void traverseTree(Program program) {
            for (ClassDeclaration declaration : program.getClasses()) {
                traverseClass(declaration);
            }
        }
    }

    private final ArrayList<PassSteps> steps;
    private int stepCount = 0;
    private final PassManager manager;

    public PassPipeline(PassManager manager) {
        steps = new ArrayList<>();
        this.manager = manager;
        nextStep();
    }

    public void addPass(Pass pass) {
        steps.get(0).addPass(pass);
    }

    public void nextStep() {
        steps.add(new PassSteps(manager));
    }

    public void printPipeline(PrintStream stream) {
        stream.println("Pipeline:");
        for (int i = 0; i < steps.size(); i++) {
            stream.println("Step " + i + ":");
            steps.get(i).printStep(stream);
        }
    }

    public boolean traverseTreeStep(Program program) {

        if (steps.size() > stepCount) {
            steps.get(stepCount++).traverseTree(program);
        }
        return steps.size() > stepCount;
    }

    public void addASTReferencePass() {
        ASTReferencePass pass = new ASTReferencePass();
        steps.get(0).td_methodPasses.addFirst(pass);
        steps.get(0).td_basicBlockPasses.addFirst(pass);
        steps.get(0).td_statementPasses.addFirst(pass);
        steps.get(0).td_expressionPasses.addFirst(pass);
    }

}
