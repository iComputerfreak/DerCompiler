package de.dercompiler.linker;

import de.dercompiler.generation.CodeGenerationErrorIds;
import de.dercompiler.io.FileResolver;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.util.ErrorStatus;

import java.io.IOException;

public final class Gcc implements Compiler, Assembler {

    private String gcc_path;

    private String output = "-o";

    private String assemble = "-c";

    public Gcc(String tool) {
        gcc_path = tool;
    }

    @Override
    public boolean checkCompiler() {
        String testFile = ToolchainUtil.prepareTestCompile();
        String exe = ToolchainUtil.generateFileWithExtension(testFile, ToolchainUtil.getExecutableExtension());
        Runner testCompile = new Runner(gcc_path);                                              //gcc
        testCompile.append(ToolchainUtil.appendCFileExtension(testFile));                       // testFile.c
        testCompile.append(output);                                                             // -o
        testCompile.append(exe);                                                                // exe

        if (!testCompile.run()) return failCompile(testCompile);

        Runner exeProcess = new LocalProgramRunner(exe);
        if (!exeProcess.run()) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).debugPrint("failed to run ./test");
            return false;
        }
        return ToolchainUtil.checkTestCompile(exeProcess.getStdOut());
    }

    public void compileFirm(String base) {
        boolean c = false;
        String runtime;
        if (c) {
            runtime = ToolchainUtil.prepareRuntimeCompile();
        } else {
            runtime = ToolchainUtil.prepareRuntimeCppCompile();
        }
        String inputFile = ToolchainUtil.appendAssembleFileExtension(base);
        String outputFile = "a.out";
        Runner runner = new Runner(gcc_path);
        runner.append(inputFile);
        runner.append("-g");
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
            System.err.println("Gcc returned:");
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

    @Override
    public void compile(CompilerCall call) {
        Runner runner = new Runner(gcc_path);                                                   //gcc
        runner.append(call.files());                                                            // sources...
        runner.append(output);                                                                  // -o
        runner.append(call.outputFile());                                                       // exe

        if (runner.run()) return;
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndContinue(CodeGenerationErrorIds.COMPILER_ERROR, "gcc for runtime failed:");
        try {
            runner.getStdErr().transferTo(System.err);
        } catch (IOException e) {
            //nothing we can do
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Can't write to error-stream, something gone wrong");
        }
        ErrorStatus.exitProgramIfError();
    }

    @Override
    public boolean checkAssembler() {
        String testFile = ToolchainUtil.prepareTestAssembler();
        Runner runner = new Runner(gcc_path);                                               //gcc
        runner.append(assemble);                                                            // -c
        runner.append(ToolchainUtil.appendAssemblerExtension(testFile));                    // test_file
        runner.append(output);                                                              // -o
        String object = ToolchainUtil.appendObjectFileExtension(testFile);                  // out
        runner.append(object);

        if (!runner.run()) return false;

        String exe = ToolchainUtil.appendExecutableExtension(testFile);
        CompilerCall call = new CompilerCall(new String[]{ object }, exe);
        if (!ExternalToolchain.unsafeCompile(call)) return false;
        Runner testCompile = new Runner(exe);
        if (!testCompile.run()) return false;

        return ToolchainUtil.checkTestCompile(testCompile.getStdOut());
    }

    private boolean failCompile(Runner runner) {
        try {
            runner.getStdErr().transferTo(System.err);
        } catch (IOException e) {
            //nothing we can do
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Can't write to error-stream, something gone wrong");
        }
        return false;
    }

    @Override
    public void assemble(AssemblerCall call) {
        Runner runner = new Runner(gcc_path);                                               //gcc
        runner.append(assemble);                                                            // -c
        runner.append(call.filenames());                                                    // file
        runner.append(output);                                                              // -o
        runner.append(call.target());                                                       // target

        if (runner.run()) return;
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndContinue(CodeGenerationErrorIds.ASSEMBLER_ERROR, "gcc for assembler failed:");
        try {
            runner.getStdErr().transferTo(System.err);
        } catch (IOException e) {
            //nothing we can do
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printInfo("Can't write to error-stream, something gone wrong");
        }
        ErrorStatus.exitProgramIfError();
    }
}
