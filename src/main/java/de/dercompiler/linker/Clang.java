package de.dercompiler.linker;

import de.dercompiler.intermediate.CodeGenerationErrorIds;
import de.dercompiler.io.FileResolver;
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
        testCompile.append(ToolchainUtil.appendCFileExtension(testFile));
        testCompile.append(output);
        testCompile.append(exe);

        if (!testCompile.run()) return false;

        Runner exeProcess = new Runner(ToolchainUtil.makeExecutioble(exe));
        if (!exeProcess.run()) return false;

        return ToolchainUtil.checkTestCompile(exeProcess.getStdOut());
    }

    @Override
    public void compile(CompilerCall call) {
        Runner runner = new Runner(clang_path);
        runner.append(call.files());
        runner.append(output);
        runner.append(call.outputFile());

        if (runner.run()) return;
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndContinue(CodeGenerationErrorIds.COMPILER_ERROR, "clang for runtime failed: ");
        try {
            runner.getStdErr().transferTo(System.err);
        } catch (IOException e) {
            //nothing we can do
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Can't write to error-stream, something gone wrong");
        }
        ErrorStatus.exitProgramIfError();
    }

    @Override
    public void compileAndLink(String base) {
        boolean c = false;
        String runtime;
        if (c) {
            runtime = ToolchainUtil.prepareRuntimeCompile();
        } else {
            runtime = ToolchainUtil.prepareRuntimeCppCompile();
        }
        String inputFile = ToolchainUtil.appendAssemblerExtension(base);
        String outputFile = "a.out";
        Runner runner = new Runner(clang_path);
        runner.append(inputFile);
        if (c) {
            runner.append(ToolchainUtil.appendCFileExtension(runtime));
        } else {
            runner.append(ToolchainUtil.appendCppFileExtension(runtime));
            runner.append("-lstdc++");
        }
        runner.append(output);
        runner.append(outputFile);

        if (runner.run()) return;
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndContinue(CodeGenerationErrorIds.COMPILER_ERROR, "gcc for runtime failed:");
        try {
            System.err.println("clang returned:");
            runner.getStdErr().transferTo(System.err);
            System.err.println();

            FileResolver resolver = new FileResolver();
            if (!resolver.resolve(outputFile).exists()) {
                resolver.printWorkingDir();
            }
        } catch (IOException e) {
            //nothing we can do
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Can't write to error-stream, something gone wrong");
        }
        ErrorStatus.exitProgramIfError();
    }
}
