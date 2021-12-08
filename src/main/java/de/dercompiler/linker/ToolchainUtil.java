package de.dercompiler.linker;

import de.dercompiler.generation.CodeGenerationErrorIds;
import de.dercompiler.io.FileResolver;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TargetTriple;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class ToolchainUtil {

    public static String[] buildCommand(String util, String[] ...args) {
        int num_args = 1;
        for (String[] arg : args) {
            num_args += arg.length;
        }
        String[] command = new String[num_args];
        command[0] = util;
        int i = 1;
        for (String[] arg : args) {
            for (String a : arg) {
                command[i++] = a;
            }
        }
        return command;
    }

    private static final String success = "success";

    public static boolean checkTestCompile(InputStream out) {
        try {
            String res = new String(out.readAllBytes(), StandardCharsets.UTF_8);
            return res.equals(success);
        } catch (IOException e) {
            return false;
        }
    }

    public static String prepareTestCompile() {
        return null;
    }

    public static String prepareRuntimeCompile() {
        return null;
    }

    public static String[] generateFileWithExtension(String file, String extension) {
        return new String[]{appendExtensionToFilename(file, extension)};
    }

    public static String[] generateFileWithExtension(String[] files, String extension) {
        if (extension == null) return files;
        String[] res = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            res[i] = appendExtensionToFilename(files[i], extension);
        }
        return res;
    }

    private static String appendExtensionToFilename(String filename, String ext) {
        if (ext == null) return filename;
        return filename + "." + ext;
    }

    public static String getObjectFileExtension() {
        if (TargetTriple.isWindows()) return "obj";
        if (TargetTriple.isMacOS() || TargetTriple.isLinux()) return "o";
        utilsTargetError();
        return null; //We never return
    }

    public static String getAssembleExtension() {
        if (ExternalToolchain.getAssemblerStyle().isIntel()) return "asm";
        if (ExternalToolchain.getAssemblerStyle().isAtAndT()) return "S";
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.UNKNOWN_ASM_STYLE, "AssemblerStyle unknown");
        return "S";
    }

    public static String getExecutableExtension() {
        if (TargetTriple.isWindows()) return "exe";
        if (TargetTriple.isMacOS() || TargetTriple.isLinux()) return null;
        utilsTargetError();
        return null; //we never return
    }

    private static void utilsTargetError() {
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.UNKNOWN_TARGET_TRIPLE, "Unknown Target triple, we only support MacOS,Linux and Windows.");
    }
}
