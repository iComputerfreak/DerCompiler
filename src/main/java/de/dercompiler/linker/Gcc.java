package de.dercompiler.linker;

import de.dercompiler.generation.CodeGenerationErrorIds;
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
        testCompile.append(assemble);                                                           // -c
        testCompile.append(ToolchainUtil.appendAssembleFileExtension(testFile));                // testFile.S
        testCompile.append(output);                                                             // -o
        testCompile.append(ToolchainUtil.appendExecutableExtension(exe));                       // exe

        if (!testCompile.run()) return false;

        Runner exeProcess = new Runner(exe);
        if (!exeProcess.run()) return false;

        return ToolchainUtil.checkTestCompile(exeProcess.getStdOut());
    }

    public void compileFirm(String base) {
        String runtime = ToolchainUtil.prepareRuntimeCompile();
        String inputFile = ToolchainUtil.generateFileWithExtension(base, "out");
        String outputFile = "a.out";
        Runner runner = new Runner(gcc_path);
        runner.append(inputFile);
        runner.append("-g");
        runner.append(ToolchainUtil.appendCFileExtension(runtime));
        runner.append(output);
        runner.append(outputFile);

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
