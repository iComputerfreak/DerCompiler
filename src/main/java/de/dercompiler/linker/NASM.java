package de.dercompiler.linker;

import de.dercompiler.intermediate.CodeGenerationErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TargetTriple;
import de.dercompiler.util.ErrorStatus;

import java.io.IOException;

public final class NASM implements Assembler {

    private String nasm_path;

    private static final String target = "-f";
    private static final String linuxTarget = "elf";
    private static final String winTarget = "win32";
    private static final String macOsTarget = "macho";

    private static final String output = "-o";

    public NASM(String nasm) {
        nasm_path = nasm;
    }

    @Override
    public boolean checkAssembler() {
        String testFile = ToolchainUtil.prepareTestAssembler();
        Runner runner = new Runner(nasm_path);                              //nasm
        if (TargetTriple.isWindows()) {
            runner.append(target);                                          // -f
            runner.append(winTarget);                                       // win32
        } else if (TargetTriple.isMacOS()) {
            runner.append(target);                                          // -f
            runner.append(macOsTarget);                                     // macho
        } else if (TargetTriple.isLinux()) {
            runner.append(target);                                          // -f
            runner.append(linuxTarget);                                     // elf
        } else {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.UNKNOWN_TARGET_TRIPLE, "We don't know which binary format we need to build.");
            return false;
        }
        runner.append(ToolchainUtil.appendAssemblerExtension(testFile));    // input
        runner.append(output);                                              // -o
        String outputFile = ToolchainUtil.appendAssembleFileExtension(testFile);
        runner.append(outputFile);                                          // output

        if (!runner.run()) return false;
        String exeFile = ToolchainUtil.appendExecutableExtension(testFile);
        LinkerCall call = new LinkerCall(new String[]{ outputFile }, exeFile);
        if (!ExternalToolchain.unsafeLink(call)) return false;
        Runner exe = new Runner(exeFile);
        if (!exe.run()) return false;
        return ToolchainUtil.checkTestCompile(exe.getStdOut());
    }

    @Override
    public void assemble(AssemblerCall call) {
        Runner runner = new Runner(nasm_path);
        if (TargetTriple.isWindows()) {
            runner.append(target);                                          // -f
            runner.append(winTarget);                                       // win32
        } else if (TargetTriple.isMacOS()) {
            runner.append(target);                                          // -f
            runner.append(macOsTarget);                                     // macho
        } else if (TargetTriple.isLinux()) {
            runner.append(target);                                          // -f
            runner.append(linuxTarget);                                     // elf
        } else {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.UNKNOWN_TARGET_TRIPLE, "We don't know which binary format we need to build.");
            return;
        }
        runner.append(call.filenames());
        runner.append(output);
        runner.append(call.target());

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
