package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.visitor.ASTLazyStatementVisitor;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.BooleanType;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.*;
import firm.nodes.Block;

import java.util.Objects;

public class FirmMethodGraphStartupPass implements MethodPass, StatementPass, ASTStatementVisitor {
    private TransformationState state;
    private FirmMethodGraphFinalizationPass finalization;

    @Override
    public boolean runOnMethod(Method method) {
        assert(state.stackSize() == 0);
        MethodDefinition def = state.globalScope.getMethod(method.getSurroundingClass().getIdentifier(),
                method.getIdentifier());
        CompoundType globalType = firm.Program.getGlobalType();
        Entity methodEntity = new Entity(globalType, method.getMangledIdentifier(), def.getFirmType());

        int n_vars = method.getNumLocalVariables();
        
        state.graph = new Graph(methodEntity, n_vars);
        state.construction = new Construction(state.graph);
        state.currentClass = state.globalScope.getClass(method.getSurroundingClass().getIdentifier());

        //store all parameters in local vars
        if (!method.isStatic()) {
            state.construction.setVariable(0,
                    state.construction.newProj(state.graph.getArgs(),
                            state.globalScope.getClass(method.getSurroundingClass().getIdentifier()).getFirmTransformationType().getMode(),
                            0));
            for (Parameter p : method.getParameters()) {
                state.construction.setVariable(p.getNodeId(),
                        state.construction.newProj(state.graph.getArgs(), p.getRefType().getFirmTransformationType().getMode(), p.getNodeId()));
            }
        }
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        if (statement.isDead()) return false;

        statement.accept(this);
        return false;
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement lvds) {
        state.pushOrigin(state.construction.getCurrentBlock());
        state.pushExpectValue();
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        state.pushExpectValue();
    }

    @Override
    public void visitBasicBlock(BasicBlock basicBlock) {
        //do nothing
    }

    @Override
    public void visitEmptyStatement(EmptyStatement emptyStatement) {
        //do nothing
    }

    @Override
    public void visitErrorStatement(ErrorStatement errorStatement) {
        new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("We can't create Code, we found a ErrorStatement, we shouldn't get even to this point of execution");
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) {
        state.pushOrigin(state.construction.getCurrentBlock());
        state.pushExpectValue();
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        state.pushOrigin(state.construction.getCurrentBlock());

        state.pushBranches(state.construction.newBlock(), state.construction.newBlock());

        state.markExpressionToPullAfter(ifStatement.getCondition());
        state.pushBlock(state.trueBlock());

        if (ifStatement.hasElse()) {
            state.pushBlock(state.falseBlock());
            state.markStatementToPullBlock(ifStatement.getElseStatement());
        }

        state.pushBlock(state.trueBlock());
        state.markStatementToPullBlock(ifStatement.getThenStatement());
        state.pushExpectBranch();
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        state.pushOrigin(state.construction.getCurrentBlock());
        Block head = state.construction.newBlock();
        state.pushHead(head);
        state.pushBranches(state.construction.newBlock(), state.construction.newBlock());

        state.markExpressionToPullAfter(whileStatement.getCondition());
        state.pushBlock(state.trueBlock());

        TransformationHelper.createDirectJump(state, head);
        state.construction.setCurrentBlock(head);
        //create memory phi
        state.construction.getCurrentMem();

        state.pushBlock(head);
        //push loop block
        state.pushBlock(state.trueBlock());
        //pull from stack after loop-operation(s)
        state.markStatementToPullBlock(whileStatement.getLoop());
        state.pushExpectBranch();
    }

    @Override
    public void doInitialization(Program program) {
        if (Objects.isNull(finalization)) new OutputMessageHandler(MessageOrigin.PASSES).internalError("FirmMethodgraphStartupPass needs FirmMethodgraphFinalizationPass, gut it is not in the PassManager");
        state = finalization.getState();
        if (Objects.isNull(state)) state = new TransformationState(program.getGlobalScope());
    }

    @Override
    public void doFinalization(Program program) {

    }

    @Override
    public AnalysisUsage getAnalysisUsage(AnalysisUsage usage) {
        usage.requireAnalysis(FirmTypePass.class);
        usage.requireAnalysis(DeadCodeEliminationPass.class);
        usage.setDependency(DependencyType.RUN_IN_NEXT_STEP);
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
        return AnalysisDirection.TOP_DOWN;
    }

    public void setFinalization(FirmMethodGraphFinalizationPass pass) {
        if (finalization != null) return;
        this.finalization = pass;
        finalization.setStartup(this);
    }

    public TransformationState getState() {
        return state;
    }
}
