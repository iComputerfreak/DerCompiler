package de.dercompiler.actions;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.io.*;
import java.util.Objects;

/**
 * Represents the action that prints the contents of the given file
 */
public class EchoAction extends Action {

    /**
     * The file to echo
     */
    private final File input;

    private final static String HELP_TEXT = "--echo <file>: prints the content of the file to the console.";

    /**
     * Creates a new EchoAction with the given input file
     * @param input The file to print
     */
    public EchoAction(File input) {
        this.input = input;
    }

    public void run() {
        if (Objects.isNull(input)) {
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err).printErrorAndExit(GeneralErrorIds.MISSING_INPUT_FILE, "No input-file given.");
        }
        try (FileInputStream file = new FileInputStream(input)) {
            file.transferTo(System.out);
        } catch (IOException e) {
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err).printErrorAndExit(GeneralErrorIds.FILE_NOT_FOUND, "Something went wrong, while reading input-file (" + input.getAbsolutePath() + ")!", e);
        }
    }

    public void help() {
        System.out.println(HELP_TEXT);
    }

    public String actionId() {
        return "echo";
    }
}
