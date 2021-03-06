package de.dercompiler.pass;

import de.dercompiler.ast.ClassDeclaration;
import de.dercompiler.ast.Method;
import de.dercompiler.Program;
import de.dercompiler.ast.expression.Expression;
import de.dercompiler.ast.statement.BasicBlock;
import de.dercompiler.ast.statement.Statement;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PassManager {


    private static long passIDs = 1;
    private static boolean printPipeline = false;
    private final List<Pass> passes;
    private PassPipeline pipeline;
    private final Lexer lexer;
    private boolean errorMode;

    public PassManager(Lexer lex) {
        passes = new LinkedList<>();
        lexer = lex;
    }

    private void addPassAfterCheck(Pass pass) {
        passes.add(pass);
    }

    /**
     * Add a Pass to the PassManager, this assures the correct ordering to run the passes defined by the getAnalysisUsage, but in general every pass gets added to the end.
     *
     * @param pass to add to the pipeline
     */
    public void addPass(Pass pass) {
        if (Objects.isNull(pass)) new OutputMessageHandler(MessageOrigin.PASSES)
                .printWarning(PassWarningIds.NULL_AS_PASS_NOT_ALLOWED,"Something may be wrong with the compiler we tried to add a null value as Pass, please report your current setup to the Developers.");
        if (pass instanceof ClassPass cp) addPassAfterCheck(cp);
        else if (pass instanceof MethodPass mp) addPassAfterCheck(mp);
        else if (pass instanceof BasicBlockPass bbp) addPassAfterCheck(bbp);
        else if (pass instanceof StatementPass sp) addPassAfterCheck(sp);
        else if (pass instanceof ExpressionPass ep) addPassAfterCheck(ep);
        else
            new OutputMessageHandler(MessageOrigin.PASSES).internalError("can't add Pass: " + pass.getClass() + " the type is not implemented currently!");
    }

    private PassPipeline generateOrder(List<Pass> passes) {
        return PassDagSolver.solveDependencies(passes, this);
    }

    private void initializeMissingPasses() {
        HashSet<Long> ids = new HashSet<>();
        for (Pass pass : passes) {
            //set passID if not set and increment counter because id is used
            if (pass.registerID(passIDs) == passIDs) passIDs++;
            pass.registerPassManager(this);
            ids.add(pass.getID());
        }
        int size = 0;
        List<Pass> tmp = new LinkedList<>();
        while (size != passes.size()) {
            size = passes.size();
            for (Pass pass : passes) {
                List<Pass> deps = PassHelper.transform(pass.getAnalysisUsage(new AnalysisUsage()).getAnalyses(), PassHelper.AnalysisUsageToPass);
                for (Pass dep : deps) {
                    //set passID if not set and increment counter because id is used
                    if (dep.registerID(passIDs) == passIDs) passIDs++;
                    dep.registerPassManager(this);
                    if (!ids.contains(dep.getID())) {
                        ids.add(dep.getID());
                        tmp.add(dep);
                    }
                }
            }
            passes.addAll(tmp);
            tmp.clear();
        }
    }

    private void initializePasses(Program program) {
        for (Pass pass : passes) {
            pass.doInitialization(program);
        }
    }

    private void finalizePasses(Program program) {
        if (this.errorMode) return;
        for (Pass pass : passes) pass.doFinalization(program);
    }

    private void traverseTree(PassPipeline pipeline, Program program) {
        if (!program.isIndexed()) {
            pipeline.addASTReferencePass(program, this);
        }
        if (printPipeline) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(baos);
            pipeline.printPipeline(stream);
            new OutputMessageHandler(MessageOrigin.PASSES)
                    .printInfo("Pipeline:\n" + baos.toString(StandardCharsets.UTF_8));
        }
        while (true) {
            try {
                boolean changed = pipeline.traverseTreeStep(program);
                if (!changed) break;
            } catch (PassException e) {
                this.errorMode = true;
                break;
            }
        }
    }

    public List<Pass> getPassesFromUsage(Pass pass) {
        List<Pass> next;
        List<Pass> current;
        final List<Pass> tmp = new LinkedList<>();

        next =  PassHelper.transform(pass.getAnalysisUsage(new AnalysisUsage()).getAnalyses(), PassHelper.AnalysisUsageToPass);

        while (next.size() > 0) {
            current = next;
            next = new LinkedList<>();
            for (Pass cur : current) {
                tmp.add(cur);
                next.addAll(PassHelper.transform(cur.getAnalysisUsage(new AnalysisUsage()).getAnalyses(), PassHelper.AnalysisUsageToPass));
            }
        }
        List<Pass> result = new LinkedList<>(passes);
        return result.stream().filter((p) -> {
           for (Pass needed : tmp) {
               if (needed.getID() == p.getID()) return true;
           }
           return false;
        }).toList();
    }

    /**
     * Runs the pipeline of passes over the provided Program.
     *
     * @param program Program to run passes on
     */
    public void run(Program program) {
        initializeMissingPasses();
        initializePasses(program);
        pipeline = generateOrder(passes);
        pipeline.compress();
        traverseTree(pipeline, program);
        finalizePasses(program);
    }

    private ClassDeclaration cur_classDeclaration = null;
    private Method cur_method = null;
    private BasicBlock cur_basicBlock = null;
    private Statement cur_statement = null;
    private Expression cur_expression = null;

    public void setCurrentClassDeclaration(ClassDeclaration declaration) {
        cur_classDeclaration = declaration;
    }

    public void setCurrentMethod(Method method) {
        cur_method = method;
    }

    public void setCurrentBasicBlock(BasicBlock block) {
        cur_basicBlock = block;
    }

    public void setCurrentStatement(Statement statement) {
        cur_statement = statement;
    }

    public void setCurrentExpression(Expression expression) {
        cur_expression = expression;
    }

    /**
     * Returns the current Class-Declaration of the AST.
     *
     * @return the current Class-Declaration
     */
    public ClassDeclaration getCurrentClass() {
        return cur_classDeclaration;
    }

    /**
     * Returns the current Method of the AST.
     *
     * @return the current Method
     */
    public Method getCurrentMethod() {
        return cur_method;
    }

    /**
     * Returns the current BasicBlock of the AST.
     *
     * @return the current BasicBlock
     */
    public BasicBlock getCurrentBasicBlock() {
        return cur_basicBlock;
    }

    /**
     * Returns the current Statement of the AST.
     *
     * @return the current Statement
     */
    public Statement getCurrentStatement() {
        return cur_statement;
    }

    /**
     * Returns the current Expression of the AST.
     * Note, because Operations aren't nested(only for Method-calls) this might only return the current Expression we're already looking at.
     *
     * @return the current Expression
     */
    public Expression getCurrentExpression() {
        return cur_expression;
    }

    public static void setPrintPipeline(boolean print) { printPipeline = print; }

    public PassPipeline getCurrentPipeline() {
        return pipeline;
    }

    public Lexer getLexer() {
        return lexer;
    }

    public void quitOnError() {
        throw new PassException("Quit on error!");
    }

    private class PassException extends RuntimeException {
        public PassException(String s) {
            super(s);
        }
    }
}
