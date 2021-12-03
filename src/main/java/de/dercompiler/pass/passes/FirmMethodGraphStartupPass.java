package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
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

public class FirmMethodGraphStartupPass extends ASTLazyStatementVisitor implements MethodPass, StatementPass, BasicBlockPass {
    private TransformationState state;
    private FirmMethodGraphFinalizationPass finalization;

    @Override
    public boolean runOnMethod(Method method) {
        assert(state.stackSize() == 0);
        MethodDefinition def = state.globalScope.getMethod(method.getSurroundingClass().getIdentifier(),
                method.getIdentifier());
        //wie bekommt man den globalType??
        CompoundType globalType = firm.Program.getGlobalType();
        Entity methodEntity = new Entity(globalType, method.getMangledIdentifier(), def.getFirmType());
        int n_vars = method.getNumLocalVariables();
        System.out.println(method.getIdentifier() + " " + method.getNumLocalVariables());
        state.graph = new Graph(methodEntity, n_vars);
        state.construction = new Construction(state.graph);
        return false;
    }

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        if (block.getSurroundingStatement() instanceof BasicBlock && block != block.getSurroundingStatement()) {
            Block inside = state.construction.newBlock();
            Block after = state.construction.newBlock();

            TransformationHelper.createDirectJump(state, inside);
            state.construction.getCurrentBlock().mature();
            state.construction.setCurrentBlock(inside);
            TransformationHelper.createDirectJump(state, after);

            state.pushBlock(after);
            state.markStatementToPullBlock(block);
        }
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        statement.accept(this);
        return false;
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement lvds) {

        //if boolean create true-false-Block
        if (!lvds.getExpression().getType().isCompatibleTo(new BooleanType())) return;

        //boolean-expression
        state.trueBlock = state.construction.newBlock();
        state.falseBlock = state.construction.newBlock();
        state.markStatementToPullBlock(lvds);
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        state.markReturn();

        //if boolean create true-false-Block
        if (!returnStatement.getExpression().getType().isCompatibleTo(new BooleanType())) return;
        state.trueBlock = state.construction.newBlock();
        state.falseBlock = state.construction.newBlock();
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        state.trueBlock = state.construction.newBlock();
        state.falseBlock = state.construction.newBlock();

        if (ifStatement.hasElse()) {
            state.pushBlock(state.falseBlock);
            state.markStatementToPullBlock(ifStatement.getElseStatement());
        }

        state.pushBlock(state.trueBlock);
        state.markStatementToPullBlock(ifStatement.getThenStatement());
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        Block head = state.construction.newBlock();
        state.trueBlock = state.construction.newBlock();
        state.falseBlock = state.construction.newBlock();


        TransformationHelper.createDirectJump(state, head);
        state.construction.setCurrentBlock(head);
        //create memory phi
        state.construction.getCurrentMem();

        state.pushBlock(head);
        //push loop block
        state.pushBlock(state.trueBlock);
        //pull from stack after loop-operation(s)
        state.markStatementToPullBlock(whileStatement.getLoop());
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
