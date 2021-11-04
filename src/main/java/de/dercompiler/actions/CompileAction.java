package de.dercompiler.actions;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.general.GeneralWarningIds;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.io.File;

/**
 * Represents the action to compile the given source code
 */
public class CompileAction extends Action {
    
    private static final String compilerName = "DerCompiler";
    
    // The input file containing the source code to compile
    private final File input;

    /**
     * Creates a new CompileAction with the given source code file
     * @param input The file containing the MiniJava source code
     */
    public CompileAction(File input) {
        this.input = input;
    }

    public void run() {
        new OutputMessageHandler(MessageOrigin.GENERAL)
            .printInfo("Compiler not Implemented YET!");
        for (MessageOrigin origin : MessageOrigin.values()) {
            new OutputMessageHandler(origin)
                    .printInfo(origin.name());
        }
        System.out.println();
        new OutputMessageHandler(MessageOrigin.GENERAL)
                .printWarning(GeneralWarningIds.INVALID_COMMAND_LINE_ARGUMENTS, "warning");
        new OutputMessageHandler(MessageOrigin.GENERAL)
                .printErrorAndContinue(GeneralErrorIds.INVALID_COMMAND_LINE_ARGUMENTS, "error");
    }

    public void help() {
        CommandLineBuilder.printHelp(compilerName);

    }

    public String actionId() {
        return "compile";
    }
}
