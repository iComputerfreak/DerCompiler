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
        return true;
    }

    @Override
    public void compile(CompilerCall call) {
        Runner runner = new Runner(ToolchainUtil.buildCommand(clang_path, call.files(), output, new String[]{ call.outputFile() }));
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
