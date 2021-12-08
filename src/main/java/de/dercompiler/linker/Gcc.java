package de.dercompiler.linker;

import de.dercompiler.generation.CodeGenerationErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.util.ErrorStatus;

import java.io.IOException;

public final class Gcc implements Compiler, Assambler {

    private String gcc_path;

    private String[] output = new String[]{"-o"};

    private String[] assemble = new String[]{"-c"};

    @Override
    public boolean checkCompiler() {
        String testfilename = ToolchainUtil.prepareTestCompile();
        String[] exe = ToolchainUtil.generateFileWithExtension(testfilename, ToolchainUtil.getExecutableExtension());
        Runner testCompile = new Runner(ToolchainUtil.buildCommand(gcc_path, assemble, new String[]{ testfilename }, output , exe));
        if (!testCompile.run()) return false;

        Runner exeProcess = new Runner(ToolchainUtil.buildCommand(exe[0]));
        if (!exeProcess.run()) return false;

        return ToolchainUtil.checkTestCompile(exeProcess.getStdOut());
    }

    @Override
    public void compile(CompilerCall call) {
        Runner runner = new Runner(ToolchainUtil.buildCommand(gcc_path, call.files(), output, new String[]{ call.outputFile() }));
        boolean success = runner.run();
        if (!success) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndContinue(CodeGenerationErrorIds.COMPILER_ERROR, "gcc for runtime failed:");
            try {
                runner.getStdErr().transferTo(System.err);
            } catch (IOException e) {
                //nothing we can do
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Can't write to error-stream, something gone wrong");
                ErrorStatus.exitProgramIfError();
            }
        }
    }

    @Override
    public boolean checkAssembler() {

        return false;
    }

    @Override
    public void assemble(AssamblerCall call) {

    }
}
