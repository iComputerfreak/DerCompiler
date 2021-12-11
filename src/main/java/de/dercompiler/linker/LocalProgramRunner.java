package de.dercompiler.linker;

import de.dercompiler.transformation.TargetTriple;

public class LocalProgramRunner extends Runner {

    private static String buildRunnerString(String exec) {
        if (TargetTriple.isLinux() || TargetTriple.isMacOS()) {
            return "./" + exec;
        }
        return exec;
    }

    public LocalProgramRunner(String command) {
        super(buildRunnerString(command));
    }
}
