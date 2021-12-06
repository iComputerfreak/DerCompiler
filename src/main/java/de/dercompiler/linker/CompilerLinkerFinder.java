package de.dercompiler.linker;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.generation.CodeGenerationErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TargetTriple;
import de.dercompiler.util.ErrorStatus;

import java.util.Objects;

public class CompilerLinkerFinder {

    private static void verifySetup() {
        if (Objects.isNull(ExternalToolchain.getCompiler())) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndContinue(CodeGenerationErrorIds.COMPILER_NOT_FOUND, "Compiler not found, may hand it over by Commandline-Argument.");
        }
        if (Objects.isNull(ExternalToolchain.getLinker())) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndContinue(CodeGenerationErrorIds.COMPILER_NOT_FOUND, "Linker not found, may hand it over by Commandline-Argument.");
        }
        ErrorStatus.exitProgramIfError();
    }

    public static void findCompilerAndLinker() {
        if (Objects.isNull(ExternalToolchain.getCompiler())) {
            findCompiler();
        }
        if (Objects.isNull(ExternalToolchain.getLinker())) {
            findLinker();
        }
        verifySetup();
    }

    private static void findCompiler() {
        if (TargetTriple.isWindows()) {
            findCompilerOnWindows();
        } else if (TargetTriple.isMacOS()) {
            findCompilerOnMacOS();
        } else if (TargetTriple.isLinux()) {
            findCompilerOnLinux();
        }
    }

    private static void findLinker() {
        if (TargetTriple.isWindows()) {
            findLinkerOnWindows();
        } else if (TargetTriple.isMacOS()) {
            findLinkerOnMacOS();
        } else if (TargetTriple.isLinux()) {
            findLinkerOnLinux();
        }
    }

    private static void findCompilerOnWindows() {

    }

    private static void findCompilerOnMacOS() {

    }

    private static void findCompilerOnLinux() {

    }

    private static void findLinkerOnWindows() {

    }

    private static void findLinkerOnMacOS() {

    }

    private static void findLinkerOnLinux() {

    }
}
