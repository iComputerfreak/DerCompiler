package de.dercompiler.actions;

import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.io.File;

public class CompileAction extends Action {

    private static final String compilerName = "DerCompiler";

    private final File input;

    public CompileAction(File input) {
        this.input = input;
    }

    @Override
    public void run() {
        new OutputMessageHandler(MessageOrigin.GENERAL, System.out)
            .printInfo("Compiler not Implemented YET!");
    }

    @Override
    public void help() {
        CommandLineBuilder.printHelp(compilerName);
    }

    @Override
    public String actionId() {
        return "compile";
    }
}
