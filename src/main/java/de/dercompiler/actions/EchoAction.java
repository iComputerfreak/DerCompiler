package de.dercompiler.actions;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
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
    private final Source source;

    private final static String HELP_TEXT = "--echo <file>: prints the content of the file to the console.";
    private final static String COMMAND_LINE_NAME = "echo";
    /**
     * Creates a new EchoAction with the given input file
     * @param source The source to print
     */
    public EchoAction(Source source) {
        this.source = source;
    }

    public void run() {
        try {
            source.getNewStream().transferTo(System.out);
        } catch (IOException e) {
            new OutputMessageHandler(MessageOrigin.GENERAL).printErrorAndExit(GeneralErrorIds.FILE_NOT_FOUND, "Something went wrong, while reading the input (" + source + ")!", e);
        }
    }

    public void help() {
        System.out.println(HELP_TEXT);
    }

    public String actionId() {
        return COMMAND_LINE_NAME;
    }
}
