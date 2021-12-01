package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.transformation.TransformationState;
import firm.*;
import firm.nodes.Block;
import firm.nodes.Node;
import firm.nodes.Start;

import java.util.List;
import java.util.Objects;

public class FirmMethodGraphStartupPass implements MethodPass, StatementPass, BasicBlockPass {
    private TransformationState state;
    private FirmMethodGraphFinalizationPass finalization;

    @Override
    public boolean runOnMethod(Method method) {
        MethodDefinition def = state.globalScope.getMethod(method.getSurroundingClass().getIdentifier(),
                method.getIdentifier());
        //wie bekommt man den globalType??
        CompoundType globalType = firm.Program.getGlobalType();
        Entity methodEntity = new Entity(globalType, method.getIdentifier(), def.getFirmType());
        int n_vars = method.getNumLocalVariables();
        state.graph = new Graph(methodEntity, n_vars);
        state.construction = new Construction(state.graph);
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        //TODO if control structure setup blocks
        //neu blöcke erzeugen, richtig sprünge setzen/vorgänger nachfolger beachten
        //stack befüllen zum abarbeiten der blöcke, wenn es basic_blocks sind
        return false;
    }

    //runOnBasicBlock
    //basic block auf stack pushen(when sie zu keiner kontrollstruktur/method gehören

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        Block currentBlock = state.construction.newBlock();
        state.construction.setCurrentBlock(currentBlock);
        state.blockStack.push(currentBlock);
        return false;
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