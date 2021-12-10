package de.dercompiler.linker;

import de.dercompiler.generation.CodeGenerationErrorIds;
import de.dercompiler.io.FileResolver;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TargetTriple;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ToolchainUtil {

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
        String runtime = "runtime";
        String runtime_c = ToolchainUtil.appendCFileExtension(runtime);
        try {
            moveFileFromResourcesToCwd(runtime_c);
        } catch (Exception e) {
            e.printStackTrace();
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.CANT_OUTPUT_FILE, "Can't copy file " + runtime_c);
        }

        return runtime;
    }

    public static String prepareRuntimeCompile() {
        String testFile = "test";
        String testFileWithExtension = generateFileWithExtension(testFile, getCLanguageExtension());
        try {
            moveFileFromResourcesToCwd(testFileWithExtension);
        } catch (Exception e) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.CANT_OUTPUT_FILE, "Can't copy file " + testFile);
        }

        return testFile;
    }

    public static String prepareTestAssembler() {
        String testFile;
        if (TargetTriple.isWindows()) {
            if (!ExternalToolchain.getAssemblerStyle().isIntel()) {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.UNSUPPORTED_ASM_STYLE, "Only Intel is supported under Windows");
            }
            testFile = "test_win";
        } else {
            testFile = "test_unix";
        }
        String testFileWithExtension = generateFileWithExtension(testFile, getAssembleExtension());
        try {
            moveFileFromResourcesToCwd(testFileWithExtension);
        } catch (Exception e) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.CANT_OUTPUT_FILE, "Can't copy file " + testFile);
        }

        return testFile;
    }

    public static String generateFileWithExtension(String file, String extension) {
        return appendExtensionToFilename(file, extension);
    }

    public static String appendAssemblerExtension(String filename) {
        return appendExtensionToFilename(filename, ToolchainUtil.getAssembleExtension());
    }

    private static String appendExtensionToFilename(String filename, String ext) {
        if (ext == null) return filename;
        return filename + "." + ext;
    }

    public static String appendObjectFileExtension(String filename) {
        return appendExtensionToFilename(filename, getObjectFileExtension());
    }

    public static String getObjectFileExtension() {
        if (TargetTriple.isWindows()) return "obj";
        if (TargetTriple.isMacOS() || TargetTriple.isLinux()) return "o";
        utilsTargetError();
        return null; //We never return
    }

    public static String appendAssembleFileExtension(String filename) {
        return appendExtensionToFilename(filename, getAssembleExtension());
    }

    public static String getAssembleExtension() {
        if (ExternalToolchain.getAssemblerStyle().isIntel()) return "asm";
        if (ExternalToolchain.getAssemblerStyle().isAtAndT()) return "S";
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.UNKNOWN_ASM_STYLE, "AssemblerStyle unknown");
        return "S";
    }

    public static String appendExecutableExtension(String filename) {
        return appendExtensionToFilename(filename, getExecutableExtension());
    }

    public static String getExecutableExtension() {
        if (TargetTriple.isWindows()) return "exe";
        if (TargetTriple.isMacOS() || TargetTriple.isLinux()) return null;
        utilsTargetError();
        return null; //we never return
    }

    public static String appendCFileExtension(String filename) {
        return appendExtensionToFilename(filename, getCLanguageExtension());
    }

    public static String getCLanguageExtension() {
        return "c";
    }

    public static String[] filesToStrings(File[] files) {
        String[] strings = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            strings[i] = files[i].getAbsolutePath();
        }
        return strings;
    }

    private static void utilsTargetError() {
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.UNKNOWN_TARGET_TRIPLE, "Unknown Target-Triple, we only support MacOS,Linux and Windows.");
    }

    //https://stackoverflow.com/questions/10308221/how-to-copy-file-inside-jar-to-outside-the-jar
    private static String moveFileFromResourcesToCwd(String file) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder = Runner.getCwd().getAbsolutePath();
        try {
            stream = ToolchainUtil.class.getResourceAsStream(file);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + file + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(jarFolder + file);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return jarFolder + file;
    }

    public static String getBaseName(String path) {
        return new File(path).getName();
    }
}
