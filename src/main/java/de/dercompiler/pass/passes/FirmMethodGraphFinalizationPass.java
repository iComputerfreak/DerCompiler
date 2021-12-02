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
        state.construction.finish();
        //Graph als .vcg datei erzeugen
        Dump.dumpGraph(state.graph, method.getSurroundingClass().getIdentifier() +  "#" + method.getIdentifier());
        return false;
    }

    private void pullBlock() {
        //skip block because we need to work on it
        if (state.blockStack.peek() != state.construction.getCurrentBlock()) {
            state.construction.getCurrentBlock().mature();
        }
        state.construction.setCurrentBlock(state.blockStack.pop());
    }


    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        pullBlock();
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        statement.accept(this);
        if (TransformationHelper.isControlStructure(statement.getSurroundingStatement()) && !(statement instanceof BasicBlock)) {
            pullBlock();
        }
        return false;
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement lvds) {
        System.out.println("visited: " + lvds.getIdentifier());
        if (lvds.getExpression().getType().isCompatibleTo(new BooleanType())) return;
        System.out.println(state.res);
        int nodeId = lvds.getNodeId();
        if (state.res != null) {
            state.construction.setVariable(nodeId, state.res);
        }

        state.res = null;
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) {/* do nothing */ }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        if (returnStatement.getExpression().getType().isCompatibleTo(new BooleanType())) return;
        TransformationHelper.createReturn(state, state.res);
        state.construction.getCurrentBlock().mature();
    }

    @Override
    public boolean shouldRunOnExpression(Expression expression) {
        return !(expression instanceof UninitializedValue);
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        //if boolean blocks are set already
        if (state.isCondition()) {
            expression.createNode(state);
            state.trueBlock.mature();
            state.falseBlock.mature();
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
