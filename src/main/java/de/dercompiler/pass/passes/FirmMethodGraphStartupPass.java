package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Parameter;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.*;
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
import firm.nodes.Node;
import firm.nodes.Start;

import java.util.List;
import java.util.Objects;

public class FirmMethodGraphStartupPass implements MethodPass, StatementPass, BasicBlockPass, ASTStatementVisitor {
    private TransformationState state;
    private FirmMethodGraphFinalizationPass finalization;

    @Override
    public boolean runOnMethod(Method method) {
        assert(state.blockStack.size() == 0);
        MethodDefinition def = state.globalScope.getMethod(method.getSurroundingClass().getIdentifier(),
                method.getIdentifier());
        //wie bekommt man den globalType??
        CompoundType globalType = firm.Program.getGlobalType();
        Entity methodEntity = new Entity(globalType, method.getMangledIdentifier(), def.getFirmType());
        int n_vars = method.getNumLocalVariables();
        System.out.println(method.getIdentifier() + " " + method.getNumLocalVariables());
        state.graph = new Graph(methodEntity, n_vars);
        state.construction = new Construction(state.graph);
        //push so we set current block not mature
        state.blockStack.push(state.construction.getCurrentBlock());
        return false;
    }

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        if (block.getSurroundingStatement() instanceof BasicBlock) {
            Block b = state.construction.newBlock();
            TransformationHelper.createDirectJump(state, b);
            state.construction.getCurrentBlock().mature();
            state.construction.setCurrentBlock(b);
        }
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        statement.accept(this);
        return false;
    }

    @Override
    public void visitBasicBlock(BasicBlock basicBlock) { /* do nothing */ }

    @Override
    public void visitEmptyStatement(EmptyStatement emptyStatement) {/* do nothing */ }

    @Override
    public void visitErrorStatement(ErrorStatement errorStatement) {/* do nothing */ }

    @Override
    public void visitExpressionStatement(ExpressionStatement expressionStatement) {
        state.res = null;
    }

    @Override
    public void visitIfStatement(IfStatement ifStatement) {
        Block after = state.construction.newBlock();
        Block then = state.construction.newBlock();
        Block current = state.construction.getCurrentBlock();
        state.construction.setCurrentBlock(then);
        TransformationHelper.createDirectJump(state, after);
        state.trueBlock = then;
        state.falseBlock = after;
        if (ifStatement.hasElse()) {
            Block elseB = state.construction.newBlock();
            state.construction.setCurrentBlock(elseB);
            TransformationHelper.createDirectJump(state, after);
            state.blockStack.push(elseB);
        }
        state.blockStack.push(then);
        state.construction.setCurrentBlock(current);
    }


    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement lvds) {
        if (lvds.getExpression().getType().isCompatibleTo(new BooleanType())) {
            int nodeId = lvds.getNodeId();
            state.construction.getCurrentBlock().mature();
            Block after = state.construction.newBlock();
            Block trueB = state.construction.newBlock();
            Block falseB = state.construction.newBlock();
            state.trueBlock = trueB;
            state.falseBlock = falseB;
            state.construction.setCurrentBlock(trueB);
            state.construction.setVariable(nodeId, TransformationHelper.createBooleanNode(state, true));
            TransformationHelper.createDirectJump(state, after);
            state.construction.setCurrentBlock(falseB);
            state.construction.setVariable(nodeId, TransformationHelper.createBooleanNode(state, false));
            TransformationHelper.createDirectJump(state, after);
            state.construction.setCurrentBlock(after);
        }
        /* do nothing */
    }

    @Override
    public void visitReturnStatement(ReturnStatement returnStatement) {
        //if boolean create to returns
        if (returnStatement.getExpression().getType().isCompatibleTo(new BooleanType())) {
            Block current = state.construction.getCurrentBlock();
            Block trueB = state.construction.newBlock();
            state.construction.setCurrentBlock(trueB);
            TransformationHelper.createReturn(state, TransformationHelper.createBooleanNode(state, true));
            Block falseB = state.construction.newBlock();
            state.construction.setCurrentBlock(falseB);
            TransformationHelper.createReturn(state, TransformationHelper.createBooleanNode(state, false));
            state.construction.setCurrentBlock(current);
            state.trueBlock = trueB;
            state.falseBlock = falseB;
        }
    }

    @Override
    public void visitWhileStatement(WhileStatement whileStatement) {
        Block head = state.construction.newBlock();
        Block loop = state.construction.newBlock();
        Block after = state.construction.newBlock();

        TransformationHelper.createDirectJump(state, head);
        state.construction.getCurrentBlock().mature();
        state.construction.setCurrentBlock(loop);
        TransformationHelper.createDirectJump(state, head);
        state.construction.setCurrentBlock(head);
        state.construction.getCurrentMem();
        state.trueBlock = loop;
        state.falseBlock = after;
        state.blockStack.push(after);
        state.blockStack.push(loop);
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
