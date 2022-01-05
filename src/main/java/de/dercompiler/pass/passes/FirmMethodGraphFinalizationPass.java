package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.expression.UninitializedValue;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.visitor.ASTLazyStatementVisitor;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.optimization.ArithmeticOptimization;
import de.dercompiler.optimization.GraphOptimization;
import de.dercompiler.optimization.PhiOptimization;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.BooleanType;
import de.dercompiler.semantic.type.VoidType;
import de.dercompiler.transformation.GraphDumper;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;

import firm.nodes.Block;

import java.util.List;
import java.util.Objects;

public class FirmMethodGraphFinalizationPass implements MethodPass, BasicBlockPass, StatementPass, ExpressionPass, ASTStatementVisitor {

    static int i = 0;

    private FirmMethodGraphStartupPass startUp;
    private TransformationState state;
    private List<GraphOptimization> opts;


    @Override
    public boolean runOnMethod(Method method) {
        MethodDefinition def = state.globalScope.getMethod(method.getSurroundingClass().getIdentifier(), method.getIdentifier());
        if (def.getType().getReturnType().isCompatibleTo(new VoidType()) && !method.getBlock().lastIsReturn()) {
            TransformationHelper.createReturn(state, null);
        }
        assert(state.stackSize() == 0);
        assert (state.getNumMarkedStatements() == 0);
        state.construction.finish();
        //Graph als .vcg datei erzeugen
        GraphDumper.dumpGraphFinal(state);
        state.clear();
        return false;
    }

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        if (state.removeStatementIfMarked(block)) {
            state.pullBlock();
        }
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        statement.accept(this);
        if (state.removeStatementIfMarked(statement)) {
            state.pullBlock();
        }
        return false;
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement lvds) {
        int nodeId = lvds.getNodeId();
        state.construction.setVariable(nodeId, state.res.genLoad(state));
        state.popExpect();
        state.res = null;
    }

    @Override
    public void visitBasicBlock(BasicBlock basicBlock) {
        //do nothing
    }

    @Override
    public void visitEmptyStatement(EmptyStatement emptyStatement) {
        // do nothing
    }

    @Override
    public void visitErrorStatement(ErrorStatement errorStatement) {
        //do nothing
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) {
        state.res = null;
        state.popExpect();
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        if (state.res != null) {
            TransformationHelper.createReturn(state, state.res.genLoad(state));
        } else {
            TransformationHelper.createReturn(state, null);
        }
        state.res = null;
        state.popExpect();
        state.markReturn();
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        Block origin = state.popOrigin();
        Block after;
        if (ifStatement.hasElse()) {
            after = state.construction.newBlock();
        } else {
            after = state.falseBlock();
        }

        state.trueBlock().mature();
        if (!state.hasReturned(state.trueBlock())) {
            state.construction.setCurrentBlock(state.trueBlock());
            TransformationHelper.createDirectJump(state, after);
        }

        if (ifStatement.hasElse()) {
            state.falseBlock().mature();
            if (!state.hasReturned(state.falseBlock())) {
                state.construction.setCurrentBlock(state.falseBlock());
                TransformationHelper.createDirectJump(state, after);
            }
        }

        origin.mature();
        state.popBranches();
        state.construction.setCurrentBlock(after);

        boolean falseBlock = origin.equals(state.falseBlock());
        boolean trueBlock = origin.equals(state.trueBlock());

        if (falseBlock) { //after
            state.exchangeFalseBlock(after);
        } else if (trueBlock) { //true
            state.exchangeTrueBlock(after);
        } else {
            if (ifStatement.hasElse() && ifStatement.getSurroundingStatement() instanceof IfStatement sur && ifStatement == sur.getElseStatement()) {
                state.exchangeFalseBlock(after);
            } else if (ifStatement.hasElse() && ifStatement.getElseStatement() instanceof IfStatement) {
                //do nothing
            } else {
                new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("this shouldn't be possible");
            }
        }
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        Block origin = state.popOrigin();
        //head must be current
        Block head = state.popHead();
        if (!state.hasReturned(state.trueBlock())) {
            state.construction.setCurrentBlock(state.trueBlock());
            TransformationHelper.createDirectJump(state, head);
        }
        origin.mature();
        state.construction.setCurrentBlock(state.falseBlock());

        Block after = state.falseBlock();

        state.popBranches();

        boolean falseBlock = origin.equals(state.falseBlock());

        if (falseBlock) { //false
            state.exchangeFalseBlock(after);
        } else { //while and then
            state.exchangeTrueBlock(after);
        }
    }

    @Override
    public boolean shouldRunOnExpression(Expression expression) {
        return !(expression instanceof UninitializedValue);
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        //if boolean blocks are set already
        //this is for while, if, boolean localVariableDeclaration and boolean return-statements
        state.res = expression.createNode(state);
        if (!state.isCondition()) return false;
        if (expression.getSurroundingStatement() instanceof WhileStatement) {
            state.trueBlock().mature();
            state.falseBlock().mature();
        }
        if (state.removeExpressionIfMarked(expression)) {
            // while -> set current block to loop block
            // if -> set current block to then block
            state.pullBlock();
        }

        return false;
    }

    @Override
    public void doInitialization(de.dercompiler.ast.Program program) {
        if (Objects.isNull(startUp)) new OutputMessageHandler(MessageOrigin.PASSES).internalError("FirmMethodgraphFinalizationPass needs FirmMethodgraphStartupPass, gut it is not in the PassManager");
        state = startUp.getState();
        if (Objects.isNull(state)) state = new TransformationState(program.getGlobalScope());
        this.opts = List.of(new ArithmeticOptimization(), new PhiOptimization());
    }

    @Override
    public void doFinalization(Program program) {

    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(FirmMethodGraphStartupPass.class);
        usage.setDependency(DependencyType.RUN_DIRECTLY_AFTER);
        return usage;
    }

    @Override
    public AnalysisUsage invalidatesAnalysis(AnalysisUsage usage) {
        return null;
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

    public void setStartup(FirmMethodGraphStartupPass pass) {
        if (startUp != null) return;
        this.startUp = pass;
        startUp.setFinalization(this);
    }

    public TransformationState getState() {
        return state;
    }
}
