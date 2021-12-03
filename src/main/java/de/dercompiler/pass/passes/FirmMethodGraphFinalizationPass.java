package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.expression.UninitializedValue;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.visitor.ASTLazyStatementVisitor;
import de.dercompiler.ast.statement.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.type.BooleanType;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;

import firm.*;
import firm.nodes.Block;
import firm.nodes.Node;

import java.util.Objects;

public class FirmMethodGraphFinalizationPass extends ASTLazyStatementVisitor implements MethodPass, BasicBlockPass, StatementPass, ExpressionPass {

    static int i = 0;

    private FirmMethodGraphStartupPass startUp;
    private TransformationState state;

    @Override
    public boolean runOnMethod(Method method) {
        if (state.noReturnYet()) {
            TransformationHelper.createReturn(state, null);
        }
        assert(state.blockStack.size() == 1);
        state.construction.finish();
        //Graph als .vcg datei erzeugen
        Dump.dumpGraph(state.graph, method.getSurroundingClass().getIdentifier() +  "#" + method.getIdentifier());
        state.clear();
        return false;
    }

    private void pullBlock() {
        assert(state.blockStack.size() >= 1);
        //skip block because we need to work on it
        if (state.blockStack.peek() != state.construction.getCurrentBlock()) {
            state.construction.getCurrentBlock().mature();
            state.construction.setCurrentBlock(state.blockStack.pop());
        }
    }


    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        //in case block == method.block
        if (block.getSurroundingStatement() != block) {
            pullBlock();
        }
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        if (TransformationHelper.isControlStructure(statement.getSurroundingStatement()) && !(statement instanceof BasicBlock)) {
            pullBlock();
        }
        statement.accept(this);
        return false;
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement lvds) {
        int nodeId = lvds.getNodeId();
        if (lvds.getExpression().getType().isCompatibleTo(new BooleanType())) {

            state.construction.setVariable(nodeId, state.construction.getVariable(nodeId, Mode.getBu()));
            pullBlock();
            return;
        }
        if (state.res != null) {
            state.construction.setVariable(nodeId, state.res);
        }
        state.res = null;
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement es) { /* do nothing */ }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        if (returnStatement.getExpression().getType().isCompatibleTo(new BooleanType())) return;
        TransformationHelper.createReturn(state, state.res);
        state.construction.getCurrentBlock().mature();
        state.res = null;
    }

    @Override
    public boolean shouldRunOnExpression(Expression expression) {
        return !(expression instanceof UninitializedValue);
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        //if boolean blocks are set already
        //this is for while, if, boolean localVariableDeclaration and boolean return-statements
        if (state.isCondition()) {
            expression.createNode(state);
            if (expression.getSurroundingStatement() instanceof LocalVariableDeclarationStatement) {
                state.construction.getCurrentBlock().mature();
                state.trueBlock.mature();
                state.falseBlock.mature();
            }
            state.trueBlock = null;
            state.falseBlock = null;
        } else {
            state.res = expression.createNode(state);
        }
        return false;
    }

    @Override
    public void doInitialization(de.dercompiler.ast.Program program) {
        if (Objects.isNull(startUp)) new OutputMessageHandler(MessageOrigin.PASSES).internalError("FirmMethodgraphFinalizationPass needs FirmMethodgraphStartupPass, gut it is not in the PassManager");
        state = startUp.getState();
        if (Objects.isNull(state)) state = new TransformationState(program.getGlobalScope());
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
