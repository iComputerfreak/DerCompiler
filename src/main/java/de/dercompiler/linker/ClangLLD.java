package de.dercompiler.linker;

import de.dercompiler.intermediate.CodeGenerationErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TargetTriple;

import java.io.File;

public final class ClangLLD implements Linker {

    private static String lld_path;

    private static String flavor = "-flavor";
    private static String GNU = "gnu";
    private static final String link = "link";
    private static final String darwin = "darwin";

    private static final String arch = "-arch";
    private static final String x86 = "x86_64";
    private static final String platform_version = "-platform_version";
    private static final String macos = "macos";
    private static final String macos_version = "10.13.0";

    private static final String output = "-o";
    private static final String out_win = "/out:";

    public ClangLLD(String lld) {
        lld_path = lld;
    }

    public void prepareLinux(Runner runner, String[] input, String target) {
        runner.append(input);                                       // input
        runner.append(output);                                      // -o
        runner.append(target);                                      // target
    }

    public void prepareMacOS(Runner runner, String[] input, String target) {
        runner.append(arch);                                        // -arch
        runner.append(x86);                                         // x86_64
        runner.append(platform_version);                            // -platform_version
        runner.append(macos);                                       // macos
        runner.append(macos_version);                               // 10.13.0 : may-update or get from commandline?
        runner.append(macos_version);                               // 10.13.0 because we built to no sdk we need to pass it twice
        runner.append(input);                                       // input
        runner.append(output);                                      // -o
        runner.append(target);                                      // target
    }

    public void prepareWindows(Runner runner, String[] input, String target) {
        runner.append(input);                                       // input
        runner.append(out_win + target);                            // /out:target
    }

    public boolean prepareLinkerCommand(Runner runner, String[] input, String target) {
        String lldName = new File(lld_path).getName();
        switch (lldName) {
            case "ld.lld": {
                if (!TargetTriple.isLinux()) return false;
                prepareLinux(runner, input, target);
            }
            break;
            case "ld64.lld": {
                if (!TargetTriple.isMacOS()) return false;
                prepareMacOS(runner, input, target);
            }
            break;
            case "lld-link": {
                if (!TargetTriple.isWindows()) return false;
                prepareWindows(runner, input, target);
            }
            break;
            case "lld": {
                boolean breakSwitch = true;
                if (TargetTriple.isWindows()) {
                    runner.append(flavor);                                      // -flavor
                    runner.append(link);                                        // link
                    prepareWindows(runner, input, target);
                } else if (TargetTriple.isLinux()) {
                    runner.append(flavor);                                      // -flavor
                    runner.append(GNU);                                         // gnu
                    prepareLinux(runner, input, target);
                } else if (TargetTriple.isMacOS()) {
                    runner.append(flavor);                                      // -flavor
                    runner.append(darwin);                                      // darwin
                    prepareMacOS(runner, input, target);
                } else { //errorcase
                    breakSwitch = false;
                }
                if (breakSwitch) {
                    break;
                }
            }
            default: {
                new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.LINKER_ERROR, "unsupported linker flavor for lld");
            }
        }

        return true;
    }

    @Override
    public boolean checkLinker() {
        String test = ToolchainUtil.prepareTestCompile();

        String object = ToolchainUtil.appendObjectFileExtension(test);
        String exe = ToolchainUtil.appendExecutableExtension(test);
        CompilerCall call = new CompilerCall(new String[]{ ToolchainUtil.appendCFileExtension(test)}, object);
        if (!ExternalToolchain.unsafeCompile(call)) return false;

        Runner runner = new Runner(lld_path);
        if (!prepareLinkerCommand(runner, new String[]{ object }, exe)) return false;
        if (!runner.run()) return false;

        Runner exeProgram = new Runner(exe);
        if (!exeProgram.run()) return false;
        return ToolchainUtil.checkTestCompile(exeProgram.getStdOut());
    }

    @Override
    public void link(LinkerCall call) {
        Runner runner = new Runner(lld_path);
        prepareLinkerCommand(runner, call.objectFiles(), call.targetName());
        runner.run();
    }
}
