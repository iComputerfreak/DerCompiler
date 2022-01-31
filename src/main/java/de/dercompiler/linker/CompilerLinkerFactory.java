package de.dercompiler.linker;

import de.dercompiler.intermediate.CodeGenerationWarningIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

public class CompilerLinkerFactory {

    public static Compiler checkIfCompilerIsValid(String compiler) {
        String base = ToolchainUtil.getBaseName(compiler);
        Compiler c;
        if (base.contains("clang")) {
            c = new Clang(compiler);
        } else if (base.contains("gcc")) {
            c = new Gcc(compiler);
        } else if (base.contains("cl")) {
            c = new MSVC_CL(compiler);
        } else {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printWarning(CodeGenerationWarningIds.UNKNOWN_TOOL, "Tool unknown: " + compiler);
            return null;
        }
        if (c.checkCompiler()) return c;
        return null;
    }

    public static Linker checkIfLinkerIsValid(String linker) {
        String base = ToolchainUtil.getBaseName(linker);
        Linker l;
        if (base.contains("ld") && !base.contains("lld")) {
            l = new GccLD(linker);
        } else if (base.contains("lld")) {
            l = new ClangLLD(linker);
        } else if (base.contains("cl")) {
            l = new MSVC_CL(linker);
        } else {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printWarning(CodeGenerationWarningIds.UNKNOWN_TOOL, "Tool unknown: " + linker);
            return null;
        }
        if (l.checkLinker()) return l;
        return null;
    }

    public static Assembler checkIfAssemblerIsValid(String assembler) {
        String base = ToolchainUtil.getBaseName(assembler);
        Assembler a;
        if (base.contains("gcc")) {
            a = new Gcc(assembler);
        } else if (base.contains("nasm")) {
            a = new NASM(assembler);
        } else {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printWarning(CodeGenerationWarningIds.UNKNOWN_TOOL, "Tool unknown: " + assembler);
            return null;
        }
        if (a.checkAssembler()) return a;
        return null;
    }
}
