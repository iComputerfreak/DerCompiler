package de.dercompiler.actions;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.io.*;
import java.util.Objects;

public class EchoAction extends Action {

    private final File input;

    private final static String HELP_TEXT = "--echo <file>: prints the content of the file to the console.";

    public EchoAction(File input) {
        this.input = input;
    }

    public void run() {
        if (Objects.isNull(input)) {
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err).printError(GeneralErrorIds.MISSING_INPUT_FILE, "No input-file given.");
        }
        try (FileInputStream file = new FileInputStream(input)) {
            file.transferTo(System.out);
        } catch (IOException e) {
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err).printError(GeneralErrorIds.FILE_NOT_FOUND, "Something went wrong, while reading input-file (" + input.getAbsolutePath() + ")!", e);
        }
    }

    public void help() {
        System.out.println(HELP_TEXT);
    }

    @Override
    public String actionId() {
        return "echo";
    }
}
