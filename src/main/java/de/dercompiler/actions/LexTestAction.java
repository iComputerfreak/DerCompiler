package de.dercompiler.actions;

public class LexTestAction extends Action {

    public static final String HELP_TEXT = "--lexTest <file>: Generates a sequence of tokens out of the file and prints it to the console.";
    public static final String COMMAND_LINE_NAME = "lexTest";

    @Override
    public void run() {
        System.err.println("LexTestAction::run is not implemented yet.");
    }

    @Override
    public void help() {
        System.out.println(HELP_TEXT);
    }

    @Override
    public String actionId() {
        return COMMAND_LINE_NAME;
    }
}
