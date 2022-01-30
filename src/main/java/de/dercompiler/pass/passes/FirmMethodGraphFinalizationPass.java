package de.dercompiler.pass.passes;

import de.dercompiler.ast.Method;
import de.dercompiler.ast.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.expression.UninitializedValue;
import de.dercompiler.ast.statement.LocalVariableDeclarationStatement;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.visitor.ASTStatementVisitor;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.pass.*;
import de.dercompiler.semantic.MethodDefinition;
import de.dercompiler.semantic.type.VoidType;
import de.dercompiler.transformation.GraphDumper;
import de.dercompiler.transformation.TransformationHelper;
import de.dercompiler.transformation.TransformationState;
import firm.nodes.Block;

import java.util.List;
import java.util.Objects;

public class FirmMethodGraphFinalizationPass implements MethodPass, BasicBlockPass, StatementPass, ExpressionPass, ASTStatementVisitor {
    
    private FirmMethodGraphStartupPass startUp;
    private TransformationState state;
    private Program program;


    @Override
    public boolean runOnMethod(Method method) {
        MethodDefinition def = state.globalScope.getMethod(method.getSurroundingClass().getIdentifier(), method.getIdentifier());
        if (def.getType().getReturnType().isCompatibleTo(new VoidType()) && !method.getBlock().hasReturn()) {
            TransformationHelper.createReturn(state, null);
        }
        assert(state.stackSize() == 0);
        assert (state.getNumMarkedStatements() == 0);
        state.construction.finish();
        //Graph als .vcg datei erzeugen
        GraphDumper.dumpGraphFinal(state);
        program.getGraphs().add(state.graph);
        state.clear();
        return false;
    }

    @Override
    public boolean runOnBasicBlock(BasicBlock block) {
        if (block.isDead()) return false;

        if (state.removeStatementIfMarked(block)) {
            state.pullBlock();
        }
        return false;
    }

    @Override
    public boolean runOnStatement(Statement statement) {
        if (statement.isDead()) return false;

        statement.accept(this);

        if (state.removeStatementIfMarked(statement)) {
            state.pullBlock();
        }
        return false;
    }

    private void checkIfOriginUpdated() {
        Block block = state.popOrigin();

        if (state.isCondition() && !block.equals(state.construction.getCurrentBlock())) {
            boolean trueBlock = block.equals(state.trueBlock());
            boolean falseBlock = block.equals(state.falseBlock());
            if (trueBlock && falseBlock) {
                new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("Expression only in one Branch possible");
            }
            if (trueBlock) {
                state.exchangeTrueBlock(state.construction.getCurrentBlock());
            } else if (falseBlock) {
                state.exchangeFalseBlock(state.construction.getCurrentBlock());
            } else {
                new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("we are in a other Block, but we have no Branch, that's odd");
            }
        }
    }

    @Override
    public void visitLocalVariableDeclarationStatement(LocalVariableDeclarationStatement lvds) {
        checkIfOriginUpdated();

        int nodeId = lvds.getNodeId();
        if (state.res != null) {
            state.construction.setVariable(nodeId, state.res.genLoad(state));
        }
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
        checkIfOriginUpdated();

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
        } else if (ifStatement.getSurroundingStatement() == ifStatement.getSurroundingMethod().getBlock()) {
            //do nothing
        } else {
            if (ifStatement.hasElse()) {
                if (ifStatement.getSurroundingStatement() instanceof IfStatement sur && ifStatement == sur.getElseStatement()) {
                    state.exchangeFalseBlock(after);
                } else if (ifStatement.getElseStatement() instanceof IfStatement) {
                    //do nothing
                }
            } else {
                new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("unknown control-flow");
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
        boolean trueBlock = origin.equals(state.trueBlock());

        if (falseBlock) { //false
            state.exchangeFalseBlock(after);
        } else if (trueBlock) { //while and then
            state.exchangeTrueBlock(after);
        } else if (whileStatement.getSurroundingStatement() == whileStatement.getSurroundingMethod().getBlock()) {
            //do nothing
        } else {
            new OutputMessageHandler(MessageOrigin.TRANSFORM).internalError("unknown control-flow");
        }
    }

    @Override
    public boolean shouldRunOnExpression(Expression expression) {
        return !(expression instanceof UninitializedValue);
    }

    @Override
    public boolean runOnExpression(Expression expression) {
        if (expression.getSurroundingStatement().isDead()) return false;
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
        System.out.println("Initializing");
        if (Objects.isNull(startUp)) new OutputMessageHandler(MessageOrigin.PASSES).internalError("FirmMethodgraphFinalizationPass needs FirmMethodgraphStartupPass, gut it is not in the PassManager");
        state = startUp.getState();
        if (Objects.isNull(state)) state = new TransformationState(program.getGlobalScope());
        this.program = program;
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
        return usage;
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
