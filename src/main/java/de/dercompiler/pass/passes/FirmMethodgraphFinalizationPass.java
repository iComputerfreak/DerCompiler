package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.GlobalScope;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.transformation.TransformationState;
import firm.*;
import firm.nodes.Node;

import java.util.Objects;

public class FirmMethodgraphFinalizationPass implements MethodPass, StatementPass, ExpressionPass  {

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
    public boolean runOnStatement(Statement statement) {
        if (statement instanceof LocalVariableDeclarationStatement lvds) {
            int nodeId = lvds.getNodeId();

            state.construction.setVariable(nodeId, state.res);
            state.res = null;
        }
        return false;
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        state.res = expression.createNode(state);
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
        return AnalysisDirection.TOP_DOWN;
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
