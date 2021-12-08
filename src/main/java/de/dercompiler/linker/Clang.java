package de.dercompiler.linker;

import de.dercompiler.generation.CodeGenerationErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.util.ErrorStatus;

import java.io.IOException;

public final class Clang implements Compiler {

    private String clang_path;

    private String[] output = new String[]{"-o"};

    public  Clang(String clang) {
        clang_path = clang;
    }

    @Override
    public boolean checkCompiler() {
        String testFile = ToolchainUtil.prepareTestCompile();
        String exe = ToolchainUtil.generateFileWithExtension(testFile, ToolchainUtil.getExecutableExtension());
        Runner testCompile = new Runner(clang_path);
        testCompile.append(testFile);
        testCompile.append(output);
        testCompile.append(exe);

        if (!testCompile.run()) return false;

        Runner exeProcess = new Runner(exe);
        if (!exeProcess.run()) return false;

        return ToolchainUtil.checkTestCompile(exeProcess.getStdOut());
    }

    @Override
    public void compile(CompilerCall call) {
        Runner runner = new Runner(clang_path);
        runner.append(call.files());
        runner.append(output);
        runner.append(call.outputFile());

        boolean success = runner.run();
        if (!success) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndContinue(CodeGenerationErrorIds.COMPILER_ERROR, "clang for runtime failed: ");
            try {
                runner.getStdErr().transferTo(System.err);
            } catch (IOException e) {
                //nothing we can do
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Can't write to error-stream, something gone wrong");
                ErrorStatus.exitProgramIfError();
            }
        }
    }
}
