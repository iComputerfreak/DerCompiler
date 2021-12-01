package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.expression.UninitializedValue;
import de.dercompiler.ast.statement.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.BooleanType;
import de.dercompiler.transformation.TransformationState;
import firm.*;
import firm.nodes.Block;
import firm.nodes.Node;

import java.util.Objects;

public class FirmMethodgraphFinalizationPass implements MethodPass, StatementPass, ExpressionPass, BasicBlockPass  {

    private FirmMethodgraphStartupPass startUp;
    private TransformationState state;

    @Override
    public boolean runOnMethod(Method method) {
        state.construction.finish();
        //Graph als .vcg datei erzeugen
        Dump.dumpGraph(state.graph, method.getSurroundingClass().getIdentifier() +  "#" + method.getIdentifier());
        return false;
    }


    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        state.construction.getCurrentBlock().mature();
        Block currentBlock = state.blockStack.pop();
        state.construction.setCurrentBlock(currentBlock);
        return false;
    }
    //runOnBasicBlock
    //block.mature()
    //von stack pullen

    @Override
    public boolean runOnStatement(Statement statement) {
        //alle statements bearbeiten, result der nodes steht in state.res
        //state.res auf null setzen

        if (statement instanceof LocalVariableDeclarationStatement lvds) {
            int nodeId = lvds.getNodeId();

            state.construction.setVariable(nodeId, state.res);
            state.res = null;
        } else if (statement instanceof IfStatement ifStatement){

            Block trueBlock = state.construction.newBlock();
            Block falseBlock = state.construction.newBlock();

            Node jmp = state.construction.newJmp();
            trueBlock.addPred(jmp);
            falseBlock.addPred(jmp);

            state.construction.setCurrentBlock(trueBlock);

            state.trueB = trueBlock;
            state.falseB = falseBlock;

            //wie bringt man state.res ein?
        } else if (statement instanceof WhileStatement whileStatement){

        }
        return false;
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
        usage.requireAnalysis(FirmMethodgraphStartupPass.class);
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

    public void setStartup(FirmMethodgraphStartupPass pass) {
        if (startUp != null) return;
        this.startUp = pass;
        startUp.setFinalization(this);
    }

    public TransformationState getState() {
        return state;
    }
}
